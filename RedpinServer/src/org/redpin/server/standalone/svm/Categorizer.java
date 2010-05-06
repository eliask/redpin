package org.redpin.server.standalone.svm;

import java.util.Hashtable;

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

