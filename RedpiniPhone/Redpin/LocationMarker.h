//
//  LocationMarker.h
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

#import <Foundation/Foundation.h>
#import "Location.h"


@class LocationMarker;
@class LocationMarkerAnnotation;


@protocol LocationMarkerDelegate <NSObject>
@optional
- (void) markerWasClicked: (LocationMarker *) marker;
- (void) markerTouchBegan: (LocationMarker *) marker;
- (void) markerTouchEnded: (LocationMarker *) marker;
- (void) markerWasMoved:(LocationMarker *) marker;
@end

@interface LocationMarker : UIImageView {
	NSNumber *index;
	Location *location;
	id<LocationMarkerDelegate> delegate;
	BOOL dragable;
	float x;
	float y;
	CGPoint lastTouch;
	
	LocationMarkerAnnotation<LocationMarkerDelegate> *annotationView;
	
	BOOL wasMoved;
	
	CGFloat scale;
	BOOL isCurrentLocation;
	
}

- (id) initWithCoordinates: (CGPoint) point;
- (id) initWithLocation: (Location *) loc;
	
- (void) setMarkerImage:(UIImage *) image;
- (void) setMarker: (float) x : (float) y;
- (void) reset;



- (void)zoomToScale:(CGFloat) aScale;

@property (nonatomic, retain) NSNumber* index;
@property (nonatomic, retain) Location *location;
@property (nonatomic, assign) id<LocationMarkerDelegate>  delegate;
@property (nonatomic) BOOL dragable;
@property (nonatomic) float x;
@property (nonatomic) float y;
@property (nonatomic) CGPoint lastTouch;
@property (nonatomic) BOOL wasMoved;
@property (nonatomic, retain) LocationMarkerAnnotation *annotationView;

@property (nonatomic, assign) CGFloat scale;
@property (nonatomic, assign) BOOL isCurrentLocation;

@end





