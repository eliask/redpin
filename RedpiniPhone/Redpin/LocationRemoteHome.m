//
//  LocationRemoteHome.m
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


#import "LocationRemoteHome.h"
#import "EntityHome.h"
#import "LocationHome.h"
#import "MapHome.h"

@implementation LocationRemoteHome

@synthesize delegate;

NSString * const LocationRemoteHomeDeletionNotification = @"LocationRemoteHomeDeletion";

- (id) initWithDelegate:(id<RemoteEntityHomeProtocolDelegate>)aDelegate {
	if((self = [super init])) {
		self.delegate = aDelegate;
	}	
	return self;
}

- (void) insertObject:(id)anObject {		
	[delegate entityHome:self didInsertObject:anObject];	
}

- (void) deleteObject:(id)anObject {
	/*
	NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
	[nc postNotificationName:LocationRemoteHomeDeletionNotification object:anObject];
	*/
	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_removeLocation data:anObject];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(delete_didRecieveResponse:fromRequest:connection:)];
	
	
	
	
}

- (void) delete_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	
	if(response.status == ServerResponseStatus_OK) {				
		[delegate entityHome:self didDeleteObject:request.data];		
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not delete location\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
		
	[request release];	
	[response release];
	[connection release];
}



- (void) updateObject:(id)anObject {
	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_updateLocation data:anObject];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(update_didRecieveResponse:fromRequest:connection:)];
	
}

- (void) update_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	if(response.status == ServerResponseStatus_OK) {				
		[delegate entityHome:self didUpdateObject:request.data];		
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not update location\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
}


- (void) fetchObjects {
	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_getLocationList data:nil];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(fetchObjects_didRecieveResponse:fromRequest:connection:)];
	
}

- (void) fetchObjects_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
		
	if(response.status == ServerResponseStatus_OK) {
		
		NSManagedObjectContext *context = [[EntityHome sharedEntityHome] managedObjectContext];
		
		if([response.data isKindOfClass:[NSArray class]]) {
			
			NSArray *serverData = [response.data retain];		
			
			NSFetchRequest *fetch_request = [[LocationHome defaultFetchRequest] retain];			
			NSError *error = nil;
			NSArray *localData = [context executeFetchRequest:fetch_request error:&error];
			if(!localData) {
				// Handle error
				NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
				UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
				[view show];
				[view release];
			}
			[fetch_request release];
			
			//init dictionary with rId -> map mapping
			NSMutableDictionary *localDict = [[NSMutableDictionary alloc] initWithCapacity:[localData count]];
			for(Location *loc in localData) {
				
				if([loc.rId intValue] == -1) {
					NSLog(@"Deleting object witout id");
					[context deleteObject:loc];
				} else {
					[localDict setObject:loc forKey:[loc.rId stringValue]];
				}				
			}			
			
			for(NSDictionary *dict in serverData) {
				NSNumber *key = [dict objectForKey:@"id"];
				Location *loc = [localDict objectForKey:[key stringValue]];
				if(!loc) {
					// create map
					Location *loc = [Location fromJSON:dict];
					
					if(!loc.map) {
						NSLog(@"%s: need to save map first", __FUNCTION__);
						NSDictionary *mapDict = [dict objectForKey:@"map"];
						if(mapDict) {
							key = [mapDict objectForKey:@"id"];
							
							if(key && ([key intValue] != -1)) {
								Map *curMap = [MapHome getMapByRemoteId:key];
								[LocationHome insertObjectInContext:loc];
								[loc setMap:curMap];
																
							} else {
								NSLog(@"%s: no map id, can't save location", __FUNCTION__);
							}								
							
						} else {
							NSLog(@"%s: no map dictionary from json", __FUNCTION__)	;
						}						
					}					
				} else {
					NSString *symbolicID = [dict objectForKey:@"symbolicID"];
					NSNumber *mapXcord = [dict objectForKey:@"mapXcord"];
					NSNumber *mapYcord = [dict objectForKey:@"mapYcord"];
					NSNumber *accuracy = [dict objectForKey:@"accouracy"];
					
					if(symbolicID && ![loc.symbolicID isEqualToString:symbolicID]) {
						[loc setSymbolicID:symbolicID];
					}
					
					if(mapXcord && ![loc.mapXcord isEqualToNumber:mapXcord]) {
						[loc setMapXcord:mapXcord];
					}
					
					if(mapYcord && ![loc.mapYcord isEqualToNumber:mapYcord]) {
						[loc setMapYcord:mapYcord];
					}
					
					if(accuracy && ![loc.accuracy isEqualToNumber:accuracy]) {
						[loc setAccuracy:accuracy];
					}
					
					[localDict removeObjectForKey:[key stringValue]];
				}
				
				
				
			}
			
			
			// delete locations no more present on server
			if([localDict count] > 0) {
				for(Location *loc in [localDict allValues]) {
					[context deleteObject:loc];
				}
			}
			
			
			[serverData release];
			[localDict release];
			
			error = nil;
			//dont use entityHome's save context because no server interaction needed
			if (![context save:&error]) {
				// Handle error
				NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
				UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
				[view show];
				[view release];
				//exit(-1);  // Fail
			}			
			
		} else {
			NSLog(@"No Array recieved");
		}		
	}
	
	if([delegate respondsToSelector:@selector(entityHomeDidFetchObjects:)]) {
		[delegate entityHomeDidFetchObjects:self];
	}
	
	[request release];	
	[response release];
	[connection release];
	
}


- (void) getLocationWithMeasurement:(Measurement *)measurement {
	

	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_getLocation data:measurement];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(getLocation_didRecieveResponse:fromRequest:connection:)];
	
}

- (void) getLocation_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	if(response.status == ServerResponseStatus_OK) {
		
		[response.data retain];
		if([response.data isKindOfClass:[NSDictionary class]]) {
			
			NSNumber *key = [response.data objectForKey:@"id"];
			Location *curLocation = [LocationHome getLocationByRemoteId:key];
			if(!curLocation) {
				curLocation =[Location fromJSON:response.data];
				[LocationHome insertObjectInContext:curLocation];
			}
			
			NSNumber *acc = [response.data objectForKey:@"accuracy"];
			if(acc) {
				[curLocation setAccuracy:acc];
			}
			
			if([delegate respondsToSelector:@selector(entityHome:gotLocation:)]) {
				[delegate entityHome:self gotLocation:curLocation];
			}
			
		}
		
		
		
		[response.data release];
		
		
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not get location\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
	
	[request release];	
	[response release];
	[connection release];
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
