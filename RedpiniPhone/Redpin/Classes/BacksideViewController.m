//
//  BacksideViewController.m
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


#import "BacksideViewController.h"
#import "RedpinAppDelegate.h"
#import "SynchronizationManager.h"
#import "EntityHome.h"
#import "MapHome.h"
#import "ActivityIndicator.h"
#import "ImageUploader.h"
#import "InternetConnectionManager.h"


@implementation BacksideViewController
@synthesize rootViewController, navigationBar, removeLocalDataButton, syncWithServerButton, fetchMapImagesButton;


- (void)viewDidLoad {
	
	[self internetConnectionChanged:nil];	
	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(internetConnectionChanged:) name:InternetConnectionManagerUpdateNotification object:nil];
	
    [super viewDidLoad];
}


- (void)viewDidUnload {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:InternetConnectionManagerUpdateNotification object:nil];
}


- (IBAction) done:(id)sender {
	[rootViewController flipBackside:self];
}

- (IBAction) resetLocalData:(id)sender {
	NSManagedObjectContext *context =[[EntityHome sharedEntityHome] managedObjectContext];
	NSFetchRequest *fetch_request = [[MapHome defaultFetchRequest] retain];			
	NSError *error = nil;
	NSArray *localData = [context executeFetchRequest:fetch_request error:&error];
	if(!localData) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
	}
	[fetch_request release];
	

	
	for(Map *map in localData) {
		[context deleteObject:map];
	}

	if (![context save:&error]) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
	}
	
	
}

- (IBAction) syncWithServer:(id)sender {
	[[SynchronizationManager sharedSynchronizationManager] synchronize];
}

- (IBAction) fetchMapImages:(id)sender {
	NSManagedObjectContext *context =[[EntityHome sharedEntityHome] managedObjectContext];
	NSFetchRequest *fetch_request = [[MapHome defaultFetchRequest] retain];			
	NSError *error = nil;
	NSArray *localData = [context executeFetchRequest:fetch_request error:&error];
	if(!localData) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
	}
	[fetch_request release];
	
	[[ActivityIndicator sharedActivityIndicator] showWithText:@"Loading map images..."];
	
	for(Map *map in localData) {
		[map setImageAndCreateThumbnail:[ImageUploader downloadImage:map.mapURL]];
	}
	
	[[ActivityIndicator sharedActivityIndicator] hide];
	

	if (![context save:&error]) {
		// Handle error
		NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
		UIAlertView *view = [[UIAlertView alloc] initWithTitle:[error domain] message:[error localizedDescription] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[view show];
		[view release];
	}
	
	
}

#pragma mark -
#pragma mark Internet Connection Mode

- (void) internetConnectionChanged:(NSNotification *) note {	
	[syncWithServerButton setEnabled:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode]];
	[fetchMapImagesButton setEnabled:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode]];
	
	[syncWithServerButton setAlpha:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode] ? 1.0f : 0.2f];
	[fetchMapImagesButton setAlpha:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode] ? 1.0f : 0.2f];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



- (void)dealloc {
	[self setRootViewController:nil];
    [super dealloc];
}


@end
