//
//  AddMapViewController.h
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
#import "ImageUploader.h"

@class Map;
@protocol AddMapDelegate;



@interface AddMapViewController : UIViewController <UIImagePickerControllerDelegate, UIAlertViewDelegate, ImageUploaderDelegate, UIActionSheetDelegate> {
	@private
		IBOutlet UIImagePickerController *imagePicker;
		IBOutlet UIButton *pickImageButton;
		IBOutlet UIImageView *imageView;
		IBOutlet UITextField *nameField;
		IBOutlet UITextField *urlField;
		IBOutlet UISegmentedControl *switcher;
		Map *map;
	
		id <AddMapDelegate> delegate;

}


- (IBAction) changeSwitch:(id)sender;
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info;
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker;

- (void)save:(id)sender;
- (void)cancel:(id)sender;


@property (nonatomic, retain) IBOutlet UIImagePickerController *imagePicker;
@property (nonatomic, retain) IBOutlet UIButton *pickImageButton;
@property (nonatomic, retain) IBOutlet UIImageView *imageView;
@property (nonatomic, retain) IBOutlet UITextField *nameField;
@property (nonatomic, retain) IBOutlet UITextField *urlField;
@property (nonatomic, retain) IBOutlet UISegmentedControl *switcher;
@property (nonatomic, retain) Map *map;
@property(nonatomic, assign) id <AddMapDelegate> delegate;

@end

@protocol AddMapDelegate <NSObject>
- (void) addMapViewController: (AddMapViewController *)controller didAddMap:(Map *)map;
@end