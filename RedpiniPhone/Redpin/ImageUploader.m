//
//  ImageUploader.m
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

#import "ImageUploader.h"
#import "ActivityIndicator.h"
#import "ServerConnection.h"


@implementation ImageUploader

@synthesize delegate;

// reciever needs to free generated data
- (NSData *)generatePostDataForData:(NSData *)uploadData {

    NSString *post = [NSString stringWithFormat:@"--redpin\r\nContent-Disposition: form-data; name=\"uploadfile\"; filename=\"redpinfile\"\r\nContent-Type: application/octet-stream\r\nContent-Length: %d\r\n\r\n", [uploadData length]];
    
    NSData *postHeaderData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];    
    NSMutableData *postData = [[NSMutableData alloc] initWithLength:[postHeaderData length] ];
    [postData setData:postHeaderData];    
    [postData appendData: uploadData];
    
    [postData appendData: [@"\r\n--redpin--\r\n" dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES]];
    
    return postData;
}

- (void) uploadImage: (UIImage *) image delegate:(id<ImageUploaderDelegate>) aDelegate; {
	
	self.delegate = aDelegate;
	
    NSData *postData = [self generatePostDataForData: UIImagePNGRepresentation(image)];
    NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
    

    NSMutableURLRequest *uploadRequest = [[[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@:%d/",[ServerConnection serverURL], [ServerConnection serverPort]]] cachePolicy: NSURLRequestReloadIgnoringLocalCacheData timeoutInterval: 30 ] autorelease];
    [uploadRequest setHTTPMethod:@"POST"];
    [uploadRequest setValue:@"multipart/form-data; boundary=redpin" forHTTPHeaderField:@"Content-Type"];
	[uploadRequest setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [uploadRequest setHTTPBody:postData];
	[postData release];
	
    connection=[[NSURLConnection alloc] initWithRequest:uploadRequest delegate:self];
	
    if (connection) {
		receivedData=[[NSMutableData data] retain];
    } else  {
		if([delegate respondsToSelector:@selector(imageUploader:didFailWithError:)]) {
			[delegate imageUploader:self didFailWithError:[[NSError alloc] initWithDomain:@"ImageUploader NSURLConnection" code:1 userInfo:[[[NSDictionary alloc] initWithObjectsAndKeys: @"Connection couldn't be created", NSErrorFailingURLStringKey,nil] autorelease]]];
		}		
    }
	
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)aConnection didReceiveData:(NSData *)data {
	[receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)aConnection  didFailWithError:(NSError *)error {
	[connection release];
	connection = nil;
	
	[receivedData release];
		
	if([delegate respondsToSelector:@selector(imageUploader:didFailWithError:)]) {
		[delegate imageUploader:self didFailWithError:error];
	}
	
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSErrorFailingURLStringKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	
	NSString *url = [[NSString alloc] initWithData:receivedData encoding:NSASCIIStringEncoding];
	NSLog(@"url: %@", url);
	
	if([delegate respondsToSelector:@selector(imageUploader:didUploadImageWithURL:)]) {
		[delegate imageUploader:self didUploadImageWithURL:[url autorelease]];
	}
	
	[receivedData release];	
}

+ (UIImage *) downloadImage: (NSString *) url {
	
	url = [[url stringByReplacingOccurrencesOfString:@"{HOST}" withString:[ServerConnection serverURL]] stringByReplacingOccurrencesOfString:@"{PORT}" withString:[[NSNumber numberWithInt:[ServerConnection serverPort]] stringValue]];
	NSLog(@"Loading URL: %@", url);			
	UIImage *image = [UIImage imageWithData: [NSData dataWithContentsOfURL: [NSURL URLWithString: url]]];	
		
	return image;
}

- (void)dealloc {
	[connection release];
    [super dealloc];
}
	


@end
