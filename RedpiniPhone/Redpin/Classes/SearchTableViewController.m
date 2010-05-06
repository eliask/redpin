//
//  SearchViewController.m
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


#import "SearchTableViewController.h"
#import "Map.h"
#import "MapHome.h"
#import "RootViewController.h"

@interface SearchTableViewController () 
- (void) startSearch;
- (void) finishSearch;
@end

@implementation SearchTableViewController
@synthesize fetchedResultsController, searchBar;




- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {				
    }
    return self;
}



- (void)viewDidLoad {
    [super viewDidLoad];	
	
	self.title = @"Search";
			
	NSError *error = nil;
	if (![[self fetchedResultsController] performFetch:&error]) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
	}
	

	if(!searchData) {
		searchData = [[NSMutableDictionary alloc] initWithCapacity:[[fetchedResultsController fetchedObjects] count]];
	}
}

- (void) viewWillAppear:(BOOL)animated {
		
	if(searching) {
		[self.tableView reloadData];
	}
	
	
	[super viewWillAppear:animated];
	
}


- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
	if(!searching) {
		[filteredMaps removeAllObjects];
		[filteredLocations removeAllObjects];
	}
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}

#pragma mark -
#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
   
	NSInteger count;
    if(searching) {
		count = [[searchData allKeys] count];
	} else {
		NSArray *sections = [fetchedResultsController sections];
		if([sections count] > 0) {
			id <NSFetchedResultsSectionInfo> sectionInfo = [sections objectAtIndex:0];
			count = [sectionInfo numberOfObjects];
		} else {
			count = 0;
		}

	}
	
    return count;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSInteger numberOfRows = 0;
	
	Map *map;
	if(searching) {
		numberOfRows = [(NSMutableArray *)[searchData objectForKey:[[searchData allKeys] objectAtIndex:section]] count];
	} else {
		map = [[fetchedResultsController fetchedObjects] objectAtIndex:section];
		numberOfRows = [map.locations count];
	}
	
	

    return numberOfRows;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    // Dequeue or if necessary create a RecipeTableViewCell, then set its recipe to the recipe for the current row.
    static NSString *LocationCellIdentifier = @"LocationCellIdentifier";
    
    LocationTableViewCell *locationCell = (LocationTableViewCell *)[tableView dequeueReusableCellWithIdentifier:LocationCellIdentifier];
	

    if (locationCell == nil) {
        locationCell = [[[LocationTableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:LocationCellIdentifier] autorelease];
		
		
    }
    
	[self configureCell:locationCell atIndexPath:indexPath];
    
    return locationCell;
}


- (void)configureCell:(LocationTableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath {
	
	[cell setLocation:[self locationAtIndexPath:indexPath]];
	
}

- (Location *) locationAtIndexPath:(NSIndexPath *)indexPath {
	Map *map = [self mapAtIndexPath:indexPath];
	Location *loc;
	
	if(searching) {
		loc = [(NSMutableArray *) [searchData objectForKey:map.mapName] objectAtIndex:indexPath.row];
	} else {
		loc = [[map.locations allObjects] objectAtIndex:indexPath.row];
	}
	
	return loc;
}

- (Map *) mapAtIndexPath:(NSIndexPath *)indexPath {
	Map *map;
	
	if(searching) {
		map = [mapNameDict objectForKey:[[searchData allKeys] objectAtIndex:indexPath.section]];
	} else {
		map = [[fetchedResultsController fetchedObjects] objectAtIndex:indexPath.section];
	}
	return map;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	Location *loc = [self locationAtIndexPath:indexPath];
	NSLog(@"Show location %@", loc);
	
	RootViewController *c = (RootViewController *) [[self.navigationController viewControllers] objectAtIndex:0];	
	[c showLocation:loc];
	[self.navigationController popToRootViewControllerAnimated:YES];

}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	
}


// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
	}   
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // The table view should not be re-orderable.
    return NO;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	

	NSString *mapName;
	
	if(searching) {
		mapName = [[searchData allKeys] objectAtIndex:section];
	} else {
		mapName = [(Map *) [[fetchedResultsController fetchedObjects] objectAtIndex:section] mapName]; 
	}
	
	return mapName;
}

#pragma mark -
#pragma mark NSFetchedResultsControllerDelegate

/**
 Delegate methods of NSFetchedResultsController to respond to additions, removals and so on.
 */

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
	UITableView *tableView = self.tableView;
	[tableView reloadData];
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
		if(!searching) {
			[self startSearch];
		}
		
		[self searchTableView];
	} else {		
		if(searching) {
			[self finishSearch];
		}
		
	}
	
	[self.tableView reloadData];
}

- (void) searchBarSearchButtonClicked:(UISearchBar *) sBar {

	[sBar resignFirstResponder];
	[self.tableView reloadData];
}

- (void) searchBarCancelButtonClicked:(UISearchBar *) sBar {
	sBar.text = @"";
	[sBar setShowsCancelButton:NO animated:YES];
	[sBar resignFirstResponder];
	
	[self finishSearch];
	
	self.tableView.scrollEnabled = YES;	
	[self.tableView reloadData];
}

- (void) searchTableView {
	
	//Remove all objects first.
	[searchData removeAllObjects];
	
	NSString *searchText = searchBar.text;
	

	for (Map *sMap in [fetchedResultsController fetchedObjects]) {
		
		NSString *mapName = sMap.mapName;
		
		NSRange mapTitleResultsRange = [ sMap.mapName  rangeOfString:searchText options:NSCaseInsensitiveSearch];
		if(mapTitleResultsRange.length > 0) {
			// found search name in map, so adding all locations
			[searchData setObject:[sMap.locations allObjects] forKey:sMap.mapName];
		} else {
			
			NSMutableArray *locations = [[NSMutableArray alloc] initWithCapacity:[sMap.locations count]];
			
			for(Location *sLoc in sMap.locations) {
				
				NSRange locationTitleResultsRange = [ [[mapName stringByAppendingString:sLoc.symbolicID] stringByReplacingOccurrencesOfString:@" " withString:@""]  rangeOfString:[searchText stringByReplacingOccurrencesOfString:@" " withString:@""] options:NSCaseInsensitiveSearch];
				
				if (locationTitleResultsRange.length > 0) {
					[locations addObject:sLoc];
				}
				
			}
			
			if([locations count] > 0) {
				[searchData setObject:locations forKey:sMap.mapName];
			}
			
			[locations release];
			
		}
		
	}
	
}

- (void) startSearch {	
	
	//needed because NSMapTable not avaible on iphone
	mapNameDict = [[NSMutableDictionary alloc] initWithCapacity:[[fetchedResultsController fetchedObjects] count]];
	for(Map *map in [fetchedResultsController fetchedObjects]) {
		[mapNameDict setObject:map forKey:map.mapName];
	}
	
	searching = YES;
	
}

- (void) finishSearch {

	[mapNameDict release];
	mapNameDict = nil;
	
	searching = NO;
}
#pragma mark -
#pragma mark Fetched results controller

- (NSFetchedResultsController *)fetchedResultsController {
    // Set up the fetched results controller if needed.
    if (fetchedResultsController == nil) {
		[self setFetchedResultsController: [MapHome defaultFetchedResultsController]];
		fetchedResultsController.delegate = self;
    }
	
	return fetchedResultsController;
}    




#pragma mark -
#pragma mark Memory management

- (void)dealloc {
	[fetchedResultsController release];
	[searchData release];	
	[searchBar release];

    [super dealloc];
}


@end
