package com.tinytimrob.ppse.napbot.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.tinytimrob.ppse.napbot.NapBot;
import net.dv8tion.jda.core.entities.Member;
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
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		ArrayList<String> strings = new ArrayList<String>();
		PreparedStatement ps = NapBot.connection.prepareStatement("SELECT * FROM napcharts");
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			Member member = channel.getGuild().getMemberById(rs.getString("id"));
			if (member != null)
			{
				strings.add(member.getEffectiveName() + " - <" + rs.getString("link").replace("http://", "https://") + ">");
			}
		}
		Collections.sort(strings, String.CASE_INSENSITIVE_ORDER);
		String msg = "**There are " + strings.size() + " members who currently have napcharts set:**";
		for (String s : strings)
		{
			msg = msg + "\n" + s;
			if (msg.length() >= 1900)
			{
				channel.sendMessage(msg).complete();
				msg = "-";
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