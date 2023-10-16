package com.richard.impl;

import com.richard.OHLCAnalyticsResult;

/**
 * OHLC Analytics Result
 * @author richard
 *
 */
public class OHLCAnalyticsResultImpl implements OHLCAnalyticsResult {

	private String stockCode;
	private String timestampHHMMSS;
	private double open = -1;
	private double high = -1;
	private double low = Double.MAX_VALUE;
	private double close = -1;
	
	public OHLCAnalyticsResultImpl(String stockCode) {
		this.stockCode = stockCode;
	}

	public OHLCAnalyticsResultImpl() {
		
	}
	
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	
	public void setOpen(double open) {
		this.open = open;
	}
	
	public void setClose(double close) {
		this.close = close;
	}
	
	public void setHigh(double high) {
		this.high = high;
	}
	
	public void setLow(double low) {
		this.low = low;
	}
	
	public void setTimestampHHMMSS(String timestampHHMMSS) {
		this.timestampHHMMSS = timestampHHMMSS;
	}
	
	@Override
	public String getTimestampHHMMSS() {
		return timestampHHMMSS;
	}

	@Override
	public double getOpen() {
		return open;
	}

	@Override
	public double getHigh() {
		return high;
	}

	@Override
	public double getLow() {
		return low;
	}

	@Override
	public double getClose() {
		return close;
	}

	@Override
	public String getStockCode() {
		return stockCode;
	}

	@Override
	public void setValues(String stockCode, double open, double high, double low, double close, String timestampHHMMSS) {
		setStockCode(stockCode);
		setOpen(open);
		setHigh(high);
		setLow(low);
		setClose(close);
		setTimestampHHMMSS(timestampHHMMSS);
	}
	
	public String toString() {
		return String.format("[OHLCAnalyticsResultImpl--stockCode:%s|timestampHHMMSS:%s|open:%s|high:%s|low:%s|close:%s]", stockCode, timestampHHMMSS, open, high, low, close); 
	}

}
