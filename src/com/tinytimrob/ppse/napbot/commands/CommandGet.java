package com.tinytimrob.ppse.napbot.commands;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.tinytimrob.ppse.napbot.NapBot;
import com.tinytimrob.ppse.napbot.NapchartHandler;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

public class CommandGet implements ICommand
{
	@Override
	public String[] getCommandName()
	{
		return new String[] { "get" };
	}

	@Override
	public boolean hasPermission(User user)
	{
		return true;
	}

	@Override
	public boolean execute(User user, TextChannel channel, String command, List<String> parameters, Message message) throws Exception
	{
		Member matchedMember = null;
		if (!parameters.isEmpty())
		{
			List<Member> matchingMembers = new ArrayList<Member>();
			String match = StringUtils.join(parameters, " ");
			while (match.startsWith(" "))
			{
				match = match.substring(1);
			}
			if (match.startsWith("<@!") && match.contains(">"))
			{
				// match based on @mention
				String id = match.substring(3, match.indexOf(">"));
				Member member = channel.getGuild().getMemberById(id);
				if (member != null)
				{
					matchingMembers.add(member);
				}
			}
			else if (match.startsWith("<@") && match.contains(">"))
			{
				// why do some snowflakes start with ! and some not? wtf?
				String id = match.substring(2, match.indexOf(">"));
				Member member = channel.getGuild().getMemberById(id);
				if (member != null)
				{
					matchingMembers.add(member);
				}
			}
			else
			{
				// look up the user by name. sadly we can't use the built in "effective match" because of the sleep schedules being part of nickname
				// so we're going to have to do this the old fashioned way
				for (Member member : channel.getGuild().getMembers())
				{
					String s = member.getEffectiveName();
					if (s.contains("["))
					{
						s = s.substring(0, s.lastIndexOf("[")).trim();
					}
					if (s.equalsIgnoreCase(match))
					{
						matchingMembers.add(member);
					}
					else if (member.getUser().getName().equalsIgnoreCase(match))
					{
						matchingMembers.add(member);
					}
					else if ((member.getUser().getName() + "#" + member.getUser().getDiscriminator()).equalsIgnoreCase(match))
					{
						matchingMembers.add(member);
					}
				}
			}

			if (matchingMembers.isEmpty())
			{
				channel.sendMessage("I wasn't able to find anyone called `" + match + "` on the server.").complete();
				return true;
			}
			else if (matchingMembers.size() > 1)
			{
				ArrayList<String> output = new ArrayList<String>();
				output.add("Matched multiple users called `" + match + "`. Please choose one of the following:");
				for (Member x : matchingMembers)
				{
					output.add((x.getUser().getName() + "#" + x.getUser().getDiscriminator()).replace("_", "\\_").replace("*", "\\*"));
				}
				channel.sendMessage(StringUtils.join(output, '\n')).complete();
				return true;
			}
			else
			{
				matchedMember = matchingMembers.get(0);
			}
		}
		else
		{
			matchedMember = channel.getGuild().getMember(user);
		}
		if (matchedMember == null)
		{
			throw new NullPointerException("Matched member was null");
		}

		PreparedStatement ps = NapBot.connection.prepareStatement("SELECT * FROM napcharts WHERE id = ? LIMIT 1");
		ps.setLong(1, matchedMember.getUser().getIdLong());
		ResultSet rs = ps.executeQuery();
		if (rs.next())
		{
			String napchartLocation = rs.getString("link").replace("http://", "https://");
			String napchartID = napchartLocation.substring(napchartLocation.length() - 5, napchartLocation.length());
			Timestamp napchartTimestamp = rs.getTimestamp("time");
			try
			{
				NapchartHandler.getNapchart(napchartID);
			}
			catch (IOException e)
			{
				// yay.
			}
			MessageBuilder b = new MessageBuilder();
			b.append("Napchart for **" + matchedMember.getEffectiveName().replace("_", "\\_").replace("*", "\\*") + "**");
            if (napchartTimestamp != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                b.append(" (since " + formatter.format(napchartTimestamp) + " UTC)");
            }
            b.append(":");
			MessageEmbedImpl embedimpl = new MessageEmbedImpl();
			embedimpl.setTitle("https://napchart.com/" + napchartID);
			embedimpl.setUrl("https://napchart.com/" + napchartID);
			embedimpl.setImage(new ImageInfo(NapBot.CONFIGURATION.napchartUrlPrefix + napchartID + "?" + NapBot.RESYNC_ID, null, 560, 560));
			embedimpl.setFields(new ArrayList<MessageEmbed.Field>());
			b.setEmbed(embedimpl);
			channel.sendMessage(b.build()).complete();
		}
		else
		{
			channel.sendMessage("There is no napchart available for **" + matchedMember.getEffectiveName().replace("_", "\\_").replace("*", "\\*") + "**.").complete();
		}
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
