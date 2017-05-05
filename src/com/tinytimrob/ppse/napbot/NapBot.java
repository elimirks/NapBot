package com.tinytimrob.ppse.napbot;

import org.apache.logging.log4j.Logger;
import com.tinytimrob.common.Application;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.common.Configuration;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.napbot.commands.CommandHelp;
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
	static volatile boolean shuttingDown = false;

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
		return "NapBot";
	}

	@Override
	protected String getVersion()
	{
		return "0.0.1";
	}

	/** The currently loaded configuration data */
	public static NapBotConfiguration CONFIGURATION;

	@Override
	protected void run() throws Exception
	{
		//=================================
		// Load JDA
		//=================================
		CONFIGURATION = Configuration.load(NapBotConfiguration.class);
		if (CommonUtils.isNullOrEmpty(CONFIGURATION.authToken))
		{
			log.error("You need to specify your bot's authToken in the configuration file in order for NapBot to work");
			return;
		}

		//=================================
		// Register commands in the order you want them shown in +help
		//=================================
		NapBotListener.register(new CommandHelp());

		//=================================
		// Connect to Discord
		//=================================
		jda = new JDABuilder(AccountType.BOT).setToken(CONFIGURATION.authToken).buildBlocking();
		jda.getPresence().setGame(new GameImpl("Type +help", null, GameType.DEFAULT));
		jda.addEventListener(new NapBotListener());

		//=================================
		// Wait for shutdown
		//=================================
		while (!shuttingDown)
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
	}

	@Override
	protected void cleanup() throws Exception
	{
		if (jda != null)
		{
			jda.shutdown();
		}
	}
}
