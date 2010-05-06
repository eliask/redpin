// 
//  Map.m
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


#import "Map.h"
#import "MapHome.h"
#import "Location.h"

@implementation Map 

@dynamic thumbnail;
@dynamic mapName;
@dynamic image;
@dynamic mapURL;
@dynamic locations;


- (void) setImageAndCreateThumbnail:(UIImage *) aImage {
	if(self.image != aImage) {
	
		self.image = aImage;
		
		CGSize size = aImage.size;
		CGFloat ratio = 0;
		if (size.width > size.height) {
			ratio = 44.0f / size.width;
		}
		else {
			ratio = 44.0f / size.height;
		}
		CGRect rect = CGRectMake(0.0f, 0.0f, ratio * size.width, ratio * size.height);
		
		UIGraphicsBeginImageContext(rect.size);
		[aImage drawInRect:rect];
		self.thumbnail = UIGraphicsGetImageFromCurrentImageContext();
	}
	
}


- (id) proxyForJson {
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:3];
	
	if([self.rId intValue] != -1)
		[dict setObject:self.rId forKey:@"id"];
	
	if(self.mapName)
		[dict setObject:self.mapName forKey:@"mapName"];
	
	if(self.mapURL) 
		[dict setObject:self.mapURL forKey:@"mapURL"];
	
	return [dict autorelease];
}

+ (Map *) fromJSON:(NSDictionary *) dict {
	
	Map *map = [[MapHome newObject] retain];
	[map setRId:[dict objectForKey:@"id"]];
	[map setMapName:[dict objectForKey:@"mapName"]];
	[map setMapURL:[dict objectForKey:@"mapURL"]];
	
	return [map autorelease];
}


@end
