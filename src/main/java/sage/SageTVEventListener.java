/*
 * SageTVEventListener.java
 *
 * Created on March 5, 2010, 3:13 PM
 *
 * Copyright 2001-2010 SageTV, LLC. All rights reserved.
 */

package sage;

/**
 * Interface definition for implementation classes that listen for events from the SageTV core
 */
public interface SageTVEventListener
{
	// This is a callback method invoked from the SageTV core for any events the listener has subscribed to
	// See the sage.SageTVPluginRegistry interface definition for details regarding subscribing and unsubscribing to events.
	// The eventName will be a predefined String which indicates the event type
	// The eventVars will be a Map of variables specific to the event information. This Map should NOT be modified.
	// The keys to the eventVars Map will generally be Strings; but this may change in the future and plugins that submit events
	// are not required to follow that rule.
	public void sageEvent(String eventName, java.util.Map eventVars);
}
