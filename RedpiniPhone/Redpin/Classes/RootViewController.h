//
//  RootViewController.h
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

#import "MapListTableViewController.h"
#import "Map.h"
#import "MapView.h"
#import "Location.h"
#import "MapViewController.h"
#import "ListTableViewController.h"
#import "SearchTableViewController.h"
#import "AddMapViewController.h"
#import "BacksideViewController.h"
#import "Sniffer.h"
#import "IntervalScanner.h"



@interface RootViewController : UIViewController <NSFetchedResultsControllerDelegate, UITabBarDelegate, SnifferDelegate, UIActionSheetDelegate> {

	IBOutlet UIBarButtonItem *addPositionButton;
	IBOutlet UIBarButtonItem *refreshPositionButton;
	IBOutlet UIActivityIndicatorView *activityIndicator;
	IBOutlet UIBarButtonItem *showListButton;
	IBOutlet UIBarButtonItem *searchButton;
	IBOutlet UIBarButtonItem *addMapButton;	
	IBOutlet UIBarButtonItem *redpinLogoButton;
	IBOutlet UIView *confirmationView;
	IBOutlet UIToolbar *toolbar;
		
	MapListTableViewController *mapListController;
	ListTableViewController *listController;
	SearchTableViewController *searchController;
	BacksideViewController *backsideController;
	IBOutlet MapViewController *mapViewController;
	
	Map *currentMap;
	Location *currentLocation;
	
	Location *showingLocation;
	
	BOOL restoredState;	
	BOOL locateInProgress;
	BOOL snifferMovementWasShown;
	BOOL backsideVisible;
	
	BOOL userWantsToSelectCurrentLocation;
	
	NSTimer *hideTimer;

}

- (IBAction) addPosition:(id)sender;
- (IBAction) refreshPosition:(id)sender;
- (IBAction) showList:(id)sender;
- (IBAction) addMap:(id)sender;
- (IBAction) search:(id)sender;
- (IBAction) flipBackside:(id)sender;

- (IBAction) userClickYes:(id)sender;
- (IBAction) userClickNo:(id)sender;
- (IBAction) userClickDontKnow:(id)sender;

- (void) showLocation:(Location *) loc animated:(BOOL) animated;

- (void) initListController;
- (void) initSearchController;
- (void) initBacksideController;

@property (nonatomic, retain) IBOutlet UIBarButtonItem *addPositionButton;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *refreshPositionButton;
@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *showListButton;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *searchButton;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *addMapButton;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *redpinLogoButton;
@property (nonatomic, retain) IBOutlet UIView *confirmationView;
@property (nonatomic, retain) IBOutlet UIToolbar *toolbar;


@property (nonatomic, retain) Map *currentMap;
@property (nonatomic, retain) Location *currentLocation;

@property (nonatomic, retain, setter=showLocation) Location *showingLocation;

@property (nonatomic, retain) MapListTableViewController *mapListController;
@property (nonatomic, retain) ListTableViewController *listController;
@property (nonatomic, retain) SearchTableViewController *searchController;
@property (nonatomic, retain) BacksideViewController *backsideController;
@property (nonatomic, retain) MapViewController *mapViewController;

@property (nonatomic, retain) NSTimer *hideTimer;

@end
