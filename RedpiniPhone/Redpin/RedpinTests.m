//
//  RedpinTests.m
//  Redpin
//
//  Created by Pascal Brogle on 02.08.09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "RedpinTests.h"


@implementation RedpinTests

#if USE_DEPENDENT_UNIT_TEST     // all "code under test" is in the iPhone Application

- (void) testAppDelegate {
    
    id yourApplicationDelegate = [[UIApplication sharedApplication] delegate];
    STAssertNotNil(yourApplicationDelegate, @"UIAppliation failed to find the AppDelegate");
    
}

#else                           // all "code under test" must be linked into the Unit Test bundle

- (void) testMath {
    
    STAssertTrue((1+1)==2, @"Compiler isn't feeling well today :-(" );
    
}


#endif


@end
