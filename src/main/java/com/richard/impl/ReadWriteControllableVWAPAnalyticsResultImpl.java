package com.richard.impl;

/**
 * This is a wrapper data object to support readonly
 * when the values are being set. Only setting readonly
 * as false to accept values resetting which can prevent
 * unnecessary programming bug violating the integrity
 * of data
 * 
 * @author richard
 *
 */
public class ReadWriteControllableVWAPAnalyticsResultImpl extends VWAPAnalyticsResultImpl {
	private boolean readonly;
	public ReadWriteControllableVWAPAnalyticsResultImpl() {
		this.readonly = false;
	}
	
	public void setValues(String stockCode, double vwap, String timestampHHMMSS) {
		if (!readonly) {
			super.setValues(stockCode, vwap, timestampHHMMSS);
			readonly = true;
		}
	}
	
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
