// 
//  Location.m
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

#import "Location.h"

#import "Fingerprint.h"
#import "Map.h"

#import "LocationHome.h"
#import "MapHome.h"

@implementation Location 

@dynamic mapYcord;
@dynamic symbolicID;
@dynamic mapXcord;
@dynamic accuracy;
@dynamic map;

- (id) proxyForJson {
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
	
	if([self.rId intValue] != -1)
		[dict setObject:self.rId forKey:@"id"];
	
	if(self.symbolicID)
		[dict setObject:self.symbolicID forKey:@"symbolicID"];
	
	if(self.mapXcord) 
		[dict setObject:self.mapXcord forKey:@"mapXcord"];
	
	if(self.mapYcord) 
		[dict setObject:self.mapYcord forKey:@"mapYcord"];
	
	
	if(self.map) {
		[dict setObject:self.map forKey:@"map"];
	}
	
	return [dict autorelease];
}

+ (Location *) fromJSON:(NSDictionary *) dict {
	
	Location *loc = [[LocationHome newObject] retain];
	[loc setRId:[dict objectForKey:@"id"]];
	[loc setSymbolicID:[dict objectForKey:@"symbolicID"]];
	[loc setMapXcord:[dict objectForKey:@"mapXcord"]];
	[loc setMapYcord:[dict objectForKey:@"mapYcord"]];
	[loc setAccuracy:[dict objectForKey:@"accuracy"]];
	
	Map *map = [MapHome getMapByRemoteId:[dict objectForKey:@"mapId"]];
	if(map) {
		[loc setMap:map];
	}
	
	return [loc autorelease];
}


@end
