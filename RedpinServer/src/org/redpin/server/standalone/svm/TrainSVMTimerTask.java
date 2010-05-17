/**
 *  Filename: TrainSVMTimerTask.java (in org.redpin.server.standalone.svm)
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
