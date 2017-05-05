package com.tinytimrob.common;

public enum TerminationReason
{
	STOP(0, "Application is being stopped"), FREEZE(10, "Application has entered automatic freeze recovery"), CRASH(11, "Application has entered automatic crash recovery"), AUTORESTART(12, "Application is performing a scheduled restart"), RESTART(13, "Application is being restarted");

	public int code;
	public String message;

	TerminationReason(int code, String message)
	{
		this.code = code;
		this.message = message;
	}
}
