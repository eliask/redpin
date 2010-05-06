//
//  IntervalScannerInfo.h
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
 * © Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */

#import <Foundation/Foundation.h>
#import <CoreFoundation/CoreFoundation.h>
#import "Location.h"


@interface IntervalScannerInfo : NSObject {
}

static NSNumber * locIDInProcess;
static Location * locInProcess;

+ (void) initialize;
- (id) init : (NSNumber*) lId forLocation : (Location *) loc;
- (id) init;
- (NSNumber *) locationIdInProcess;
- (Location *) locationInProcess;

@end

