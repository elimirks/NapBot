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
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

public class CommandCreate implements ICommand
{
	Pattern TIMESTAMP_PATTERN = Pattern.compile("^(([01]\\d|2[0-3]):([0-5]\\d))-(([01]\\d|2[0-3]):([0-5]\\d))$");

	public static class ChartDataElement implements Comparable<ChartDataElement>
	{
		public ChartDataElement(int id, int start, int end)
		{
			this.id = id;
			this.start = start;
			this.end = end;
		}

		@Expose
		public final int id;

		@Expose
		public final int start;

		@Expose
		public final int end;

		@Expose
		public final int typeId = 0;

		@Expose
		public final String text = "";

		@Expose
		public final int lane = 1;

		@Override
		public int compareTo(ChartDataElement o)
		{
			int i = Integer.compare(this.start, o.start);
			if (i == 0)
			{
				i = Integer.compare(this.end, o.end);
			}
			return i;
		}
	}

	public static class ChartData
	{
		@Expose
		public final ArrayList<ChartDataElement> elements = new ArrayList<ChartDataElement>();

		@Expose
		public final ChartType[] types = new ChartType[] { new ChartType(0, "Sleep", "red"), new ChartType(1, "", "gray"), new ChartType(2, "", "green"), new ChartType(3, "", "blue") };

		@Expose
		public final String shape = "circle";
	}

	public static class ChartType
	{
		public ChartType(int id, String name, String style)
		{
			this.id = id;
			this.name = name;
			this.style = style;
		}

		@Expose
		public final int id;

		@Expose
		public final String name;

		@Expose
		public final String style;
	}

	public static class Payload
	{
		public Payload(PayloadInner payloadInner)
		{
			this.data = Communicator.GSON.toJson(payloadInner);
		}

		@Expose
		public final String data;
	}

	public static class PayloadInner
	{
		@Expose
		public final ChartData chartData = new ChartData();

		int elementid = 0;

		public void addSleepBlock(int start, int end)
		{
			int xend = start < end ? end : end + 1440;
			if ((xend - start) <= 90)
			{
				this.chartData.elements.add(new ChartDataElement(this.elementid, start, end));
			}
			else
			{
				this.chartData.elements.add(new ChartDataElement(this.elementid, start, end));
			}
			this.elementid++;
		}

		public void addSleepBlock(int startH, int startM, int endH, int endM)
		{
			this.addSleepBlock((60 * startH) + startM, (60 * endH) + endM);
		}

		@Override
		public void finalize()
		{
			Collections.sort(this.chartData.elements);
		}
	}

	public static class PayloadResponse
	{
		@Expose
		public String id;
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
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		if (parameters.size() < 1)
		{
			channel.sendMessage("You need to specify a series of time ranges to create a napchart. For example: `" + NapBot.CONFIGURATION.messagePrefix + "create 03:00-05:00 08:00-08:20 14:00-14:20 21:00-23:00`").complete();
			return true;
		}
		PayloadInner payloadInner = new PayloadInner();
		for (String parameter : parameters)
		{
			Matcher matcher = this.TIMESTAMP_PATTERN.matcher(parameter);
			if (matcher.matches())
			{
				int startH = Integer.parseInt(matcher.group(2));
				int startM = Integer.parseInt(matcher.group(3));
				int endH = Integer.parseInt(matcher.group(5));
				int endM = Integer.parseInt(matcher.group(6));
				payloadInner.addSleepBlock(startH, startM, endH, endM);
			}
			else
			{
				channel.sendMessage("`" + parameter + "` doesn't seem to be a valid time range.\n\nYou need to specify a series of time ranges to create a napchart. For example: `" + NapBot.CONFIGURATION.messagePrefix + "create 03:00-05:00 08:00-08:20 14:00-14:20 21:00-23:00`").complete();
				return true;
			}
		}
		payloadInner.finalize();
		Payload payload = new Payload(payloadInner);
		String napchartID = Communicator.basicJsonMessage("Generate napchart", "https://napchart.com/api/create", payload, PayloadResponse.class, false).id;
		try
		{
			NapchartHandler.getNapchart(napchartID);
		}
		catch (IOException e)
		{
			// yay.
		}
		MessageBuilder b = new MessageBuilder();
		MessageEmbedImpl embedimpl = new MessageEmbedImpl();
		embedimpl.setTitle("https://napchart.com/" + napchartID);
		embedimpl.setUrl("https://napchart.com/" + napchartID);
		embedimpl.setImage(new ImageInfo(NapBot.CONFIGURATION.napchartUrlPrefix + napchartID + "?" + NapBot.RESYNC_ID, null, 560, 560));
		embedimpl.setFields(new ArrayList<MessageEmbed.Field>());
		b.setEmbed(embedimpl);
		channel.sendMessage(b.build()).complete();
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
