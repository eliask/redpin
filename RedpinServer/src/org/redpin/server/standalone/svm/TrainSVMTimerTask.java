package org.redpin.server.standalone.svm;

import java.util.Timer;
import java.util.TimerTask;

public final class TrainSVMTimerTask extends TimerTask {

	private final static long ONCE_PER_HOUR = 1000 * 60 * 60;
	
	public static void start(){
		TimerTask trainSVM = new TrainSVMTimerTask();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(trainSVM, 0, ONCE_PER_HOUR);
	}
	
	@Override
	public void run() {
		SVMSupport.train();
	}

}
