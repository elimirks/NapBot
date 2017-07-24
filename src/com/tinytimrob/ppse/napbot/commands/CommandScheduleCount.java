package com.tinytimrob.ppse.napbot.commands;

import java.util.LinkedHashMap;
import java.util.List;
import com.tinytimrob.ppse.napbot.CommonPolyStuff;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandScheduleCount implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "schedulecount" };
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "schedulecount";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "count the number of people on each schedule";
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		LinkedHashMap<NapSchedule, Integer> hm = new LinkedHashMap<NapSchedule, Integer>();
		for (NapSchedule s : NapSchedule.values())
		{
			hm.put(s, 0);
		}
		List<Member> mlist = channel.getGuild().getMembers();
		int memberCount = 0;
		for (Member m : mlist)
		{
			if (!m.getUser().isBot())
			{
				String en = m.getEffectiveName();
				NapSchedule s = NapBot.determineScheduleFromMemberName(en);
				if (s != null)
				{
					int l = hm.get(s);
					hm.put(s, (l + 1));
				}
				memberCount++;
			}
		}
		String currentMessage = "There are **" + memberCount + "** members on this server.\n";
		for (NapSchedule s : NapSchedule.values())
		{
			int l = hm.get(s);
			String MSG = "**" + s.name + ":** " + l + "  (" + CommonPolyStuff.formatPercentage(l, memberCount, 2) + ")";
			String MSG2 = currentMessage + "\n" + MSG;
			if (MSG2.length() > 2000)
			{
				channel.sendMessage(currentMessage).complete();
				currentMessage = ".\n" + MSG;
			}
			else
			{
				currentMessage = MSG2;
			}
		}
		if (!currentMessage.isEmpty())
		{
			channel.sendMessage(currentMessage).complete();
		}
		return true;
	}
}
