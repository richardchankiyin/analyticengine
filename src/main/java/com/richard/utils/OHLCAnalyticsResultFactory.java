package com.richard.utils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.richard.OHLCAnalyticsResult;
import com.richard.impl.ReadWriteControllableOHLCAnalyticsResultImpl;

/**
 * OHLCAnalyticsResult object factory
 * @author richard
 *
 */
public class OHLCAnalyticsResultFactory extends BasePooledObjectFactory<OHLCAnalyticsResult> {
	@Override
	public OHLCAnalyticsResult create() throws Exception {
		return new ReadWriteControllableOHLCAnalyticsResultImpl();
	}

	@Override
	public PooledObject<OHLCAnalyticsResult> wrap(OHLCAnalyticsResult obj) {
		return new DefaultPooledObject<OHLCAnalyticsResult>(obj);
	}
	
	@Override
    public void passivateObject(final PooledObject<OHLCAnalyticsResult> p)
        throws Exception {
		OHLCAnalyticsResult r = p.getObject();
		if (r instanceof ReadWriteControllableOHLCAnalyticsResultImpl) {
			((ReadWriteControllableOHLCAnalyticsResultImpl)r).setReadonly(false);
		}
    }
}
