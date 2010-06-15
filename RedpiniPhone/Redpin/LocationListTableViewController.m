//
//  LocationListTableViewController.m
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


#import "LocationListTableViewController.h"
#import "LocationTableViewCell.h"
#import "Map.h"
#import "RootViewController.h"
#import "EntityHome.h"
#import "LocationHome.h"
#import "InternetConnectionManager.h"


@implementation LocationListTableViewController

@synthesize fetchedResultsController,
			currentMap, searchBar, searchResult;

- (id) initWithMapFilter:(Map *) map {
	[self init];
	[self setFilterWithMap: map];	
		
	return self;
}


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization

    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	
	NSError *error = nil;
	if (![[self fetchedResultsController] performFetch:&error]) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];	
	}
	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(internetConnectionChanged:) name:InternetConnectionManagerUpdateNotification object:nil];
	
	self.navigationItem.rightBarButtonItem = self.editButtonItem;
	[self.editButtonItem setEnabled:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode]];
	if(!searchResult) {
		searchResult = [[NSMutableArray alloc] initWithCapacity:[[fetchedResultsController fetchedObjects] count]];
	}
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
	
	if(!searching) {
		[searchResult removeAllObjects];
	}
}

- (void)viewDidUnload {
	self.searchBar = nil;
	[[NSNotificationCenter defaultCenter] removeObserver:self name:InternetConnectionManagerUpdateNotification object:nil];

}

#pragma mark -
#pragma mark Filtering

- (void) setFilterWithMap:(Map *) newMap {
	if (newMap != currentMap) {
        [currentMap release];
        currentMap = [newMap retain];
		
		[self setTitle:[currentMap mapName]];
		[self.tableView reloadData];
	}
}

- (void) removeMapFilter {
	[currentMap release];
	currentMap = nil;
	
	[self setTitle:@"Locations"];
	[self.tableView reloadData];
}

- (BOOL) isFilteredByMap {
	return currentMap != nil;
}

