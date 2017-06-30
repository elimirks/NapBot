package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandMemberList implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "memberlist" };
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "memberlist";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "list everyone by schedule";
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		LinkedHashMap<NapSchedule, ArrayList<String>> hm = new LinkedHashMap<NapSchedule, ArrayList<String>>();
		for (NapSchedule s : NapSchedule.values())
		{
			hm.put(s, new ArrayList<String>());
		}
		List<Member> mlist = channel.getGuild().getMembers();
		int memberCount = 0;
		for (Member m : mlist)
		{
			if (!m.getUser().isBot())
			{
				String en = m.getEffectiveName();
				NapSchedule s = NapBot.determineScheduleFromMemberName(en);
				ArrayList<String> l = hm.get(s);
				String suf = " [" + s.name + "]";
				if (en.endsWith(suf))
				{
					en = en.substring(0, en.length() - suf.length());
				}
				l.add(en.replace("_", "\\_").replace("*", "\\*"));
				memberCount++;
			}
		}
		String currentMessage = "There are **" + memberCount + "** members on this server.\n";
		for (NapSchedule s : NapSchedule.values())
		{
			ArrayList<String> l = hm.get(s);
			Collections.sort(l, String.CASE_INSENSITIVE_ORDER);
			String MSG = "**" + s.name + "** (" + l.size() + "): " + StringUtils.join(l, ", ");
			String MSG2 = currentMessage + "\n---\n" + MSG;
			if (MSG2.length() > 2000)
			{
				channel.sendMessage(currentMessage).complete();
				currentMessage = ".\n---\n" + MSG;
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
