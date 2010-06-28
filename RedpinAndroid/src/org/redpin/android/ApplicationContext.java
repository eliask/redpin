/**
 *  Filename: ApplicationContext.java (in org.repin.android)
 *  This file is part of the Redpin project.
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
 *  (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.android;

import android.content.Context;

/**
 *  ApplicationContext is used to get a static reference to the android application {@link Context}
 *  
 *  @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class ApplicationContext {
	private static Context applicationContext;

	/**
	 * 
	 * @param context Application {@link Context}
	 */
	public static void init(Context context) {
		synchronized (ApplicationContext.class) {
			applicationContext = context;
			ApplicationContext.class.notifyAll();
		}
		
	}
	
	/**
	 * 
	 * @return application {@link Context} or throws IllegalStateException in case the {@link Context} is not yet available
	 */
	public static Context get() throws IllegalStateException {
		//prevent monitor locking after initialization
		if(applicationContext == null) {
			
			synchronized (ApplicationContext.class) {
				try {
					if(applicationContext == null) {
						ApplicationContext.class.wait();
					}
				} catch (InterruptedException e) {
				}		
			}
			if(applicationContext == null) {
				throw new IllegalStateException("ApplicationContext was not initialised"); 
			}
		}		
		
		return applicationContext;
	}
}
