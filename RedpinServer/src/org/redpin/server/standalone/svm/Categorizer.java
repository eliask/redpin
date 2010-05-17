/**
 *  Filename: Categorizer.java (in org.redpin.server.standalone.svm)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.svm;

import java.util.Hashtable;

/**
 *  
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 *
 */
public class Categorizer {
	protected Hashtable<String, Integer> dictionary = new Hashtable<String, Integer>();
	protected Integer ID = 0;
	
	public void clear() {
		ID = 0;
		dictionary.clear();
	}
	
	public Integer AddCategory(String name) {
		if (dictionary.containsKey(name)) return dictionary.get(name);
		ID++;
		dictionary.put(name, ID);
		return ID;
	}
	
	public String GetCategory(Integer id) {
		if (dictionary.contains(id)){
			for (String key : dictionary.keySet()) {
				if (dictionary.get(key).compareTo(id) == 0) return key;
			}
		}
		return null;
	}
	
	public Integer GetCategoryID(String name) {
		if (dictionary.containsKey(name)) return dictionary.get(name);
		return -1;
	}
}

