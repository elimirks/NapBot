package com.tinytimrob.ppse.napbot;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.tinytimrob.common.Application;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.common.Configuration;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.common.PlatformData;
import com.tinytimrob.common.TerminationReason;
import com.tinytimrob.ppse.napbot.commands.CommandAboutSchedule;
import com.tinytimrob.ppse.napbot.commands.CommandAdaptedList;
import com.tinytimrob.ppse.napbot.commands.CommandChartList;
import com.tinytimrob.ppse.napbot.commands.CommandCreate;
import com.tinytimrob.ppse.napbot.commands.CommandGet;
import com.tinytimrob.ppse.napbot.commands.CommandHelp;
import com.tinytimrob.ppse.napbot.commands.CommandMHelp;
import com.tinytimrob.ppse.napbot.commands.CommandMSet;
import com.tinytimrob.ppse.napbot.commands.CommandMSetNick;
import com.tinytimrob.ppse.napbot.commands.CommandMemberList;
import com.tinytimrob.ppse.napbot.commands.CommandSay;
import com.tinytimrob.ppse.napbot.commands.CommandScheduleCount;
import com.tinytimrob.ppse.napbot.commands.CommandSet;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.impl.GameImpl;

/**
 * NapBot
 * @author Robert Dennington
 */
public class NapBot extends Application
{
	/** Logger */
	static Logger log = LogWrapper.getLogger();

	/** Java Discord API */
	static JDA jda = null;

	/** Whether or not the bot should shut down */
	static volatile TerminationReason terminationReason = null;

	/** Database connecton */
	public static Connection connection = null;

	/** Discord Resync ID */
	public static final int RESYNC_ID = 1;

	/** Jetty server */
	static Server SERVER;

	/**
	 * Entry point
	 * @param args Command line arguments
	 */
	public static void main(String[] args)
	{
		Application.execute(new NapBot());
	}

	@Override
	protected String getName()
	{
		return "Nap God";
	}

	@Override
	protected String getVersion()
	{
		return "0.0.3";
	}

	/** The currently loaded configuration data */
	public static NapBotConfiguration CONFIGURATION;

	@Override
	protected TerminationReason run() throws Exception
	{
		//=================================
		// Load configuration
		//=================================
		CONFIGURATION = Configuration.load(NapBotConfiguration.class);
		if (CommonUtils.isNullOrEmpty(CONFIGURATION.authToken))
		{
			log.error("You need to specify your bot's authToken in the configuration file in order for NapBot to work");
			return TerminationReason.STOP;
		}

		//=================================
		// Register commands in the order you want them shown in +help
		//=================================
		NapBotListener.register(new CommandHelp());
		NapBotListener.register(new CommandGet());
		NapBotListener.register(new CommandSet());
		NapBotListener.register(new CommandCreate());
		for (NapSchedule schedule : NapSchedule.values())
		{
			if (!schedule.totalSleep.isEmpty())
			{
				NapBotListener.register(new CommandAboutSchedule(schedule));
			}
		}
		NapBotListener.register(new CommandChartList());
		NapBotListener.register(new CommandMemberList());
		NapBotListener.register(new CommandSay());
		NapBotListener.register(new CommandMHelp());
		NapBotListener.register(new CommandMSet());
		NapBotListener.register(new CommandMSetNick());
		NapBotListener.register(new CommandScheduleCount());
		NapBotListener.register(new CommandAdaptedList());

		//=================================
		// Connect to database
		//=================================
		initializeDatabase();

		//=================================
		// Connect to Firefox
		//=================================
		NapchartHandler.init();

		//=================================
		// Start embedded Jetty server for napcharts
		//=================================
		SERVER = new Server();
		ServerConnector httpConnector = new ServerConnector(SERVER);
		httpConnector.setPort(CONFIGURATION.napchartServerPort);
		httpConnector.setName("Main");
		SERVER.addConnector(httpConnector);
		HandlerCollection handlerCollection = new HandlerCollection();
		StatisticsHandler statsHandler = new StatisticsHandler();
		statsHandler.setHandler(handlerCollection);
		SERVER.setStopTimeout(5000);
		SERVER.setHandler(statsHandler);
		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setContextPath("/");
		ServletHolder napchartServlet = new ServletHolder("default", new NapchartServlet());
		contextHandler.addServlet(napchartServlet, "/*");
		handlerCollection.addHandler(contextHandler);
		NCSARequestLog requestLog = new NCSARequestLog(new File(PlatformData.installationDirectory, "logs/requestlog-yyyy_mm_dd.request.log").getAbsolutePath());
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone("GMT");
		requestLog.setLogLatency(true);
		requestLog.setRetainDays(90);
		requestLog.setLogServer(true);
		requestLog.setPreferProxiedForAddress(true);
		SERVER.setRequestLog(requestLog);
		SERVER.start();
		HttpGenerator.setJettyVersion(this.getName().replace(" ", "") + "/" + this.getVersion().replace(" ", ""));

		//=================================
		// Connect to Discord
		//=================================
		jda = new JDABuilder(AccountType.BOT).setToken(CONFIGURATION.authToken).buildBlocking();
		jda.getPresence().setGame(new GameImpl("Type " + NapBot.CONFIGURATION.messagePrefix + "help", null, GameType.DEFAULT));
		//		jda.getSelfUser().getManager().setName(this.getName()).complete();
		jda.addEventListener(new NapBotListener());

		//=================================
		// update nap god's schedule
		//=================================
		{
			PreparedStatement ps = NapBot.connection.prepareStatement("INSERT OR REPLACE INTO napcharts (id, link) VALUES (?, ?)");
			ps.setLong(1, jda.getSelfUser().getIdLong());
			ps.setString(2, "https://napchart.com/hu3xo");
			ps.executeUpdate();
		}

		//=================================
		// Wait for shutdown
		//=================================
		while (terminationReason == null)
		{
			try
			{
				// don't waste loads of CPU
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// *shrug*
			}
		}
		return terminationReason;
	}

