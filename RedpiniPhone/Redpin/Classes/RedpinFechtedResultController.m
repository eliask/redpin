//
//  RedpinFechtedResultController.m
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


#import "RedpinFechtedResultController.h"


@implementation RedpinFechtedResultController


- (id)initWithFetchRequest:(NSFetchRequest *)fetchRequest managedObjectContext:(NSManagedObjectContext *)context sectionNameKeyPath:(NSString *)sectionNameKeyPath cacheName:(NSString *)name {
	if((self = [super initWithFetchRequest:fetchRequest managedObjectContext:context sectionNameKeyPath:sectionNameKeyPath cacheName:name])) {
		[super setDelegate:self];		
	}
	
	return self;
}

- (id) init {
	if((self = [super init])) {
		[super setDelegate:self];
	}
	
	return self;
}


- (void)setDelegate:(id)aDelegate {
	NSLog(@"add delegate %@", aDelegate);	
	[self addDelegate:aDelegate];	

}




- (void) addDelegate: (id <NSFetchedResultsControllerDelegate>) aDelegate {

	if(!delegateList) {		
		delegateList = [[NSMutableArray alloc] initWithCapacity:2];		
	}
	
	if([delegateList containsObject:aDelegate]) {
		return;
	}
	
	[delegateList addObject:aDelegate];
	
}

#pragma mark -
#pragma mark NSFetchedResultsControllerDelegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
	for(id<NSFetchedResultsControllerDelegate> aDelegate in delegateList) {
		if([(NSObject *)aDelegate respondsToSelector:@selector(controllerWillChangeContent:)]) {
			[aDelegate controllerWillChangeContent:controller];		
		}
	}
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
	for(id<NSFetchedResultsControllerDelegate> aDelegate in delegateList) {
		if([(NSObject *)aDelegate respondsToSelector:@selector(controller:didChangeObject:atIndexPath:forChangeType:newIndexPath:)]) {
			[aDelegate controller:controller didChangeObject:anObject atIndexPath:indexPath forChangeType:type newIndexPath:newIndexPath];		
		}
	}
}


- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id <NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
	for(id<NSFetchedResultsControllerDelegate> aDelegate in delegateList) {
		if([(NSObject *)aDelegate respondsToSelector:@selector(controller:didChangeSection:sectionInfo:atIndex:forChangeType:)]) {
			[aDelegate controller:controller didChangeSection:sectionInfo atIndex:sectionIndex forChangeType:type];		
		}
	}
	
}



- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
	for(id<NSFetchedResultsControllerDelegate> aDelegate in delegateList) {
		if([(NSObject *)aDelegate respondsToSelector:@selector(controllerDidChangeContent:)]) {
			[aDelegate controllerDidChangeContent:controller];		
		}
	}
}

#pragma mark -


- (void)dealloc {
	[delegateList release];
    [super dealloc];
}


@end
