package com.tinytimrob.ppse.napbot;

public enum NapRole
{
	SUPERHUMAN("Superhumans", "Nap-only/Superhuman schedules"), //
	DUAL_CORE("Dual Core", "Dual core schedules"), //
	EVERYMAN("Everyman", "Everyman schedules"), //
	TRIPHASIC("Triphasic", "Triphasic schedules"), //
	BIPHASIC("Biphasic", "Biphasic schedules"), //
	EXPERIMENTAL("Experimental", "Experimental/Unproven schedules"), //
	MONOPHASIC("Monophasic", null);

	public final String name;
	public final String helpName;

	NapRole(String name, String helpName)
	{
		this.name = name;
		this.helpName = helpName;
	}
}
