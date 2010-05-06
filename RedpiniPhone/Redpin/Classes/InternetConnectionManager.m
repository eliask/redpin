//
//  InternetConnectionManager.m
//  Redpin
//
//  Created by Pascal Brogle on 27.08.09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "InternetConnectionManager.h"
#import "ServerConnection.h"


NSString *const InternetConnectionManagerUpdateNotification = @"InternetConnectionUpdate";

@interface InternetConnectionManager()

- (void) initStatus;
- (void) checkRedpinServer;
- (void) notifyChange;
- (void) alertNotReachable;
- (void) alertReachable;


@end


@implementation InternetConnectionManager
@synthesize internetConnectionStatus, remoteHostStatus, reachableAlert, notReachableAlert;

SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(InternetConnectionManager)




- (id)init {
	
	if((self = [super init])) {
		[[Reachability sharedReachability] retain];
		[[Reachability sharedReachability] setHostName:[ServerConnection serverURL]];
		[[Reachability sharedReachability] setNetworkStatusNotificationsEnabled:YES];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:@"kNetworkReachabilityChangedNotification" object:nil];
		[self initStatus];

	}
	
	return self;
}

- (void) initStatus {
	//kick up internet connection
	[self checkRedpinServer];
	
    self.remoteHostStatus = [[Reachability sharedReachability] remoteHostStatus];
	self.internetConnectionStatus = [[Reachability sharedReachability] internetConnectionStatus];
}

- (void) dealloc {	
	[[Reachability sharedReachability] release];
	
	reachableAlert = nil;
	notReachableAlert = nil;
	
	[super dealloc];
}


- (BOOL) onlineMode {
	return serverRechable;
}

- (BOOL) offlineMode {
	return !serverRechable;
}

#pragma mark -
#pragma mark Redpin Server Test

- (void) checkRedpinServer {
	if(socket) {
		return;
	}
	
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	socket = [[AsyncSocket alloc] initWithDelegate:self];
	
	NSError *error = nil;
	
	if(![socket connectToHost:[ServerConnection serverURL] onPort:[ServerConnection serverPort] withTimeout:30.0  error:&error]) {
		[self serverDown];
		
		[socket release];
		socket = nil;
	}
	
}

- (void) serverDown {

	NSLog(@"%s: server is down", __FUNCTION__);
	serverRechable = NO;
	if(!goneOffline) {
		[self alertNotReachable];
	}
	[self notifyChange];
	
	goneOffline = YES;
}

- (void) serverUp {
	NSLog(@"%s: server is up", __FUNCTION__);
	serverRechable = YES;
	if(goneOffline) {
		[self alertReachable];
		goneOffline = NO;
	}
	[self notifyChange];
	
}

#pragma mark AsyncSocketDelegate

-(void)onSocket:(AsyncSocket *)sock willDisconnectWithError:(NSError *)err {
	if(err) {
		[self serverDown];
	}
}




-(void)onSocket:(AsyncSocket *)sock didConnectToHost:(NSString *)host port:(UInt16)port {
	[self serverUp];
	[sock disconnect];
}

-(void)onSocketDidDisconnect:(AsyncSocket *)sock {
	[socket release];
	socket = nil;
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

- (void) notifyChange {
	[[NSNotificationCenter defaultCenter] postNotificationName:InternetConnectionManagerUpdateNotification object:nil];
}

- (void) alertNotReachable {
	
	if (alertNotReachablePresent) {
		return;
	}
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Status" message:@"Sorry, the network is not available. You are now in offline mode" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
	[alert show];
	notReachableAlert = alert;
	alertNotReachablePresent = YES;
	
	[alert release];
	
}

- (void) alertReachable {
	
	if (alertReachablePresent) {
		return;
	}
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Status" message:@"You are now in online mode" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
	[alert show];
	
	reachableAlert = alert;
	alertReachablePresent = YES;
	[alert release];
	
}

#pragma mark -
#pragma mark UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (reachableAlert == alertView) {		
		reachableAlert = nil;
		alertReachablePresent = NO;
		
	}
	
	if (notReachableAlert == alertView) {		
		notReachableAlert = nil;
		alertNotReachablePresent = NO;
	}
}

#pragma mark -
#pragma mark Rechability


- (void)reachabilityChanged:(NSNotification *)note {
    [self updateStatus];
}

- (void) updateStatus {

	self.remoteHostStatus = [[Reachability sharedReachability] remoteHostStatus];
	self.internetConnectionStatus = [[Reachability sharedReachability] internetConnectionStatus];
	
	NSLog(@"remote status = %d, internet status = %d", self.remoteHostStatus, self.internetConnectionStatus);
	
	
	if (self.internetConnectionStatus == NotReachable && self.remoteHostStatus == NotReachable) {
		[self serverDown];
	} else {
		// check if we can reach thr redpin server
		[self checkRedpinServer];
	}	
}



@end