	@Override
	protected void cleanup() throws Exception
	{
		if (jda != null)
		{
			jda.shutdown();
		}
		if (SERVER != null)
		{
			SERVER.stop();
		}
		NapchartHandler.shutdown();
	}

	public static NapSchedule determineScheduleFromMemberName(String name)
	{
		if (!name.contains("["))
		{
			return NapSchedule.UNKNOWN;
		}
		String a = name.substring(name.lastIndexOf("[") + 1);
		if (!a.contains("]"))
		{
			return NapSchedule.UNKNOWN;
		}
		a = a.substring(0, a.lastIndexOf("]")).trim();
		// okay so now 'a' should contain this person's schedule name...
		for (NapSchedule schedule : NapSchedule.values())
		{
			if (a.toUpperCase().contains(schedule.name.toUpperCase()))
			{
				return schedule;
			}
		}
		return NapSchedule.EXPERIMENTAL;
	}

	private static boolean tableExists(String name) throws SQLException
	{
		PreparedStatement exists =
			connection.prepareStatement("SELECT name FROM sqlite_master " +
										"WHERE type = 'table' " +
										"AND name=?");
		try {
			exists.setString(1, name);
			return exists.executeQuery().next();
		} finally {
			exists.close();
		}
	}

	private static int getSchemaVersion() throws SQLException
	{
		if ( ! tableExists("meta")) {
			Statement vc = connection.createStatement();
			vc.executeUpdate("CREATE TABLE meta (version INTEGER DEFAULT 0)");
			vc.executeUpdate("INSERT INTO meta (version) VALUES (0)");
			vc.close();
			// Version 0 means a version before we tracked the version
			return 0;
		}

		Statement vq = connection.createStatement();
		try {
			ResultSet vqResult = vq.executeQuery("SELECT version FROM meta");
			// Guaranteed to exist at this point
			vqResult.next();
			log.debug(vqResult);
			return vqResult.getInt("version");
		} finally {
			vq.close();
		}
	}

	private static void setSchemaVersion(int version) throws SQLException
	{
		PreparedStatement sq =
			connection.prepareStatement("UPDATE meta SET version = ?");
		sq.setLong(1, version);
		sq.executeUpdate();
		sq.close();
	}
	
	private static void initializeDatabase() throws SQLException
	{
		connection = DriverManager.getConnection("jdbc:sqlite:napbot.db");

		int schemaVersion = getSchemaVersion();

		// If you add a new schema version, add a NONBREAKING case statement!
		// This will let the changing propogate through each version
		switch (schemaVersion) {
		case 0:
			log.debug("Updating to db version 1");
			if (tableExists("napcharts")) {
				String q = "ALTER TABLE napcharts ADD COLUMN time TIMESTAMP";
				Statement ncs = connection.createStatement();
				ncs.executeUpdate(q);
				ncs.close();
			} else {
				String q = "CREATE TABLE IF NOT EXISTS " +
					"napcharts (id TEXT PRIMARY KEY NOT NULL, " +
					"link TEXT, " +
					"time TIMESTAMP)";
				Statement ncs = connection.createStatement();
				ncs.executeUpdate(q);
				ncs.close();
			}
			// nobreak
		}

		// This should be 1 greater than the last case statement index
		setSchemaVersion(1);
	}
}
