//
//  BacksideViewController.h
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

@class RootViewController;

@interface BacksideViewController : UIViewController {
	RootViewController *rootViewController;
	IBOutlet UINavigationBar *navigationBar;
	
	IBOutlet UIButton *removeLocalDataButton;
	IBOutlet UIButton *syncWithServerButton;
	IBOutlet UIButton *fetchMapImagesButton;
}

@property (nonatomic, retain) RootViewController *rootViewController;
@property (nonatomic, retain) IBOutlet UINavigationBar *navigationBar;

@property (nonatomic, retain) IBOutlet UIButton *removeLocalDataButton;
@property (nonatomic, retain) IBOutlet UIButton *syncWithServerButton;
@property (nonatomic, retain) IBOutlet UIButton *fetchMapImagesButton;


- (IBAction) done:(id)sender;

- (IBAction) resetLocalData:(id)sender;
- (IBAction) syncWithServer:(id)sender;
- (IBAction) fetchMapImages:(id)sender;

- (void) internetConnectionChanged:(NSNotification *) note;

@end
