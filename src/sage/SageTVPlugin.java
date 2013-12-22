/*
 * SageTVPlugin.java
 *
 * Created on March 5, 2010, 3:10 PM
 *
 * Copyright 2001-2010 SageTV, LLC. All rights reserved.
 */

package sage;

/**
 * This interface should be implemented by a Java class to act as a generalized SageTV plugin
 *
 * IMPORTANT: The implementation class MUST have a single argument constructor that takes a sage.SageTVPluginRegistry object
 * as its only argument.
 */
public interface SageTVPlugin extends SageTVEventListener
{
	// This method is called when the plugin should startup
	public void start();
	
	// This method is called when the plugin should shutdown
	public void stop();

	// This method is called after plugin shutdown to free any resources used by the plugin
	public void destroy();

	// These methods are used to define any configuration settings for the plugin that should be
	// presented in the UI. If your plugin does not need configuration settings; you may simply return null or zero from these methods.

	// Returns the names of the settings for this plugin
	public String[] getConfigSettings();

	// Returns the current value of the specified setting for this plugin
	public String getConfigValue(String setting);

	// Returns the current value of the specified multichoice setting for this plugin
	public String[] getConfigValues(String setting);

	// Constants for different types of configuration values
	public static final int CONFIG_BOOL = 1;
	public static final int CONFIG_INTEGER = 2;
	public static final int CONFIG_TEXT = 3;
	public static final int CONFIG_CHOICE = 4;
	public static final int CONFIG_MULTICHOICE = 5;
	public static final int CONFIG_FILE = 6;
	public static final int CONFIG_DIRECTORY = 7;

	// Returns one of the constants above that indicates what type of value is used for a specific settings
	public int getConfigType(String setting);

	// Sets a configuration value for this plugin
	public void setConfigValue(String setting, String value);

	// Sets a configuration values for this plugin for a multiselect choice
	public void setConfigValues(String setting, String[] values);

	// For CONFIG_CHOICE settings; this returns the list of choices
	public String[] getConfigOptions(String setting);

	// Returns the help text for a configuration setting
	public String getConfigHelpText(String setting);

	// Returns the label used to present this setting to the user
	public String getConfigLabel(String setting);
	
	// Resets the configuration of this plugin
	public void resetConfig();
}
