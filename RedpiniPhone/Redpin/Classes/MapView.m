//
//  MapView.m
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

#import "MapView.h"
#import "Map.h"

@implementation MapView

-(void) touchesEnded: (NSSet *) touches withEvent: (UIEvent *) event {	
		
	UITouch	*touch = [touches anyObject];
	
    if ([touch tapCount] == 2) {
        CGPoint tapPoint = [touch locationInView:self];
		
        // Process a double-tap gesture
		if([self.delegate conformsToProtocol:@protocol(MapViewDelegate)]) {
			[(id<MapViewDelegate>) self.delegate mapViewWasDoubleClicked:tapPoint];
		}
    } else {
	
		if([self.delegate conformsToProtocol:@protocol(MapViewDelegate)]) {
			[(id<MapViewDelegate>) self.delegate mapViewWasClicked];
		}		
	}	
	[super touchesEnded: touches withEvent: event];	
}

- (void) motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
		if([self.delegate conformsToProtocol:@protocol(MapViewDelegate)]) {
			[(id<MapViewDelegate>) self.delegate mapViewWasShaked];
		}
    }
	
	if ([super respondsToSelector:@selector(motionEnded:withEvent:)]) {
		[super motionEnded:motion withEvent:event];
	}
}

- (BOOL)canBecomeFirstResponder { 
	return YES; 
}


@end