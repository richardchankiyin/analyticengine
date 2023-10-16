package com.richard.impl;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.richard.impl.AnalyticsResultPublisherImpl;
import com.richard.impl.AnalyticsServiceEndpointImpl;
import com.richard.impl.MarketDataImpl;


public class AnalyticsServiceEndpointImplTest {

	private AnalyticsServiceEndpointImpl impl;
	private AnalyticsResultPublisherImpl pub;
	private ExecutorService es;
	
	@Before
	public void setup() {
		es = Executors.newFixedThreadPool(2);
		pub = new AnalyticsResultPublisherImpl();
		pub.run();
		impl = new AnalyticsServiceEndpointImpl(es, pub, 10, 10);
	}
	
	@Test
	public void testSampleRun() {
		MarketDataImpl md1 = new MarketDataImpl();
		md1.set("40,1,\n1,39,161\n3690,1,132.6|50|D|-|09:00:00|1\n");
		MarketDataImpl md2 = new MarketDataImpl();
		md2.set("40,1,\n1,39,161\n9618,1,137.6|24|D|-|09:00:00|1\n");
		
		impl.onEvent(md1);
		impl.onEvent(md2);
		
		assertTrue(md1.initialized());
		assertTrue(md2.initialized());
		
		md1.reset();
		md2.reset();
		
		assertFalse(md1.initialized());
		assertFalse(md2.initialized());
		
		md1.set("43,1,\n1,39,161\n27,1,52.426|130200|P|-|09:00:00|1\n");
		md2.set("42,1,\n1,39,161\n27,2,52.425|86800|P|-|09:00:00|2\n");
		
		impl.onEvent(md1);
		impl.onEvent(md2);
		
		assertTrue(md1.initialized());
		assertTrue(md2.initialized());
	}

	@After
	public void teardown() {
		pub.stop();
		es.shutdown();
	}
}
