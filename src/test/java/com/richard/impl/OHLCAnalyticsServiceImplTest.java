package com.richard.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;

import com.richard.AnalyticsResult;
import com.richard.OHLCAnalyticsResult;
import com.richard.impl.MarketDataImpl;
import com.richard.impl.OHLCAnalyticsResultImpl;
import com.richard.impl.OHLCAnalyticsServiceImpl;
import com.richard.utils.OHLCAnalyticsResultFactory;


public class OHLCAnalyticsServiceImplTest {

	MarketDataImpl mdi;
	ObjectPool<OHLCAnalyticsResult> pool;
	GenericObjectPoolConfig<OHLCAnalyticsResult> config;
	
	@Before
	public void setup() {
		config = new GenericObjectPoolConfig<>();
		config.setMinIdle(5);
		config.setMaxIdle(10);
		pool = new GenericObjectPool<OHLCAnalyticsResult>(new OHLCAnalyticsResultFactory(), config);
	}
	
	@Test
	public void testPreOpenTradesShowingHighAndLowCloseWithoutOpen() throws Exception {
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(132.6);
		when(mdi.getTradeType()).thenReturn('D');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:00:00");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "3690");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		OHLCAnalyticsResultImpl ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(Double.isNaN(ohlcresult.getOpen()));
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(132.6 == ohlcresult.getLow());
		assertTrue(132.6 == ohlcresult.getClose());
		assertEquals("09:00:00", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(132d);
		when(mdi.getTradeType()).thenReturn('P');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:05:01");
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(Double.isNaN(ohlcresult.getOpen()));
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(132d == ohlcresult.getLow());
		assertTrue(132d == ohlcresult.getClose());
		assertEquals("09:05:01", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
	}
	
	@Test
	public void testOpenUOrder() throws Exception{
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(131.8);
		when(mdi.getTradeType()).thenReturn('U');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:20:18");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "3690");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		OHLCAnalyticsResultImpl ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(131.8 == ohlcresult.getOpen());
		assertTrue(131.8 == ohlcresult.getHigh());
		assertTrue(131.8 == ohlcresult.getLow());
		assertTrue(131.8 == ohlcresult.getClose());
		assertEquals("09:20:18", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
	}

	@Test
	public void testOpenUAndCloseUOrder() throws Exception{
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(131.8, 129.9);
		when(mdi.getTradeType()).thenReturn('U');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:20:18","16:08:24");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "3690");
		AnalyticsResult[] result = impl.calculate(mdi); //perform open processing
		OHLCAnalyticsResultImpl ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi); //perform close processing
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(131.8 == ohlcresult.getOpen());
		assertTrue(131.8 == ohlcresult.getHigh());
		assertTrue(129.9 == ohlcresult.getLow());
		assertTrue(129.9 == ohlcresult.getClose());
		assertEquals("16:08:24", ohlcresult.getTimestampHHMMSS());
		assertTrue(impl.isServiceComplete());
		
		pool.returnObject(ohlcresult);
		result = null;
	}
	
	@Test
	public void testServiceCompleteReturnNull() throws Exception {
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(131.8, 129.9);
		when(mdi.getTradeType()).thenReturn('U');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:20:18","16:08:24");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "3690");
		AnalyticsResult[] result = impl.calculate(mdi); //perform open processing
		OHLCAnalyticsResultImpl ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi); //perform close processing
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		
		assertTrue(impl.isServiceComplete());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(129.8);
		when(mdi.getTradeType()).thenReturn('U');
		when(mdi.getTimestampHHMMSS()).thenReturn("16:08:24");

		result = impl.calculate(mdi);
		assertNull(result);
	}
	
	@Test
	public void testGoThroughWholeCycle() throws Exception{
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(132.6, 132d, 131.8, 131.9,129.9);
		when(mdi.getTradeType()).thenReturn(Character.valueOf('D'),Character.valueOf('P'),Character.valueOf('U'),Character.valueOf(' '),Character.valueOf('U'));
		when(mdi.getTimestampHHMMSS()).thenReturn("09:00:00","09:05:01","09:20:18","09:30:00","16:08:24");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "3690");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		OHLCAnalyticsResultImpl ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(Double.isNaN(ohlcresult.getOpen()));
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(132.6 == ohlcresult.getLow());
		assertTrue(132.6 == ohlcresult.getClose());
		assertEquals("09:00:00", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(Double.isNaN(ohlcresult.getOpen()));
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(132d == ohlcresult.getLow());
		assertTrue(132d == ohlcresult.getClose());
		assertEquals("09:05:01", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(131.8 == ohlcresult.getOpen());
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(131.8 == ohlcresult.getLow());
		assertTrue(131.8 == ohlcresult.getClose());
		assertEquals("09:20:18", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(131.8 == ohlcresult.getOpen());
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(131.8 == ohlcresult.getLow());
		assertTrue(131.9 == ohlcresult.getClose());
		assertEquals("09:30:00", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
		
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof OHLCAnalyticsResultImpl);
		ohlcresult = (OHLCAnalyticsResultImpl)result[0];
		assertTrue(131.8 == ohlcresult.getOpen());
		assertTrue(132.6 == ohlcresult.getHigh());
		assertTrue(129.9 == ohlcresult.getLow());
		assertTrue(129.9 == ohlcresult.getClose());
		assertEquals("16:08:24", ohlcresult.getTimestampHHMMSS());
		
		pool.returnObject(ohlcresult);
		result = null;
	}
	
	@Test
	public void testStockCodeMismatchReturnNull() throws Exception {
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("3690");
		when(mdi.getTradedPrice()).thenReturn(131.8);
		when(mdi.getTradeType()).thenReturn('U');
		when(mdi.getTimestampHHMMSS()).thenReturn("09:20:18");
		
		OHLCAnalyticsServiceImpl impl = new OHLCAnalyticsServiceImpl(pool, "9618");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertNull(result);
	}
	
}
