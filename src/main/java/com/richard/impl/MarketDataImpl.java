package com.richard.impl;

import com.richard.MarketData;

/**
 * This is an implementation of MarketData. The data
 * format follows below comment for marketDataContent
 * variable
 * 
 * @author richard
 *
 */
public class MarketDataImpl implements MarketData {
	
	private static final char NEWLINE = '\n';
	private static final char FIELDDELIMITER = ',';
	private static final char FIELDDELIMITER2 = '|';
	/**
	 * example:
	 *  41,1,\n1,39,161\n700,3,328.74|100|P|-|09:05:05|3\n
	 *  41,1->message size & no of symbols
	 *  
	 *  1,39,161->fields to shown which are sym, nooftxn and execution detail
	 *  700->sym value
	 *  3->nooftxn
	 *  328.74|100|P|-|09:05:05|3
	 *  ------
	 *  price
	 *         ---
	 *         tradedvol
	 *             -
	 *             tradetype
	 *                 --------
	 *                 timestamp
	 */
	private String marketDataContent;
	private boolean initialized;
	private String stockCode;
	private long noOfTxn;
	private double tradedPrice;
	private long tradedVolume;
	private char tradeType;
	private String timestampHHMMSS;

	public MarketDataImpl() {
		this.initialized = false;
	}
	
	public void set(String marketDataContent) {
		if (initialized) {
			throw new IllegalStateException("object has been initialized!");
		}
		
		this.marketDataContent = marketDataContent;
		parse();
		initialized = true;
	}
	
	private int findContentStartingPos() {
		int firstdelimiter = marketDataContent.indexOf(NEWLINE, 0);
		int seconddelimiter = marketDataContent.indexOf(NEWLINE
				, firstdelimiter + 1);
		return seconddelimiter+1;
	}
	
	private void parse() {
		// gathering stockCode
		int startingpos = findContentStartingPos();
		int endpos = marketDataContent.indexOf(FIELDDELIMITER, startingpos);
		stockCode = marketDataContent.substring(startingpos, endpos);
		
		// gathering noOfTxn
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER, startingpos);
		noOfTxn = Long.parseLong(marketDataContent, startingpos, endpos, 10);
		
		// gathering tradedPrice
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER2, startingpos);
		tradedPrice = Double.parseDouble(marketDataContent.substring(startingpos, endpos));

		// gathering tradedVolume
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER2, startingpos);
		tradedVolume = Long.parseLong(marketDataContent, startingpos, endpos, 10);
		
		// gathering tradeType
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER2, startingpos);
		tradeType = marketDataContent.charAt(startingpos);
		
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER2, startingpos);
		
		// gathering timestamp
		startingpos = endpos + 1;
		endpos = marketDataContent.indexOf(FIELDDELIMITER2, startingpos);
		timestampHHMMSS = marketDataContent.substring(startingpos, endpos);		
	}
	
	
	@Override
	public String getStockCode() {
		return initialized ? stockCode : null;
	}

	public long getNoOfTxn() {
		return initialized ? noOfTxn : -1;		
	}
	
	public double getTradedPrice() {
		return initialized ? tradedPrice : Double.NaN;
	}
	
	public long getTradedVolume() {
		return initialized ? tradedVolume : -1;
	}
	
	public char getTradeType() {
		return initialized ? tradeType : Character.MIN_VALUE;
	}
	
	public String getTimestampHHMMSS() {
		return initialized ? timestampHHMMSS : null;
	}
	
	public void reset() {
		initialized = false;
	}
	
	public boolean initialized() {
		return initialized;
	}
}
