//
//  ServerRequest.h
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

typedef enum  {
	ServerRequestAction_setFingerprint,
	ServerRequestAction_getLocation,
	ServerRequestAction_getMapList,
	ServerRequestAction_setMap,
	ServerRequestAction_removeMap,
	ServerRequestAction_getLocationList,
	ServerRequestAction_updateLocation,
	ServerRequestAction_removeLocation,
	
	ServerRequestAction_Unknown
} ServerRequestAction;

@interface ServerRequest : NSObject {
	ServerRequestAction action;
	id data;
}

@property (nonatomic, assign) ServerRequestAction action;
@property (nonatomic, retain) id data;


+ (NSString *) stringForServerRequestAction:(ServerRequestAction) aStatus;
+ (ServerRequestAction) actionForServerRequestAction:(NSString *) aString;

- (id) initWithAction: (ServerRequestAction) anAction data: (id) someData;

- (id)jsonProxyObject;
- (NSString *) toJSON;

@end
