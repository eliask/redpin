/**
 *  Filename: RedpinStandaloneServer.java (in org.repin.server.standalone)
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
package org.redpin.server.standalone;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.net.ConnectionHandler;
import org.redpin.server.standalone.svm.TrainSVMTimerTask;
import org.redpin.server.standalone.util.Configuration;
import org.redpin.server.standalone.util.Log;
/**
 * Basic class of the redpin standalone server
 * 
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class RedpinStandaloneServer implements Runnable {
	
	/**
	 * Does start the standalone server
	 * 
	 * @param args configuration
	 */
	public static void main(String[] args) {
		initConfig(args);
		startServer();
				
	}
	
	
	
	private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private static Logger log;
    private boolean running;
    
    /**
     * creates a new server instance
     * 
     * @param port Port number
     * @throws IOException
     */
    public RedpinStandaloneServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);		
		threadPool = Executors.newCachedThreadPool();
		log = Log.getLogger();
	}
    
    /**
     * sets the configuration according to the arguments
     * @param args configuration
     */
    public static void initConfig(String[] args) {
    	if(args.length > 0) {
    		Configuration.ServerPort = new Integer(args[0]);
    	}
    }
    
    /**
     * starts the server by creating an instance an run it
     */
    public static void startServer() {
    	try {
    		RedpinStandaloneServer server = new RedpinStandaloneServer(Configuration.ServerPort);
			new Thread(server).start();
			Runtime.getRuntime().addShutdownHook(new Thread(server.new ShutdownHandler(server)));
			TrainSVMTimerTask.start();
		} catch (IOException e) {
			Log.getLogger().log(Level.SEVERE, "Failed to start server", e);			
			e.printStackTrace();
		}
    }
  
    /**
     * waits for incoming connection, accepts and passes them to the connection handler
     */
    public void run() {
    	log.info("Started server at " + serverSocket.getInetAddress().getHostName() + ":"+ serverSocket.getLocalPort() );
    	running = true;
    	try {
    	
    		while(running) {
	    		threadPool.execute(new ConnectionHandler(serverSocket.accept()));
	    	}
    		
	    	
    	} catch (IOException ex) {
    		if(running) {
    			log.log(Level.SEVERE, "caught io execpton: "+ex.getMessage(), ex);
    		} else {
    			log.fine(ex.getMessage());
    		}
    	} 
    	
    	threadPool.shutdown();
    	log.fine("Shutting down thread pool...");
    	
    	while(!threadPool.isTerminated()) {
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
    	}
    	log.fine("Thread pool shut down");
    	
    	
    	synchronized (this) {
			notifyAll();
		}
    	System.out.println("Stopped server at " + serverSocket.getInetAddress().getHostName() + ":"+ serverSocket.getLocalPort() );
    	
    }
    
    /**
     * stops the server.
     */
    public void stopServer() {
    	log.info("Stopping server...");
    	running = false;
    	
    	
    	try {
			serverSocket.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	    	
    }
    
    
    public class ShutdownHandler implements Runnable {

    	RedpinStandaloneServer server = null;
    	
    	public ShutdownHandler(RedpinStandaloneServer server) {
			this.server = server;
		}
    	//TODO: check synchronization issue (not all messages get logged). system.out.println gets always printed
		@Override
		public void run() {
			log.fine("Control-C caught. Shutting down gracefully...");
			//System.out.println("Shutdown Handler: Shutting down gracefully...");
			server.stopServer();
			try { 
				synchronized (server) {
					log.info("Waiting for server shutdown...");
					//System.out.println("Shutdown Handler: Wait for server shutdown...");
					server.wait();
					log.info("Server shut down, now quitting...");
					
					//System.out.println("Shutdown Handler: Server shut down, now quitting");
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace(); 
			} 
		}
    	
    }
	
}
