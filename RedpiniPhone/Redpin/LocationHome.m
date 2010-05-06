//
//  LocationHome.m
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

#import "LocationHome.h"


@implementation LocationHome


static NSString *key = @"symbolicID";

+ (Location *) newObjectInContext {
	return [[EntityHome sharedEntityHome] newObjectWithEntityName:[Location entityName]];
}

+ (Location *) newObject {
	return [[[Location alloc] initWithEntity:[NSEntityDescription entityForName:[Location entityName] inManagedObjectContext:[[EntityHome sharedEntityHome] managedObjectContext]] insertIntoManagedObjectContext:nil] autorelease];
}

+ (Location *) getLocationByRemoteId:(NSNumber *) rId {
	if(rId) {
		NSPredicate *predicate = [NSPredicate predicateWithFormat:@"rId == %@", rId];
		NSFetchRequest *request = [[[EntityHome sharedEntityHome] fetchRequestForEntityName:[Location entityName] sortDescriptors:[self defaultSortDescriptors] predicate:predicate] retain];
		
		NSError *error = nil;
		NSArray *result = [[[EntityHome sharedEntityHome] managedObjectContext]  executeFetchRequest:request error:&error];
		if(!result) {
			NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
			return nil;
		} else {
			[request release];			
			if([result count] > 0) {				
				return [result objectAtIndex:0];
			} 
		}		
	}
	
	return nil;	
}

+ (void) insertObjectInContext:(Location *) anObject {
	[[[EntityHome sharedEntityHome] managedObjectContext] insertObject:anObject];
}

+ (NSArray *) defaultSortDescriptors {
	return [[EntityHome sharedEntityHome] sortDescriptorWithKey:key ascending:YES];
}

+ (NSFetchedResultsController *) defaultFetchedResultsController {
	return [[EntityHome sharedEntityHome] fetchedResultsControllerForEntityName:[Location entityName] sortDescriptors:[self defaultSortDescriptors] predicate:nil];
}

+ (NSFetchedResultsController *) filteredFechtedResultControllerWithMap: (Map *) map {
	NSLog(@"filtered result controller with map %@", [map mapName]);
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"map == %@", map];
	return [[EntityHome sharedEntityHome] fetchedResultsControllerForEntityName:[Location entityName] sortDescriptors:[self defaultSortDescriptors] predicate:predicate];
}

+ (NSFetchRequest *) defaultFetchRequest {
	return [[EntityHome sharedEntityHome] fetchRequestForEntityName:[Location entityName] sortDescriptors:[self defaultSortDescriptors] predicate:nil];
}

@end
