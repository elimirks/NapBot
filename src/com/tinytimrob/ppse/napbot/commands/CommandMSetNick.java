package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class CommandMSetNick implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "msetnick" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return NapBot.CONFIGURATION.moderators.contains(user.getId());
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "msetnick [username] [nickname]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set nickname of user";
	}

	@Override
	public boolean execute(User moderator, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		if (parameters.size() < 2)
		{
			return false;
		}

		Member matchedMember = null;
		List<Member> matchingMembers = new ArrayList<Member>();
		String match = parameters.get(0);
		if (match.startsWith("<@!") && match.contains(">"))
		{
			// match based on @mention
			String id = match.substring(3, match.indexOf(">"));
			Member member = channel.getGuild().getMemberById(id);
			if (member != null)
			{
				matchingMembers.add(member);
			}
		}
		else if (match.startsWith("<@") && match.contains(">"))
		{
			// why do some snowflakes start with ! and some not? wtf?
			String id = match.substring(2, match.indexOf(">"));
			Member member = channel.getGuild().getMemberById(id);
			if (member != null)
			{
				matchingMembers.add(member);
			}
		}
		else
		{
			// look up the user by name. sadly we can't use the built in "effective match" because of the sleep schedules being part of nickname
			// so we're going to have to do this the old fashioned way
			for (Member member : channel.getGuild().getMembers())
			{
				String s = member.getEffectiveName();
				if (s.contains("["))
				{
					s = s.substring(0, s.lastIndexOf("[")).trim();
				}
				if (s.equalsIgnoreCase(match))
				{
					matchingMembers.add(member);
				}
				else if (member.getUser().getName().equalsIgnoreCase(match))
				{
					matchingMembers.add(member);
				}
				else if ((member.getUser().getName() + "#" + member.getUser().getDiscriminator()).equalsIgnoreCase(match))
				{
					matchingMembers.add(member);
				}
			}
		}
		if (matchingMembers.isEmpty())
		{
			channel.sendMessage("I wasn't able to find anyone called `" + match + "` on the server.").complete();
			return true;
		}
		else if (matchingMembers.size() > 1)
		{
			ArrayList<String> output = new ArrayList<String>();
			output.add("Matched multiple users called `" + match + "`. Please choose one of the following:");
			for (Member x : matchingMembers)
			{
				output.add(x.getUser().getName() + "#" + x.getUser().getDiscriminator());
			}
			channel.sendMessage(StringUtils.join(output, '\n')).complete();
			return true;
		}
		else
		{
			matchedMember = matchingMembers.get(0);
		}
		ArrayList<String> newlist = new ArrayList<String>();
		for (int i = 1; i < parameters.size(); i++)
		{
			newlist.add(parameters.get(i));
		}
		try
		{
			channel.getGuild().getController().setNickname(matchedMember, StringUtils.join(newlist, " ")).complete();
			channel.sendMessage(moderator.getAsMention() + " The nickname for **" + matchedMember.getEffectiveName() + "** has been changed.").complete();
		}
		catch (PermissionException e)
		{
			// no permission to set nickname, FGS
			e.printStackTrace();
			channel.sendMessage(moderator.getAsMention() + " The nickname for **" + matchedMember.getEffectiveName() + "** could not be changed. They probably have a higher permission level than Nap God :/").complete();
		}
		return true;
	}
}
