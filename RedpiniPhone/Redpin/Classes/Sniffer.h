//
//  Sniffer.h
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
#import <CoreFoundation/CoreFoundation.h>
#include <dlfcn.h>
#import "RemoteEntityHome.h"
#import "Measurement.h"
#import "Location.h"

@protocol SnifferDelegate;


@interface Sniffer : NSObject <RemoteEntityHomeProtocolDelegate, UIAccelerometerDelegate>{
	
	BOOL scanning;
	id<SnifferDelegate> delegate;	
	
	void *libHandle;
	void *airportHandle;
	int (*open)(void *);
	int (*bind)(void *, NSString *);
	int (*close)(void *);
	int (*scan)(void *, NSArray **, void *);	

	BOOL movedWhileScanning;
	BOOL skipAccelerator;
}

@property (nonatomic, assign) id<SnifferDelegate> delegate;


- (id) initWithDelegate:(id) aDelegate;
- (id) init;
- (void) retrieveLocationForMeasurement:(Measurement *) measurement;
- (void) performScan;
- (void) dealloc;

@end

@protocol SnifferDelegate <NSObject>

@optional

- (void) sniffer:(Sniffer *)aSniffer detectedContinuingMovement:(NSUInteger) numberOfSeconds;
- (void) sniffer:(Sniffer *)aSniffer estimatedLocation:(Location *)currentLocation;
- (void) sniffer:(Sniffer *)aSniffer didScan:(Measurement *) measurement;

@end



