package com.tinytimrob.ppse.napbot;

import com.google.gson.annotations.Expose;

/**
 * Configuration settings + defaults
 * @author Robert Dennington
 */
public class NapBotConfiguration
{
	@Expose
	public String authToken = "";

	@Expose
	public String messagePrefix = "+";

	@Expose
	public String napchartUrlPrefix = "";

	@Expose
	public int napchartServerPort = 19991;
}
