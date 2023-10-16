package com.richard;

/**
 * Publishes {@link AnalyticsResult} to the rest of the system
 */
public interface AnalyticsResultPublisher {
	void publish(AnalyticsResult[] results);
}
