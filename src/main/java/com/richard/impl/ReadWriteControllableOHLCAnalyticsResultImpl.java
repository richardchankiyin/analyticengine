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
public class ReadWriteControllableOHLCAnalyticsResultImpl extends OHLCAnalyticsResultImpl {
	private boolean readonly;
	public ReadWriteControllableOHLCAnalyticsResultImpl() {
		this.readonly = false;
	}
	
	public void setValues(String stockCode, double open, double high, double low, double close, String timestampHHMMSS) {
		if (!readonly) {
			super.setValues(stockCode, open, high, low, close, timestampHHMMSS);
			readonly = true;
		}
	}
	
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
