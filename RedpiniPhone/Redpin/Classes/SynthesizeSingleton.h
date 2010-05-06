/*
 *  SynthesizeSingleton.h
 *  Redpin
 * http://developer.apple.com/documentation/Cocoa/Conceptual/CocoaFundamentals/CocoaObjects/CocoaObjects.html#//apple_ref/doc/uid/TP40002974-CH4-SW32
 *
 */

#define SYNTHESIZE_SINGLETON_INTERFACE_FOR_CLASS(className) \
+ (className *) shared##className;

#define SYNTHESIZE_SINGLETON_IMPLEMENTATION_FOR_CLASS(className) \
\
static className *shared##className = nil; \
\
+ (className *)shared##className { \
	@synchronized(self) { \
		if (shared##className == nil) { \
			shared##className = [[self alloc] init]; \
		} \
	} \
	return shared##className; \
} \
\
+ (id)allocWithZone:(NSZone *)zone { \
	@synchronized(self) { \
		if (shared##className == nil) { \
			shared##className = [super allocWithZone:zone]; \
			return shared##className; \
		} \
	} \
	return nil; \
} \
\
- (id)copyWithZone:(NSZone *)zone { \
	return self; \
} \
\
- (id)retain { \
	return self; \
} \
\
- (NSUInteger)retainCount { \
	return NSUIntegerMax; \
} \
\
- (void)release { \
} \
\
- (id)autorelease { \
	return self; \
}

