//
//  MapViewController.m
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


#import "MapViewController.h"
#import "Location.h"
#import "LocationHome.h"
#import "LocationMarkerAnnotation.h"
#import "EntityHome.h"
#import "ActivityIndicator.h"
#import "ImageUploader.h"
#import "InternetConnectionManager.h"


#define TOOLBAR_HEIGTH 44


@interface MapViewController () 
- (void) setupImageView;
- (void) setupAllMapMarkers;
- (void) removeAllMarkers;
- (void) removeMarker:(LocationMarker *) marker;
- (UIImage *) loadMapImage;
- (CGRect) visibleMapViewRect;
- (void) scrollToMarker:(LocationMarker *) marker animated:(BOOL) animated;

- (LocationMarker *) markerForLocation:(Location *) loc;
- (void) addMarker: (LocationMarker *) marker;
- (LocationMarkerAnnotation *) setupMarkerAnnotation:(LocationMarker *)marker;
@end


@implementation MapViewController

@synthesize scrollView, contentView, imageView, map, markers, currentLocationMarker, curLocationCircle;

- (UIView *) viewForZoomingInScrollView: (UIScrollView *) ScrollView {
	return contentView;
}



- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
		[scrollView setDecelerationRate:UIScrollViewDecelerationRateFast];
		zoomScale = 1.0f;

    }
    return self;
}


- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)viewDidUnload {
	self.scrollView = nil;
	self.contentView = nil;
	self.imageView = nil;
}

- (void) viewWillAppear:(BOOL)animated {
    [scrollView becomeFirstResponder];
    [super viewWillAppear:animated];
	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}


