package com.richard.impl;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.richard.AnalyticsResult;
import com.richard.AnalyticsService;
import com.richard.MarketData;

/**
 * This is an implementation of AnalyticsService. It shows the decorator
 * pattern and two analytics works (VWAP and OHLC) can be found.
 * @author richard
 *
 */
public class AnalyticsServiceImpl implements AnalyticsService {
	
	private static final Logger LOGGER = Logger.getLogger(AnalyticsServiceImpl.class.getName());
	private ExecutorService es;
	private VWAPAnalyticsServiceImpl vwapservice;
	private OHLCAnalyticsServiceImpl ohlcservice;
	public AnalyticsServiceImpl(ExecutorService es, VWAPAnalyticsServiceImpl vwapservice, OHLCAnalyticsServiceImpl ohlcservice) {
		this.es = es;
		this.vwapservice = vwapservice;
		this.ohlcservice = ohlcservice;
		
	}
	
	
	@Override
	public AnalyticsResult[] calculate(MarketData marketData) {
		Future<AnalyticsResult[]> vwapresult = es.submit(new Callable<AnalyticsResult[]>() {
			public AnalyticsResult[] call() throws Exception {
				return vwapservice.calculate(marketData);
			}
		});
		
		
		Future<AnalyticsResult[]> ohlcresult = es.submit(new Callable<AnalyticsResult[]>() {
			public AnalyticsResult[] call() throws Exception {
				return ohlcservice.calculate(marketData);
			}
		});
		
		

		try {
			AnalyticsResult[] vr = vwapresult.get();
			AnalyticsResult[] or = ohlcresult.get();
			AnalyticsResult[] result = new AnalyticsResult[vr.length + or.length];
			for (int i = 0; i < vr.length; i++) {
				result[i]=vr[i];
			}
			for (int j = 0; j < or.length; j++) {
				result[vr.length+j]=or[j];
			}
			
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "failed to get result", e);
		}
		
		return null;
	}

}
