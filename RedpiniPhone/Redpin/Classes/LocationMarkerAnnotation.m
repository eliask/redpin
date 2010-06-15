//
//  LocationMarkerAnnotation.m
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

#import "LocationMarkerAnnotation.h"
#import "LocationMarker.h"
#import "EntityHome.h"
#import "InternetConnectionManager.h"

#define ANNOTATION_OFFSET 0

@interface LocationMarkerAnnotation()

- (UILabel *) buildLabel;
- (UIButton *) buildDisclosureButton;
- (UITextField *) buildTextField;
- (void) buttonClicked:(id) sender;

@end


@implementation LocationMarkerAnnotation
@synthesize marker, locationLabel, disclosureButton, locationNameField, delegate;


- (id) initWithMarker:(LocationMarker *) aMarker{

	[self setMarker:aMarker];
	[marker setAnnotationView:self];
	
	if ((self = [self initWithFrame:CGRectMake(0, 0, 150, 40)])) {
		[self setCenter:CGPointMake(aMarker.center.x, aMarker.center.y - (aMarker.frame.size.height+ ANNOTATION_OFFSET))];
	}
	
	[self zoomToScale:aMarker.scale];
	
	return self;
}

- (id)initWithFrame:(CGRect)frame {
    if ((self = [super initWithFrame:frame])) {
        [self setUserInteractionEnabled:YES];
				
		UILabel *label = [[self buildLabel] retain];

		[self setLocationLabel:label];
		[self addSubview:label];		
		
		UITextField *textField = [[self buildTextField] retain];
		[textField setHidden:YES];
		[self setLocationNameField:textField];
		[self addSubview:textField];		
		
		UIButton *button = [[self buildDisclosureButton] retain];
		[button setCenter:CGPointMake(locationLabel.frame.size.width + locationLabel.frame.origin.x + 20, 15)];
		[button addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchDown];
		[button setEnabled:[[InternetConnectionManager sharedInternetConnectionManager] onlineMode]];
		
		[self addSubview:button];
		[self setDisclosureButton:button];
		
		[self setFrame:CGRectMake(0, 0, disclosureButton.frame.origin.x + disclosureButton.frame.size.width +10 , 40)];		
		
		[textField release];
		[label release];
		[button release];
		
		[self setAutoresizingMask:UIViewAutoresizingFlexibleWidth];
		[self setImage:[UIImage imageNamed:@"annotation.png"]];
			
    }
    return self;
}


- (void)drawRect:(CGRect)rect {
}

#pragma mark -
#pragma mark subviews;

- (UILabel *) buildLabel {
	
	NSString *labelText;
	if(marker.location.symbolicID) {
		labelText = marker.location.symbolicID;
	} else {
		labelText = @"Location Name";
	}

	CGSize size = [labelText sizeWithFont:[UIFont boldSystemFontOfSize:[UIFont labelFontSize]]];
	
	UILabel *label = nil;
	if (size.width < 100) {
		label = [[UILabel alloc] initWithFrame:CGRectMake(10,-7, 100, 40)];
	}
	else if(size.width < 250) {
		label = [[UILabel alloc] initWithFrame:CGRectMake(10,-7, size.width, 40)];
	} else {
		label = [[UILabel alloc] initWithFrame:CGRectMake(10, -7, 250, 40)];
		label.adjustsFontSizeToFitWidth = YES;
		label.minimumFontSize = 12;
	}
	label.font = [UIFont boldSystemFontOfSize:[UIFont labelFontSize]];
	label.textColor = [UIColor whiteColor];
	label.backgroundColor = [UIColor clearColor];
	label.text = marker.location.symbolicID;
	return [label autorelease];	
}

- (UIButton *) buildDisclosureButton {
	UIButton *button = [[UIButton buttonWithType:UIButtonTypeDetailDisclosure] retain];
	[button setCenter:CGPointMake(locationLabel.frame.size.width + locationLabel.frame.origin.x + 20, 20)];
	[button addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchDown];
	
	return [button autorelease];
}

- (UITextField *) buildTextField {
	
	UITextField *field = [[UITextField alloc] initWithFrame:CGRectMake(10,1, self.frame.size.width - 20, 28)];
	field.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	field.textColor = [UIColor blackColor];
	field.backgroundColor = [UIColor clearColor];
	field.clearsOnBeginEditing = NO;
	field.borderStyle = UITextBorderStyleRoundedRect;
	field.returnKeyType = UIReturnKeyDone;
	field.enablesReturnKeyAutomatically = YES;
	field.placeholder = @"Location Name";
	field.delegate = self;
	return [field autorelease];
}


#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	if(textField == locationNameField) {
		if([locationNameField.text length] == 0) {
			return NO;
		}
		[self finishEditing];	
	}
	
	return YES;
}

#pragma mark -
#pragma mark context switches

- (void) beginEditing {
	if(!locationNameField) {
		[self setLocationNameField:[self buildTextField]];
	}

	[locationLabel setHidden:YES];
	[disclosureButton setHidden:YES];
	[locationNameField setHidden:NO];
	
	locationNameField.text = marker.location.symbolicID;	
	[self addSubview:locationNameField];
	[locationNameField becomeFirstResponder];

}

- (void) finishEditing {
	
	[marker.location setSymbolicID:locationNameField.text];	
	locationLabel.text = marker.location.symbolicID;
	
	[locationNameField resignFirstResponder];
	[self.superview.superview becomeFirstResponder];

	[locationNameField setHidden:YES];
	[locationLabel setHidden:NO];
	[disclosureButton setHidden:NO];
	
	[[EntityHome sharedEntityHome] saveContext];
}

#pragma mark -
#pragma mark toches

- (void) buttonClicked:(id) sender {
	
	if([[InternetConnectionManager sharedInternetConnectionManager] offlineMode]) {
		
		NSLog(@"%s: can't change location label in offline mode", __FUNCTION__);
		return;
	}
	
	NSLog(@"disclosure button clicked");
	
	[self beginEditing];
	
}

-(void) touchesEnded: (NSSet *) touches withEvent: (UIEvent *) event {	
	NSLog(@"location marker annotation clicked: %@", self);	
}

#pragma mark -
#pragma mark LocationMarkerDelegate

- (void) markerWasMoved:(LocationMarker *) aMarker{	
	[self setCenter:CGPointMake(aMarker.center.x, aMarker.center.y - (aMarker.frame.size.height + ANNOTATION_OFFSET))];
}

#pragma mark -

- (void)zoomToScale:(CGFloat) aScale {
	CGFloat scale = 1.0f/aScale;
	
	CGAffineTransform trans =  CGAffineTransformMakeScale(scale, scale);
	[self setTransform:trans];		
		
	[self setCenter:CGPointMake(self.marker.center.x, self.marker.center.y - (self.marker.frame.size.height+ ANNOTATION_OFFSET))];

}


- (void)dealloc {
	[marker release];
    [super dealloc];
}


@end
