//
//  RemoteEntityHome.h
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


#import <Foundation/Foundation.h>
#import "ServerRequest.h"
#import "ServerConnection.h"

@protocol RemoteEntityHomeProtocol;
@protocol RemoteEntityHomeDelegate;


UIKIT_EXTERN NSString *const RemoteEntityHomeErrorDomain;

//protocol which a remote entity home delegate must implement. E.g RemoteEntityHome does implement this, or SynchronizationManager
@protocol RemoteEntityHomeProtocolDelegate <NSObject>
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didInsertObject:(id)anObject;
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didDeleteObject:(id)anObject;
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didUpdateObject:(id)anObject;
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didFailWithError:(NSError *) error;

@optional
- (void) entityHomeDidFetchObjects:(id <RemoteEntityHomeProtocol>) objectHome;
@end


@interface RemoteEntityHome : NSObject <RemoteEntityHomeProtocolDelegate> {
	id <RemoteEntityHomeDelegate> delegate;
	NSMutableSet *objectsToInsert;
	NSMutableSet *objectsToDelete;
	NSMutableSet *objectsToUpdate;

	
}

@property (nonatomic, assign) id <RemoteEntityHomeDelegate> delegate;
@property (nonatomic, retain) NSMutableSet *objectsToInsert;
@property (nonatomic, retain) NSMutableSet *objectsToDelete;
@property (nonatomic, retain) NSMutableSet *objectsToUpdate;



- (id) initWithDelegate:(id<RemoteEntityHomeDelegate>)aDelegate;

- (id<RemoteEntityHomeProtocol>) remoteEntityHomeForEntity:(NSManagedObject *) object;

- (void) insertObjects:(NSSet *) objects;
- (void) deleteObjects:(NSSet *) objects;
- (void) updateObjects:(NSSet *) objects;

@end


@protocol RemoteEntityHomeProtocol <NSObject>

- (id) initWithDelegate:(id<RemoteEntityHomeProtocolDelegate>)aDelegate;
- (void) insertObject:(id)anObject;
- (void) deleteObject:(id)anObject;
- (void) updateObject:(id)anObject;
- (void) fetchObjects;

- (void) serverRequest:(ServerRequest *) request didFailWithError:(NSError *) error connection:(ServerConnection *) connection;

@end





@protocol RemoteEntityHomeDelegate <NSObject>

- (void) remoteEntityHomeDidInsertObjects;
- (void) remoteEntityHomeDidDeleteObjects;
- (void) remoteEntityHomeDidUpdateObjects;
- (void) remoteEntityHomeDidFailWithError:(NSError *) error;

@end

