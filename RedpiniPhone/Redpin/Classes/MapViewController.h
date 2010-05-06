//
//  MapViewController.h
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


@class MapView;

#import "Map.h"
#import "LocationMarker.h"
#import "LocationMarkerAnnotation.h"
#import "MapView.h"
#import "CurrentLocationCircle.h"

@interface MapViewController : UIViewController <UIScrollViewDelegate, LocationMarkerDelegate, LocationMarkerAnnotationDelegate, MapViewDelegate> {
	IBOutlet MapView *scrollView;
	IBOutlet UIView *contentView;
	IBOutlet UIImageView *imageView;	
	Map *map;
	NSMutableDictionary* markers;	
	BOOL keyboardIsShowing;
	float zoomScale;	
	LocationMarker *currentLocationMarker;		
	CurrentLocationCircle *curLocationCircle;
}

@property (nonatomic, retain) IBOutlet MapView *scrollView;
@property (nonatomic, retain) IBOutlet UIView *contentView;
@property (nonatomic, retain) IBOutlet UIImageView *imageView;
@property (nonatomic, retain) Map *map;
@property (nonatomic, readonly) NSMutableDictionary *markers;
@property (nonatomic, retain) LocationMarker *currentLocationMarker;
@property (nonatomic, retain) CurrentLocationCircle *curLocationCircle;


- (void) addEmptyMarker;
- (void) enableMarker: (LocationMarker *) marker;
- (void) disableMarker:(LocationMarker *) marker;
- (void) removeMarkerWithLocation:(Location *) loc;
- (void) setLocation:(Location *) loc animated:(BOOL) animated;
- (void) setCurrentLocation:(Location *) loc animated:(BOOL) animated;
- (void) removeLocation:(Location *) loc;
- (void) mapViewWasDoubleClicked:(CGPoint) point;
- (void) mapViewWasClicked;
- (void) mapViewWasShaked;
- (void) addEmptyMarkerWithLocation:(Location *) loc;
- (void) scrollToMarker:(LocationMarker *) marker animated:(BOOL) animated;

@end