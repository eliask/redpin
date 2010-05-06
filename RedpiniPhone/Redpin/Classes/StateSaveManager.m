//
//  StateSaveManager.m
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

#import "StateSaveManager.h"
#import "RedpinAppDelegate.h"


@implementation StateSaveManager
@synthesize rootViewController;

SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(StateSaveManager);

+ (id) sharedStateSaveManagerWithRootViewController:(RootViewController *) controller {
	StateSaveManager *manager = [self sharedStateSaveManager];
	[manager setRootViewController:controller];	
	return manager;
}

- (void) getRootViewController {
	RedpinAppDelegate *delegate = [UIApplication sharedApplication].delegate;
	self.rootViewController =  (RootViewController *)[[delegate navigationController] topViewController];	
}

#pragma mark -
#pragma mark Util

- (void) optainPermanentID:(NSManagedObject *) obj {
	if([obj.objectID isTemporaryID]) {
		NSError *error;
		
		if(![obj.managedObjectContext obtainPermanentIDsForObjects:[NSArray arrayWithObject:obj] error:&error]) {
			//NSLog(@"%s error %@, %@", __FUNCTION__, error, [error userInfo]);
		}		
	}
}

- (NSString *) translateManageObjectToID:(NSManagedObject *) obj {
	[self optainPermanentID:obj];
	return [[obj.objectID URIRepresentation] absoluteString];
}

- (NSManagedObject *) getManagedObjectByID:(NSString *) idString {
	RedpinAppDelegate *delegate = [UIApplication sharedApplication].delegate;
	NSPersistentStoreCoordinator *coordinator = delegate.persistentStoreCoordinator;
	NSManagedObjectContext *context = delegate.managedObjectContext;
	
	NSManagedObjectID *objId = [coordinator managedObjectIDForURIRepresentation:[NSURL URLWithString:idString]];
	NSError *error = nil;
	NSManagedObject *obj = [context existingObjectWithID:objId error:&error];
	if(!obj) {
		NSLog(@"%s: no existing object found: %@",__FUNCTION__, error);
	}
	return obj;
}

#pragma mark -
#pragma mark MapView
static NSString *mapViewScrollContentOffset_key = @"mapViewScrollViewOffset";
- (BOOL) saveMapView {
	CGPoint point = [[rootViewController.mapViewController scrollView] contentOffset];
	[[NSUserDefaults standardUserDefaults] setObject:NSStringFromCGPoint(point) forKey:mapViewScrollContentOffset_key];
	
	return YES;
}

- (BOOL) restoreMapView {
	NSString *str = [[NSUserDefaults standardUserDefaults] stringForKey:mapViewScrollContentOffset_key];
	if(!str) {
		return NO;		
	}	
	[[rootViewController.mapViewController scrollView] setContentOffset:CGPointFromString(str)];
	
	return YES;
}

#pragma mark -
#pragma mark Map
static NSString *currentMap_key = @"currentMapObjectId";

- (BOOL) saveMap {
	
	Map *map = rootViewController.currentMap;
	
	if(!map) {
		[[NSUserDefaults standardUserDefaults] removeObjectForKey:currentMap_key];
		return NO;
	} else {	
		[[NSUserDefaults standardUserDefaults] setObject:[self translateManageObjectToID:map] forKey:currentMap_key];		
		[self saveMapView];
		return YES;
	}	
}

- (BOOL) restoreMap {	
	
	NSString *url = [[NSUserDefaults standardUserDefaults] stringForKey:currentMap_key];
	
	if(!url) {
		return NO;
	}
	Map *map = (Map *) [self getManagedObjectByID:url];
	
	if(!map) {
		return NO;
	}
	
	[rootViewController setCurrentMap:map];	
	[self restoreMapView];
	
	return YES;	
}



#pragma mark -
#pragma mark Location
static NSString *showingLocation_key = @"currentLocationObjectId";

- (BOOL) saveLocation {
	
	Location *loc = rootViewController.currentLocation;
	
	if(!loc) {		
		[[NSUserDefaults standardUserDefaults] removeObjectForKey:showingLocation_key];	
		return NO;
	} else {			
		[[NSUserDefaults standardUserDefaults] setObject:[self translateManageObjectToID:loc] forKey:showingLocation_key];
		
		[self saveMapView];
		return YES;
	}
	
}

- (BOOL) restoreLocation {
	
	NSString *url = [[NSUserDefaults standardUserDefaults] stringForKey:showingLocation_key];
	
	if(!url) {
		return NO;		
	}
	
	
	Location *loc = (Location *) [self getManagedObjectByID:url];	
	
	if(!loc) {
		return NO;
	}
	
	[rootViewController showLocation:loc animated:NO];

	[self restoreMapView];
	return YES;
	
}

#pragma mark -
#pragma mark Top View Controller

static NSString *locationListMapFilter_key = @"locationListMapFilter";

- (BOOL) saveListTableViewController:(LocationListTableViewController *)controller {
	[[NSUserDefaults standardUserDefaults] setObject:[self translateManageObjectToID:controller.currentMap] forKey:locationListMapFilter_key];
	return YES;
}

- (BOOL) restoreListTableViewController:(LocationListTableViewController *)controller {
	NSString *objID = [[NSUserDefaults standardUserDefaults] stringForKey:locationListMapFilter_key];
	
	if(!objID) {
		return NO;
	} else {
		
		Map *map = (Map *) [self getManagedObjectByID:objID];
		if(!map) {
			return NO;
		} else {
			[controller setFilterWithMap:map];
			
			return YES;
		}					
	}				
	
}



