package com.tinytimrob.ppse.napbot;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class CommonPolyStuff
{
	public static void setNapchart(User user, TextChannel channel, String napchart) throws SQLException
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

	public static String removeNapchart(User user, TextChannel channel) throws SQLException
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

	public static NapSchedule setSchedule(User user, TextChannel channel, String scheduleString)
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
			case "mutated-mono":
			case "mutated_mono":
				schedule = NapSchedule.MUTATEDMONO;
			case "tricore":
			case "tri-core":
			case "tri_core":
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
		ArrayList<Role> rolesToAdd = new ArrayList<Role>();
		ArrayList<Role> rolesToRemove = new ArrayList<Role>();
		rolesToAdd.addAll(channel.getGuild().getRolesByName(schedule.role.name, true));
		// determine whether this member needs attempted tag
		List<Role> rolesTheMemberHas = member.getRoles();
		boolean needsTag = true;
		for (Role role : rolesTheMemberHas)
		{
			if (role.getName().equals("Adapted-" + schedule.name))
			{
				needsTag = false;
				break;
			}
		}
		if (needsTag)
		{
			rolesToAdd.addAll(channel.getGuild().getRolesByName("Attempted-" + schedule.name, true));
		}
		for (Role role : rolesTheMemberHas)
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

	public static String formatPercentage(float a, float b, int digits)
	{
		float c = b == 0 ? 0 : a / b;
		return String.format("%." + digits + "f", c * 100) + "%";
	}
}
