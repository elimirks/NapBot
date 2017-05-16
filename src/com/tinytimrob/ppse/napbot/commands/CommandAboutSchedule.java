package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandAboutSchedule implements ICommand
{
	final NapSchedule schedule;

	public CommandAboutSchedule(NapSchedule schedule)
	{
		this.schedule = schedule;
	}

	@Override
	public String[] getCommandName()
	{
		return new String[] { this.schedule.name };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		ArrayList<String> info = new ArrayList<String>();
		info.add("`" + this.schedule.name + "` (" + this.schedule.longName + ") - <https://napchart.com/" + this.schedule.napchartID + "> - " + NapBot.CONFIGURATION.napchartUrlPrefix + this.schedule.napchartID);
		info.add("- **Total sleep:** " + this.schedule.totalSleep);
		info.add("- **Identification:** " + this.schedule.identification);
		info.add("- **Specification:** " + this.schedule.specification);
		info.add("- **Mechanism:** " + this.schedule.mechanism);
		info.add("- **Adaptation difficulty:** " + this.schedule.difficulty);
		info.add("- **Ideal scheduling:** " + this.schedule.scheduling);
		info.add("- **Popularity:** " + this.schedule.popularity);
		channel.sendMessage(StringUtils.join(info, "\n")).complete();
		return true;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return this.schedule.name;
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "Display info about " + this.schedule.longName;
	}
}
