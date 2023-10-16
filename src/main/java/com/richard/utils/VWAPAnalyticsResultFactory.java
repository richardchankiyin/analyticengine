package com.richard.utils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.richard.VWAPAnalyticsResult;
import com.richard.impl.ReadWriteControllableVWAPAnalyticsResultImpl;

/**
 * VWAPAnalyticsResult object factory
 * @author richard
 *
 */
public class VWAPAnalyticsResultFactory extends BasePooledObjectFactory<VWAPAnalyticsResult> {

	@Override
	public VWAPAnalyticsResult create() throws Exception {
		return new ReadWriteControllableVWAPAnalyticsResultImpl();
	}

	@Override
	public PooledObject<VWAPAnalyticsResult> wrap(VWAPAnalyticsResult obj) {
		return new DefaultPooledObject<VWAPAnalyticsResult>(obj);
	}
	
	@Override
    public void passivateObject(final PooledObject<VWAPAnalyticsResult> p)
        throws Exception {
		VWAPAnalyticsResult r = p.getObject();
		if (r instanceof ReadWriteControllableVWAPAnalyticsResultImpl) {
			((ReadWriteControllableVWAPAnalyticsResultImpl)r).setReadonly(false);
		}
    }
}
