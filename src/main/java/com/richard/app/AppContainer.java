package com.richard.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.richard.impl.AnalyticsResultPublisherImpl;
import com.richard.impl.AnalyticsServiceEndpointImpl;
import com.richard.impl.MarketDataImpl;
import com.richard.utils.MarketDataFactory;

/**
 * This is the application container demonstrating
 * the usage of multiple analytics service endpoints.
 * As this is a demo and there are predefined two
 * endpoints. However we can choose to implement
 * another AppContainer to have configuration no of
 * endpoints to be used and corresponding routing
 * mechanism
 * 
 * @author richard
 *
 */
public class AppContainer {
	private static final Logger LOGGER = Logger.getLogger(AppContainer.class.getName());
	private String marketDataFilePath;
	private ObjectPool<MarketDataImpl> marketdataobjpool;
	private GenericObjectPoolConfig<MarketDataImpl> marketdataobjpoolconfig;
	private ArrayBlockingQueue<MarketDataImpl> appQueue0;
	private ArrayBlockingQueue<MarketDataImpl> appQueue1;
	private List<ArrayBlockingQueue<MarketDataImpl>> queues;
	private AnalyticsResultPublisherImpl pub;
	private AnalyticsServiceEndpointImpl appendpoint0;
	private AnalyticsServiceEndpointImpl appendpoint1;
	private List<AnalyticsServiceEndpointImpl> appendpoints;
	private ExecutorService es;
	private boolean isRunning;
	
	public AppContainer(String marketDataFilePath, int marketdatapoolmaxidle, int marketdatapoolminindle, int queuedepth, int noofanalyticservicethreads, int analyticdatapoolmaxidle, int analyticdatapoolminidle) {
		this.marketdataobjpoolconfig = new GenericObjectPoolConfig<>();
		this.marketdataobjpoolconfig.setMaxTotal(marketdatapoolmaxidle);
		this.marketdataobjpoolconfig.setMinIdle(marketdatapoolminindle);
		this.marketdataobjpoolconfig.setMaxIdle(marketdatapoolmaxidle);
		this.marketdataobjpool = new GenericObjectPool<>(new MarketDataFactory(), marketdataobjpoolconfig);
		this.marketDataFilePath = marketDataFilePath;
		
		this.appQueue0 = new ArrayBlockingQueue<>(queuedepth);
		this.appQueue1 = new ArrayBlockingQueue<>(queuedepth);
		this.queues = new ArrayList<>(2);
		this.queues.add(appQueue0);
		this.queues.add(appQueue1);
		
		this.isRunning = false;
		this.es = Executors.newFixedThreadPool(noofanalyticservicethreads);
		this.pub = new AnalyticsResultPublisherImpl();
		
		this.appendpoint0 = new AnalyticsServiceEndpointImpl(es, pub, analyticdatapoolmaxidle, analyticdatapoolminidle);
		this.appendpoint1 = new AnalyticsServiceEndpointImpl(es, pub, analyticdatapoolmaxidle, analyticdatapoolminidle);
		this.appendpoints = new ArrayList<>(2);
		this.appendpoints.add(appendpoint0);
		this.appendpoints.add(appendpoint1);
	}
	
	private void loadMarketData() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(marketDataFilePath));
			String line = null;
			MarketDataImpl md = null;
			do {
				line = reader.readLine();
				md = (MarketDataImpl)marketdataobjpool.borrowObject();
				if (line != null) {
					// this is to make the parsing work in MarketDataImpl
					String line2 = line.replace("\\n", "\n");
					try {
						md.set(line2);
						routeToServiceQueue(md);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "not valid marketdata", e);
					}
				}
			} while (line != null);
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "failed to load market data", e);
		}
	}
	
	private void routeToServiceQueue(MarketDataImpl md) {
		String stockCode = md.getStockCode();
		int stockCodeInt = Integer.parseInt(stockCode);
		// we have two queues here and use % 2 to identify queue to process
		int routeid = stockCodeInt % 2;
		ArrayBlockingQueue<MarketDataImpl> queue = queues.get(routeid);
		try {
			queue.put(md);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "fail to put into queue", e);
		}
	}
	
	
	private void processServiceQueue() {
		Thread t1 = new Thread(()->{
			ArrayBlockingQueue<MarketDataImpl> queue = queues.get(0);
			while (isRunning) {
				try {
					MarketDataImpl md = queue.poll(1, TimeUnit.SECONDS);
					if (md != null) {
						appendpoints.get(0).onEvent(md);
						try {
							marketdataobjpool.returnObject(md);
						}
						catch (Exception e) {
							LOGGER.log(Level.SEVERE, "failed to return md object", e);
						}
					}
				}
				catch (Exception e) {
					LOGGER.log(Level.SEVERE, "failed to poll from queue", e);
				}
			}
		});
		
		Thread t2 = new Thread(()->{
			ArrayBlockingQueue<MarketDataImpl> queue = queues.get(1);
			while (isRunning) {
				try {
					MarketDataImpl md = queue.poll(1, TimeUnit.SECONDS);
					if (md != null) {
						appendpoints.get(1).onEvent(md);
						try {
							marketdataobjpool.returnObject(md);
						}
						catch (Exception e) {
							LOGGER.log(Level.SEVERE, "failed to return md object", e);
						}
					}
				}
				catch (Exception e) {
					LOGGER.log(Level.SEVERE, "failed to poll from queue", e);
				}
			}
		});
		
		t1.start();
		t2.start();
	}
	
	private void startupComponents() {
		pub.run();
	}
	
	private void shutdownComponents() {
		pub.stop();
		es.shutdown();
	}
	
	
	public void start() {
		isRunning = true;
		startupComponents();
		processServiceQueue();
		loadMarketData();
		
		// we leave a sleep time before shutting
		// down all the components
		try {
			Thread.sleep(3000);
		}
		catch (Exception e) {
			
		}
		isRunning = false;
		shutdownComponents();
	}
	
}
