package com.tinytimrob.ppse.napbot.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
		Collections.sort(strings);
		channel.sendMessage("**There are " + strings.size() + " members who currently have napcharts set:**\n" + StringUtils.join(strings, "\n")).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "get [username]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "get someone's napchart link";
	}
}
