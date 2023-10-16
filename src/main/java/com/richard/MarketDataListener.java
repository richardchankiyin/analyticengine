package com.richard;

/**
 * Listens to market data change event
 */
public interface MarketDataListener {
	void onEvent(MarketData md);
}
