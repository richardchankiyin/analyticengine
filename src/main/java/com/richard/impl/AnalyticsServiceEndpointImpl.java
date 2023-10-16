package com.richard.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.richard.AnalyticsResult;
import com.richard.AnalyticsResultPublisher;
import com.richard.AnalyticsService;
import com.richard.MarketData;
import com.richard.MarketDataListener;
import com.richard.OHLCAnalyticsResult;
import com.richard.VWAPAnalyticsResult;
import com.richard.utils.OHLCAnalyticsResultFactory;
import com.richard.utils.VWAPAnalyticsResultFactory;

/**
 * This is an implementation of MarketDataListener. The class
 * will receive incoming MarketData and perform vwap and ohlc
 * calculation/detection. Once both analytics work complete
 * the results will be published via instance passed to
 * constructor.
 * 
 * @author richard
 *
 */
public class AnalyticsServiceEndpointImpl implements MarketDataListener {
	private static final Logger LOGGER = Logger.getLogger(AnalyticsServiceEndpointImpl.class.getName());
	private Map<String,AnalyticsService> serviceMap;
	private AnalyticsResultPublisher publisher;
	private ExecutorService es;
	private ObjectPool<VWAPAnalyticsResult> vwappool;
	private GenericObjectPoolConfig<VWAPAnalyticsResult> vwapconfig;
	private ObjectPool<OHLCAnalyticsResult> ohlcpool;
	private GenericObjectPoolConfig<OHLCAnalyticsResult> ohlcconfig;
	
	public AnalyticsServiceEndpointImpl(ExecutorService es, AnalyticsResultPublisher publisher, int maxAnalyticsResultObject, int minAnalyticsResultObject) {
		this.es = es;
		this.publisher = publisher;
		this.serviceMap = new HashMap<>();
		vwapconfig = new GenericObjectPoolConfig<>();
		vwapconfig.setMaxTotal(maxAnalyticsResultObject);
		vwapconfig.setMinIdle(minAnalyticsResultObject);
		vwapconfig.setMaxIdle(maxAnalyticsResultObject);
		vwappool = new GenericObjectPool<VWAPAnalyticsResult>(new VWAPAnalyticsResultFactory(), vwapconfig);

		ohlcconfig = new GenericObjectPoolConfig<>();
		ohlcconfig.setMaxTotal(maxAnalyticsResultObject);
		ohlcconfig.setMinIdle(minAnalyticsResultObject);
		ohlcconfig.setMaxIdle(maxAnalyticsResultObject);
		ohlcpool = new GenericObjectPool<OHLCAnalyticsResult>(new OHLCAnalyticsResultFactory(), ohlcconfig);
		
	}

	@Override
	public void onEvent(MarketData md) {
		String stockCode = md.getStockCode();
		AnalyticsService service = serviceMap.computeIfAbsent(stockCode, k->new AnalyticsServiceImpl(es, new VWAPAnalyticsServiceImpl(vwappool, stockCode), new OHLCAnalyticsServiceImpl(ohlcpool, stockCode)));
		AnalyticsResult[] result = service.calculate(md);
		publisher.publish(result);
		
		
		// free AnalyticsResult objects
		if (result[0] instanceof VWAPAnalyticsResult) {
			try {
				vwappool.returnObject((VWAPAnalyticsResult)result[0]);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "failed to return VWAPAnalyticsResult obj to pool", e);
			}
		}
		
		if (result[1] instanceof OHLCAnalyticsResult) {
			try {
				ohlcpool.returnObject((OHLCAnalyticsResult)result[1]);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "failed to return OHLCAnalyticsResult obj to pool", e);
			}
		}
		result = null;
	}

}