- (void)viewWillDisappear:(BOOL)animated {
	[scrollView resignFirstResponder];
    [super viewWillDisappear:animated];
	
	[[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

#pragma mark -
#pragma mark Keyboard Handling

- (void)keyboardWillShow:(NSNotification *)aNotification {
	
	if (keyboardIsShowing)
        return;
	
    NSDictionary* info = [aNotification userInfo];
	
    // Get the size of the keyboard.
    NSValue* aValue = [info objectForKey:UIKeyboardBoundsUserInfoKey];
    CGSize keyboardSize = [aValue CGRectValue].size;
	
    // Resize the scroll view (which is the root view of the window)
    CGRect viewFrame = [scrollView frame];
    viewFrame.size.height -= (keyboardSize.height - TOOLBAR_HEIGTH);
	
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	
    [scrollView setFrame:viewFrame];
	
	[UIView commitAnimations];
	keyboardIsShowing = YES;
	
}

- (void)keyboardWillHide:(NSNotification *)aNotification {
	
	if(!keyboardIsShowing)
		return;
	
	NSDictionary* info = [aNotification userInfo];
	
    // Get the size of the keyboard.
    NSValue* aValue = [info objectForKey:UIKeyboardBoundsUserInfoKey];
    CGSize keyboardSize = [aValue CGRectValue].size;
	
    // Reset the height of the scroll view to its original value
    CGRect viewFrame = [scrollView frame];
    viewFrame.size.height += (keyboardSize.height - TOOLBAR_HEIGTH);
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	[scrollView setFrame:viewFrame];
	[UIView commitAnimations];
	

	keyboardIsShowing = NO;
	
}

- (void) checkCurrentLocationMarker {
	
	if(self.currentLocationMarker) {
		if(self.currentLocationMarker.location.map == map) {
			[self addMarker:currentLocationMarker];
		} else {
			[self removeMarker:currentLocationMarker];
		}
	}
	
}

#pragma mark -

- (void)setMap: (Map *) newMap {
	if(map != newMap) {
		
				
		[map release];		
		map = [newMap retain];
		
		[self removeAllMarkers];
		
		[self setupImageView];
		[self checkCurrentLocationMarker];
		[self setupAllMapMarkers];
		
	} else if([map.locations count] != [markers count]) {
		[self removeAllMarkers];
		[self setupAllMapMarkers];
	}	
}

- (void)setLocation:(Location *) loc isCurrent:(BOOL) isCurrent animated:(BOOL) animated {
		
	if(map != loc.map) {
		[map release];
		map = [loc.map retain];
		
		
		[self setupImageView];
		[self checkCurrentLocationMarker];
					
	}
	[self removeAllMarkers];
		
	LocationMarker *m = [[self markerForLocation:loc] retain];
	if(isCurrent) {
		
		[currentLocationMarker setIsCurrentLocation:NO];
		[m setIsCurrentLocation:YES];
		if(self.currentLocationMarker) {
			[self.currentLocationMarker removeFromSuperview];
		}
		if(self.curLocationCircle) {
			[self.curLocationCircle removeFromSuperview];
		}
	}
	
	[self addMarker:m];	
	[self scrollToMarker:m animated:animated];
	
	[m release];
	
}

- (void) setCurrentLocation:(Location *) loc animated:(BOOL) animated {
	[self setLocation:loc isCurrent:YES animated:animated];
}

- (void)setLocation:(Location *) loc animated:(BOOL) animated { 
	[self setLocation:loc isCurrent:NO animated:animated];
}

- (void) removeLocation:(Location *) loc {
	
	[self removeMarkerWithLocation:loc];	
	
	if([markers count] == 0) {
		[markers release];
		markers = nil;
	}
	 
}



- (void) setupImageView {	
	UIImage *img = [self loadMapImage];	
	
	[scrollView setZoomScale:1.0f];
	zoomScale = 1.0f;
	
	if(img) {
		
				
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:1.0];
		[UIView setAnimationTransition: UIViewAnimationTransitionCurlUp forView:self.contentView cache:YES];
		
		[imageView removeFromSuperview];
		[self setImageView:nil];
	
		UIImageView *newImageView = [[UIImageView alloc] initWithImage:img];
		[self setImageView:newImageView];
		[contentView addSubview:newImageView];
		[newImageView release];
		
		[scrollView setContentSize:imageView.image.size];
		[contentView setFrame:CGRectMake(0,0,imageView.image.size.width, imageView.image.size.height)];
	
		[UIView commitAnimations];
		
		
		float minW = scrollView.bounds.size.width / img.size.width;
		float minH = scrollView.bounds.size.height / img.size.height ;
		if (minW > minH) {
			scrollView.minimumZoomScale = minW;
		} else {
			scrollView.minimumZoomScale = minH;
		}

		
		
	}	
}
	
- (UIImage *) loadMapImage {
	UIImage *image;
	
	if(map.image) {
		image = map.image;
	} else {
		[[ActivityIndicator sharedActivityIndicator] showWithText:@"Loading map image..."];
		image = [ImageUploader downloadImage:map.mapURL];
		[[ActivityIndicator sharedActivityIndicator] hide];
		if(image) {
			[map setImageAndCreateThumbnail:image];
			
			[[EntityHome sharedEntityHome] saveContext];
		} else {
			UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Image Loading failed" message:@"Map Image could not be loaded. Please check your internet connection" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:nil];
			[alert show];
			[alert release];
		}		
	} 
	
	return image;	
}


- (CGRect) visibleMapViewRect {
	
	CGRect visibleRect;
	visibleRect.origin = scrollView.contentOffset;
	visibleRect.size = scrollView.bounds.size;	
	
	if(zoomScale != 1.0f) {
		
		CGFloat theScale = 1.0f / zoomScale;
		visibleRect.origin.x *= theScale;
		visibleRect.origin.y *= theScale;
		visibleRect.size.width *= theScale;
		visibleRect.size.height *= theScale;
		
	}
	 
	return visibleRect;
}
#pragma mark -
#pragma mark Marker
- (LocationMarker *) markerForLocation:(Location *) loc {
	return [[[LocationMarker alloc] initWithLocation:loc] autorelease];
}
		
- (void) addMarker: (LocationMarker *) marker {
	static int uniqueMarkerIndex = 0;

	marker.delegate = self;
	
	if(!markers) {
		markers = [[NSMutableDictionary alloc] init];
	}
	if(!marker.isCurrentLocation) {
		NSNumber *idx = [NSNumber numberWithInteger:uniqueMarkerIndex++];
		[markers setObject:marker forKey:idx];
		marker.index = idx;
	} else {		
		if(self.currentLocationMarker != marker) {		
			self.currentLocationMarker = marker;
			CurrentLocationCircle *circle = [[CurrentLocationCircle alloc] initWithMarker:marker];
		
			[self setCurLocationCircle:circle];
			[contentView addSubview:circle];
		
			[circle release];
		} else {			
			if(currentLocationMarker && !currentLocationMarker.superview) {
				[contentView addSubview:currentLocationMarker];
			}			
			if(curLocationCircle && !curLocationCircle.superview) {
				[contentView addSubview:curLocationCircle];
			}
		}
		
		[self setupMarkerAnnotation:marker];
		[self.contentView addSubview:marker.annotationView];
	}
	
	[marker zoomToScale:zoomScale];
	[contentView addSubview:marker];
}

- (void) addEmptyMarkerWithLocation:(Location *) loc {

	
	CGRect visibleRect = [self visibleMapViewRect];	
	[loc setMapXcord: [NSNumber numberWithFloat:visibleRect.origin.x + (visibleRect.size.width / 2.0f)]];
	[loc setMapYcord: [NSNumber numberWithFloat:visibleRect.origin.y + (visibleRect.size.height / 2.0f) - 20]];
	[loc setMap:map];
	
	LocationMarker *marker = [[self markerForLocation:loc] retain];
	[self addMarker:marker];	
	
	[marker release];
	
	[self enableMarker:marker];
	[marker.annotationView beginEditing];	
}

- (void) addEmptyMarker {
	Location *loc = [[LocationHome newObjectInContext] retain];

	[self addEmptyMarkerWithLocation:loc];
	[loc release];
}

- (void) removeMarkerWithLocation:(Location *) loc {
	if(loc.map == map) {
		
		for(LocationMarker *marker in [markers allValues]) {
			if([marker.location isEqual:loc]) {
				[self removeMarker:marker];
				return;
			}
		}
		
		if([markers count] == 0) {
			[markers release];
			markers = nil;
		}
		
	}
	
}

- (void) removeMarker:(LocationMarker *) marker{
	
	if(marker.annotationView) {
		[marker.annotationView removeFromSuperview];
	}
	
	[marker removeFromSuperview];	
	
	if(!marker.isCurrentLocation) {
		[markers removeObjectForKey:marker.index];
	} else {
		[curLocationCircle removeFromSuperview];
	}
	
}



- (void) enableMarker: (LocationMarker *) marker {
	
	if(!marker.annotationView) {
		[self setupMarkerAnnotation:marker];
	}
	
	[self.contentView addSubview:marker.annotationView];
	[marker setDragable:YES];	
	
}

- (void) disableMarker:(LocationMarker *) marker {
	[marker.annotationView removeFromSuperview];
	[marker setAnnotationView:nil];	
	[marker setDragable:NO];
}


- (LocationMarkerAnnotation *) setupMarkerAnnotation:(LocationMarker *)marker {
	LocationMarkerAnnotation *annotation = [[LocationMarkerAnnotation alloc] initWithMarker:marker];
	[marker setAnnotationView:annotation];
	[annotation setDelegate:self];
	return [annotation autorelease];
}

- (void) setupAllMapMarkers {
	NSArray *data = [map.locations allObjects];
	if(currentLocationMarker) {
		data = [data mutableCopy];
		[(NSMutableArray *) data removeObject:currentLocationMarker.location];
	}
	
	for(Location *loc in data) {
		LocationMarker *m = [[self markerForLocation:loc] retain];
		[self addMarker:m];
		[m release];
	}
	
	if(currentLocationMarker) {
		[data release];
	}
	
}

- (void) removeAllMarkers {
	
	for(LocationMarker *marker in [markers allValues]) {
		[self removeMarker: marker];
	}
	[markers removeAllObjects];
	[markers release];
	markers = nil;	
}

- (void) disableAllMarkers:(BOOL) b {
	for(id aKey in markers) {
		[self disableMarker:(LocationMarker *)[markers objectForKey:aKey]];
	}
}


#pragma mark -
#pragma mark LocationMarkerDelegate

- (void) markerWasClicked: (LocationMarker *) marker {
	
	if(marker.isCurrentLocation) {
		if(marker.annotationView) {
			[marker.annotationView removeFromSuperview];
			[marker setAnnotationView:nil];
		} else {
			[self setupMarkerAnnotation:marker];
			[self.contentView addSubview:marker.annotationView];
		}
		return;		
	}

	if(marker.dragable) {
		NSLog(@"disable dragging");
		[self disableMarker:marker];		
	} else {		
		NSLog(@"enable dragging");
		[self enableMarker:marker];
	}
	
	
}
- (void) markerTouchBegan: (LocationMarker *) marker {
	NSLog(@"disabling scrolling");
	[scrollView setScrollEnabled:NO];
}
- (void) markerTouchEnded: (LocationMarker *) marker {
	NSLog(@"enabling scrolling");
	[scrollView setScrollEnabled:YES];
}

- (void) markerWasMoved:(LocationMarker *) marker {
		
}

- (void) scrollToMarker:(LocationMarker *) marker animated:(BOOL) animated{
	CGRect frame = [scrollView frame];	
	
	frame.origin.x  = marker.center.x*zoomScale - (frame.size.width / 2);
	frame.origin.y = marker.center.y*zoomScale - (frame.size.height / 2);

	[scrollView scrollRectToVisible:frame animated:animated];
}

#pragma mark -
#pragma mark touches

- (void)scrollViewDidEndZooming:(UIScrollView *)sView withView:(UIView *)view atScale:(float)scale {
	
	
	

	zoomScale = scale;
	
	[UIView beginAnimations:nil context:nil];
	[UIView setAnimationDuration:0.2f];
	[UIView setAnimationCurve: UIViewAnimationCurveEaseInOut];
		
	for(LocationMarker *marker in [markers allValues]) {
		[marker zoomToScale:scale];
	}
	
	if(currentLocationMarker) {
		[currentLocationMarker zoomToScale:scale];
	}	
	
	
	//prevent the scrollview from sticking at the borders
	scrollView.frame = scrollView.frame;
	
	[UIView commitAnimations];	
}


- (void)scrollViewWillBeginDecelerating:(UIScrollView *)aScrollView {
	//fix in order to prevent scrollview from decelerating
	[aScrollView setContentOffset:aScrollView.contentOffset animated:YES];	
}


#pragma mark -
#pragma mark MapViewDelegate
- (void) mapViewWasDoubleClicked:(CGPoint) point {
	
	CGRect visibleRect = [self visibleMapViewRect];	
		
	if (zoomScale != 1.0f) {
		point.x /= zoomScale;
		point.y /= zoomScale;
		
	}
	
	CGFloat scale = 2.0f;
	
	// zoom in
	visibleRect.size.width /= scale;
	visibleRect.size.height /= scale;
	visibleRect.origin.x = point.x - visibleRect.size.width/2.0f;
	visibleRect.origin.y = point.y - visibleRect.size.height/2.0f;
	
	[scrollView zoomToRect:visibleRect animated:YES];	
	
	
	
	
	
}

- (void) mapViewWasClicked {

	[self disableAllMarkers:NO];
	
}

- (void) mapViewWasShaked {
	[scrollView setZoomScale:1.0f animated:YES];
}


#pragma mark -
#pragma mark Memory


- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}




- (void)dealloc {
	[curLocationCircle release];
	[markers release];
	[scrollView release];
	[contentView release];
	[imageView release];
	[map release];
    [super dealloc];
}


@end
