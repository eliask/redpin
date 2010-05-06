//
//  Sniffer.m
//  Redpin
/**  This file is part of the Redpin project.
 * 
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 * © Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */

#import "Sniffer.h"

#import "Measurement.h"
#import "MeasurementHome.h"
#import "WifiReading.h"
#import "WifiReadingHome.h"
#import "Location.h"
#import "LocationRemoteHome.h"

#define NUM_OF_SCANS 3
#define SCAN_INTERVAL 1.0f
#define REQUIRED_OCCURRENCE_IN_PERCENTAGE 1.0f/3.0f

@interface Sniffer()

- (void) startScan;
- (void)fireScan:(NSTimer*)theTimer;
- (void) scanFinished:(NSMutableArray *) measurements;
- (Measurement *) scanWiFi;
- (NSMutableDictionary *) joinDictionary:(NSMutableDictionary *) dict withMeasurement:(Measurement *) m;
- (NSString *) fixBSSID:(NSString *) bssid;

@end


@implementation Sniffer
@synthesize delegate;


- (id) initWithDelegate:(id) aDelegate {
	if((self = [self init])) {
		self.delegate = aDelegate;
	}
	
	return self;	
}

- (id)init {
	
	if((self = [super init])) {
		
		#if TARGET_IPHONE_SIMULATOR
		return self;
		#endif
		
		scanning = NO;		
		
		libHandle = dlopen("/System/Library/SystemConfiguration/WiFiManager.bundle/WiFiManager", RTLD_LAZY);
				
		if(!libHandle) {
			return nil;
		}
		open = dlsym(libHandle, "Apple80211Open");
		bind = dlsym(libHandle, "Apple80211BindToInterface");
		close = dlsym(libHandle, "Apple80211Close");
		scan = dlsym(libHandle, "Apple80211Scan");
		
		open(&airportHandle);
		bind(airportHandle, @"en0");
		
		
	}
	
	return self;
	
}

- (void)dealloc {
#if TARGET_IPHONE_SIMULATOR
	
#else
	close(airportHandle);
    dlclose(libHandle);
#endif
	[super dealloc];
}

static NSUInteger skipCount = 0;
static NSUInteger totalSkipCount = 0;

- (void) startScan {
	
	NSMutableArray *measurementArray = [[NSMutableArray alloc] initWithCapacity:NUM_OF_SCANS];	
	[NSTimer scheduledTimerWithTimeInterval:SCAN_INTERVAL target:self selector:@selector(fireScan:) userInfo:measurementArray repeats:YES];	
	
	UIAccelerometer *accel = [UIAccelerometer sharedAccelerometer];
	accel.delegate = self;
	accel.updateInterval = 1.0f/60.0f;
	skipCount = 0;
	totalSkipCount = 0;
	movedWhileScanning = NO;
	skipAccelerator = NO;
			
}


- (void)fireScan:(NSTimer*)theTimer {
		
	NSLog(@"Timer fired, taking measurement");
	
	if(!skipAccelerator && movedWhileScanning) {
		skipCount++;
		
		if(skipCount > 4) {
			
			if([delegate respondsToSelector:@selector(sniffer:detectedContinuingMovement:)]) {
				[delegate sniffer:self detectedContinuingMovement:skipCount];
			}
			totalSkipCount += skipCount;
			skipCount = 0;
			
			if(totalSkipCount > 10) {
				NSLog(@"to many skips, no taking measurements");
				skipAccelerator = YES;
			}
		}
		movedWhileScanning = NO;
		
		NSLog(@"skip current scan");
		return;
	}
	
	NSMutableArray *mArray = [theTimer userInfo];
	
	if([mArray count] > NUM_OF_SCANS) {
		return;
	}
	
	Measurement *m = [self scanWiFi];
	[mArray addObject:m];
	
	if([mArray count] == NUM_OF_SCANS) {
		[mArray retain];
		[theTimer invalidate];
		[self scanFinished:mArray];
		[mArray release];
	}	
}

static CGFloat threashold = 2.0f;
- (void)accelerometer:(UIAccelerometer *)acel didAccelerate:(UIAcceleration *)aceler {
	
    if ((aceler.x * aceler.x + aceler.y * aceler.y + aceler.z * aceler.z) > threashold*threashold  )  {
		movedWhileScanning = YES;
		NSLog(@"acceleration found");
    }
}

- (void) scanFinished: (NSMutableArray *) measurements {
	
	[[UIAccelerometer sharedAccelerometer] setDelegate:nil];
	
	NSMutableDictionary *dict = nil;
	for(Measurement *m in measurements) {
		dict = [self joinDictionary:dict withMeasurement:m];
	}
	
	[measurements release];
	
	CGFloat required_corrurrences =  ((CGFloat)NUM_OF_SCANS)*REQUIRED_OCCURRENCE_IN_PERCENTAGE;
	
	Measurement *finalMeasurement = [[MeasurementHome newObject] retain];
	NSUInteger count;
	NSInteger accRssi;
	for(NSMutableArray *a in [dict allValues]) {
		count = [a count];
		
		if(count >= required_corrurrences) {
			accRssi = 0;
			for(NSUInteger i=0; i < count; i++) {
				accRssi += [[[a objectAtIndex:i] rssi] intValue];
			}
			
			WifiReading *r = [a objectAtIndex:0];			
			[r setRssi:[NSNumber numberWithInt:accRssi / (NSInteger)count]];
			[finalMeasurement addWifiReadingsObject:r];
		} else {
			//Skip because signal is not alway captured
			//NSLog(@"Skiped wifireading %@ with count %d", [a objectAtIndex:0], [a count]);
		}
		
	}
	[dict release];
	
	if([delegate respondsToSelector:@selector(sniffer:didScan:)]) {
		[delegate sniffer:self didScan:finalMeasurement];
	}
	
}

