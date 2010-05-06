//
//  SynchronizationManager.m
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

#import "SynchronizationManager.h"
#import "EntityHome.h"
#import "InternetConnectionManager.h"
#import "RedpinAppDelegate.h"


@implementation SynchronizationManager

SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(SynchronizationManager)

- (id) init {	
	if((self = [super init])) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(internetConnectionChanged:) name:InternetConnectionManagerUpdateNotification object:nil];
	}
		
	return self;
}

- (void) synchronize {
	
	mapHome = [[MapRemoteHome alloc] initWithDelegate:self];
	locationHome = [[LocationRemoteHome alloc] initWithDelegate:self];
	
	[mapHome fetchObjects];
	synched = YES;	
}


- (void) internetConnectionChanged:(NSNotification *) note {
		
	if(!synched && [[InternetConnectionManager sharedInternetConnectionManager] onlineMode]) {
		[self synchronize];
	}
	
}


- (void)dealloc {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:InternetConnectionManagerUpdateNotification object:nil];
    [super dealloc];
}


- (void) entityHomeDidFetchObjects:(id <RemoteEntityHomeProtocol>) objectHome {
	if(objectHome == mapHome) {		
		[locationHome fetchObjects];		
		[mapHome release];
		mapHome = nil;
	}
	
	if(objectHome == locationHome) {
		[locationHome release];
		locationHome = nil;
	}
	
	
}

- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didInsertObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didDeleteObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didUpdateObject:(id)anObject {
	
}
- (void) entityHome:(id <RemoteEntityHomeProtocol>) objectHome didFailWithError:(NSError *) error {
	[objectHome release];	
}

@end
