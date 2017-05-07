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
		TerminationReason tr = null;
		try
		{
			Logging.initialize(application);
			tr = application.run();
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
		System.exit(tr == null ? 0 : tr.code); // makes sure the program closes no matter what
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
	 * @return The reason why the running of the application ended
	 * @throws Exception If the application messes up in any way
	 */
	protected abstract TerminationReason run() throws Exception;

	/**
	 * Cleans up the application
	 * @throws Exception If the cleanup fails in any way
	 */
	protected abstract void cleanup() throws Exception;
}
