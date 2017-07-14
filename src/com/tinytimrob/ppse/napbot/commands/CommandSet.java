package com.tinytimrob.ppse.napbot.commands;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapRole;
import com.tinytimrob.ppse.napbot.NapSchedule;
import com.tinytimrob.ppse.napbot.NapchartHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
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
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		if (parameters.size() == 1)
		{
			String param = parameters.get(0);
			if (param.equals("none"))
			{
				String text = this.removeNapchart(user, channel);
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
				this.setNapchart(user, channel, param);
				channel.sendMessage(user.getAsMention() + " Your napchart has been saved.").complete();
			}
			else
			{
				NapSchedule newSchedule = this.setSchedule(user, channel, param);
				if (newSchedule == null)
				{
					channel.sendMessage("I don't know what you mean by `" + NapBot.CONFIGURATION.messagePrefix + "set " + param + "`. `" + param + "` doesn't seem to be a valid sleep schedule OR a valid napchart link.\n\n**If you are trying to change your sleep schedule:**\nTry specifying one of the following schedules: " + NapSchedule.getScheduleList() + ". If you don't see your schedule listed, or you are doing some variant (e.g. a modified/extended/underage version), select the closest option and then correct your nickname by hand.\n\n**If you are trying to change your napchart:**\nCheck to make sure the link you pasted is not malformed.\n\nFor more detailed usage instructions, type `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
					return true;
				}
				else
				{
					String rnc = this.removeNapchart(user, channel);
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
				NapSchedule newSchedule = this.setSchedule(user, channel, schedule);
				if (newSchedule == null)
				{
					channel.sendMessage("`" + schedule + "` doesn't seem to be a valid sleep schedule.\n\nTry specifying one of the following sleep schedules: " + NapSchedule.getScheduleList() + ". If you don't see your schedule listed, or you are doing some variant (e.g. a modified/extended/underage version), select the closest option and then correct your nickname by hand.\n\nIf you're already on the correct schedule and just want to set your napchart, you can do that by omitting the schedule name like so:\n`" + NapBot.CONFIGURATION.messagePrefix + "set " + napchart + "`\n\nFor more detailed usage instructions, type `" + NapBot.CONFIGURATION.messagePrefix + "help`.").complete();
					return true;
				}
				else
				{
					if (napchart.equals("none"))
					{
						String ret = this.removeNapchart(user, channel);
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
						this.setNapchart(user, channel, napchart);
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

	private void setNapchart(User user, TextChannel channel, String napchart) throws SQLException
	{
		napchart = napchart.replace("http://", "https://");
		PreparedStatement ps = NapBot.connection.prepareStatement("INSERT OR REPLACE INTO napcharts (id, link) VALUES (?, ?)");
		ps.setLong(1, user.getIdLong());
		ps.setString(2, napchart);
		ps.executeUpdate();
		try
		{
			NapchartHandler.getNapchart(napchart.substring(napchart.length() - 5, napchart.length()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String removeNapchart(User user, TextChannel channel) throws SQLException
	{
		PreparedStatement ps = NapBot.connection.prepareStatement("SELECT * FROM napcharts WHERE id = ? LIMIT 1");
		ps.setLong(1, user.getIdLong());
		ResultSet rs = ps.executeQuery();
		if (rs.next())
		{
			String napchartLocation = rs.getString("link").replace("http://", "https://");
			ps = NapBot.connection.prepareStatement("DELETE FROM napcharts WHERE id = ?");
			ps.setLong(1, user.getIdLong());
			ps.executeUpdate();
			return "\n**Attention:** Your old napchart (<" + napchartLocation + ">) was removed. If you wanted to keep it, type `" + NapBot.CONFIGURATION.messagePrefix + "set " + napchartLocation + "` to set it against your name again.";
		}
		return "";
	}

	private NapSchedule setSchedule(User user, TextChannel channel, String scheduleString)
	{
		NapSchedule schedule = null;
		for (NapSchedule schedule_ : NapSchedule.values())
		{
			if (schedule == NapSchedule.UNKNOWN)
			{
				continue;
			}
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
			case "tricore":
			case "tri-core":
			case "prototype1":
			case "prototype-1":
			case "prototype_1":
				schedule = NapSchedule.TC1;
				break;
			case "u6":
				schedule = NapSchedule.UBERMAN;
				break;
			case "u12":
				schedule = NapSchedule.NAPTATION;
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
		return "set [schedule-name] [napchart-link]";
	}

	@Override
	public String getCommandHelpDescription()
	{
		return "set your current sleep schedule and/or napchart link";
	}
}
