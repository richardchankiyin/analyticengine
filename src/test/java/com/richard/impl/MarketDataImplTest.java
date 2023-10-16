package com.richard.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richard.impl.MarketDataImpl;

public class MarketDataImplTest {

	@Test
	public void testObjectCreatedNotInitialized() {
		MarketDataImpl impl = new MarketDataImpl();
		assertFalse(impl.initialized());		
	}
	
	@Test
	public void testObjectValuesReturnedIfNotInitialized() {
		MarketDataImpl impl = new MarketDataImpl();
		assertNull(impl.getStockCode());
		assertTrue(-1L == impl.getNoOfTxn());
		assertTrue(Double.isNaN(impl.getTradedPrice()));
		assertTrue(-1L == impl.getTradedVolume());
		assertTrue(Character.MIN_VALUE == impl.getTradeType());
		assertNull(impl.getTimestampHHMMSS());
	}
	
	@Test
	public void testSetValuesAndInitialized() {
		final String data = "41,1,\n1,39,161\n700,3,328.74|100|P|-|09:05:05|3\n";
		MarketDataImpl impl = new MarketDataImpl();
		impl.set(data);
		assertTrue(impl.initialized());
		assertEquals("700", impl.getStockCode());
		assertTrue(3L == impl.getNoOfTxn());
		assertTrue(328.74 == impl.getTradedPrice());
		assertTrue(100L == impl.getTradedVolume());
		assertTrue('P' == impl.getTradeType());
		assertEquals("09:05:05", impl.getTimestampHHMMSS());
	}

	@Test
	public void testResetToUninitialized() {
		final String data = "41,1,\n1,39,161\n700,3,328.74|100|P|-|09:05:05|3\n";
		MarketDataImpl impl = new MarketDataImpl();
		impl.set(data);
		assertTrue(impl.initialized());
		impl.reset();
		assertFalse(impl.initialized());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetValuesOnInitializedObjectThrowingException() {
		final String data = "41,1,\n1,39,161\n700,3,328.74|100|P|-|09:05:05|3\n";
		MarketDataImpl impl = new MarketDataImpl();
		impl.set(data);
		assertTrue(impl.initialized());
		impl.set(data);
		fail("should not reach here");
	}
	
	@Test
	public void testSetValuesResetAndThenSetAnotherValues() {
		String data = "41,1,\n1,39,161\n700,3,328.74|100|P|-|09:05:05|3\n";
		MarketDataImpl impl = new MarketDataImpl();
		impl.set(data);
		assertTrue(impl.initialized());
		assertEquals("700", impl.getStockCode());
		assertTrue(3L == impl.getNoOfTxn());
		assertTrue(328.74 == impl.getTradedPrice());
		assertTrue(100L == impl.getTradedVolume());
		assertTrue('P' == impl.getTradeType());
		assertEquals("09:05:05", impl.getTimestampHHMMSS());
		impl.reset();
		assertFalse(impl.initialized());
		data = "48,1,\n1,39,161\n2800,5095,18.45|2000| |A|15:59:43|5095\n";		
		impl.set(data);
		assertTrue(impl.initialized());
		assertEquals("2800", impl.getStockCode());
		assertTrue(5095L == impl.getNoOfTxn());
		assertTrue(18.45 == impl.getTradedPrice());
		assertTrue(2000L == impl.getTradedVolume());
		assertTrue(' ' == impl.getTradeType());
		assertEquals("15:59:43", impl.getTimestampHHMMSS());
		impl.reset();
		assertFalse(impl.initialized());
	}
}
