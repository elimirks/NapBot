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
		String currentMessage = "There are " + mlist.size() + " members on this server.\n";
		for (Member m : mlist)
		{
			String en = m.getEffectiveName();
			NapSchedule s = NapBot.determineScheduleFromMemberName(en);
			//String sn = en.contains("[") ? en.substring(0, en.lastIndexOf("[")).trim() : en;
			ArrayList<String> l = hm.get(s);
			l.add(en);
		}
		for (NapSchedule s : NapSchedule.values())
		{
			ArrayList<String> l = hm.get(s);
			Collections.sort(l, String.CASE_INSENSITIVE_ORDER);
			String MSG = "**" + s.name + "** (" + l.size() + "): " + StringUtils.join(l, ", ");
			String MSG2 = currentMessage + "\n" + MSG;
			if (MSG2.length() > 2000)
			{
				channel.sendMessage(currentMessage).complete();
				currentMessage = "-\n" + MSG;
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
