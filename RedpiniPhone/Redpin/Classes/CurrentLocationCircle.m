//
//  CurrentLocationCircle.h
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

#import "CurrentLocationCircle.h"
#import "LocationMarker.h"

#define DIAMETER_ACCOURACY_PROPORTION 10.0f
#define DEFAULT_SIZE 50.0f
#define MIN_SIZE 50.0f
#define MAX_SIZE 150.0f
#define MAX_ACCOURACY 10.0f



@implementation CurrentLocationCircle
@synthesize circleSize;

- (id) initWithMarker:(LocationMarker *) marker {
	if((self = [self init])) {
		NSNumber *acc = marker.location.accuracy;
		if(acc && ([acc floatValue] > 0.0f)) {
			[self setCircleSize:((MAX_ACCOURACY*DIAMETER_ACCOURACY_PROPORTION + MIN_SIZE) - ([marker.location.accuracy floatValue] * DIAMETER_ACCOURACY_PROPORTION))];
		} else {
			[self setCircleSize:DEFAULT_SIZE];
		}
		
		[self setCenter:marker.center];
	}
	
	return self;
	
}


- (id) init {
	if((self = [super init])) {
		[self setOpaque:NO];
	}
	
	return self;
}

- (void) setCircleSize:(CGFloat) size {

	[UIView beginAnimations:nil context:nil];
	[UIView setAnimationDuration:0.4f];
	[UIView setAnimationCurve: UIViewAnimationCurveEaseOut];
		
	[self setBounds:CGRectMake(0, 0, size+4.0f, size+4.0f)];
	
	circleSize = size;
	[UIView commitAnimations];
}	

- (void)drawRect:(CGRect)rect {

	CGContextRef contextRef = UIGraphicsGetCurrentContext();
		
	CGContextSetRGBFillColor(contextRef, 1, 0, 0, 0.1f);
	CGContextSetRGBStrokeColor(contextRef, 1, 0, 0, 1.0f);	
	
	// Draw a circle (filled)
	CGContextFillEllipseInRect(contextRef, CGRectMake(2, 2, circleSize, circleSize));
	
	// Draw a circle (border only)
	CGContextStrokeEllipseInRect(contextRef, CGRectMake(2, 2, circleSize, circleSize));
}



- (void)dealloc {
    [super dealloc];
}


@end
