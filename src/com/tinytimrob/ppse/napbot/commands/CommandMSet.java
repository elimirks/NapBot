package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.CommonPolyStuff;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandMSet implements ICommand
{
	Pattern NAPCHART_PATTERN = Pattern.compile("\\Qhttp\\Es?\\Q://napchart.com/\\E[a-zA-Z0-9]{5}");

	@Override
	public String[] getCommandName()
	{
		return new String[] { "mset" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return NapBot.CONFIGURATION.moderators.contains(user.getId());
	}

	@Override
	public boolean execute(User moderator, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		if (parameters.size() < 3)
		{
			return false;
		}

		String schedule = parameters.get(0);
		String napchart = parameters.get(1);
		Member matchedMember = null;

		ArrayList<String> newlist = new ArrayList<String>();
		for (int i = 2; i < parameters.size(); i++)
		{
			newlist.add(parameters.get(i));
		}
		List<Member> matchingMembers = new ArrayList<Member>();
		String match = StringUtils.join(newlist, " ");
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
		User user = matchedMember.getUser();

		if (!napchart.equals("none") && !this.NAPCHART_PATTERN.matcher(napchart).matches())
		{
			channel.sendMessage("`" + napchart + "` doesn't seem to be a valid napchart link. Check to make sure the link you pasted is not malformed.").complete();
			return true;
		}
		else
		{
			NapSchedule newSchedule = CommonPolyStuff.setSchedule(user, channel, schedule);
			if (newSchedule == null)
			{
				channel.sendMessage("`" + schedule + "` doesn't seem to be a valid sleep schedule.\n\nTry specifying one of the following sleep schedules: " + NapSchedule.getScheduleList() + ". If you don't see your schedule listed, or you are doing some variant (e.g. a modified/extended/underage version), select the closest option and then correct your nickname by hand.\n\nIf you're already on the correct schedule and just want to set your napchart, you can do that by omitting the schedule name like so:\n`" + NapBot.CONFIGURATION.messagePrefix + "set " + napchart + "`\n\nFor more detailed usage instructions, type `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
				return true;
			}
			else
			{
				if (napchart.equals("none"))
				{
					String ret = CommonPolyStuff.removeNapchart(user, channel);
					if (ret.isEmpty())
					{
						channel.sendMessage(moderator.getAsMention() + " The sleep schedule for **" + matchedMember.getEffectiveName() + "** has been set to " + newSchedule.longName + ".").complete();
					}
					else
					{
						channel.sendMessage(moderator.getAsMention() + " The sleep schedule for **" + matchedMember.getEffectiveName() + "** has been set to " + newSchedule.longName + " and their napchart has been removed.").complete();
					}
				}
				else
				{
					CommonPolyStuff.setNapchart(user, channel, napchart);
					channel.sendMessage(moderator.getAsMention() + " The sleep schedule for **" + matchedMember.getEffectiveName() + "** has been set to " + newSchedule.longName + " and their napchart has been saved.").complete();
				}
			}
		}
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "set [schedule-name] [napchart-link]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set your current sleep schedule and/or napchart link";
	}
}
