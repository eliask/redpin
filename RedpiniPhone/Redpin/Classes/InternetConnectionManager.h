//
//  InternetConnectionManager.h
//  Redpin
//
//  Created by Pascal Brogle on 27.08.09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reachability.h"
#import "SynthesizeSingleton.h"
#import "AsyncSocket.h"

@interface InternetConnectionManager : NSObject <UIAlertViewDelegate> {
	NetworkStatus internetConnectionStatus;
	NetworkStatus remoteHostStatus;
	
	AsyncSocket *socket;
	
	BOOL serverRechable;
	BOOL goneOffline;
	
	BOOL alertNotReachablePresent;
	BOOL alertReachablePresent;
	
	UIAlertView *reachableAlert;
	UIAlertView *notReachableAlert;

}
SYNTHESIZE_SINGLETON_INTERFACE_FOR_CLASS(InternetConnectionManager)

UIKIT_EXTERN  NSString *const InternetConnectionManagerUpdateNotification;

@property (nonatomic, assign) NetworkStatus internetConnectionStatus;
@property (nonatomic, assign) NetworkStatus remoteHostStatus;
@property (nonatomic, retain) UIAlertView *reachableAlert;
@property (nonatomic, retain) UIAlertView *notReachableAlert;


- (BOOL) onlineMode;
- (BOOL) offlineMode;
- (void) initStatus;
- (void) reachabilityChanged:(NSNotification *)note;
- (void) updateStatus;

- (void) serverUp;
- (void) serverDown;

@end
