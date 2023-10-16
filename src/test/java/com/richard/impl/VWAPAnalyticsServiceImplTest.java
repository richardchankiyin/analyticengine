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
import com.richard.VWAPAnalyticsResult;
import com.richard.impl.MarketDataImpl;
import com.richard.impl.VWAPAnalyticsResultImpl;
import com.richard.impl.VWAPAnalyticsServiceImpl;
import com.richard.utils.VWAPAnalyticsResultFactory;

public class VWAPAnalyticsServiceImplTest {

	MarketDataImpl mdi;
	ObjectPool<VWAPAnalyticsResult> pool;
	GenericObjectPoolConfig<VWAPAnalyticsResult> config;
	
	@Before
	public void setup() {
		config = new GenericObjectPoolConfig<>();
		config.setMinIdle(5);
		config.setMaxIdle(10);
		pool = new GenericObjectPool<VWAPAnalyticsResult>(new VWAPAnalyticsResultFactory(), config);
	}
	
	@Test
	public void testCalculateVWAP() throws Exception {
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("700");
		when(mdi.getTradedPrice()).thenReturn(330.20);
		when(mdi.getTradedVolume()).thenReturn(100L);
		when(mdi.getTimestampHHMMSS()).thenReturn("09:20:18");
		
		VWAPAnalyticsServiceImpl impl = new VWAPAnalyticsServiceImpl(pool, "700");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof VWAPAnalyticsResultImpl);
		VWAPAnalyticsResultImpl vwapresult = (VWAPAnalyticsResultImpl)result[0];
		assertTrue((330.20 * 100L)/ 100L == vwapresult.getVWAP());
		assertEquals("09:20:18", vwapresult.getTimestampHHMMSS());
		
		pool.returnObject(vwapresult);
		result = null;
		
		when(mdi.getStockCode()).thenReturn("700");
		when(mdi.getTradedPrice()).thenReturn(330.40);
		when(mdi.getTradedVolume()).thenReturn(300L);
		when(mdi.getTimestampHHMMSS()).thenReturn("09:30:01");
		
		result = impl.calculate(mdi);
		assertTrue(result.length == 1);
		assertTrue(result[0] instanceof VWAPAnalyticsResultImpl);
		vwapresult = (VWAPAnalyticsResultImpl)result[0];
		assertTrue((330.20 * 100L + 330.4 * 300L)/ (100L + 300L) == vwapresult.getVWAP());
		assertEquals("09:30:01", vwapresult.getTimestampHHMMSS());
		
		pool.returnObject(vwapresult);
		result = null;
	}

	@Test
	public void testStockCodeMismatchReturnNull() throws Exception {
		mdi = mock(MarketDataImpl.class);
		when(mdi.getStockCode()).thenReturn("700");
		when(mdi.getTradedPrice()).thenReturn(330.20);
		when(mdi.getTradedVolume()).thenReturn(100L);
		
		VWAPAnalyticsServiceImpl impl = new VWAPAnalyticsServiceImpl(pool, "9988");
		AnalyticsResult[] result = impl.calculate(mdi);
		assertNull(result);
	}
	
}
