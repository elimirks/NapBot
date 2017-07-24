package com.tinytimrob.ppse.napbot.commands;

import java.util.List;
import java.util.regex.Pattern;
import com.tinytimrob.ppse.napbot.CommonPolyStuff;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandSet implements ICommand
{
	Pattern NAPCHART_PATTERN = Pattern.compile("\\Qhttp\\Es?\\Q://napchart.com/\\E[a-zA-Z0-9]{5}");

	@Override
	public String[] getCommandName()
	{
		return new String[] { "set" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		if (parameters.size() == 1)
		{
			String param = parameters.get(0);
			if (param.equals("none"))
			{
				String text = CommonPolyStuff.removeNapchart(user, channel);
				if (!text.isEmpty())
				{
					channel.sendMessage(user.getAsMention() + " Your napchart has been removed.").complete();
				}
				else
				{
					channel.sendMessage(user.getAsMention() + " You did not have a napchart to remove!").complete();
				}
			}
			else if (this.NAPCHART_PATTERN.matcher(param).matches())
			{
				CommonPolyStuff.setNapchart(user, channel, param);
				channel.sendMessage(user.getAsMention() + " Your napchart has been saved.").complete();
			}
			else
			{
				NapSchedule newSchedule = CommonPolyStuff.setSchedule(user, channel, param);
				if (newSchedule == null)
				{
					channel.sendMessage("I don't know what you mean by `" + NapBot.CONFIGURATION.messagePrefix + "set " + param + "`. `" + param + "` doesn't seem to be a valid sleep schedule OR a valid napchart link.\n\n**If you are trying to change your sleep schedule:**\nTry specifying one of the following schedules: " + NapSchedule.getScheduleList() + ". If you don't see your schedule listed, or you are doing some variant (e.g. a modified/extended/underage version), select the closest option and then correct your nickname by hand.\n\n**If you are trying to change your napchart:**\nCheck to make sure the link you pasted is not malformed.\n\nFor more detailed usage instructions, type `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
					return true;
				}
				else
				{
					String rnc = CommonPolyStuff.removeNapchart(user, channel);
					channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName + "." + rnc).complete();
				}
			}
			return true;
		}
		else if (parameters.size() == 2)
		{
			String schedule = parameters.get(0);
			String napchart = parameters.get(1);
			if (!napchart.equals("none") && !this.NAPCHART_PATTERN.matcher(napchart).matches())
			{
				channel.sendMessage("`" + napchart + "` doesn't seem to be a valid napchart link. Check to make sure the link you pasted is not malformed.").complete();
				return true;
			}
			else
			{
				NapSchedule newSchedule = CommonPolyStuff.setSchedule(user, channel, schedule);
				if (newSchedule == null)
				{
					channel.sendMessage("`" + schedule + "` doesn't seem to be a valid sleep schedule.\n\nTry specifying one of the following sleep schedules: " + NapSchedule.getScheduleList() + ". If you don't see your schedule listed, or you are doing some variant (e.g. a modified/extended/underage version), select the closest option and then correct your nickname by hand.\n\nIf you're already on the correct schedule and just want to set your napchart, you can do that by omitting the schedule name like so:\n`" + NapBot.CONFIGURATION.messagePrefix + "set " + napchart + "`\n\nFor more detailed usage instructions, type `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
					return true;
				}
				else
				{
					if (napchart.equals("none"))
					{
						String ret = CommonPolyStuff.removeNapchart(user, channel);
						if (ret.isEmpty())
						{
							channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName + ".").complete();
						}
						else
						{
							channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName + " and your napchart has been removed.").complete();
						}
					}
					else
					{
						CommonPolyStuff.setNapchart(user, channel, napchart);
						channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName + " and your napchart has been saved.").complete();
					}
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "set [schedule-name] [napchart-link]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set your current sleep schedule and/or napchart link";
	}
}
