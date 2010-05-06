//
//  RemoteEntityHome.m
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


#import "RemoteEntityHome.h"
#import "MapRemoteHome.h"
#import "Map.h"
#import "LocationRemoteHome.h"
#import "Location.h"
#import "FingerprintRemoteHome.h"
#import "Fingerprint.h"
#import "MeasurementRemoteHome.h"
#import "Measurement.h"
#import "WifiReadingRemoteHome.h"
#import "WifiReading.h"


NSString *const RemoteEntityHomeErrorDomain = @"RemoteEntityHomeError";

@implementation RemoteEntityHome
@synthesize delegate, objectsToInsert, objectsToUpdate, objectsToDelete;


- (id<RemoteEntityHomeProtocol>) remoteEntityHomeForEntity:(id) object {	
	
	if([object isKindOfClass:[Map class]]) {
		return [[MapRemoteHome alloc] initWithDelegate:self];
	}
	
	if([object isKindOfClass:[Location class]]) {
		return [[LocationRemoteHome alloc] initWithDelegate:self];
	}
	
	if([object isKindOfClass:[Fingerprint class]]) {
		return [[FingerprintRemoteHome alloc] initWithDelegate:self];
	}
	
	if([object isKindOfClass:[Measurement class]]) {
		return [[MeasurementRemoteHome alloc] initWithDelegate:self];
	}
	
	if([object isKindOfClass:[WifiReading class]]) {
		return [[WifiReadingRemoteHome alloc] initWithDelegate:self];
	}
		
	NSLog(@"No matching remote entity home found");
	return nil;
	
}

- (id) initWithDelegate:(id)aDelegate {
	if((self = [super init])) {
		self.delegate = aDelegate;
	}
	
	return self;
}

- (void) insertObjects:(NSSet *) objects {

	NSMutableSet *mset = [objects mutableCopy];
	self.objectsToInsert = mset ;
	[mset release];
	
	for(DBEntity *obj in objects) {
		NSLog(@"DBEntity: %@, %d", obj, [obj.rId intValue]);
		if([[obj rId] intValue] == -1) {
			[[self remoteEntityHomeForEntity:obj] insertObject:obj];
		} else {
			[objectsToInsert removeObject:obj];
		}
	}
}

- (void) deleteObjects:(NSSet *) objects {
	
	NSMutableSet *mset =  [objects mutableCopy];
	self.objectsToDelete = mset;
	[mset release];
	
	for(id obj in objects) {		
		[[self remoteEntityHomeForEntity:obj] deleteObject:obj];		
	}	
}

- (void) updateObjects:(NSSet *) objects {
	
	NSMutableSet *mset =  [objects mutableCopy];
	self.objectsToUpdate = mset;
	[mset release];
	
	for(id obj in objects) {
		[[self remoteEntityHomeForEntity:obj] updateObject:obj];		
	}
}


- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didInsertObject:(id)anObject {
	if(objectsToInsert && [objectsToInsert containsObject:anObject]) {
		[objectsToInsert removeObject:anObject];		
		
		if([objectsToInsert count] == 0) {
			NSLog(@"%s: inserted all objects, sending message to delegate %@ ",__FUNCTION__,delegate);
			[delegate remoteEntityHomeDidInsertObjects];
			self.objectsToInsert = nil;
		}
		
	} else {
		NSLog(@"%s: object %@ not in set %@",__FUNCTION__, anObject, objectsToInsert);		
	}
	
	[objectHome release];
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didDeleteObject:(id)anObject {
	if(objectsToDelete && [objectsToDelete containsObject:anObject]) {
		[objectsToDelete removeObject:anObject];		
		
		if([objectsToDelete count] == 0) {
			NSLog(@"%s: deleted all objects, sending message to delegate %@ ",__FUNCTION__,delegate);
			[delegate remoteEntityHomeDidDeleteObjects];
			self.objectsToDelete = nil;
		}
		
	} else {
		NSLog(@"%s: object %@ not in set %@",__FUNCTION__, anObject, objectsToDelete);	
		
	}
	
	[objectHome release];
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didUpdateObject:(id)anObject {
	if(objectsToUpdate && [objectsToUpdate containsObject:anObject]) {
		[objectsToUpdate removeObject:anObject];
		
		
		if([objectsToUpdate count] == 0) {
			NSLog(@"%s: updated all objects, sending message to delegate %@ ",__FUNCTION__,delegate);
			[delegate remoteEntityHomeDidUpdateObjects];
			self.objectsToUpdate = nil;
		}
		
	} else {
		NSLog(@"%s: object %@ not in set %@",__FUNCTION__, anObject, objectsToUpdate);	
		
	}
	
	[objectHome release];
	
}

- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didFailWithError:(NSError *) error {
	[delegate remoteEntityHomeDidFailWithError:error];
	[objectHome release];
}

- (void)dealloc {
    [super dealloc];
}


@end
