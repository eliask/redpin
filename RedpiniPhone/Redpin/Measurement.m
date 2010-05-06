// 
//  Measurement.m
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


#import "Measurement.h"

#import "WifiReading.h"
#import "MeasurementHome.h"

@implementation Measurement 

@dynamic timestamp;
@dynamic wifiReadings;


- (id) proxyForJson {
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:3];
	
	if([self.rId intValue] != -1)
		[dict setObject:self.rId forKey:@"id"];
	
	if(self.timestamp)
		[dict setObject:self.timestamp forKey:@"timestamp"];
	
	if(self.wifiReadings) 
		[dict setObject:[self.wifiReadings allObjects] forKey:@"wifiReadings"];

	
	return [dict autorelease];
}

+ (Measurement *) fromJSON:(NSDictionary *) dict {
	
	Measurement *ms = [[MeasurementHome newObject] retain];
	[ms setRId:[dict objectForKey:@"id"]];
		
	return [ms autorelease];
}

@end
