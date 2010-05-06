//
//  LocationMarkerAnnotation.h
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
#import "LocationMarker.h"


@protocol LocationMarkerAnnotationDelegate;
@class LocationMarker;


@interface LocationMarkerAnnotation : UIImageView <LocationMarkerDelegate, UITextFieldDelegate> {
	LocationMarker *marker;
	UILabel *locationLabel;
	UIButton *disclosureButton;
	UITextField *locationNameField;
	id <LocationMarkerAnnotationDelegate> delegate;
}

- (id) initWithMarker:(LocationMarker *) marker;

@property (nonatomic, retain) LocationMarker *marker;
@property (nonatomic, retain) UILabel *locationLabel;
@property (nonatomic, retain) UITextField *locationNameField;
@property (nonatomic, retain) UIButton *disclosureButton;
@property(nonatomic, assign) id <LocationMarkerAnnotationDelegate> delegate;

- (void) beginEditing;
- (void) finishEditing;
- (void) zoomToScale:(CGFloat) aScale;
@end

@protocol LocationMarkerAnnotationDelegate <NSObject>

@end