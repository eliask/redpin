//
//  ServerRequest.m
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

#import "ServerRequest.h"
#import "JSON.h"

@implementation ServerRequest
@synthesize action, data;

#pragma mark -
#pragma mark ServerRequestAction


static NSString *setFingerprint = @"setFingerprint";
static NSString *getLocation = @"getLocation";
static NSString *getMapList = @"getMapList";
static NSString *setMap = @"setMap";
static NSString *removeMap = @"removeMap";
static NSString *getLocationList = @"getLocationList";
static NSString *updateLocation = @"updateLocation";
static NSString *removeLocation = @"removeLocation";

+ (NSString *) stringForServerRequestAction:(ServerRequestAction) aStatus {
	switch (aStatus) {
		case ServerRequestAction_setFingerprint:
			return setFingerprint;
			break;
		case ServerRequestAction_getLocation:
			return getLocation;
			break;
		case ServerRequestAction_getMapList:
			return getMapList;
			break;
		case ServerRequestAction_setMap:
			return setMap;
			break;
		case ServerRequestAction_removeMap:
			return removeMap;
			break;
		case ServerRequestAction_getLocationList:
			return getLocationList;
		case ServerRequestAction_updateLocation:
			return updateLocation;
		case ServerRequestAction_removeLocation:
			return removeLocation;

		default:
			return nil;
			break;
	}
}
+ (ServerRequestAction) actionForServerRequestAction:(NSString *) aString {
	if([aString isEqualToString:setFingerprint]) {
		return ServerRequestAction_setFingerprint;
	} else if([aString isEqualToString:getLocation]) {
		return ServerRequestAction_getLocation;
	} else if ([aString isEqualToString:getMapList]) {
		return ServerRequestAction_getMapList;
	} else if ([aString isEqualToString:setMap]) {
		return ServerRequestAction_setMap;
	} else if ([aString isEqualToString:removeMap]) {
		return ServerRequestAction_removeMap;
	} else if([aString isEqualToString:getLocationList]) {
		return ServerRequestAction_getLocationList;
	} else if([aString isEqualToString:updateLocation]) {
		return ServerRequestAction_updateLocation;
	} else if([aString isEqualToString:removeLocation]) {
		return ServerRequestAction_removeLocation;
	} else {
		return ServerRequestAction_Unknown;
	}
}


#pragma mark -
#pragma mark init

- (id) initWithAction: (ServerRequestAction) anAction data: (id) someData {
	if((self = [self init])) {
		self.action = anAction;
		self.data = someData;
	}	
	return self;
}


#pragma mark -
#pragma mark JSON Serialization

- (id)jsonProxyObject {
	return [NSDictionary dictionaryWithObjectsAndKeys:
			[ServerRequest stringForServerRequestAction:self.action], @"action",
			self.data, @"data",
			nil];
}

- (NSString *) toJSON {
    SBJsonWriter *jsonWriter = [SBJsonWriter new];
    NSString *json = [jsonWriter stringWithObject:[self jsonProxyObject]];    
    if (!json)
        NSLog(@"toJSON failed. Error trace is: %@", [jsonWriter errorTrace]);
    [jsonWriter release];
    return json;
}

- (void)dealloc {
	[data release];
    [super dealloc];
}

@end
