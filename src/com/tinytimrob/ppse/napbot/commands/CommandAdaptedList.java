package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandAdaptedList implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "adaptedlist" };
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "adaptedlist";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "list everyone who has adapted to at least one schedule";
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		ArrayList<String> strings = new ArrayList<String>();
		List<Member> mlist = channel.getGuild().getMembers();
		for (Member m : mlist)
		{
			if (!m.getUser().isBot())
			{
				List<Role> roles = m.getRoles();
				for (Role r : roles)
				{
					if (r.getName().equalsIgnoreCase("Adapted"))
					{
						String en = m.getEffectiveName();
						strings.add(en.replace("_", "\\_").replace("*", "\\*"));
					}
				}
			}
		}
		Collections.sort(strings, String.CASE_INSENSITIVE_ORDER);
		String currentMessage = "There are **" + strings.size() + "** members on this server who have adapted to at least one schedule:\n" + StringUtils.join(strings, ", ");
		channel.sendMessage(currentMessage).complete();
		return true;
	}
}
