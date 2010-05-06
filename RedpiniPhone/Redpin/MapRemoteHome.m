//
//  MapRemoteHome.m
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


#import "MapRemoteHome.h"
#import "MapHome.h"
#import "Map.h"



@interface MapRemoteHome()
- (BOOL) checkObject:(id)anObject;
@end


@implementation MapRemoteHome
@synthesize delegate;



- (id) initWithDelegate:(id<RemoteEntityHomeProtocolDelegate>)aDelegate {
	if((self = [super init])) {
		self.delegate = aDelegate;
	}
		
	return self;
}

- (void) insertObject:(id)anObject {		
	
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_setMap data:anObject];
	//cast needed inorder to prevent warning
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];		
	[connection performRequest:request responseAction:@selector(insert_didRecieveResponse:fromRequest:connection:)];		

		
}

- (void) insert_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	
	Map *map = request.data;
	
	if(response.status == ServerResponseStatus_OK) {
		if([response.data isKindOfClass:[NSDictionary class]]) {
			NSNumber *key = [response.data objectForKey:@"id"];
			if([key intValue] != -1) {
				NSLog(@"%s: set key %@ of map %@", __FUNCTION__,key, map);
				[map setRId:key];
				[((Map *) request.data) setRId:key];
			}
		}
		
		[delegate entityHome:self didInsertObject:request.data];
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not insert map\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
	
	
	[request release];	
	[response release];
	[connection release];
}


- (void) deleteObject:(id)anObject {
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_removeMap data:anObject];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(delete_didRecieveResponse:fromRequest:connection:)];
}

- (void) delete_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {

	if(response.status == ServerResponseStatus_OK) {				
		[delegate entityHome:self didDeleteObject:request.data];		
	} else {		
		NSDictionary *userInfo =  [NSDictionary dictionaryWithObjectsAndKeys:  [NSString stringWithFormat:@"Could not delete map\n%@",response], NSLocalizedDescriptionKey,  nil];
		[delegate entityHome:self didFailWithError:[NSError errorWithDomain:RemoteEntityHomeErrorDomain code:1 userInfo:userInfo]];
	}
	
	
	[request release];	
	[response release];
	[connection release];
}

- (void) updateObject:(id)anObject {
	// do nothing
	[delegate entityHome:self didUpdateObject:anObject];	
}


- (void) fetchObjects {
	ServerRequest *request = [[ServerRequest alloc] initWithAction:ServerRequestAction_getMapList data:nil];
	ServerConnection *connection = [(ServerConnection *)[ServerConnection alloc] initWithDelegate:self];
	[connection performRequest:request responseAction:@selector(fetchObjects_didRecieveResponse:fromRequest:connection:)];			
}

- (void) fetchObjects_didRecieveResponse:(ServerResponse*) response fromRequest:(ServerRequest *) request connection:(ServerConnection *)connection; {
	
	if(response.status == ServerResponseStatus_OK) {		
		NSManagedObjectContext *context = [[EntityHome sharedEntityHome] managedObjectContext];
		
		if([response.data isKindOfClass:[NSArray class]]) {
			
			NSArray *serverData = [response.data retain];	
						
			NSFetchRequest *fetch_request = [[MapHome defaultFetchRequest] retain];			
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
			for(Map *map in localData) {
				
				if([map.rId intValue] == -1) {
					NSLog(@"Deleting object witout id");
					[context deleteObject:map];
				} else {
					[localDict setObject:map forKey:[map.rId stringValue]];
				}
			}
			
			
			for(NSDictionary *dict in serverData) {
				NSNumber *key = [dict objectForKey:@"id"];
				Map *map = [localDict objectForKey:[key stringValue]];
				if(!map) {
					// create map
					Map *map = [Map fromJSON:dict];
					[MapHome insertObjectInContext:map];
					NSLog(@"imported map %@", map);					
				} else {
					NSString *name = [dict objectForKey:@"mapName"];
					NSString *url = [dict objectForKey:@"mapURL"];
					
					if(name && ![map.mapName isEqualToString:name]) {
						[map setMapName:name];
					}
					
					if(url && ![map.mapURL isEqualToString:url]) {
						[map setMapURL:url];
					}
					
					[localDict removeObjectForKey:[key stringValue]];
				}
				
				
				
			}
			
			
			// delete maps no more present on server
			if([localDict count] > 0) {
				for(Map *map in [localDict allValues]) {
					[context deleteObject:map];
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


- (BOOL) checkObject:(id)anObject {
	if(![anObject isKindOfClass:[Map class]]) {
		NSLog(@"Wrong object is of Kind %@", [anObject class]);
		return NO;
	} else {
		return YES;
	}
}

- (void)dealloc {

    [super dealloc];
}

@end
