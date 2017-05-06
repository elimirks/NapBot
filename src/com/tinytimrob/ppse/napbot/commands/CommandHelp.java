package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapBotListener;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandHelp implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "help", "h", "?" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		if (parameters.size() > 0)
		{
			return false;
		}
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<ICommand> commands = new ArrayList<ICommand>();
		for (ICommand icc : NapBotListener.commands.values())
		{
			if (!commands.contains(icc))
			{
				commands.add(icc); // this workaround is required since the commands get listed once in the original hashmap for each alias
				String usage = icc.getCommandHelpUsage();
				String desc = icc.getCommandHelpDescription();
				if (usage != null && !usage.isEmpty())
				{
					String string = NapBot.CONFIGURATION.messagePrefix + usage;
					if (desc != null && !desc.isEmpty())
					{
						string = string + " - " + desc;
					}
					output.add(string);
				}
			}
		}
		channel.sendMessage("--- NapBot help ---\n" + StringUtils.join(output, '\n')).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "help";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "Show the help";
	}
}