- (NSMutableDictionary *) joinDictionary:(NSMutableDictionary *) dict withMeasurement:(Measurement *) m {
	if(!dict) {	
		NSMutableDictionary *new_dict = [[NSMutableDictionary alloc] initWithCapacity:[m.wifiReadings count]];
		
		for(WifiReading *r in m.wifiReadings) {		
			[new_dict setObject:[NSMutableArray arrayWithObject:r] forKey:[r bssid]];
		}	
		
		return new_dict;
		
	} else {
		
		NSMutableArray *arr;
		
		for(WifiReading *r in m.wifiReadings) {
			arr = [dict objectForKey:[r bssid]];
			if(arr) {
				[arr addObject:r];				
			} else {
				[dict setObject:[NSMutableArray arrayWithObject:r] forKey:[r bssid]];
			}
			
		}
		return dict;
	}
	

	
}



- (Measurement *) scanWiFi {
	
	
#if TARGET_IPHONE_SIMULATOR
	Measurement *demo_measurement = [[MeasurementHome newObject] retain];
	WifiReading *r1 = [[WifiReadingHome newObject] retain];
	[r1 setBssid:[self fixBSSID:@"0:ff:11:22:33:44"]];
	[r1 setSsid:@"SSID"];
	[r1 setRssi:[NSNumber numberWithInt:-24+rand()%25]];
	[r1 setWepEnabled:[NSNumber numberWithBool:NO]];
	[r1 setIsInfrastructure:[NSNumber numberWithBool:YES]];
	
	[demo_measurement addWifiReadingsObject:r1];	
	[r1 release];
	return demo_measurement;
	

#endif	
	
	if(scanning) {
		return nil;
	}
		
    scanning = YES;
    
	Measurement *measurement = [[MeasurementHome newObject] retain];
   	
    NSArray	*networks;
    NSDictionary *params = [[NSDictionary alloc] init];
    scan(airportHandle, &networks, params);
	
    NSUInteger count = [networks count];
	
    for(NSUInteger i=0 ; i < count ; i++) {
		NSDictionary *nw = [networks objectAtIndex:i];
		
		WifiReading *reading = [[WifiReadingHome newObject] retain];
		
		[reading setSsid:[nw objectForKey:@"SSID_STR"]];
		[reading setBssid:[self fixBSSID:[nw objectForKey:@"BSSID"]]];
		[reading setRssi:[nw objectForKey:@"RSSI"]];
		[reading setIsInfrastructure:[NSNumber numberWithBool:[[nw objectForKey:@"AP_MODE"] isEqual:[NSNumber numberWithInt:2]]]];
		
		[measurement addWifiReadingsObject:reading];
		[reading release];		
	}	
	
    [networks	release];
    [params	release];
	
    scanning = NO;	
	
	return measurement;	

}


- (void) performScan {	
	[self startScan];		
}

- (void) retrieveLocationForMeasurement:(Measurement *) measurement {

	LocationRemoteHome *locRemoteHome = [[LocationRemoteHome alloc] initWithDelegate:self];
	[locRemoteHome getLocationWithMeasurement:measurement]; 
	
}

- (NSString *)fixBSSID:(NSString *)bssid {
	
	NSArray *parts = [bssid componentsSeparatedByString:@":"];
	NSString *str = [NSString string];
	
	for (uint i=0; i < parts.count; i++) {
		if (i > 0) {
			str = [str stringByAppendingString:@":"];
		}
		NSString *s = [parts objectAtIndex:i];
		if (s && s.length == 1) {
			str = [str stringByAppendingString:@"0"];
		} 
		str = [str stringByAppendingString:s];
	}
	
	return str;
}


#pragma mark -
#pragma mark RemoteEntityHomeRemoteDelegate

- (void) entityHome:(id <RemoteEntityHomeProtocol>)objectHome gotLocation:(Location *) location {
	NSLog(@"%s: got location %@",__FUNCTION__, location);
	[objectHome release];
	
	if([delegate respondsToSelector:@selector(sniffer:estimatedLocation:)]) {
		[delegate sniffer:self estimatedLocation:location];
	}
	
}


- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didInsertObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didDeleteObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didUpdateObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didFailWithError:(NSError *) error {
	[objectHome release];	
	if([delegate respondsToSelector:@selector(sniffer:estimatedLocation:)]) {
		[delegate sniffer:self estimatedLocation:nil];
	}
	
}


@end
