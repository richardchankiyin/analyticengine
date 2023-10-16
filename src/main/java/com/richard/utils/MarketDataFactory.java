package com.richard.utils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.richard.impl.MarketDataImpl;

/**
 * MarketData object factory
 * @author richard
 *
 */
public class MarketDataFactory extends BasePooledObjectFactory<MarketDataImpl> {
	@Override
	public MarketDataImpl create() throws Exception {
		return new MarketDataImpl();
	}

	@Override
	public PooledObject<MarketDataImpl> wrap(MarketDataImpl obj) {
		return new DefaultPooledObject<MarketDataImpl>(obj);
	}
	
	@Override
    public void passivateObject(final PooledObject<MarketDataImpl> p)
        throws Exception {
		MarketDataImpl r = p.getObject();
		r.reset();	
    }
}
