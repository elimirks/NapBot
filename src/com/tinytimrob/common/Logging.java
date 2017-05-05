package com.tinytimrob.common;

import java.io.PrintStream;
import java.util.Calendar;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Deals with initialization of Log4j2
 * @author Robert Dennington
 */
public class Logging
{
	static int minCopyrightYear = 2017; // min year
	public static Logger log = null;
	static PrintStream oldOut = System.out;
	static PrintStream oldErr = System.err;
	static Application application;

	public static void initialize(Application application)
	{
		// STORE APPLICATION FOR LATER USE
		Logging.application = application;

		// REDIRECT JAVA UTIL LOGGER TO LOG4J2 (MUST BE BEFORE ALL LOG4J2 CALLS)
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

		// STARTING CONFIGURATION
		final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
		org.apache.logging.log4j.core.config.Configuration configuration = loggerContext.getConfiguration();
		LoggerConfig rootLogger = configuration.getLoggerConfig("");
		rootLogger.setLevel(Level.ALL);

		// PATTERNS
		PatternLayout consolePattern = PatternLayout.createLayout("%d{yyyy-MM-dd HH:mm:ss} [%level] [%logger{1}]: %msg%n", configuration, null, null, true, false, null, null);
		PatternLayout logfilePattern = PatternLayout.createLayout("%d{yyyy-MM-dd HH:mm:ss} [%level] [%logger]: %msg%n", configuration, null, null, true, false, null, null);

		// LOG FILE STRINGS
		String logFilePrefix = PlatformData.installationDirectory.getAbsolutePath().replace("\\", "/") + "/logs/" + WordUtils.capitalizeFully(application.getName(), new char[] { '_', '-', ' ' }).replaceAll("_", "").replaceAll("_", "").replaceAll("-", "").replaceAll(" ", "");

		// CLIENT LOG FILE APPENDER (ROLLING)
		RollingRandomAccessFileAppender clientInfoLogFile = RollingRandomAccessFileAppender.createAppender(logFilePrefix + "-0.log", logFilePrefix + "-%i.log", null, "InfoFile", null, null, OnStartupTriggeringPolicy.createPolicy(), DefaultRolloverStrategy.createStrategy("2", "1", "min", null, configuration), logfilePattern, null, null, null, null, configuration);
		clientInfoLogFile.start();
		configuration.addAppender(clientInfoLogFile);
		rootLogger.addAppender(clientInfoLogFile, Level.INFO, null);

		// FINER DETAIL LOG FILE (REPLACED ON EACH RUN)
		RandomAccessFileAppender detailLogFile = RandomAccessFileAppender.createAppender(logFilePrefix + "-latest-fine.log", "false", "DetailFile", null, null, null, logfilePattern, null, null, null, configuration);
		detailLogFile.start();
		configuration.addAppender(detailLogFile);
		rootLogger.addAppender(detailLogFile, Level.ALL, null);

		// CONSOLE APPENDER
		ConsoleAppender console = ConsoleAppender.createAppender(consolePattern, null, "SYSTEM_OUT", "Console", null, null); // must be named "Console" to work correctly
		console.start();
		configuration.addAppender(console);
		rootLogger.addAppender(console, Level.INFO, null);

		// UPDATE LOGGERS
		loggerContext.updateLoggers();

		// REDIRECT STDOUT AND STDERR TO LOG4J2
		System.setOut(new PrintStream(new StdOutErrOutputStream(LogManager.getLogger("java.lang.System.out"), Level.INFO)));
		System.setErr(new PrintStream(new StdOutErrOutputStream(LogManager.getLogger("java.lang.System.err"), Level.ERROR)));

		// set main engine log
		log = LogManager.getLogger();

		// print opening header
		log.info("===============================================================================================================");
		log.info(" " + application.getName() + " v" + application.getVersion());
		log.info(" (c) Robert James Dennington, " + Math.max(Calendar.getInstance().get(Calendar.YEAR), minCopyrightYear));
		log.info("===============================================================================================================");
		log.debug("The system log manager is " + System.getProperty("java.util.logging.manager"));
		log.info("Install path: " + PlatformData.installationDirectory.getAbsolutePath());
		log.info("Computer name: " + PlatformData.computerName);
		log.info("Platform: " + PlatformData.platformName);
	}

	public static void shutdown()
	{
		if (log != null)
		{
			log.info("===============================================================================================================");
			log.info(" Thank you for using " + Logging.application.getName());
			log.info("===============================================================================================================");
		}
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// 
		}
		System.setOut(oldOut);
		System.setErr(oldErr);
	}

	/** Resets error stream, used on shutdown if the engine crashes in order to dump the crash reason to stderr */
	static void resetErrStreamForLog()
	{
		if (oldErr != null)
		{
			System.setErr(oldErr);
		}
	}
}
