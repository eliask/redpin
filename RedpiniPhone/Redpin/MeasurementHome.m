//
//  MeasurementHome.m
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

#import "MeasurementHome.h"


@implementation MeasurementHome

static NSString *key = @"rId";

+ (Measurement *) newObjectInContext {
	return [[EntityHome sharedEntityHome] newObjectWithEntityName:[Measurement entityName]];
}

+ (Measurement *) newObject {
	return [[[Measurement alloc] initWithEntity:[NSEntityDescription entityForName:[Measurement entityName] inManagedObjectContext:[[EntityHome sharedEntityHome] managedObjectContext]] insertIntoManagedObjectContext:nil] autorelease];
}

+ (void) insertObjectInContext:(Measurement *) anObject {
	[[[EntityHome sharedEntityHome] managedObjectContext] insertObject:anObject];
}

+ (NSArray *) defaultSortDescriptors {
	return [[EntityHome sharedEntityHome] sortDescriptorWithKey:key ascending:YES];
}

+ (NSFetchedResultsController *) defaultFetchedResultsController {
	return [[EntityHome sharedEntityHome] fetchedResultsControllerForEntityName:[Measurement entityName] sortDescriptors:[self defaultSortDescriptors] predicate:nil];
}
+ (NSFetchRequest *) defaultFetchRequest {
	return [[EntityHome sharedEntityHome] fetchRequestForEntityName:[Measurement entityName] sortDescriptors:[self defaultSortDescriptors] predicate:nil];
}

@end
