//
//  WifiReadingRemoteHome.m
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

#import "WifiReadingRemoteHome.h"


@implementation WifiReadingRemoteHome
@synthesize delegate;

- (id) initWithDelegate:(id<RemoteEntityHomeProtocolDelegate>)aDelegate {
	if((self = [super init])) {
		self.delegate = aDelegate;
	}
	
	return self;
}

- (void) insertObject:(id)anObject {	
	[delegate entityHome:self didInsertObject:anObject];	
}

- (void) deleteObject:(id)anObject {
	[delegate entityHome:self didDeleteObject:anObject];
}


- (void) updateObject:(id)anObject {
	[delegate entityHome:self didUpdateObject:anObject];
	
}

- (void) fetchObjects {
	
}

- (void) serverRequest:(ServerRequest *) request didFailWithError:(NSError *) error connection:(ServerConnection *) connection {
	if(delegate) {
		[delegate entityHome:self didFailWithError:error];
	} else {
		NSLog(@"%@", error);
	}
	
	[request release];
	//need autorlease because AsyncSocket does sent onSocketDidDisconnect afterwards to connection
	[connection autorelease];
	
}

@end
