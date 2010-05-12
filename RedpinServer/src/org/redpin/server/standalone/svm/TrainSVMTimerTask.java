package org.redpin.server.standalone.svm;

import java.util.Timer;
import java.util.TimerTask;

import org.redpin.server.standalone.util.Configuration;

public final class TrainSVMTimerTask extends TimerTask {

	public final static long DEFAULT_TRAIN_RATE = 60; //minutes
	
	public static void start(){
		TimerTask trainSVM = new TrainSVMTimerTask();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(trainSVM, 0, getTrainRate());
	}
	
	private final static long getTrainRate() {
		long rate = Configuration.SVMTrainRate;
		
		if (rate < 1.0) {
			rate = DEFAULT_TRAIN_RATE;
		}
		
		return rate * 1000 * 60;
	}
	
	@Override
	public void run() {
		SVMSupport.train();
	}

}
