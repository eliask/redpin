//
//  RedpinTests.h
//  Redpin
//
//  Created by Pascal Brogle on 02.08.09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//
//  See Also: http://developer.apple.com/iphone/library/documentation/Xcode/Conceptual/iphone_development/135-Unit_Testing_Applications/unit_testing_applications.html

//  Dependent unit tests mean unit test code depends on an application to be injected into.
//  Setting this to 0 means the unit test code is designed to be linked into an independent executable.


#import <SenTestingKit/SenTestingKit.h>
#import <UIKit/UIKit.h>
//#import "application_headers" as required


@interface RedpinTests : SenTestCase {

	- (void) testFail {
		STFail(@"Must fail to succeed.");
	}
	
	- (void) testAppDelegate {
		id app_delegate = [[UIApplication sharedApplication] delegate];
		STAssertNotNil(app_delegate, @"Cannot find the application delegate.");
	}
}


@end
