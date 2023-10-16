package com.richard.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.ObjectPool;

import com.richard.AnalyticsResult;
import com.richard.AnalyticsService;
import com.richard.MarketData;
import com.richard.OHLCAnalyticsResult;

/**
 * OHLC Analytics Service Implementation
 * @author richard
 *
 */
public class OHLCAnalyticsServiceImpl implements AnalyticsService {
	private static final Logger LOGGER = Logger.getLogger(OHLCAnalyticsServiceImpl.class.getName());
	
	private ObjectPool<OHLCAnalyticsResult> pool;
	private String stockCode;
	private double open;
	private double high;
	private double low;
	private double close;
	private boolean isOpenUOrderFound;
	private boolean isCloseUOrderFound;
	private boolean isServiceComplete;
	private String lastupdatetimestampHHMMSS;
	
	public OHLCAnalyticsServiceImpl(ObjectPool<OHLCAnalyticsResult> pool, String stockCode) {
		this.pool = pool;
		this.stockCode = stockCode;
		this.open = Double.NaN;
		this.high = Double.MIN_VALUE;
		this.low = Double.MAX_VALUE;
		this.close = Double.NaN;
		this.isOpenUOrderFound = false;
		this.isCloseUOrderFound = false;
		this.isServiceComplete = false;
		this.lastupdatetimestampHHMMSS = null;
	}

	private void update(double tradedPrice, char tradeType, String timestampHHMMSS) {
		close = tradedPrice;
		lastupdatetimestampHHMMSS = timestampHHMMSS;
		
		if (tradedPrice > high) {
			high = tradedPrice;
			lastupdatetimestampHHMMSS = timestampHHMMSS;
		}
		
		if (tradedPrice < low) {
			low = tradedPrice;
			lastupdatetimestampHHMMSS = timestampHHMMSS;
		}
		
		// trade type = 'U' means auction trades
		if (tradeType == 'U') {
			if (!isOpenUOrderFound) {
				open = tradedPrice;
				lastupdatetimestampHHMMSS = timestampHHMMSS;
				isOpenUOrderFound = true;
			} else {
				if (!isCloseUOrderFound) {
					close = tradedPrice;
					lastupdatetimestampHHMMSS = timestampHHMMSS;
					isCloseUOrderFound = true;
					isServiceComplete = true;
				} else {
					// should not reach this state
					LOGGER.warning("tradedPrice: " + tradedPrice + " tradeType:" + tradeType + " timestamp:" + timestampHHMMSS + " reaches abnormal state!");
				}
				
			}
		}
	}
	
	
	@Override
	public AnalyticsResult[] calculate(MarketData marketData) {
		if (stockCode.equals(marketData.getStockCode())) {
			if (marketData instanceof MarketDataImpl) {
				MarketDataImpl mdi = (MarketDataImpl)marketData;
				double tradedPrice = mdi.getTradedPrice();
				char tradeType = mdi.getTradeType();
				String timestampHHMMSS = mdi.getTimestampHHMMSS();
				
				if (!isServiceComplete) {
					update(tradedPrice, tradeType, timestampHHMMSS);
					try {
						OHLCAnalyticsResult result = pool.borrowObject();
						result.setValues(stockCode, open, high, low, close, timestampHHMMSS);
						return new AnalyticsResult[] {result};	
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "failed to create object", e);
					}
					
				} else {
					return null;
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	public boolean isServiceComplete() {
		return isServiceComplete;
	}
	
	public String toString() {
		return String.format("[stockCode:%s|open:%s|high:%s|low:%s|close:%s|isOpenUOrderFound:%s|isCloseUOrderFound:%s|isServiceComplete:%s|lastupdatetimestampHHMMSS:%s]"
				, stockCode, open, high, low, close, isOpenUOrderFound, isCloseUOrderFound, isServiceComplete, lastupdatetimestampHHMMSS); 
	}

}
