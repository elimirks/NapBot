package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.CommonPolyStuff;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

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
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		ArrayList<String> info = new ArrayList<String>();
		info.add("`" + this.schedule.name + "` (" + this.schedule.longName + ")"); // - <https://napchart.com/" + this.schedule.napchartID + "> - " + NapBot.CONFIGURATION.napchartUrlPrefix + this.schedule.napchartID);
		if (!this.schedule.experimental.isEmpty())
		{
			info.add("-----------------------------------------------");
			info.add("_" + this.schedule.experimental + "_");
			info.add("-----------------------------------------------");
		}
		info.add("- **Total sleep:** " + this.schedule.totalSleep);
		info.add("- **Identification:** " + this.schedule.identification);
		info.add("- **Specification:** " + this.schedule.specification);
		info.add("- **Mechanism:** " + this.schedule.mechanism);
		info.add("- **Adaptation difficulty:** " + this.schedule.difficulty);
		info.add("- **Ideal scheduling:** " + this.schedule.scheduling);
		info.add("- **Popularity:** " + this.schedule.popularity);
		MessageBuilder b = new MessageBuilder();
		b.append(StringUtils.join(info, "\n"));
		MessageEmbedImpl embedimpl = new MessageEmbedImpl();
		embedimpl.setTitle("https://napchart.com/" + this.schedule.napchartID);
		embedimpl.setUrl("https://napchart.com/" + this.schedule.napchartID);
		embedimpl.setImage(new ImageInfo(NapBot.CONFIGURATION.napchartUrlPrefix + this.schedule.napchartID + "?" + NapBot.RESYNC_ID, null, 560, 560));
		embedimpl.setFields(new ArrayList<MessageEmbed.Field>());
		b.setEmbed(embedimpl);
		channel.sendMessage(b.build()).complete();
		ArrayList<String> l = new ArrayList<String>();
		List<Member> mlist = channel.getGuild().getMembers();
		String suf = " [" + this.schedule.name + "]";
		int attemptedcount = 0;
		int adaptedcount = 0;
		int membercount = 0;
		for (Member m : mlist)
		{
			if (!m.getUser().isBot())
			{
				membercount++;

				String en = m.getEffectiveName();
				NapSchedule s = NapBot.determineScheduleFromMemberName(en);
				// is this the user's current schedule?
				if (s == this.schedule)
				{
					if (en.endsWith(suf))
					{
						en = en.substring(0, en.length() - suf.length());
					}
					l.add(en.replace("_", "\\_").replace("*", "\\*"));
				}
				// did this user attempt this schedule in the past?
				int tagcount = 0;
				for (Role role : m.getRoles())
				{
					if (role.getName().equals("Attempted-" + this.schedule.name))
					{
						attemptedcount++;
						tagcount++;
					}
					else if (role.getName().equals("Adapted-" + this.schedule.name))
					{
						attemptedcount++;
						adaptedcount++;
						tagcount++;
					}
				}
				if (tagcount == 2)
				{
					attemptedcount--; // this user has both tags. a huge SIGH to whoever mistagged this person
				}
			}
		}
		Collections.sort(l, String.CASE_INSENSITIVE_ORDER);
		String msg = (l.size() == 1 ? "is **1 member**" : l.size() == 0 ? "are **no members**" : "are **" + l.size() + " members**" + " (" + CommonPolyStuff.formatPercentage(l.size(), membercount, 2) + ")");
		String currentMessage = "SCHEDULE STATISTICS:\n\nThere " + msg + " currently on the schedule " + this.schedule.name;
		if (!l.isEmpty())
		{
			currentMessage = currentMessage + ":\n" + StringUtils.join(l, ", ");
		}
		else
		{
			currentMessage = currentMessage + ".";
		}
		if (this.schedule != NapSchedule.MONOPHASIC)
		{
			currentMessage = currentMessage + "\n\n**Attempted:** " + attemptedcount + " / " + membercount + " (" + CommonPolyStuff.formatPercentage(attemptedcount, membercount, 2) + " of members)\n**Adapted:** " + adaptedcount + " / " + membercount + " (" + CommonPolyStuff.formatPercentage(adaptedcount, membercount, 2) + " of members, " + CommonPolyStuff.formatPercentage(adaptedcount, attemptedcount, 2) + " of those who attempted the schedule)";
		}
		channel.sendMessage(currentMessage).complete();
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
