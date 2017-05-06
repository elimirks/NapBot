package com.tinytimrob.ppse.napbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapRole;
import com.tinytimrob.ppse.napbot.NapSchedule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

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
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception
	{
		if (parameters.size() == 1)
		{
			String param = parameters.get(0);
			if (this.NAPCHART_PATTERN.matcher(param).matches())
			{
				this.setNapchart(user, channel, param);
				channel.sendMessage(user.getAsMention() + " Your napchart has been saved").complete();
			}
			else
			{
				NapSchedule newSchedule = this.setSchedule(user, channel, param);
				if (newSchedule == null)
				{
					channel.sendMessage("'" + param + "' doesn't seem to be a valid sleep schedule or napchart link").complete();
					return false;
				}
				else
				{
					channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName).complete();
				}
			}
			return true;
		}
		else if (parameters.size() == 2)
		{
			String schedule = parameters.get(0);
			String napchart = parameters.get(1);
			if (!this.NAPCHART_PATTERN.matcher(napchart).matches())
			{
				channel.sendMessage("'" + napchart + "' doesn't seem to be a valid napchart link").complete();
				return false;
			}
			else
			{
				NapSchedule newSchedule = this.setSchedule(user, channel, schedule);
				if (newSchedule == null)
				{
					channel.sendMessage("'" + schedule + "' doesn't seem to be a valid sleep schedule").complete();
					return false;
				}
				else
				{
					this.setNapchart(user, channel, napchart);
					channel.sendMessage(user.getAsMention() + " Your sleep schedule has been set to " + newSchedule.longName + " and your napchart has been saved").complete();
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private void setNapchart(User user, TextChannel channel, String napchart)
	{
		NapBot.userIdToNapchart.put(user.getId(), napchart);
	}

	private NapSchedule setSchedule(User user, TextChannel channel, String scheduleString)
	{
		NapSchedule schedule = null;
		for (NapSchedule schedule_ : NapSchedule.values())
		{
			if (schedule_.name.equalsIgnoreCase(scheduleString))
			{
				schedule = schedule_;
				break;
			}
		}
		if (schedule == null) // for very lazy people allow some extra strings
		{
			switch (scheduleString.toLowerCase())
			{
			case "mono":
				schedule = NapSchedule.MONOPHASIC;
				break;
			case "quad":
				schedule = NapSchedule.QUADPHASIC;
				break;
			default:
				break;
			}
		}
		if (schedule == null)
		{
			return null;
		}

		ArrayList<String> napRoles = new ArrayList<String>();
		for (NapRole role : NapRole.values())
		{
			napRoles.add(role.name.toLowerCase(Locale.ENGLISH));
		}
		Member member = channel.getGuild().getMember(user);
		List<Role> rolesToAdd = channel.getGuild().getRolesByName(schedule.role.name, true);
		ArrayList<Role> rolesToRemove = new ArrayList<Role>();
		for (Role role : member.getRoles())
		{
			if (!rolesToAdd.contains(role) && napRoles.contains(role.getName().toLowerCase(Locale.ENGLISH)))
			{
				rolesToRemove.add(role);
			}
		}
		channel.getGuild().getController().modifyMemberRoles(member, rolesToAdd, rolesToRemove).complete();

		// update the nickname
		String s = member.getEffectiveName();
		if (s.contains(" ["))
		{
			s = s.substring(0, s.lastIndexOf(" ["));
		}
		if (schedule.appendToNick)
		{
			s = s + " [" + schedule.name + "]";
		}
		try
		{
			channel.getGuild().getController().setNickname(member, s).complete();
		}
		catch (PermissionException e)
		{
			// no permission to set nickname, FGS
			e.printStackTrace();
		}
		return schedule;
	}

	@Override
	public String getCommandHelpUsage()
	{
		return "set [name] [napchart-link]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set your current sleep schedule and/or napchart link";
	}
}
