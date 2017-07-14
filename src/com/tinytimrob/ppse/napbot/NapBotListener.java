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
		if (NapBot.terminationReason != null || event.getAuthor().getIdLong() == event.getAuthor().getJDA().getSelfUser().getIdLong())
		{
			// don't respond to messages if the bot is shutting down, or if the messages are coming the bot itself
			return;
		}
		try
		{
			if (event.isFromType(ChannelType.PRIVATE))
			{
				log.info("PRIVATE/{}: {}", event.getAuthor().getName(), event.getMessage().getRawContent());
				event.getChannel().sendMessage("I haven't been programmed to respond to private messages O_O").complete();
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
						channel.sendMessage("I don't know what you mean by `" + content + "`. If you're not sure what you're doing, try typing `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
						return;
					}
					if (!icommand.hasPermission(author))
					{
						channel.sendMessage("You don't have permission to execute that command").complete();
						return;
					}
					if (!icommand.execute(author, channel, command, split, message))
					{
						channel.sendMessage("I don't know what you mean by `" + content + "`. If you're not sure what you're doing, try typing `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
						return;
					}
				}
			}
		}
		catch (Throwable t)
		{
			event.getChannel().sendMessage("An internal error occurred and I wasn't able to process that command. Please ask <@147356941860077568> to investigate").complete();
			t.printStackTrace();
		}
	}
}
