// 
//  Fingerprint.m
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

#import "Fingerprint.h"

#import "Location.h"
#import "Measurement.h"

#import "FingerprintHome.h"
#import "LocationHome.h"

@implementation Fingerprint 

@dynamic location;
@dynamic measurement;

- (id) proxyForJson {
	
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:3];
	
	if([self.rId intValue] != -1)
		[dict setObject:self.rId forKey:@"id"];
	
	if(self.measurement)
		[dict setObject:self.measurement forKey:@"measurement"];
	
	if(self.location) 
		[dict setObject:self.location forKey:@"location"];
	
	
	return [dict autorelease];
}

+ (Fingerprint *) fromJSON:(NSDictionary *) dict {
	
	Fingerprint *fprint = [[FingerprintHome newObject] retain];
	[fprint setRId:[dict objectForKey:@"id"]];
	[fprint setMeasurement:[Measurement fromJSON:[dict objectForKey:@"measurement"]]];
	
	
	Location *loc = [LocationHome getLocationByRemoteId:[dict objectForKey:@"locationId"]];
	if(loc) {
		[fprint setLocation:loc];
	} else {
		[fprint setLocation:[Location fromJSON:[dict objectForKey:@"location"]]];
	}
	
	return [fprint autorelease];
}


@end
