package com.tinytimrob.ppse.napbot.commands;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.tinytimrob.ppse.napbot.CommonPolyStuff;
import com.tinytimrob.ppse.napbot.NapBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandMSetTimestamp implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "msettimestamp" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return NapBot.CONFIGURATION.moderators.contains(user.getId());
	}

	@Override
	public boolean execute(User moderator, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		if (parameters.size() != 3)
		{
			return false;
		}

		Member matchedMember = CommonPolyStuff.findMemberMatch(channel, parameters.get(0));

		if (matchedMember == null)
		{
			return true;
		}

		String    timestring = parameters.get(1) + " " + parameters.get(2);
		Timestamp timestamp;

		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date             parsedDate = dateFormat.parse(timestring);

			// Fucks with SQLite formatting.. I don't think we have any users that old anyway!
			if (parsedDate.getTime() < 0)
			{
				throw new Exception();
			}

			timestamp = new java.sql.Timestamp(parsedDate.getTime());
		}
		catch(Exception e)
		{
			channel.sendMessage(moderator.getAsMention() +
								" Bad timestamp: " + timestring).complete();
			return true;
		}

		User user = matchedMember.getUser();
		CommonPolyStuff.setNapchartTimestamp(user, timestamp);

		channel.sendMessage(moderator.getAsMention() +
							" The sleep schedule timestamp for **" +
							matchedMember.getEffectiveName() +
							"** has been set to " +
							timestamp).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "msettimestamp [username] [timestamp, e.g. 2017-08-30 20:06:58]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set someone's napchart start timestamp";
	}
}