- (BOOL) saveTableViewController:(UITableViewController *)controller {
	/*
	NSIndexPath *path = [[controller.tableView indexPathsForVisibleRows] objectAtIndex:0];
	NSUInteger *indexes = NSZoneMalloc([self zone], sizeof(NSUInteger) * [path length]);
   
	if(!indexes) {
		NSLog(@"%s: could not allocate index array",__FUNCTION__);
		
	} else {
	
		[path getIndexes:indexes];
		NSData *indexData = [[NSData alloc] initWithBytes:indexes length:sizeof(NSUInteger)*[path length]];
	
		NSZoneFree([self zone], indexes);
		[[NSUserDefaults standardUserDefaults] setObject:indexData forKey:tableViewIndexPath_key];
	}
	*/

	
	return YES;
}

- (BOOL) restoreTableViewController:(UITableViewController *)controller {
	/*
	NSData *indexData = [[[NSUserDefaults standardUserDefaults] dataForKey:tableViewIndexPath_key] retain];

	if(!indexData) {
		NSLog(@"%s: no stored index path", __FUNCTION__);
	} else {
	
		NSUInteger length = [indexData length] / sizeof(NSUInteger);	
		NSUInteger *indexes = NSZoneMalloc([self zone], length);
	
		[indexData getBytes:indexes];
		NSIndexPath *indexPath = [[NSIndexPath alloc] initWithIndexes:indexes length:length];
		NSLog(@"index path: %@", indexPath);
		[controller.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionTop animated:NO];
	}
	*/
	return YES;
}


- (BOOL) saveTopViewController {
	UIViewController *controller = [rootViewController.navigationController topViewController];
	
	if([controller isKindOfClass:[LocationListTableViewController class]]) {
		[self saveListTableViewController:(LocationListTableViewController *)controller];
	} 
	
	if([controller isKindOfClass:[UITableViewController class]]) {
		[self saveTableViewController:(UITableViewController *)controller];
	}
	
	return YES;
}

- (BOOL) restoreTopViewController {
	UIViewController *controller = [rootViewController.navigationController topViewController];
	
	if([controller isKindOfClass:[LocationListTableViewController class]]) {
		[self restoreListTableViewController:(LocationListTableViewController *)controller];
	} 
	
	if([controller isKindOfClass:[UITableViewController class]]) {
		[self restoreTableViewController:(UITableViewController *)controller];
	}	
	
	return YES;
}

#pragma mark - 
#pragma mark Navigation Controller

- (NSMutableArray *) navigationControllerArrayToStringArray:(NSArray *) controller {
	
	NSMutableArray *stringArr = [[NSMutableArray alloc] initWithCapacity:[controller count]];
	for(UIViewController *c in controller) {
		[stringArr addObject:NSStringFromClass([c class])];		
	}
	
	return stringArr;
}

static NSString *viewStack_key = @"viewControllerStack";
- (BOOL) saveNavigationState {
	
	NSArray *views = [rootViewController.navigationController viewControllers];
		
	if([views count] > 1) {
		NSMutableArray *arr = [self navigationControllerArrayToStringArray:views];
		[[NSUserDefaults standardUserDefaults] setObject:arr forKey:viewStack_key];
		[arr release];
		
		return YES;
	} else {
		[[NSUserDefaults standardUserDefaults] removeObjectForKey:viewStack_key];
		return NO;
	}
	
}



- (BOOL) restoreNavigationState {
	NSArray *views = [[NSUserDefaults standardUserDefaults] arrayForKey:viewStack_key];
	UINavigationController *controller = rootViewController.navigationController;
	if(!views) {
		NSLog(@"%s: no stored view controller", __FUNCTION__);
		return NO;
	}
	
	for(NSString *className in views) {
		if(NSClassFromString(className) == [ListTableViewController class]) {
			[rootViewController initListController];
			[controller pushViewController:rootViewController.listController animated:NO];
	
		} else if(NSClassFromString(className) == [SearchTableViewController class]) {
			[rootViewController initSearchController];
			[controller pushViewController:rootViewController.searchController animated:NO];
		} else if(NSClassFromString(className) == [MapListTableViewController class]) {
			[rootViewController.listController initMapListController];
			[controller pushViewController:rootViewController.listController.mapListController animated:NO];
		} else if(NSClassFromString(className) == [LocationListTableViewController class]) {
			
			if([[controller topViewController] isKindOfClass:[MapListTableViewController class]]) {
				[rootViewController.listController.mapListController initLocationListController];
				[controller pushViewController:rootViewController.listController.mapListController.locationListController animated:NO];
			} else {
				[rootViewController.listController initLocationListController];
				[controller pushViewController:rootViewController.listController.locationListController animated:NO];
			}	
			
		} else if(NSClassFromString(className) == [AddMapViewController class]) {
		
			AddMapViewController *view = [[AddMapViewController alloc] initWithNibName:@"AddMapViewController" bundle: nil];	
			[rootViewController.navigationController pushViewController:view animated:NO];
			
		}
	}			
		
	return YES;	
}


#pragma mark -
#pragma mark General Store/Restore Functions
- (void) restoreState {
	if(!rootViewController) {
		[self getRootViewController];
	}
	
	
	if(![self restoreLocation]) {
		[self restoreMap];
	}
	
	[self restoreNavigationState];
	[self restoreTopViewController];   
	   
}
	   
- (void) saveState {
	if(!rootViewController) {
		[self getRootViewController];
	}
	
	
	if(![self saveLocation]) {
		[self saveMap];
	}
	
	[self saveNavigationState];
	[self saveTopViewController];

		
}
	   

@end
