//
//  EntityHome.m
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

#import "EntityHome.h"
#import "RemoteEntityHome.h"
#import "ActivityIndicator.h"
#import "InternetConnectionManager.h"
#import "RedpinAppDelegate.h"


@implementation EntityHome
@synthesize managedObjectContext, remoteHome;

#pragma mark -
#pragma mark Singleton
SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(EntityHome);

- (id) init {
	if((self = [super init])) {
		self.managedObjectContext =  [(RedpinAppDelegate *) [UIApplication sharedApplication].delegate managedObjectContext];
		self.remoteHome =  [[RemoteEntityHome alloc] initWithDelegate:self];		
	}
		
	return self;	
}

- (void) dealloc {
	
	self.managedObjectContext = nil;
	self.remoteHome = nil;
	
	[super dealloc];	
}


#pragma mark -

- (id) newObjectWithEntityName: (NSString *) entityName {
	NSLog(@"Creating new object: %@", entityName);
	return [NSEntityDescription insertNewObjectForEntityForName:(NSString *)entityName inManagedObjectContext:managedObjectContext];
	
}

- (NSEntityDescription *) entityDescriptionForEntityName: (NSString *) entityName {
		return [NSEntityDescription entityForName:entityName inManagedObjectContext:managedObjectContext];
}

- (NSFetchedResultsController *)fetchedResultsControllerForEntityName: (NSString *) entityName sortDescriptors: (NSArray *) sortDescriptors predicate: (NSPredicate *) predicate{
    // Set up the fetched results controller if needed.
	
	NSFetchRequest *fetchRequest = [[self fetchRequestForEntityName:entityName sortDescriptors:sortDescriptors predicate:predicate] retain];		
    NSFetchedResultsController *aFetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:managedObjectContext sectionNameKeyPath:nil cacheName:entityName];
	[fetchRequest release];    
    	
	return [aFetchedResultsController autorelease];
}    

- (NSArray *) sortDescriptorWithKey: (NSString *) key ascending: (BOOL) asc {
	
	NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:key ascending:asc];
	NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:sortDescriptor, nil];
	[sortDescriptor release];
	
	return [sortDescriptors autorelease];
}



- (NSFetchRequest *) fetchRequestForEntityName: (NSString *) entityName sortDescriptors: (NSArray *) sortDescriptors predicate: (NSPredicate *) predicate {
	// Create the fetch request for the entity.
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    // Edit the entity name as appropriate.		
    [fetchRequest setEntity:[self entityDescriptionForEntityName:entityName]];
	
    // Edit the sort key as appropriate.
	if(sortDescriptors != nil) {
		[fetchRequest setSortDescriptors:sortDescriptors];
	}
	
	if(predicate != nil) {
		[fetchRequest setPredicate:predicate];
	}
	
	return [fetchRequest autorelease];
	
}

- (void) saveContext {
	
	if([[InternetConnectionManager sharedInternetConnectionManager] offlineMode]) {
		NSLog(@"%s: server not reachable", __FUNCTION__);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"Server not reachable" message:@"The server is currently not reachable" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
		return;
	}
	
	if(asyncRemoteRequestInProgress) {
		NSLog(@"%s: ansync remote request already in progress, canceling saveContext", __FUNCTION__);
		return;
	}
	
	if([managedObjectContext hasChanges]) {

		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];

		asyncRemoteRequestInProgress = YES;
		//UIActivityIndicatorView *view = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
		//[view startAnimating];
		
		if([[managedObjectContext insertedObjects] count] == 0) {
			objectsInserted = YES;
		}
		
		if([[managedObjectContext updatedObjects] count] == 0) {
			objectsUpdated = YES;
		}
		
		if([[managedObjectContext deletedObjects] count] == 0) {
			objectsDeleted = YES;
		}
				
		[self.remoteHome insertObjects:[managedObjectContext insertedObjects]];
		[self.remoteHome updateObjects:[managedObjectContext updatedObjects]];
		[self.remoteHome deleteObjects:[managedObjectContext deletedObjects]];
		
				
	}
}

- (void) resetSaveContext {

	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
	asyncRemoteRequestInProgress = NO;
	objectsInserted = NO;
	objectsUpdated = NO;
	objectsDeleted = NO;	
}

- (void) finishSaveContext {
	if(asyncRemoteRequestInProgress && objectsInserted && objectsUpdated && objectsDeleted) {
		
		
		NSError *error;
		if (![managedObjectContext save:&error]) {
			// Handle error
			NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
			UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
			[view show];
			[view release];
			//exit(-1);  // Fail
		}			
		
		[self resetSaveContext];
	}
}


#pragma mark -
#pragma mark RemoteEntityHomeDelegate

- (void) remoteEntityHomeDidInsertObjects {
	objectsInserted = YES;
	[self finishSaveContext];
}
- (void) remoteEntityHomeDidDeleteObjects {
	objectsDeleted = YES;
	[self finishSaveContext];
}
- (void) remoteEntityHomeDidUpdateObjects {
	objectsUpdated = YES;
	[self finishSaveContext];
}

- (void) remoteEntityHomeDidFailWithError:(NSError *) error {
	UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"ServerConnection" message:[error localizedDescription] delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:nil];
	[view show];
	[view release];
	
	[self resetSaveContext];
}

@end


