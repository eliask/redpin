//
//  Fingerprint.h
//  Redpin
//
//  Created by Pascal Brogle on 06.05.10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <CoreData/CoreData.h>
#import "DBEntity.h"

@class Location;
@class Measurement;

@interface Fingerprint :  DBEntity  
{
}

@property (nonatomic, retain) Location * location;
@property (nonatomic, retain) Measurement * measurement;

- (id) proxyForJson;
+ (Fingerprint *) fromJSON:(NSDictionary *) dict;


@end



