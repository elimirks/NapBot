package com.tinytimrob.ppse.napbot.commands;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandSay implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "say" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return NapBot.CONFIGURATION.moderators.contains(user.getId());
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		channel.sendMessage(StringUtils.join(parameters, " ")).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "say [message ...]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "say a message";
	}
}
