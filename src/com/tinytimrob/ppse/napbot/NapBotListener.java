package com.tinytimrob.ppse.napbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import org.apache.logging.log4j.Logger;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.napbot.commands.ICommand;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class NapBotListener extends ListenerAdapter
{
	static final Logger log = LogWrapper.getLogger();
	public static LinkedHashMap<String, ICommand> commands = new LinkedHashMap<String, ICommand>();

	public static void register(ICommand command)
	{
		String[] names = command.getCommandName();
		if (names == null || names.length < 1)
		{
			throw new RuntimeException("Command must have names: " + command);
		}
		for (String name : names)
		{
			ICommand old = commands.put(name.toLowerCase(Locale.ENGLISH), command);
			if (old != null)
			{
				throw new RuntimeException("Conflicting commands: " + old + " and " + command);
			}
		}
		log.info("Registering " + command + " with names: " + Arrays.toString(names));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		try
		{
			if (event.getAuthor().getIdLong() == event.getAuthor().getJDA().getSelfUser().getIdLong())
			{
				// don't respond to your own messages you silly bot
				return;
			}
			else if (event.isFromType(ChannelType.PRIVATE))
			{
				log.info("PRIVATE/{}: {}", event.getAuthor().getName(), event.getMessage().getRawContent());
				event.getChannel().sendMessage("I haven't been programmed to respond to private messages O_O").queue();
			}
			else if (event.isFromType(ChannelType.TEXT))
			{
				Message message = event.getMessage();
				User author = event.getAuthor();
				TextChannel channel = event.getTextChannel();
				String content = message.getRawContent();
				if (content.startsWith(NapBot.CONFIGURATION.messagePrefix) && content.length() > NapBot.CONFIGURATION.messagePrefix.length())
				{
					log.info("PUBLIC/{}/{}/{}: {}", author.getName(), channel.getGuild().getName(), channel.getName(), content);
					ArrayList<String> split = new ArrayList<String>(Arrays.asList(content.substring(NapBot.CONFIGURATION.messagePrefix.length()).split(" ")));
					String command = split.remove(0);
					ICommand icommand = commands.get(command.toLowerCase(Locale.ENGLISH));
					if (icommand == null)
					{
						channel.sendMessage("Unknown command: " + NapBot.CONFIGURATION.messagePrefix + command).queue();
						return;
					}
					if (!icommand.hasPermission(author))
					{
						channel.sendMessage("You don't have permission to execute that command").queue();
						return;
					}
					if (!icommand.execute(author, channel, command, split))
					{
						channel.sendMessage("You're using that command incorrectly. Usage is as follows:\n" + NapBot.CONFIGURATION.messagePrefix + icommand.getCommandHelpUsage()).queue();
						return;
					}
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
