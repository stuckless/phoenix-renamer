/*
 * SageTVPluginRegistry.java
 *
 * Created on March 5, 2010, 3:17 PM
 *
 * Copyright 2001-2010 SageTV, LLC. All rights reserved.
 */

package sage;

/**
 * This interface is used for the single argument passed to the constructor of a SageTVPlugin implementation.
 * It is also the interface returned from the Plugin API call GetSageTVPluginRegistry() which can be used to subscribe/unsubscribe to
 * SageTV events from outside of the plugin framework.
 */
public interface SageTVPluginRegistry
{
	// Call this method to subscribe to a specific event
	public void eventSubscribe(SageTVEventListener listener, String eventName);

	// Call this method to unsubscribe from a specific event
	public void eventUnsubscribe(SageTVEventListener listener, String eventName);

	// This will post the event asynchronously to SageTV's plugin event queue and return immediately
	public void postEvent(String eventName, java.util.Map eventVars);

	// This will post the event asynchronously and return immediately; unless waitUntilDone is true,
	// and then it will not return until all the subscribed plugins have received the event.
	public void postEvent(String eventName, java.util.Map eventVars, boolean waitUntilDone);
}
