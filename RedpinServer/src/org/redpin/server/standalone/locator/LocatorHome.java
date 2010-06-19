/**
 *  Filename: LocatorHome.java (in org.redpin.server.standalone.locator)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.locator;

import org.redpin.server.standalone.svm.SVMSupport;

/**
 * Factory for {@link ILocator}
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class LocatorHome {
	
	private static ILocator locator = null;
	
	public synchronized static ILocator getLocator() {
		if(locator == null) {
			locator = new RedpinLocator();
		}
		
		if (locator instanceof RedpinLocator) {
			if (SVMSupport.isTrained()) {
				locator = new SVMLocator();		//locator is changed only once 
			}
		}
		
		return locator;
	}
	
	
	
}
