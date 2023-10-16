package com.richard.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.ObjectPool;

import com.richard.AnalyticsResult;
import com.richard.AnalyticsService;
import com.richard.MarketData;
import com.richard.VWAPAnalyticsResult;

/**
 * VWAP Analytics Service Implementation
 * @author richard
 *
 */
public class VWAPAnalyticsServiceImpl implements AnalyticsService{
	private static final Logger LOGGER = Logger.getLogger(VWAPAnalyticsServiceImpl.class.getName());
	
	private ObjectPool<VWAPAnalyticsResult> pool;
	private String stockCode;
	private double totalvolumemultiplyprice;
	private double totalvolume;
	private double vwap;
	private String lastupdatetimestampHHMMSS;
	
	public VWAPAnalyticsServiceImpl(ObjectPool<VWAPAnalyticsResult> pool, String stockCode) {
		this.pool = pool;
		this.stockCode = stockCode;
		this.totalvolumemultiplyprice = 0;
		this.totalvolume = 0;
		this.vwap = Double.NaN;
		this.lastupdatetimestampHHMMSS = null;
	}
	
	@Override
	public AnalyticsResult[] calculate(MarketData marketData) {
		if (stockCode.equals(marketData.getStockCode())) {
			if (marketData instanceof MarketDataImpl) {
				MarketDataImpl mdi = (MarketDataImpl)marketData;
				totalvolumemultiplyprice += mdi.getTradedVolume() 
						* mdi.getTradedPrice();
				totalvolume += mdi.getTradedVolume();
				lastupdatetimestampHHMMSS = mdi.getTimestampHHMMSS();
				if (totalvolume > 0)
					vwap = totalvolumemultiplyprice / totalvolume;
				else
					vwap = Double.NaN;
				
				try {
					VWAPAnalyticsResult result = pool.borrowObject();
					result.setValues(stockCode, vwap, lastupdatetimestampHHMMSS);
					return new AnalyticsResult[] {result};	
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "failed to create object", e);
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	public String toString() {
		return String.format("[stockCode:%s|totalvolumemultiplyprice:%s|totalvolume:%s|vwap:%s|lastupdatetimestampHHMMSS:%s]"
				, stockCode, totalvolumemultiplyprice, totalvolume, vwap, lastupdatetimestampHHMMSS); 
	}

}
