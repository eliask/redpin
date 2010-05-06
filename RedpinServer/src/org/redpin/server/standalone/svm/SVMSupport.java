package org.redpin.server.standalone.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.libsvm.core.svm_predict;
import org.libsvm.core.svm_scale;
import org.libsvm.core.svm_train;
import org.redpin.server.standalone.core.Fingerprint;
import org.redpin.server.standalone.core.Location;
import org.redpin.server.standalone.core.Measurement;
import org.redpin.server.standalone.core.measure.WiFiReading;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.util.Log;


public class SVMSupport {

	final static String DIRECTORY = "";
	final static String TRAIN = DIRECTORY + "train.1";
	final static String TEST = DIRECTORY + "test.1";
	final static String TEMP = DIRECTORY + "temp";
	final static String TRAIN_SCALE = DIRECTORY + "train.1.scale";
	final static String TEST_SCALE = DIRECTORY + "test.1.scale";
	final static String RANGE = DIRECTORY + "range1";
	final static String OUT = DIRECTORY + "out";
	final static String MODEL = DIRECTORY + "train.1.scale.model";	
	final static String TRAIN_SCRIPT = DIRECTORY + "train.pl";
	final static String PREDICT_SCRIPT = DIRECTORY + "predict.pl";
	
	/**
	 * Train
	 * @param setupdata
	 */
	public static synchronized void train() 
	{
		System.out.println("Starting SVM train.");
		System.out.println("Building categories...");
		CategorizerFactory.buildCategories();
		
		List<Measurement> setupdata = HomeFactory.getMeasurementHome().getAll();
		if (setupdata == null || setupdata.size() == 0) return;
		
		System.out.println("Transforming data to the format of an SVM package...");
		transformToSVMFormat(setupdata, TRAIN, false);
		
		System.out.println("Creating the model...");
		if (!runScript(TRAIN_SCRIPT)) {
			String[] scaleargs = {"-l","-1","-u","1","-s",RANGE,TRAIN}; 		
			String[] args={"-t","0","-c","512",TRAIN_SCALE+"",TEMP+""};
			svm_train t = new svm_train();
			svm_scale s = new svm_scale();
			try {        	     	
				s.run(scaleargs, TRAIN_SCALE);
				t.run(args);
				File f = new File(TEMP);
				//lock.lock(); not needed because method is synchronized
				f.renameTo(new File(MODEL)); // change to model file shall be done atomically 
				//lock.unlock(); nod needed
			} catch (IOException e) {
				Log.getLogger().log(Level.SEVERE, "Failed to create SVM model: " + e.getMessage());
			} 
		}
		System.out.println("SVM train finished..");
	}
	
	/**
	 * Predict
	 * @param m
	 * @return
	 */
	public synchronized static String predict(final Measurement m) 
	{
		File modelfile = new File(MODEL);
		File outputfile = new File(OUT);
		Vector<Measurement> testMeasurements = new Vector<Measurement>();
		testMeasurements.add(m);
		transformToSVMFormat(testMeasurements, TEST, true);
		
		try {
			
			if(!runScript(PREDICT_SCRIPT)){
				
				String[] scaleargs = {"-r", RANGE, TEST};
				svm_scale s = new svm_scale();
				s.run(scaleargs, TEST_SCALE);
			
				String[] args={TEST_SCALE,modelfile+"",outputfile+""};
				//lock.lock();
				svm_predict.main(args);	
				//lock.unlock();
			}		
					
		} catch (FileNotFoundException e) {
			Log.getLogger().log(Level.SEVERE, "predict failed due to FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.getLogger().log(Level.SEVERE, "predict failed due to IOException: " + e.getMessage());
		}
		
		return OUT;
	}
	
	
	/**
	 * Function transforms data (measurements) to the format of an SVM package. 
	 * Each measurement is represented as a vector of real numbers. 
	 * @param data - list of measurements
	 * @param fileName - destination file name
	 */
	public synchronized static void transformToSVMFormat(final List<Measurement> data, String fileName, boolean isNew)
	{
		
		File testfile = new File(fileName); 
		try {
			BufferedWriter writertest = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(testfile)));
			Hashtable<Integer, Integer> rssis = new Hashtable<Integer, Integer>();
			Vector<Integer> sarray = new Vector<Integer>();
			for (Measurement m : data){
				Integer categoryID = -1;
				if (!isNew) {
					categoryID = getLocationCategory(m);
					if (categoryID == -1) continue;
				}
				StringBuffer line = new StringBuffer();
				line.append(categoryID);
				for (WiFiReading r : m.getWiFiReadings()) {
					if (r != null && r.getBssid() != null) {
						Integer id = CategorizerFactory.BSSIDCategorizer().GetCategoryID(r.getBssid());
						if (id != -1) {
							rssis.put(id, r.getRssi());
							sarray.add(id);
						}
					}
				}
				Collections.sort(sarray);
				for(int i = 0; i < sarray.size(); i++) {
					if (rssis.get(sarray.get(i)) != null) line.append(" " + sarray.get(i) + ":" + rssis.get(sarray.get(i)));
				}
				line.append("\n");
				writertest.write(line.toString());
				rssis.clear();
				sarray.clear();
			}
			writertest.close();
			
		} catch (FileNotFoundException e) {
			Log.getLogger().log(Level.SEVERE, "transformToSVMFormat failed due to FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.getLogger().log(Level.SEVERE, "transformToSVMFormat failed due to IOException: " + e.getMessage());
		}
	}
	
	
	/**
	 * Runs a shell script
	 * @param scriptName
	 * @return true in case of a successful run
	 */
	private static synchronized boolean runScript(String scriptName)
	{
		String command = "./" + scriptName;
    	try {
    		Process p = Runtime.getRuntime().exec(command);
    		int exitvalue = p.waitFor();
    		if (exitvalue == 0) return true;
		} catch (InterruptedException e) {
			Log.getLogger().log(Level.WARNING, "runScript failed due to InterruptedException: " + e.getMessage());
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING, "runScript failed due to IOException: " + e.getMessage());
		}
		return false;
	}
	
	
	/**
	 * Returns location category
	 * @param m
	 * @return
	 */
	private static Integer getLocationCategory(Measurement m)
	{
		if (m == null || m.getId() == null) return -1;
		
		Fingerprint f = HomeFactory.getFingerprintHome().getByMeasurementId(m.getId());
				
		if (f != null) {
			Location l = (Location) f.getLocation();
			if (l != null && l.getSymbolicID() != null) {
				return CategorizerFactory.LocationCategorizer().GetCategoryID(l.getSymbolicID());
			}
		}
		return -1;
	}
}
