//
//  IntervalScanner.m
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
 * © Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */

#import "IntervalScanner.h"
#import "IntervalScannerInfo.h"
#import "Fingerprint.h"
#import "FingerprintHome.h"
#import "Location.h"
#import "LocationHome.h"
#import "Measurement.h"
#import "MeasurementHome.h"
#import "WifiReading.h"
#import "WifiReadingHome.h"

#define SCAN_INTERVAL 30.0f // time in seconds between scans
#define MAX_INTERVAL 3600.f // maximal interval

extern NSString * const IntervalScanStopNotification;

@implementation IntervalScanner

@synthesize delegate;


- (id) initWithDelegate:(id) aDelegate {
	if([super init]) {
		self.delegate = aDelegate;
		NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
		[nc addObserver:self selector:@selector(endScan:) name:IntervalScanStopNotification object:nil];
	}
	
	return self;
}


- (void) endScan {
	if (inProcess) {
		inProcess = NO;
		[[UIAccelerometer sharedAccelerometer] setDelegate:nil];
		if (timer) {
			[timer invalidate];
		}
		if([delegate respondsToSelector:@selector(scanner:finishScan:)]) {
			[delegate scanner:self finishScan:count];
		}
	}
}


- (void) endScan: (NSNotification *) note {
	[self endScan];
}


- (void) takeNextMeasurement : (NSTimer *) aTimer {
	
	if (!inProcess || MAX_INTERVAL / SCAN_INTERVAL < count) {
		[self endScan];
		return;
	}
	IntervalScannerInfo *info = [[IntervalScannerInfo alloc] init];
	NSNumber *lId = [info locationIdInProcess];
	if ([lId intValue] != -1) {
		Sniffer *sniffer = [[Sniffer alloc] initWithDelegate:self];
		if(sniffer) {
			count++;
			[sniffer performScan];
		} else {
			[self endScan];
		}	
	}
	[info release];
	
}

- (void) copyPosition:(Measurement *) m {
	IntervalScannerInfo *info = [[IntervalScannerInfo alloc] init];
	NSNumber *lId = [info locationIdInProcess];
	if ([lId intValue] != -1) {
		Fingerprint *fp = [[FingerprintHome newObjectInContext] retain];
		Location *loc = [[LocationHome newObjectInContext] retain];
		[loc setMap:[[info locationInProcess] map]];
		[loc setAccuracy:[[info locationInProcess] accuracy]];
		[loc setMapXcord:[[info locationInProcess] mapXcord]];
		[loc setMapYcord:[[info locationInProcess] mapYcord]];
		[loc setSymbolicID:[[info locationInProcess] symbolicID]];
		[loc setReflocationId:lId];
		[loc setRId:lId];
		
		for(WifiReading *r in m.wifiReadings) {
			[WifiReadingHome insertObjectInContext:r];
		}
		[MeasurementHome insertObjectInContext:m];
		
		[fp setLocation:loc];
		[fp setMeasurement:m];
		
		[[EntityHome sharedEntityHome] saveContext];
	}
	
	[info release];
	[m release];
}



- (void) startScan {
	inProcess = YES;
	moved = NO;
	count = 0;
	timer = [NSTimer scheduledTimerWithTimeInterval:SCAN_INTERVAL 
											 target:self 
										   selector:@selector(takeNextMeasurement:) 
										   userInfo:nil 
											repeats:YES];
}



#pragma mark -
#pragma mark SnifferDelegate

- (void) sniffer:(Sniffer *)aSniffer didScan:(Measurement *) measurement {
	[self copyPosition: measurement];
	[measurement release];
	[aSniffer release];
	if (moved) {
		[self endScan];	  
	}
	
	UIAccelerometer *accel = [UIAccelerometer sharedAccelerometer];
	accel.delegate = self;
	accel.updateInterval = 1.0f/60.0f;
}


- (void) sniffer:(Sniffer *)aSniffer detectedContinuingMovement:(NSUInteger) numberOfSeconds  {
	moved = YES;
}

- (void) dealloc {
	NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
	[nc removeObserver:self];
	[super dealloc];
}


#pragma mark -
#pragma mark UIAccelerometerDelegate
- (void)accelerometer:(UIAccelerometer *)acel didAccelerate:(UIAcceleration *)aceler {
	
	CGFloat threashold = 1.3f;
    if ((aceler.x * aceler.x + aceler.y * aceler.y + aceler.z * aceler.z) > threashold * threashold )  {
		[self endScan];
    }
}

@end
