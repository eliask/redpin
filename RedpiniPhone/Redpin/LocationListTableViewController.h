//
//  LocationListTableViewController.h
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


#import <UIKit/UIKit.h>

@class Location;
@class LocationTableViewCell;
@class Map;

@interface LocationListTableViewController : UITableViewController <NSFetchedResultsControllerDelegate> {

	NSFetchedResultsController *fetchedResultsController;
	
	Map *currentMap;	
	
	IBOutlet UISearchBar *searchBar;
	BOOL searching;
	NSMutableArray *searchResult;
}

@property (nonatomic, retain) NSFetchedResultsController *fetchedResultsController;

@property (nonatomic, retain) Map *currentMap;

@property (nonatomic, retain) IBOutlet UISearchBar *searchBar;
@property (nonatomic, retain) NSMutableArray *searchResult;


- (id) initWithMapFilter:(Map *) map;
- (void) configureCell:(LocationTableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath;

- (void) searchTableView;

- (void) showLocation:(Location *) location;
- (void) setFilterWithMap:(Map *) newMap;
- (void) removeMapFilter;
- (BOOL) isFilteredByMap;


@end
