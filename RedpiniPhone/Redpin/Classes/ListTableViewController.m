//
//  ListViewController.m
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


#import "ListTableViewController.h"


@implementation ListTableViewController

@synthesize list, mapListController, locationListController;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
		list = [[NSMutableArray alloc] init];
		
		[list addObject:@"Maps"];
		[list addObject:@"Locations"];
    }
    return self;
}




- (void)viewDidLoad {
	[self setTitle:@"List"];	
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
	
    [super didReceiveMemoryWarning];
	[self setMapListController:nil];
	[self setLocationListController:nil];
}


	
#pragma mark -
#pragma mark View Controllers
- (void) initMapListController {
	if(self.mapListController == nil) {
		MapListTableViewController *view = [[MapListTableViewController alloc] initWithNibName:@"MapListTableViewController" bundle:nil];
		[self setMapListController:view];
		[view release];
	}
}

- (void) initLocationListController {
	if(self.locationListController == nil) {
		LocationListTableViewController *view = [[LocationListTableViewController alloc] initWithNibName:@"LocationListTableViewController" bundle:nil];
		[self setLocationListController:view];
		[view release];
	}
}


#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [list count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
	[cell setAccessoryType:UITableViewCellAccessoryDisclosureIndicator];
	[cell.textLabel setText:[list objectAtIndex:indexPath.row]];
	
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	switch (indexPath.row) {
		case 0: {
			
			[self initMapListController];
			[self.navigationController pushViewController:self.mapListController animated:YES];
			
			}
			break;
		case 1: {
			[self initLocationListController];
			[self.navigationController pushViewController:self.locationListController animated:YES];
			
			}
		default:
			break;
	}
}



- (void)dealloc {
    [super dealloc];
}


@end
