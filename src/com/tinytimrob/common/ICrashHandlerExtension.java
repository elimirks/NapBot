package com.tinytimrob.common;

/** Interface for a crash handler extension
 * 
 * @author Robert Dennington
 */
public interface ICrashHandlerExtension
{
	/** Performs any special logic that is required when a crash happens
	 * 
	 * @throws Exception If something went wrong handling the crash
	 */
	void onCrash() throws Exception;
}
