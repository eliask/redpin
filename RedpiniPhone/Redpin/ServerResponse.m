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

#import "ServerResponse.h"
#import "SBJsonParser.h"


@implementation ServerResponse

@synthesize status, message, data;

static NSString *ok = @"ok";
static NSString *failed= @"failed";
static NSString *warning = @"warning";
static NSString *jsonError = @"jsonError";

+ (NSString *) stringForServerResponseStatus:(ServerResponseStatus) aStatus {
	switch (aStatus) {
		case ServerResponseStatus_OK:
			return ok;
			break;
		case ServerResponseStatus_Failed:
			return failed;
			break;
		case ServerResponseStatus_Warning:
			return warning;
			break;
		case ServerResponseStatus_JSONError:
			return jsonError;
		default:
			return nil;
			break;
	}
}

+ (ServerResponseStatus) statusForServerResponseStatus:(NSString *) aString {
	if([aString isEqualToString:ok]) {
		return ServerResponseStatus_OK;
	} else if([aString isEqualToString:warning]) {
		return ServerResponseStatus_Warning;
	} else if([aString isEqualToString:failed]) {
		return ServerResponseStatus_Failed;
	} else if([aString isEqualToString:jsonError]) {
		return ServerResponseStatus_JSONError;
	} else {
		return ServerResponseStatus_Unknown;
	}
}


+ (ServerResponse *) fromJSON:(NSString *) string {
	
	SBJsonParser *parser = [[SBJsonParser alloc] init];
	NSDictionary *json_dict = [parser objectWithString:string];
	
	ServerResponse *response = [[ServerResponse alloc] initWithStatus:[ServerResponse statusForServerResponseStatus:[json_dict objectForKey:@"status"]] Message:[json_dict objectForKey:@"message"] Data:[json_dict objectForKey:@"data"]];
	[parser release];
	
	return [response autorelease];
}

- (id) initWithStatus: (ServerResponseStatus) aStatus Message: (NSString *) aMessage Data: (id) someData {
	
	if((self = [super self])) {
		self.status = aStatus;
		self.message = aMessage;
		self.data = someData;
	}
	return self;	
}

- (NSString *) responseStatus {
	return [ServerResponse stringForServerResponseStatus:self.status];
}

- (NSString *) description {
	return [[super description] stringByAppendingFormat:@"\n\tStatus = %@ \n\tMessage = %@ \n\tData = %@", [ServerResponse stringForServerResponseStatus:self.status], self.message, self.data];
}

- (void)dealloc {

	[message release];
	[data release];
    [super dealloc];
}



@end
