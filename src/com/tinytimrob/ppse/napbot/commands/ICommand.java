package com.tinytimrob.ppse.napbot.commands;

import java.util.List;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public interface ICommand
{
	public String[] getCommandName();

	public String getCommandHelpUsage();

	public String getCommandHelpDescription();

	public boolean hasPermission(User user);

	public boolean execute(User user, TextChannel channel, String command, List<String> parameters) throws Exception;
}
