package com.tinytimrob.ppse.napbot.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandChartList implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "chartlist" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		LinkedHashMap<NapSchedule, ArrayList<String>> hm = new LinkedHashMap<NapSchedule, ArrayList<String>>();
		for (NapSchedule s : NapSchedule.values())
		{
			hm.put(s, new ArrayList<String>());
		}
		int chartcount = 0;
		PreparedStatement ps = NapBot.connection.prepareStatement("SELECT * FROM napcharts");
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			Member member = channel.getGuild().getMemberById(rs.getString("id"));
			if (member != null)
			{
				String en = member.getEffectiveName();
				NapSchedule s = NapBot.determineScheduleFromMemberName(en);
				if (s != null)
				{
					ArrayList<String> l = hm.get(s);
					String suf = " [" + s.name + "]";
					if (en.endsWith(suf))
					{
						en = en.substring(0, en.length() - suf.length());
					}
					l.add("\\* " + en.replace("_", "\\_").replace("*", "\\*") + " - <" + rs.getString("link").replace("http://", "https://") + ">");
					chartcount++;
				}
			}
		}
		String msg = "**There are " + chartcount + " members who currently have napcharts set:**\n";
		for (NapSchedule s : NapSchedule.values())
		{
			ArrayList<String> l = hm.get(s);
			Collections.sort(l, String.CASE_INSENSITIVE_ORDER);
			for (int i = 0; i < l.size(); i++)
			{
				msg = msg + "\n" + (i == 0 ? ("**" + s.name + ":**\n") : "") + l.get(i);
				if (msg.length() >= 1900)
				{
					channel.sendMessage(msg).complete();
					msg = "-";
				}
			}
		}
		if (!msg.equals("-"))
		{
			channel.sendMessage(msg).complete();
		}
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "chartlist";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "list everyone by chart";
	}
}