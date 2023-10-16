package com.richard;

/**
 * Performs intensive analytics calculation received from {@link MarketDataListener}
 * Feel free to implement any simple calculation you like. 
 */
public interface AnalyticsService {	
	AnalyticsResult[] calculate(MarketData marketData);
}
