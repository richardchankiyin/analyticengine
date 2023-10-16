package com.richard;

public interface VWAPAnalyticsResult extends AnalyticsResult {
	String getTimestampHHMMSS();
	double getVWAP();
	void setValues(String stockCode, double vwap, String timestampHHMMSS);
}
