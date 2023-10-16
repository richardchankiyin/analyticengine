package com.richard.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.richard.AnalyticsResult;
import com.richard.AnalyticsResultPublisher;
import com.richard.OHLCAnalyticsResult;
import com.richard.VWAPAnalyticsResult;

/**
 * This is an implementation of analytics result publisher. Inside the class
 * there are two queues vwappublishqueue and ohlcpublishqueue
 * which receive vwapanalyticsresult and ohlcanalyticsresult
 * correspondingly.
 * 
 * @author richard
 *
 */
public class AnalyticsResultPublisherImpl implements AnalyticsResultPublisher {
	private static final Logger LOGGER = Logger.getLogger(AnalyticsResultPublisherImpl.class.getName());
	private ArrayBlockingQueue<String> vwappublishqueue;
	private ArrayBlockingQueue<String> ohlcpublishqueue;
	private boolean isStopped;
	
	public AnalyticsResultPublisherImpl() {
		this.vwappublishqueue = new ArrayBlockingQueue<>(100);
		this.ohlcpublishqueue = new ArrayBlockingQueue<>(100);
		this.isStopped = false;
	}
	
	@Override
	public void publish(AnalyticsResult[] results) {
		for (int i = 0; i < results.length; i++) {
			try {
				if (results[i] instanceof VWAPAnalyticsResult) {
					vwappublishqueue.put(results[i].toString());
				}
				if (results[i] instanceof OHLCAnalyticsResult) {
					ohlcpublishqueue.put(results[i].toString());
				}
			}
			catch (Exception e) {
				LOGGER.log(Level.SEVERE, "fail to publish", e);
			}
		}
	}
	
	private void displayVWAP(String msg) {
		if (msg != null)
			System.out.println(msg);
	}
	
	private void displayOHLC(String msg) {
		if (msg != null)
			System.out.println(msg);
	}
	
	public void stop() {
		this.isStopped = true;
	}
	
	public void run() {
		Thread vwapthread = new Thread(()->{
			while (!isStopped) {
				try {
					String output = vwappublishqueue.poll(1, TimeUnit.SECONDS);
					displayVWAP(output);
				}
				catch (Exception e) {
					
				}
			}
		});
		
		Thread ohlcthread = new Thread(()->{
			while (!isStopped) {
				try {
					String output = ohlcpublishqueue.poll(1, TimeUnit.SECONDS);
					displayOHLC(output);
				}
				catch (Exception e) {
					
				}
			}
		});
		
		vwapthread.start();
		ohlcthread.start();
	}

}
