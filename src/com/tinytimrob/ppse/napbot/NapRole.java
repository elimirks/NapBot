package com.tinytimrob.ppse.napbot;

public enum NapRole
{
	SUPERHUMAN("Superhumans"), //
	DUAL_CORE("Dual Core"), //
	EVERYMAN("Everyman"), //
	BIPHASIC("Biphasic"), //
	RANDOM("Random"), //
	MONOPHASIC("Monophasic");

	public final String name;

	NapRole(String name)
	{
		this.name = name;
	}
}
