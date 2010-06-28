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
	protected Hashtable<String, Integer> categoryToIdDictionary = new Hashtable<String, Integer>();
	protected Integer ID = 0;
	private Hashtable<Integer, String> idToCategoryDictionary = new Hashtable<Integer, String>();
	
	public void clear() {
		ID = 0;
		categoryToIdDictionary.clear();
		idToCategoryDictionary.clear();
	}
	
	public Integer AddCategory(String name) {
		if (categoryToIdDictionary.containsKey(name)) return categoryToIdDictionary.get(name);
		ID++;
		categoryToIdDictionary.put(name, ID);
		idToCategoryDictionary.put(ID, name);
		return ID;
	}
	
	public String GetCategory(Integer id) {
		return idToCategoryDictionary.get(id);
	}
	
	public Integer GetCategoryID(String name) {
		if (categoryToIdDictionary.containsKey(name)) return categoryToIdDictionary.get(name);
		return -1;
	}
}

