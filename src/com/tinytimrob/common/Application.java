package com.tinytimrob.common;

/**
 * Base application class
 * @author Robert Dennington
 */
public abstract class Application
{
	/** Executes an application, dealing with crashes and odd terminations in the standard GE way
	 * @param application The application to execute
	 */
	public static void execute(Application application)
	{
		try
		{
			Logging.initialize(application);
			application.run();
		}
		catch (Throwable t)
		{
			CrashHandler report = new CrashHandler(t);
			report.crash(TerminationReason.CRASH);
		}
		finally
		{
			try
			{
				application.cleanup();
				Logging.shutdown();
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
		System.exit(0); // makes sure the program closes no matter what
	}

	/**
	 * @return The name of the application
	 */
	protected abstract String getName();

	/**
	 * @return The version of the application
	 */
	protected abstract String getVersion();

	/**
	 * Runs the logic for the application 
	 * @throws Exception If the application messes up in any way
	 */
	protected abstract void run() throws Exception;

	/**
	 * Cleans up the application
	 * @throws Exception If the cleanup fails in any way
	 */
	protected abstract void cleanup() throws Exception;
}
