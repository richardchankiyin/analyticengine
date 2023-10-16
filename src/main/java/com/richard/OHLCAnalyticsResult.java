package com.richard;

public interface OHLCAnalyticsResult extends AnalyticsResult{
	String getTimestampHHMMSS();
	double getOpen();
	double getHigh();
	double getLow();
	double getClose();
	void setValues(String stockCode, double open, double high, double low, double close, String timestampHHMMSS);
}
