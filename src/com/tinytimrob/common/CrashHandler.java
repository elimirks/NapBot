package com.tinytimrob.common;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

/** A class representing a crash report
 * 
 * @author Robert Dennington
 */
public class CrashHandler
{
	static Logger log = LogWrapper.getLogger();
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
	static Random rand = new Random();
	static ArrayList<CrashHandlerSection> globalCrashSections = new ArrayList<CrashHandlerSection>();
	ArrayList<CrashHandlerSection> relevantSections = new ArrayList<CrashHandlerSection>();
	static ArrayList<ICrashHandlerExtension> extensions = new ArrayList<ICrashHandlerExtension>();
	final Throwable throwable;

	/**
	 * Creates a new crash handler for a throwable
	 * @param throwable The throwable to create a crash handler for
	 */
	public CrashHandler(Throwable throwable)
	{
		this.throwable = throwable;
	}

	/**
	 * Add a global section to the crash handler. This should be used sparingly
	 * @param section The section to add globally
	 */
	public static void addGlobalSection(CrashHandlerSection section)
	{
		if (section != null)
		{
			globalCrashSections.add(section);
		}
	}

	/**
	 * Re
	 * @param extension
	 */
	public static void registerExtension(ICrashHandlerExtension extension)
	{
		if (extension != null)
		{
			extensions.add(extension);
		}
	}

	/**
	 * Writes the crash report to the log/console and to a separate file on disk and then terminates the JVM
	 * @param terminationReason The reason that should be reported for termination (typically {@link TerminationReason#CRASH})
	 */
	public void crash(TerminationReason terminationReason)
	{
		for (ICrashHandlerExtension extension : extensions)
		{
			try
			{
				extension.onCrash();
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}

		// write crash report to disk
		File userDir = PlatformData.installationDirectory;
		if (userDir == null)
		{
			userDir = new File(".");
		}
		File crashReportFile = new File(userDir, "crash-reports/crash-" + dateFormatter.format(new Date()) + ".log");

		// ArrayList of the crash report
		ArrayList<String> crashMessages = new ArrayList<String>();
		crashMessages.add("============================================================================");
		crashMessages.add("APPLICATION CRASH REPORT");
		crashMessages.add(" ~~ " + this.getSillyMessage());
		crashMessages.add("============================================================================");
		crashMessages.add("");

		// get stack trace
		StringWriter sw = new StringWriter();
		this.throwable.printStackTrace(new PrintWriter(sw));
		for (String s : sw.toString().split("\\r?\\n", -1))
		{
			crashMessages.add(s);
		}
		crashMessages.add("");

		// print all subsection data
		for (CrashHandlerSection section : this.relevantSections)
		{
			try
			{
				section.add(crashMessages);
			}
			catch (Throwable th)
			{
				th.printStackTrace();
			}
		}
		for (CrashHandlerSection section : globalCrashSections)
		{
			try
			{
				section.add(crashMessages);
			}
			catch (Throwable th)
			{
				th.printStackTrace();
			}
		}

		// dump java thread data
		for (String s : DebugHandler.getThreadDebugData())
		{
			crashMessages.add(s);
		}

		// write crash report to log file
		for (String message : crashMessages)
		{
			log.error(message);
		}

		try
		{
			FileUtils.writeLines(crashReportFile, crashMessages);
			log.error("");
			log.error("This crash report has been written to " + crashReportFile.getAbsolutePath());
			log.error("");

			try
			{
				Thread.sleep(100);
			}
			catch (Throwable t)
			{
			}

			Logging.resetErrStreamForLog();
			System.err.println("##!## APPCRASH ##!## " + crashReportFile.getAbsolutePath());
			System.exit(terminationReason == null ? -1 : terminationReason.code);
		}
		catch (Throwable e)
		{
			log.error("");
			log.error("Crash report could not be saved");
			log.error("");
			e.printStackTrace();

			try
			{
				Thread.sleep(100);
			}
			catch (Throwable t)
			{
			}

			Logging.resetErrStreamForLog();
			System.err.println("##!## APPCRASH ##?## Could not be saved");
			System.exit(terminationReason == null ? -2 : terminationReason.code);
		}
	}

	/**
	 * Generates a random silly message to put at the top of the crash report
	 * @return A silly message
	 */
	String getSillyMessage()
	{
		String[] remarks = new String[] { "Crash splash, hooray!", "A wild CRASH appeared!", "Has anyone heard of bug tracking?", "I blame Java!", "This crash definitely wasn\'t Rob\'s fault...", "Maybe we could blame someone else for this one?", "Report this crash on the bug tracker, you silly moo!", "Is it about my crash?", "Spent three years searching for a crash... got SOMEWHERE!", "Sorry, I got distracted :/", "Well, crap.", "Well, everyone hates me, so I'll just blow up...", "SUDDENLY, BOOM!", "CRASH REPORT would like to battle!", "The fingers you have used to play this game are too fat. To obtain a special typing wand, please mash the keyboard with your palm now." };
		return remarks[rand.nextInt(remarks.length)];
	}
}
