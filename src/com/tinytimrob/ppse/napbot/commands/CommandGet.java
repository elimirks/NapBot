package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandGet implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "get" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		User matchedUser = null;
		if (parameters.size() > 1)
		{
			return false;
		}
		else if (parameters.size() == 1)
		{
			List<User> matchingUsers = new ArrayList<User>();
			String match = parameters.get(0);
			if (match.startsWith("<@") && match.contains(">"))
			{
				// match based on @mention
				String id = match.substring(match.indexOf("<@" + 2, match.indexOf(">")));
				Member member = channel.getGuild().getMemberById(id);
				if (member != null)
				{
					matchingUsers.add(member.getUser());
				}
			}
			else
			{
				// look up the user by name. sadly we can't use the built in "effective match" because of the sleep schedules being part of nickname
				// so we're going to have to do this the old fashioned way
				for (Member member : channel.getGuild().getMembers())
				{
					String s = member.getEffectiveName();
					if (s.contains(" ["))
					{
						s = s.substring(0, s.lastIndexOf(" ["));
					}

					if (s.equalsIgnoreCase(match))
					{
						matchingUsers.add(member.getUser());
					}
					else if (member.getUser().getName().equalsIgnoreCase(match))
					{
						matchingUsers.add(member.getUser());
					}
					else if ((member.getUser().getName() + "#" + member.getUser().getDiscriminator()).equalsIgnoreCase(match))
					{
						matchingUsers.add(member.getUser());
					}
				}
			}

			// temp
			System.out.println("matched " + matchingUsers.size());
			for (User x : matchingUsers)
			{
				System.out.println(x.getName() + "#" + x.getDiscriminator());
			}

			if (matchingUsers.isEmpty())
			{
				channel.sendMessage("I wasn't able to find anyone called '" + match + "' on the server.").complete();
				return true;
			}
			else if (matchingUsers.size() > 1)
			{
				ArrayList<String> output = new ArrayList<String>();
				output.add("Matched multiple users called '" + match + "'. Please choose one of the following:");
				for (User x : matchingUsers)
				{
					output.add(x.getName() + "#" + x.getDiscriminator());
				}
				channel.sendMessage(StringUtils.join(output, '\n')).complete();
				return true;
			}
			else
			{
				matchedUser = matchingUsers.get(0);
			}
		}
		else
		{
			matchedUser = user;
		}
		String napchartLocation = NapBot.userIdToNapchart.get(matchedUser.getId());
		if (napchartLocation == null)
		{
			channel.sendMessage("There is no napchart available for **" + matchedUser.getName() + "#" + matchedUser.getDiscriminator() + "**").complete();
		}
		else
		{
			channel.sendMessage("Napchart for **" + matchedUser.getName() + "#" + matchedUser.getDiscriminator() + "**: " + napchartLocation).complete();
		}
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "get [name]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "get someone's napchart link";
	}
}
