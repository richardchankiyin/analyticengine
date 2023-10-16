package com.richard.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.richard.AnalyticsResult;
import com.richard.MarketData;
import com.richard.OHLCAnalyticsResult;
import com.richard.VWAPAnalyticsResult;
import com.richard.impl.AnalyticsServiceImpl;
import com.richard.impl.OHLCAnalyticsServiceImpl;
import com.richard.impl.VWAPAnalyticsServiceImpl;

public class AnalyticsServiceImplTest {

	private OHLCAnalyticsServiceImpl ohlcservice;
	private VWAPAnalyticsServiceImpl vwapservice;
	private OHLCAnalyticsResult ohlcresult;
	private VWAPAnalyticsResult vwapresult;

	private AnalyticsServiceImpl impl;
	private ExecutorService es;
	private MarketData md;
	@Before
	public void setup() {
		es = Executors.newFixedThreadPool(2);
		vwapservice = mock(VWAPAnalyticsServiceImpl.class);
		ohlcservice = mock(OHLCAnalyticsServiceImpl.class);
		vwapresult = mock(VWAPAnalyticsResult.class);
		ohlcresult = mock(OHLCAnalyticsResult.class);
		md = mock(MarketData.class);
		impl = new AnalyticsServiceImpl(es, vwapservice, ohlcservice);
	}
	
	@Test
	public void testCalculateWithResultsCombined() {
		when(vwapresult.getStockCode()).thenReturn("700");
		when(vwapresult.getTimestampHHMMSS()).thenReturn("09:00:00");
		when(vwapresult.getVWAP()).thenReturn(320.8);
		when(ohlcresult.getStockCode()).thenReturn("700");
		when(ohlcresult.getTimestampHHMMSS()).thenReturn("09:00:00");
		when(ohlcresult.getHigh()).thenReturn(320.8);
		when(ohlcresult.getLow()).thenReturn(320.6);
		when(ohlcresult.getOpen()).thenReturn(Double.NaN);
		when(ohlcresult.getClose()).thenReturn(320.8);
		
		when(vwapservice.calculate(md)).thenReturn(new AnalyticsResult[] {vwapresult});
		when(ohlcservice.calculate(md)).thenReturn(new AnalyticsResult[] {ohlcresult});
		
		AnalyticsResult[] result = impl.calculate(md);
		assertTrue(2 == result.length);
		assertTrue(result[0] instanceof VWAPAnalyticsResult);
		assertTrue(result[1] instanceof OHLCAnalyticsResult);
		
		VWAPAnalyticsResult vwapresultActual = (VWAPAnalyticsResult)result[0];
		assertEquals("700", vwapresultActual.getStockCode());
		assertEquals("09:00:00", vwapresultActual.getTimestampHHMMSS());
		assertTrue(320.8 == vwapresultActual.getVWAP());
		OHLCAnalyticsResult ohlcresultActual = (OHLCAnalyticsResult)result[1];
		assertEquals("700", ohlcresultActual.getStockCode());
		assertEquals("09:00:00", ohlcresultActual.getTimestampHHMMSS());
		assertTrue(Double.isNaN(ohlcresultActual.getOpen()));
		assertTrue(320.8 == ohlcresultActual.getHigh());
		assertTrue(320.6 == ohlcresultActual.getLow());
		assertTrue(320.8 == ohlcresultActual.getClose());		
	}

}
