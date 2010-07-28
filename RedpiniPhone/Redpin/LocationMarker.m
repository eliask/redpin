//
//  LocationMarker.m
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

#import "LocationMarker.h"
#import "Location.h"
#import "EntityHome.h"
#import "LocationMarkerAnnotation.h"
#import "InternetConnectionManager.h"


#define IMAGE_DRAGABLE @"purple_map_pin_big.png"
#define IMAGE_DRAGABLE_CURRENT_LOCATION @"current_location.png"
#define IMAGE_NONDRAGABLE @"red_map_pin_big.png"
#define IMAGE_NONDRAGABLE_CURRENT_LOCATION @"current_location.png"


@implementation LocationMarker
@synthesize index, location, delegate, dragable, x, y, lastTouch, wasMoved, annotationView, scale, isCurrentLocation;

- (id) init {	
	
	UIImage *image = [UIImage imageNamed:IMAGE_NONDRAGABLE];
	[super initWithImage:image];	
	[self setUserInteractionEnabled:YES];
	
	scale = 1.0f;

	dragable = NO;		
	return self;	
}

- (id) initWithCoordinates: (CGPoint) point {
	[self init];
	[self setMarker:point.x :point.y];
	
	return self;
}

- (id) initWithLocation: (Location *) loc {
	[self init];
	[self setLocation:loc];
	
	return self;
}

- (id)initWithFrame:(CGRect)frame {
	if ((self = [super initWithFrame:frame])) {
	}
	
	return self;
}


- (void)dealloc {
	[location release];
	[annotationView release];
	[super dealloc];
	
}

- (void) setStatusImage {
	NSString *imageName;
	
	if(!isCurrentLocation && dragable) {
		imageName = IMAGE_DRAGABLE;
	}
	
	if(!isCurrentLocation && !dragable) {
		imageName = IMAGE_NONDRAGABLE;
	}
	
	if(isCurrentLocation && dragable) {
		imageName = IMAGE_DRAGABLE_CURRENT_LOCATION;
	}
	
	if(isCurrentLocation && !dragable) {
		imageName = IMAGE_NONDRAGABLE_CURRENT_LOCATION;
	}
	
	[self setImage:[UIImage imageNamed:imageName]];		
}

- (void) setIsCurrentLocation:(BOOL) b {	
	if(isCurrentLocation != b) {
		isCurrentLocation = b;
		[self setStatusImage];
	}	
}
	

- (void) setDragable: (BOOL) b {
	
	if(isCurrentLocation && b)
		return;
	
	if(dragable != b) {
		if(b && [[InternetConnectionManager sharedInternetConnectionManager] offlineMode]) {			
			NSLog(@"%s: can't drag in offline mode", __FUNCTION__);
			return;					
		} 		
		dragable = b;
		[self setStatusImage];		
	}	
}


- (void) setMarker: (float) ax : (float) ay {
	self.x = ax;
	self.y = ay;
	[self setCenter:CGPointMake(x, y)];
}


- (void) setLocation: (Location *) loc {
	if(location != loc) {
		[location release];
		location = [loc retain];
		[self setMarker: [location.mapXcord floatValue] : [location.mapYcord floatValue]];
	}
}

- (void) updateLocation {
	if(location != nil) {
		if([[InternetConnectionManager sharedInternetConnectionManager] offlineMode]) {
			NSLog(@"%s: can't update location in offline mode", __FUNCTION__);
			return;
		}
		
		[location setMapXcord:[NSNumber numberWithFloat:self.center.x]];
		[location setMapYcord:[NSNumber numberWithFloat:self.center.y]];
		NSLog(@"Updating Location position: %@", location);
		[[EntityHome sharedEntityHome] saveContext];
	}
}

- (void) setMarkerImage: (UIImage *) img {
	[self setImage:img];
}

- (void) moveMarker:(float) ax :(float) ay {
	CGPoint c = self.center;
	c.x -= ax;
	c.y -= ay;
	
	[self setCenter: c];
	
	if(delegate) {
		if([delegate respondsToSelector:@selector(markerWasMoved:)]) {
			[delegate markerWasMoved:self];			
		}
	}
	
	if(annotationView) {
		[annotationView markerWasMoved:self];
	}
	
}

- (void) reset {
	lastTouch = CGPointMake(-1, -1);
}

- (void) touchesBegan:(NSSet* )touches withEvent:(UIEvent* )event {
		
	[delegate markerTouchBegan:self];
	wasMoved = NO;

	NSSet *allTouches = [event allTouches];
	
	switch ([allTouches count]) {
		case 1: {
			UITouch *touch = [[allTouches allObjects] objectAtIndex:0];
			lastTouch = [touch locationInView:self];			
			} break;
		default:
			break;
	}
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
	if(!self.dragable) {
		return;
	}
	
	wasMoved = YES;	
	NSSet *allTouches  = [event allTouches];
	
	switch ([allTouches  count]) {
		case 1: {
		
			UITouch *touch = [[allTouches allObjects] objectAtIndex:0];
			CGPoint curLoc = [touch locationInView:self];
			float dX = (curLoc.x - lastTouch.x);
			float dY = (curLoc.y - lastTouch.y);
						
			[self moveMarker:-dX :-dY];
			
		} break;
		default:
			break;
	}	
}

- (void) touchesEnded: (NSSet *) touches withEvent:(UIEvent *) event {
		
	if(delegate) {
		
		if([delegate respondsToSelector:@selector(markerTouchEnded:)]) {
			[delegate markerTouchEnded:self];
		}
		
		if(!wasMoved) {		
			if([delegate respondsToSelector:@selector(markerWasClicked:)]) {				
				[delegate markerWasClicked:self];				
			}
		} 
	}
	
	if(wasMoved) {
		[self updateLocation];
	}
	
	[self reset];
}




- (void)zoomToScale:(CGFloat) aScale {
	scale = aScale;

	CGAffineTransform transform = CGAffineTransformMakeScale(1.0f/aScale, 1.0f/aScale);  
	[self setTransform:transform];
	if(self.annotationView) {
		[annotationView zoomToScale:aScale];
	}		
}


- (void)removeFromSuperview {
	if(self.annotationView) {
		[annotationView removeFromSuperview];
	}
	[super removeFromSuperview];
}



@end
