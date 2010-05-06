//
//  AddMapViewController.m
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


#import "AddMapViewController.h"
#import "MapHome.h"
#import "Map.h"
#import "EntityHome.h"
#import "RootViewController.h"
#import "ImageUploader.h"
#import "ActivityIndicator.h"

@interface AddMapViewController () 
- (void) enableSave;
- (BOOL) textFieldShouldReturn:(UITextField *) textField;
- (BOOL) loadImageFromURL:(NSString *) url;
- (void) uploadImage:(UIImage *) image;
- (void) showImagePicker:(BOOL) hasCamera;
- (void) showActionSheet;
@end


@implementation AddMapViewController

@synthesize imagePicker, imageView, pickImageButton, nameField, urlField, switcher, map, delegate;



- (void)viewDidLoad {
    [super viewDidLoad];
	self.title = @"Add Map";
		
	UIBarButtonItem *cancelButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleBordered target:self action:@selector(cancel:)];
    self.navigationItem.leftBarButtonItem = cancelButtonItem;
    [cancelButtonItem release];
    
    UIBarButtonItem *saveButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStyleDone target:self action:@selector(save:)];
	[saveButtonItem setEnabled:NO];
    self.navigationItem.rightBarButtonItem = saveButtonItem;
    [saveButtonItem release];

	 
}


- (void) enableSave {
	if( ([nameField.text length] > 0) && ([urlField.text length] > 0)) {
		self.navigationItem.rightBarButtonItem.enabled = YES;
	} else {
		self.navigationItem.rightBarButtonItem.enabled = NO;
	}
	
}

- (BOOL) textFieldShouldReturn:(UITextField *) textField {
	
	
	if (textField == nameField) {
		[nameField resignFirstResponder];
		[self enableSave];
		return YES;
	}
	
	if(textField == urlField) {

		if([self loadImageFromURL:urlField.text]) {
			[urlField resignFirstResponder];
			[self enableSave];
			return YES;
		} else {
			return NO;
		}

	}

	return YES;
}
- (BOOL) loadImageFromURL:(NSString *) url {
	NSLog(@"Load URL %@", url);
	[[ActivityIndicator sharedActivityIndicator] showWithText:@"Loading map image..."];
	UIImage *image = [ImageUploader downloadImage:url];
	[[ActivityIndicator sharedActivityIndicator] hide];
	if(image) {
		[imageView setImage:image];
		
		return YES;
	} else {
		//Error
		
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Image loading failed" message:@"Please correct your URL" delegate:self cancelButtonTitle:@"Correct URL" otherButtonTitles:nil];
		[alert show];
		[alert release];
		
		return NO;	
	}
}


- (void) save:(id)sender {
	NSLog(@"save");
	Map *newMap = [[MapHome newObjectInContext] retain];
	

	UIImage *image = imageView.image;
	
	newMap.mapName = nameField.text;
	newMap.mapURL = urlField.text;
	[newMap setImageAndCreateThumbnail:image];

	
	[[EntityHome sharedEntityHome] saveContext];
	
	RootViewController *c = (RootViewController *) [[self.navigationController viewControllers] objectAtIndex:0];	
	[c setCurrentMap:newMap];
	[self.navigationController popToRootViewControllerAnimated:YES];
	
	[newMap release];


	
}
- (void) cancel:(id)sender {
	NSLog(@"cancel");

	[self.navigationController popToRootViewControllerAnimated:YES];

}



- (IBAction) changeSwitch:(id)sender {
	UISegmentedControl *control = (UISegmentedControl *) sender;

	switch ([control selectedSegmentIndex]) {
		case 0:	{
			NSLog(@"switch to url");
			[urlField setHidden:NO];
			[urlField setEnabled:YES];
			[urlField setText:@""];
			if(imageView.image) {
				[imageView setImage:nil];
			}
		}
			
			break;
		case 1: {
			NSLog(@"switch to image picker");
			[urlField setHidden:YES];
			[urlField resignFirstResponder];
			[self showActionSheet];
		}
		default:
			break;
	}
	
	[self enableSave];
	
}

#pragma mark -
#pragma mark UIImagePickerControllerDelegate

- (void) imagePickerController:(UIImagePickerController *) picker didFinishPickingMediaWithInfo:(NSDictionary *) info {
	
	UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
	[imageView setImage:image];
	
	[[ActivityIndicator sharedActivityIndicator] showWithText:@"Uploading map image..."];
    [self dismissModalViewControllerAnimated:YES];	
	
	[self uploadImage:image];
}

- (void) uploadImage:(UIImage *) image {
	
	ImageUploader *uploader = [[ImageUploader alloc] init];
	[uploader uploadImage:image delegate:self];
	
}

- (void) imagePickerControllerDidCancel:(UIImagePickerController *) picker {
	[self dismissModalViewControllerAnimated:YES];
	
}

#pragma mark -
#pragma mark Image Picker

- (void)showImagePicker:(BOOL)hasCamera {
    if (hasCamera) {
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
	} else {
		imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
	}
	
	[self presentModalViewController:imagePicker animated:YES];
    
}

- (void) showActionSheet {
	BOOL hasCamera = [UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera];
    
	UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];

    if (hasCamera) {
        [as addButtonWithTitle:@"Take Photo"];
    } 
    
    [as addButtonWithTitle:@"Choose Existing Photo"];
    [as addButtonWithTitle:@"Cancel"];
    as.cancelButtonIndex = [as numberOfButtons] - 1;
    
    [as showInView:self.view];
    [as release];
}

#pragma mark -
#pragma mark UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)as clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (as.cancelButtonIndex == buttonIndex) {
        return;
    } else if (as.destructiveButtonIndex == buttonIndex) {
		return;
    }
    
	NSString *title = [as buttonTitleAtIndex:buttonIndex];
	
	if ([title isEqualToString:@"Take Photo"]) {
		[self showImagePicker:true];
	} else {
		[self showImagePicker:false];
	}
}


#pragma mark -
#pragma mark UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Retry"]) {
		ImageUploader *uploader = [[ImageUploader alloc] init];
		[uploader uploadImage:imageView.image delegate:self];
	}
	
}

#pragma mark -
#pragma mark ImageUploaderDelegate


- (void) imageUploader:(ImageUploader *) imageUploader didUploadImageWithURL:(NSString *) url {
	[url retain];
	[urlField setText:url];
	urlField.enabled = NO;
	[url release];
	[imageUploader release];
	
	[[ActivityIndicator sharedActivityIndicator] hide];
	
	[self enableSave];
	
}



- (void) imageUploader:(ImageUploader *) imageUploader didFailWithError:(NSError *) error {
	[[ActivityIndicator sharedActivityIndicator] hide];

	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Upload failed" message:[NSString stringWithFormat:@"%@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSErrorFailingURLStringKey]] delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Retry",nil];
	[alert show];
	[alert release];
	[imageUploader release];
	
}



#pragma mark -
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
	self.imagePicker = nil;
	self.imageView = nil;
	self.pickImageButton = nil;
	self.nameField = nil;
	self.urlField = nil;
	self.switcher = nil;	
}


- (void)dealloc {
    [super dealloc];
}


@end
