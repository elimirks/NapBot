package com.tinytimrob.ppse.napbot.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.annotations.Expose;
import com.tinytimrob.common.Communicator;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapchartHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandCreate implements ICommand
{
	Pattern TIMESTAMP_PATTERN = Pattern.compile("^(([01]\\d|2[0-3]):([0-5]\\d))-(([01]\\d|2[0-3]):([0-5]\\d))$");

	public static class SleepEntry implements Comparable<SleepEntry>
	{
		public SleepEntry(int start, int end)
		{
			this.start = start;
			this.end = end;
		}

		@Expose
		public final int start;

		@Expose
		public final int end;

		@Override
		public int compareTo(SleepEntry o)
		{
			int i = Integer.compare(this.start, o.start);
			if (i == 0)
			{
				i = Integer.compare(this.end, o.end);
			}
			return i;
		}
	}

	public static class SleepEntries
	{
		@Expose
		public final ArrayList<SleepEntry> core = new ArrayList<SleepEntry>();

		@Expose
		public final ArrayList<SleepEntry> nap = new ArrayList<SleepEntry>();
	}

	public static class Payload
	{
		public final SleepEntries data_ = new SleepEntries();

		@Expose
		public String data;

		public void addSleepBlock(int start, int end)
		{
			int xend = start < end ? end : end + 1440;
			if ((xend - start) <= 90)
			{
				this.data_.nap.add(new SleepEntry(start, end));
			}
			else
			{
				this.data_.core.add(new SleepEntry(start, end));
			}
		}

		public void addSleepBlock(int startH, int startM, int endH, int endM)
		{
			this.addSleepBlock((60 * startH) + startM, (60 * endH) + endM);
		}

		@Override
		public void finalize()
		{
			Collections.sort(this.data_.core);
			Collections.sort(this.data_.nap);
			this.data = Communicator.GSON.toJson(this.data_);
		}
	}

	@Override
	public String[] getCommandName()
	{
		return new String[] { "create" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		if (parameters.size() < 1)
		{
			channel.sendMessage("You need to specify a series of time ranges to create a napchart. For example: `" + NapBot.CONFIGURATION.messagePrefix + "create 03:00-05:00 08:00-08:20 14:00-14:20 21:00-23:00`").complete();
			return true;
		}
		Payload payload = new Payload();
		for (String parameter : parameters)
		{
			Matcher matcher = this.TIMESTAMP_PATTERN.matcher(parameter);
			if (matcher.matches())
			{
				int startH = Integer.parseInt(matcher.group(2));
				int startM = Integer.parseInt(matcher.group(3));
				int endH = Integer.parseInt(matcher.group(5));
				int endM = Integer.parseInt(matcher.group(6));
				payload.addSleepBlock(startH, startM, endH, endM);
			}
			else
			{
				channel.sendMessage("`" + parameter + "` doesn't seem to be a valid time range.\n\nYou need to specify a series of time ranges to create a napchart. For example: `" + NapBot.CONFIGURATION.messagePrefix + "create 03:00-05:00 08:00-08:20 14:00-14:20 21:00-23:00`").complete();
				return true;
			}
		}
		payload.finalize();
		String napchartID = Communicator.basicJsonMessage("Generate napchart", "https://napchart.com/post", payload, String.class, false);
		String napchartURL = "";
		try
		{
			NapchartHandler.getNapchart(napchartID);
			napchartURL = " " + NapBot.CONFIGURATION.napchartUrlPrefix + napchartID;
		}
		catch (IOException e)
		{
			// yay.
		}
		channel.sendMessage("https://napchart.com/" + napchartID + napchartURL).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "create [XX:XX-YY:YY] [...]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "Create a napchart";
	}
}
