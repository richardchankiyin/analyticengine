package com.richard.impl;

import com.richard.VWAPAnalyticsResult;

/**
 * VWAP Analytics Result
 * @author richard
 *
 */
public class VWAPAnalyticsResultImpl implements VWAPAnalyticsResult{
	
	private String stockCode;
	private String timestampHHMMSS;
	private double vwap;
	public VWAPAnalyticsResultImpl(String stockCode) {
		this.stockCode = stockCode;
	}
	
	public VWAPAnalyticsResultImpl() {
		
	}
	
	public void setValues(String stockCode, double vwap, String timestampHHMMSS) {
		setStockCode(stockCode);
		setVWAP(vwap);
		setTimestampHHMMSS(timestampHHMMSS);
	}
	
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Override
	public String getStockCode() {
		return stockCode;
	}

	@Override
	public String getTimestampHHMMSS() {
		return timestampHHMMSS;
	}

	public void setTimestampHHMMSS(String timestampHHMMSS) {
		this.timestampHHMMSS = timestampHHMMSS;
	}

	public void setVWAP(double vwap) {
		this.vwap = vwap;
	}
	
	@Override
	public double getVWAP() {
		return vwap;
	}
	
	public String toString() {
		return String.format(
				"[VWAPAnalyticsResultImpl--stockCode:%s|timestampHHMMSS:%s|vwap:%s]", stockCode, timestampHHMMSS, vwap); 
	}
}
