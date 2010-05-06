//
//  FingerprintRemoteHome.m
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
 * © Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */

#import "FingerprintRemoteHome.h"
#import "Fingerprint.h"
#import "Measurement.h"
#import "WifiReading.h"
#import "Map.h"
#import "Location.h"
#import "MapHome.h"
#import "LocationHome.h"

@implementation FingerprintRemoteHome
@synthesize delegate;

- (id) initWithDelegate:(id<RemoteEntityHomeProtocolDelegate>)aDelegate {
	if((self = [super init])) {
		self.delegate = aDelegate;
	}
	
	return self;
}

- (void) insertObject:(id)anObject {	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_setFingerprint data:anObject];

	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];		
	[connection performRequest:request responseAction:@selector(insert_didRecieveResponse:fromRequest:connection:)];	
}


- (void) insert_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	
	Fingerprint *fprint = request.data;
	
	if(response.status == ServerResponseStatus_OK) {	
		
		if([response.data isKindOfClass:[NSDictionary class]]) {
			NSNumber *key = [response.data objectForKey:@"id"];
			if([key intValue] != -1) {
				NSLog(@"%s: set key %@ of fingerprint %@", __FUNCTION__,key, fprint);
				[fprint setRId:key];			
			}		
			
			NSDictionary *locationDict = [response.data objectForKey:@"location"];
			if([locationDict isKindOfClass:[NSDictionary class]]) {
				key = [locationDict objectForKey:@"id"];
				if([key intValue] != -1) {
					Location *loc = ((Fingerprint *) request.data).location;
					if ([[loc rId] intValue] == -1) {
											
						[loc setRId:key];
						[loc setMapXcord:[locationDict objectForKey:@"mapXcord"]];
						[loc setMapYcord:[locationDict objectForKey:@"mapYcord"]];
						[loc setAccuracy:[locationDict objectForKey:@"accuracy"]];
						[loc setSymbolicID:[locationDict objectForKey:@"symbolicID"]];
						/*
						NSDictionary *mapDict = [locationDict objectForKey:@"map"];
						if([mapDict isKindOfClass:[NSDictionary class]]) {
							Map *map = [MapHome getMapByRemoteId:[mapDict objectForKey:@"id"]];
							if(map) {
								[loc setMap:map];
							}
						}
						*/
					}
					
					[fprint setLocation:loc];
				}				
			}
			
			NSDictionary *measurementDict = [response.data objectForKey:@"measurement"];
			if([measurementDict isKindOfClass:[NSDictionary class]]) {
				key = [measurementDict objectForKey:@"id"];
				if([key intValue] != -1) {
					[fprint.measurement setRId:key];
				}
				
				NSArray *wifiReadingsDicts = [measurementDict objectForKey:@"wifiReadings"];
				NSArray *wifiReadings = [fprint.measurement.wifiReadings allObjects];
								
				if([wifiReadings isKindOfClass:[NSArray class]]) {
					NSUInteger count = [wifiReadings count];
					
					for(NSUInteger i=0; i < count; i++) {
						
						NSDictionary *curWifiDict = [wifiReadingsDicts objectAtIndex:i];
						WifiReading *curWifiReading = [wifiReadings objectAtIndex:i];
						
						key = [curWifiDict objectForKey:@"id"];
						if([key intValue] != -1) {
							[curWifiReading setRId:key];
						}						
					}					
				}
			}			
		}
		
		[delegate entityHome:self didInsertObject:request.data];
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not insert fingerprint\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
	
	[request release];	
	[response release];
	[connection release];
}


- (void) deleteObject:(id)anObject {
	[delegate entityHome:self didDeleteObject:anObject];
}


- (void) updateObject:(id)anObject {
	[delegate entityHome:self didUpdateObject:anObject];
	
}

- (void) fetchObjects {
	
}

- (void) serverRequest:(ServerRequest *) request didFailWithError:(NSError *) error connection:(ServerConnection *) connection {
	if(delegate) {
		[delegate entityHome:self didFailWithError:error];
	} else {
		NSLog(@"%@", error);
	}
	
	[request release];
	//need autorlease because AsyncSocket does sent onSocketDidDisconnect afterwards to connection
	[connection autorelease];
	
}

@end
