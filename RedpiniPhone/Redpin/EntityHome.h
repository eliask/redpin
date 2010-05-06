//
//  EntityHome.h
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
#import "ServerConnection.h"
#import "SynthesizeSingleton.h"
#import "RemoteEntityHome.h"


#define SYNTHESIZE_REMOTE_ENTITY_HOME(className) \
static RemoteEntityHome *rHome = nil;\
\
+ (RemoteEntityHome *) remoteHome {\
	@synchronized(self) {\
		if (rHome == nil) {\
			rHome = [[RemoteEntityHome alloc] initWithHome:[className shared##className]];\
		}\
	}\
	return rHome;\
}


@interface EntityHome : NSObject <NSFetchedResultsControllerDelegate, RemoteEntityHomeDelegate> {
	NSManagedObjectContext *managedObjectContext;
	RemoteEntityHome *remoteHome;
	BOOL asyncRemoteRequestInProgress;
	BOOL objectsInserted;
	BOOL objectsDeleted;
	BOOL objectsUpdated;
}


@property (nonatomic, retain) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, retain) RemoteEntityHome *remoteHome;

SYNTHESIZE_SINGLETON_INTERFACE_FOR_CLASS(EntityHome);

- (id) newObjectWithEntityName: (NSString *) entityName;
- (void) saveContext;
- (NSEntityDescription *) entityDescriptionForEntityName: (NSString *) entityName;
- (NSFetchedResultsController *) fetchedResultsControllerForEntityName: (NSString *) entityName sortDescriptors: (NSArray *) sortDescriptors  predicate: (NSPredicate *) predicate;
- (NSArray *) sortDescriptorWithKey: (NSString *) key ascending: (BOOL) asc;
- (NSFetchRequest *) fetchRequestForEntityName: (NSString *) entityName sortDescriptors: (NSArray *) sortDescriptors predicate: (NSPredicate *) predicate;

@end


@protocol EntityHomeProtocol <NSObject>

+ (NSManagedObject *) newObjectInContext;
+ (NSManagedObject *) newObject;
+ (void) insertObjectInContext:(NSManagedObject *) anObject;
+ (NSFetchedResultsController *) defaultFetchedResultsController;
+ (NSArray *) defaultSortDescriptors;
+ (NSFetchRequest *) defaultFetchRequest;

@end