- (void) showLocation:(Location *) location {
	RootViewController *c = (RootViewController *) [[self.navigationController viewControllers] objectAtIndex:0];	
	[c showLocation:location animated:YES];
	[self.navigationController popToRootViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    NSInteger count = [[fetchedResultsController sections] count];
    
	if(searching || currentMap || (count == 0)) {
		count = 1;
	}
	
    return count;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSInteger numberOfRows = 0;
	
	if (searching) {
		numberOfRows = [searchResult count];
		
	} else if (currentMap) {
		numberOfRows = [currentMap.locations count];
	} else {
		
		if ([[fetchedResultsController sections] count] > 0) {
			id <NSFetchedResultsSectionInfo> sectionInfo = [[fetchedResultsController sections] objectAtIndex:section];
			numberOfRows = [sectionInfo numberOfObjects];
		}
	}
	
    return numberOfRows;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *LocationCellIdentifier = @"LocationCellIdentifier";
    
    LocationTableViewCell *locationCell = (LocationTableViewCell *)[tableView dequeueReusableCellWithIdentifier:LocationCellIdentifier];
    if (locationCell == nil) {
        locationCell = [[[LocationTableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:LocationCellIdentifier] autorelease];
		locationCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
	[self configureCell:locationCell atIndexPath:indexPath];
    
    return locationCell;
}


- (void)configureCell:(LocationTableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath {
	if(searching) {
		cell.location = [searchResult objectAtIndex:indexPath.row];
	} else if (currentMap) { 
		cell.location = [[currentMap.locations allObjects] objectAtIndex:indexPath.row];
	} else {
		cell.location = (Location *)[fetchedResultsController objectAtIndexPath:indexPath];
	}	
}



- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	Location *loc;
	if(searching) {
		loc = (Location *) [searchResult objectAtIndex:indexPath.row];
	} else if (currentMap) { 
		loc = (Location *) [[currentMap.locations allObjects] objectAtIndex:indexPath.row];
	} else {
		loc = (Location *)[fetchedResultsController objectAtIndexPath:indexPath];
	}
    
	if(!loc.map.image && [[InternetConnectionManager sharedInternetConnectionManager] offlineMode]) {
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"Offline Mode" message:@"You're in offline mode. The map image hasn't been cached yet." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
		
		return;
	}
	
	NSLog(@"Show location %@", loc);
	
	[self showLocation: loc];

	[loc release];	

}


- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
		NSManagedObjectContext *context = [fetchedResultsController managedObjectContext];
		Location *location;
		if(searching) {
			location = [searchResult objectAtIndex:indexPath.row];
		} else if(currentMap) {
			location = [[currentMap.locations allObjects] objectAtIndex:indexPath.row];
		} else {
			location = [fetchedResultsController objectAtIndexPath:indexPath];
		}
		
		[context deleteObject:location];
		
		
		//remove marker
		RootViewController *c = (RootViewController *) [[self.navigationController viewControllers] objectAtIndex:0];
		[c.mapViewController removeMarkerWithLocation:location];
		
		
		[[EntityHome sharedEntityHome] saveContext];
		
		
	}   
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

#pragma mark -
#pragma mark UISearchBarDelegate
- (void)searchBarTextDidBeginEditing:(UISearchBar *)sBar {
	sBar.autocorrectionType = UITextAutocorrectionTypeNo;
	[sBar setShowsCancelButton:YES animated:YES];
	
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)sBar {
	[sBar setShowsCancelButton:NO animated:YES];
}

- (void)searchBar:(UISearchBar *)theSearchBar textDidChange:(NSString *)searchText {
	
	if([searchText length] > 0) {		
		searching = YES;
		self.tableView.scrollEnabled = YES;
		[self searchTableView];
	} else {		
		searching = NO;
		self.tableView.scrollEnabled = NO;
	}
	
	[self.tableView reloadData];
}

- (void) searchBarSearchButtonClicked:(UISearchBar *) theSearchBar {	
	[self searchTableView];
	[self.tableView reloadData];
	
	[theSearchBar resignFirstResponder];
}

- (void) searchBarCancelButtonClicked:(UISearchBar *) sBar {
	sBar.text = @"";
	[sBar setShowsCancelButton:NO animated:YES];
	[sBar resignFirstResponder];
	
	searching = NO;
	
	self.tableView.scrollEnabled = YES;	
	[self.tableView reloadData];
}

- (void) searchTableView {
	
	//Remove all objects first.
	[searchResult removeAllObjects];
	
	NSString *searchText = searchBar.text;
	
	NSArray *data;
	
	if(currentMap) {
		data = [currentMap.locations allObjects];
	} else {
		data = [fetchedResultsController fetchedObjects];
	}
	
	for (Location *loc in data) {
		NSRange titleResultsRange = [loc.symbolicID rangeOfString:searchText options:NSCaseInsensitiveSearch];
		
		if (titleResultsRange.length > 0) {
			[searchResult addObject:loc];
		}
	}
	
}


#pragma mark -
#pragma mark Fetched results controller
- (NSFetchedResultsController *) fetchedResultsController {
	
	if (fetchedResultsController == nil) {
		[self setFetchedResultsController: [LocationHome defaultFetchedResultsController]];
		fetchedResultsController.delegate = self;        
    }
	
	return fetchedResultsController;

}


- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
	if(searching || currentMap) {
		return;
	}
	
	if (self.tableView.editing) {	
		[self.tableView beginUpdates];
	}

}


- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
	
	//prevent automatic updates on searching and filter, because indexPath does not match, instead reload data
	if(searching || currentMap) {
		[self.tableView reloadData];
		return;
	}
	
	if(!self.tableView.editing) {
		return;
	}
	
	UITableView *tableView = self.tableView;
	
	switch(type) {
		case NSFetchedResultsChangeInsert:
			[tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
			break;
			
		case NSFetchedResultsChangeDelete:
			[tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
			break;
			
		case NSFetchedResultsChangeUpdate:
			

			[self configureCell:(LocationTableViewCell *)[tableView cellForRowAtIndexPath:indexPath] atIndexPath:indexPath];
			[tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationNone];

			
			break;
			
		case NSFetchedResultsChangeMove:
			[tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
			
			[tableView reloadSections:[NSIndexSet indexSetWithIndex:newIndexPath.section] withRowAnimation:UITableViewRowAnimationFade];
			break;
	}
}


- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id <NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
	
	if(searching || currentMap) {
		[self.tableView reloadData];
		return;
	}
	
	switch(type) {
		case NSFetchedResultsChangeInsert:
			[self.tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
			break;
			
		case NSFetchedResultsChangeDelete:
			[self.tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
			break;
	}
}


- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
	if(searching || currentMap) {
		return;
	}
	
	
	if (self.tableView.editing) {	
		[self.tableView endUpdates];
	} else {
		[self.tableView reloadData];
	}

}

#pragma mark -
#pragma mark Internet Connection Mode

- (void) internetConnectionChanged:(NSNotification *) note {	
	[self.editButtonItem setEnabled:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode]];
}


#pragma mark -
#pragma mark Memory management

- (void)dealloc {
	[fetchedResultsController release];
	
	[currentMap release];
    [super dealloc];
}

@end
