//
//  ActivityIndicator.m
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

#import "ActivityIndicator.h"


@implementation ActivityIndicator
@synthesize window, textLabel;

- (id) init {
	if((self = [super init])) {			
		[[NSBundle mainBundle] loadNibNamed:@"ActivityIndicator" owner:self options:nil];		
		window.windowLevel = UIWindowLevelAlert;
	}	
	return self;	
}

SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(ActivityIndicator)

- (void)show {
	[window makeKeyAndVisible];
	window.hidden = NO;
}

- (void)showWithText:(NSString *) text {
	[textLabel setText:text];
	[self show];
}

- (void)hide {
	[window resignKeyWindow];
	window.hidden = YES;
	[textLabel setText:@""];
}

@end
