//
//  Server.m
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

#import "ServerConnection.h"
#import "JSON.h"
#import "InternetConnectionManager.h"

#define DEFAULT_HOST @"localhost"
#define DEFAULT_PORT 8000

#define WriteTag 1
#define ReadTag 2

//TODO: perhaps timeout
@implementation ServerConnection

- (id) initWithDelegate:(id) aDelegate {
	if((self = [super init])) {
		delegate = aDelegate;		
	}
	
	return self;
	
}

static NSString *server_url_key = @"server_url_preference";
static NSString *server_port_key = @"server_port_preference";

+ (NSString *) serverURL {
	NSString *url = [[NSUserDefaults standardUserDefaults] stringForKey:server_url_key];
	if(!url) {
		url = DEFAULT_HOST;
		[[NSUserDefaults standardUserDefaults] setObject:url forKey:server_url_key];
	}
	return url;
}
+ (NSInteger) serverPort {
	NSInteger port = [[NSUserDefaults standardUserDefaults] integerForKey:server_port_key];
	if(!port) {
		port = DEFAULT_PORT;
		[[NSUserDefaults standardUserDefaults] setInteger:port forKey:server_port_key];
	}
	return port;
}


/**
 selector must release all 3 arguments
 
 */
- (void) performRequest:(ServerRequest *) request responseAction:(SEL)aSelector {
	if(responseAction) {
		NSLog(@"%s: Warning: overriding response action, peding request might call wrong selector",__FUNCTION__);
	}
	
	responseAction = aSelector;
	lastRequest = [request retain];
	
	if(socket) {
		NSLog(@"%s: Warning: overriding socket", __FUNCTION__);
		[socket release];
	}
	socket = [[AsyncSocket alloc] initWithDelegate:self];
	
	if(recievedData) {
		[recievedData release];
		recievedData = nil;
	}
	
	recievedData = [[NSMutableData alloc] initWithLength:0];
	
	NSError *error = nil;
	
	if(![socket connectToHost:[ServerConnection serverURL] onPort:[ServerConnection serverPort] error:&error]) {
		NSLog(@"%@", error);
	}
	
	
	
	NSString *request_str = [[request toJSON] stringByAppendingString:@"\n"];	
	[socket writeData:[request_str dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES] withTimeout:-1 tag:WriteTag];
	
}

- (void) returnResponse:(NSString *) responseString {
	[responseString retain];
	
	ServerResponse *response = [[ServerResponse fromJSON:responseString] retain];
	
	NSMethodSignature *sign = [delegate methodSignatureForSelector:responseAction];	
	NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:sign];
	
	if([invocation.methodSignature numberOfArguments] == 2+3) {
	
		invocation.target = delegate;
		invocation.selector = responseAction;
		
		//self must not be released because it was temporaly created
		[invocation setArgument:&response atIndex:2];
		[invocation setArgument:&lastRequest atIndex:3];
		[invocation setArgument:&self atIndex:4];		
		
		[invocation invoke];	
		
	}	
	
	[responseString release];
	[lastRequest release];
	lastRequest = nil;
	
}



#pragma mark -
#pragma mark AsyncSocketDelegate

-(void)onSocket:(AsyncSocket *)sock willDisconnectWithError:(NSError *)err {
	if(!err) {
		//Server conversation finished
		
		NSLog(@"%s: recieved in total %d bytes",__FUNCTION__, recievedData.length);
		NSString *responseString = [[NSString alloc] initWithData:recievedData encoding:NSASCIIStringEncoding];
		
		[sock disconnect];
		[socket release];
		socket = nil;
		
		[recievedData release];
		recievedData = nil;
		
		[self returnResponse:[responseString autorelease]];
		
	} else {
		NSLog(@"%s: %@", __FUNCTION__, [err localizedDescription]);
	
		
		[[InternetConnectionManager sharedInternetConnectionManager] serverDown];

		
		if([delegate respondsToSelector:@selector(serverRequest:didFailWithError:connection:)]) {
			[delegate serverRequest:lastRequest didFailWithError:err connection:self ];			
		}
		[lastRequest release];
		lastRequest = nil;
		
	}
}

-(void)onSocketDidDisconnect:(AsyncSocket *)sock {
	NSLog(@"%s: ", __FUNCTION__);
}





-(void)onSocket:(AsyncSocket *)sock didConnectToHost:(NSString *)host port:(UInt16)port {
	NSLog(@"%s: %@:%d", __FUNCTION__, host, port);
	
	
	
	
	//[sock disconnectAfterReadingAndWriting];
}


-(void)onSocket:(AsyncSocket *)sock didReadData:(NSData*)data withTag:(long)tag {
	//NSLog(@"%s: tag %d", __FUNCTION__, tag);
	if(tag == ReadTag) {
		[recievedData appendData:data];
		NSLog(@"%s: recieved %d bytes", __FUNCTION__, data.length);
		[sock readDataWithTimeout:-1 tag:ReadTag];		
	} else {
		NSLog(@"Read data with wrong tag");
	}
	
}


-(void)onSocket:(AsyncSocket *)sock didWriteDataWithTag:(long)tag {
	//NSLog(@"%s: tag %d", __FUNCTION__, tag);
	if(tag == WriteTag) {
		[sock readDataWithTimeout:-1 tag:ReadTag];
	} else {
		NSLog(@"Wrote data with wrong tag");
	}
	

	
}
	
- (void)dealloc {
		[socket release];
		[super dealloc];
}


@end
