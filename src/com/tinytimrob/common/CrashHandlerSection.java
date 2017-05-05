package com.tinytimrob.common;

import java.util.ArrayList;

/** A class representing an addable subsection of a crash report
 * 
 * @author Robert Dennington
 */
public abstract class CrashHandlerSection
{
	final String name;

	/** Constructs a new crash handler section
	 * 
	 * @param name The name of this section
	 */
	public CrashHandlerSection(String name)
	{
		this.name = name;
	}

	/** Adds this section to a crash report
	 * 
	 * @param crashMessages The array list containing the crash report
	 */
	void add(ArrayList<String> crashMessages)
	{
		crashMessages.add("------------------------------------------------------------");
		crashMessages.add(this.name);
		crashMessages.add("------------------------------------------------------------");
		this.addMessages(crashMessages);
		crashMessages.add("");
	}

	/** Adds messages to this section of the crash report
	 * 
	 * @param crashMessages The array list containing the crash report so far
	 */
	public abstract void addMessages(ArrayList<String> crashMessages);
}
