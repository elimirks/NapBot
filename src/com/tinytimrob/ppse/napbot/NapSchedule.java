package com.tinytimrob.ppse.napbot;

public enum NapSchedule
{
	BIPHASIC("Biphasic", "Biphasic", NapRole.BIPHASIC, true,//
			"30lbn", //
			"6.3 hours", //
			"Origin of Everyman sleep, biphasic sleep", //
			"1 long core sleep, 1 short nap", //
			"Two sleeps per day, main sleep through graveyard hours and a little rest around noon. Main sleep resembles monophasic sleep the most.", //
			"Very easy", //
			"Core at midnight, nap around noon", //
			"Most commonly used"), //
	DC1("DC1", "Dual Core 1", NapRole.DUAL_CORE, true,//
			"qwzt5", //
			"5.3 hours", //
			"Dual Core sleep", //
			"2 core sleeps, 1 nap", //
			"One core sleep around dusk, one core sleep around dawn, one nap around noon. Sleep stage division: Dusk core with mostly SWS and dawn core mostly REM.", //
			"Medium", //
			"Dusk core around 21:00 to 22:00, dawn core is free to place till 07:00 or 08:00, REM nap around noon or early afternoon", //
			"Rather high, most popular of Dual Core sleep."), //
	DC2("DC2", "Dual Core 2", NapRole.DUAL_CORE, true,//
			"h58rh", //
			"4 hours 40 minutes", //
			"Dual Core sleep", //
			"2 core sleeps, 2 naps", //
			"One core sleep around dusk, one core sleep around 02:00ish, one dawn nap and one nap around noon. Sleep stage division: Dusk core SWS, night core REM.", //
			"Hard", //
			"First core around 21:00, second core around 02:00, dawn nap before work, noon-early-afternoon nap", //
			"Very low"), //
	DC3("DC3", "Dual Core 3", NapRole.DUAL_CORE, true,//
			"arx4r", //
			"4 hours", //
			"Dual Core sleep", //
			"2 core sleeps, 3 naps", //
			"One core sleep before midnight, one after midnight, dawn nap, noon nap, and afternoon nap. 1.5-hour BRAC and 3-hour rhythm. Sleep stage division: Dusk core SWS, night core REM.", //
			"Very hard", //
			"3-hour wake between 2 cores, dawn nap, noon nap and afternoon nap", //
			"Very low"), //
	DYMAXION("Dymaxion", "Dymaxion", NapRole.SUPERHUMAN, true,//
			"zgqtz", //
			"2 hours", //
			"Ultrashort naps", //
			"4 naps of 30 minutes", //
			"4 naps per day, equidistantly spread throughout the day. Circadian spots include: midnight nap, dawn nap, noon nap, and evening nap. 1.5-hour BRAC, and 3-hour rhythm between each nap. Mixed-stage nap(s) in all 4 naps, or possibly pure-REM or pure-SWS nap.", //
			"Almost impossible", //
			"Arranging naps to fit in 4 aforementioned circadian needs to rest", //
			"Decent"), //
	E2("E2", "Everyman 2", NapRole.EVERYMAN, true,//
			"fflmu", //
			"5 hours 10 minutes", //
			"Everyman sleep", //
			"1 long core sleep, 2 REM naps", //
			"1 core sleep (3 full cycles), 2 naps", //
			"Easy", //
			"Core close to midnight, nap before work, early-afternoon nap", //
			"High"), //
	E3("E3", "Everyman 3", NapRole.EVERYMAN, true,//
			"2b62f", //
			"4 hours", //
			"Everyman sleep", //
			"1 core, 3 naps", //
			"1 main sleep (2 full cycles), 3 REM naps", //
			"Decently hard", //
			"Core as close to dusk as possible to gain more SWS, one nap around 04:00, one nap post-dawn, one early-afternoon nap. All naps should contain REM and little light sleep only.", //
			"Very high. Most famous of all Everyman schedules."), //
	E4("E4", "Everyman 4", NapRole.EVERYMAN, true,//
			"jkfzt", //
			"2.8 hours", //
			"Everyman sleep", //
			"1 min-length core (1 full cycle), 4 REM naps", //
			"1 core sleep before midnight to gain as much SWS as possible, 4 naps should only contain REM.", //
			"Extreme", //
			"Core before midnight, 2 naps before the day starts, noon nap, afternoon nap", //
			"Low"), //
	NAPTATION("Naptation", "Naptation", NapRole.SUPERHUMAN, true,//
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			""), //
	QUADPHASIC("Quadphasic", "Quadphasic", NapRole.DUAL_CORE, true,//
			"qamnm", //
			"4 hours", //
			"Dual Core sleep, Dymaxion naps", //
			"2 core sleeps of minimum length, 2 naps, and the late Dymaxion nap can contain both SWS and REM.", //
			"One core sleep before midnight, one around dawn, Dymaxion noon nap, and Dymaxion afternoon nap. Sleep stage division: Dusk core SWS, dawn core REM. Late nap can contain mixed sleep stages. More flexible than Dymaxion because there are 2 core sleeps to gain SWS and so 2 naps can be moved around to some extent.", //
			"Super", //
			"The whole schedule’s sleep distribution looks like Dymaxion's- one core before midnight, one core around dawn, one noon nap, one afternoon/evening nap.", //
			"Very low"), //
	SPAMAYL("SPAMAYL", "Sleep Polyphasically As Much As You Like", NapRole.SUPERHUMAN, true,//
			"yh1pp", //
			"Undefined, but on average 2.5 hours (7-8 20-min naps per day)", //
			"Ultrashort naps", //
			"Multiple short naps, naptation", //
			"No rhythm, no BRAC concepts, there usually have to have more than 6 20-minute naps per day average. Stocking up sleep in a day then having few or even no naps the next day is also possible. Strong reliance on the ability to detect when the need to rest is, to get quality REM naps. At least 1 SWS nap, and some other naps can be mixed-stages nap. SPAMAYL can be stabilized and formed into a rhythm if one has a stable work/schedule everyday (set nap times, like 2-hour rhythm, napping once every 1.7 hours at night for example).", //
			"Super hard", //
			"Most naps should be spread during graveyard hours to avoid the need to nap during the day with work, social life, etc", //
			"Rather low"), //
	SEGMENTED("Segmented", "Segmented", NapRole.BIPHASIC, true,//
			"k0mot", //
			"7 hours", //
			"Origin of Dual Core sleep, biphasic sleep", //
			"2 core sleeps", //
			"Two sleeps per day, one sleep in the first half of the night (gives mostly SWS), another sleep in the second half of the night (gives mostly REM), 2-hour wake between 2 cores to trigger sleep stage division between 2 cores", //
			"Medium easy", //
			"1 core around 21:00, should not be later than 22:00ish, 1 core till morning", //
			"Rather high"), //
	SIESTA("Siesta", "Siesta", NapRole.BIPHASIC, true,//
			"e72xy", //
			"6.5 hours", //
			"Biphasic sleep", //
			"1 long core sleep, 1 long nap", //
			"Two sleeps per day, main sleep through most of the night, a long rest around noon (max-length nap)", //
			"Extremely easy", //
			"Core around midnight, nap at noon", //
			"Europe, generally inferior to Biphasic"), //
	TESLA("Tesla", "Tesla", NapRole.SUPERHUMAN, true,//
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			""), //
	TRI_CORE("Tri_Core", "Tri Core", NapRole.EXPERIMENTAL, true,//
			"tocl4", //
			"4.8 hours", //
			"Tri Core, Dual Core sleep presumably", //
			"4 sleeps per day, 3 core sleeps of minimum length, 1 short nap", //
			"Considered following Dual Core sleep, sleep stage division: 1 SWS core and 2 REM core. 1 REM nap. 3-hour rhythm to line up with sleep-wake cycle length of each core sleep. Predicted to be more flexible than other Dual Core sleep schedules because the core component of Tri Core consists of 3 cores to be shifted quite comfotably once adapted. ", //
			"Unknown", //
			"3 core sleeps are concentrated during the night to boost alertness for the whole day, so only one small nap in the afternoon is needed. 3-hour rhythm among each core sleep is recommended. Distance of wake time among each core sleep could be shortened if possible, but should not be less than 2 hours. Such a schedule will include a dusk core, night core, and dawn core.", //
			"Nonexistent\n\n*Note: Untested. No signs of successful adaptations."), //
	TRIMAXION("Trimaxion", "Trimaxion", NapRole.EXPERIMENTAL, true,//
			"awkwb", //
			"3 hours", //
			"Everyman sleep, Dymaxion naps", //
			"1 core sleep of minimum length (one full cycle), 3 Dymaxion naps", //
			"4 sleeps per day. Core sleep before midnight for most SWS, naps for REM/mixed stages. Circadian spots include: Core sleep around dusk, dawn nap, noon nap, and evening nap. Mixed-stage nap(s) in all 3 naps, or possibly pure-REM or pure-SWS nap. More flexible than Dymaxion because of having a core sleep, so after adaptation 3 naps can be moved around when convenient.", //
			"Extremely hard", //
			"Distribution of sleeps resembles Dymaxion's and Quadphasic's. Transition step to Dymaxion and also as a fallback from Dymaxion should adaptation fail.", //
			"Virtually non-existent\n\n*Note: Being tested. No evidences of successful adaptations."), //
	TRIPHASIC("Triphasic", "Triphasic", NapRole.DUAL_CORE, true,//
			"8z46u", //
			"4.5 hours", //
			"Dual Core sleep", //
			"3 naps, or 2 cores, 1 long nap", //
			"One core sleep before midnight, one around dawn, and a long nap around noon. Sleep stage separation of SWS and REM into 2 cores of the night, like Dual Core sleep. Long siesta to balance out the schedule.", //
			"Varies", //
			"One core sleep around 21:00, one around 05:30, nap around noon", //
			"Rising"), //
	UBERMAN("Uberman", "Uberman", NapRole.SUPERHUMAN, true,//
			"omr2x", //
			"2 hours", //
			"Ultrashort naps", //
			"6 20-min naps", //
			"6 naps, equidistantly spread during the day. 4-hour rhythm, napping at the end of each BRAC. At least 1 SWS nap, and there could be mixed-stage naps.", //
			"Extremely hard", //
			"Time slots free for rotation, and equidistant sleep is recommended", //
			"Very high"), //
	EXPERIMENTAL("Experimental", "Experimental", NapRole.EXPERIMENTAL, false,//
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			""), //
	ZOIDBERG("Zoidberg", "Zoidberg", NapRole.EXPERIMENTAL, true,//
			"m7hpg", //
			"Around 3-4 hours", //
			"Ultrashort naps", //
			"Multiple 20-min short naps, core sleep simulation", //
			"Multiple 20-min naps are stacked together; one wakes up after 20-min nap then go back to sleep immediately for another 20-min nap. Rinse and repeat. Naps can be stacked together any time possible, as many as needed, or just 1 20-min nap at a time is needed, in case of emergency. The stacked 20-min naps can form a disrupted core sleep/ longer nap. Theoretically the naps prevent one from reaching the completely wakeful state while one still remains somewhat in a sleepy state (last minutes of REM) to ease fall asleep back and as a way to \"resume\" the sleep cycle from lighter stages of sleep (or continuing in REM and SWS); however, it still grants the ability to nap in only 20 minutes. The schedule creates an impression of having both a \"core\" sleep for sustainability like any other schedules with at least one continuous core sleep, while containing multiple small power naps to promote vivid dreams. After adaptation, it is assumed that adapters can stack naps or take lone nap whenever they are sleepy enough, similar to SPAMAYL.", //
			"Unknown", //
			"Stack naps in the night, avoid napping much in the day.", //
			"Virtually non-existent\n\n*Note: Being experimented. Results unknown."), //
	MONOPHASIC("Monophasic", "Monophasic", NapRole.MONOPHASIC, false,//
			"3sicn", //
			"", //
			"", //
			"", //
			"", //
			"", //
			"", //
			"");

	public final String name;
	public final String longName;
	public final NapRole role;
	public final boolean appendToNick;
	public final String napchartID;
	public final String totalSleep;
	public final String identification;
	public final String specification;
	public final String mechanism;
	public final String difficulty;
	public final String scheduling;
	public final String popularity;

	NapSchedule(String name, String longName, NapRole role, boolean appendToNick, String napchartID, String totalSleep, String identification, String specification, String mechanism, String difficulty, String scheduling, String popularity)
	{
		this.name = name;
		this.longName = longName;
		this.role = role;
		this.appendToNick = appendToNick;
		this.napchartID = napchartID;
		this.totalSleep = totalSleep;
		this.identification = identification;
		this.specification = specification;
		this.mechanism = mechanism;
		this.difficulty = difficulty;
		this.scheduling = scheduling;
		this.popularity = popularity;
	}

	public static String getScheduleList()
	{
		String s = "";
		for (NapSchedule schedule : NapSchedule.values())
		{
			s += (!s.isEmpty() ? " " : "") + "`" + schedule.name + "`";
		}
		return s;
	}
}
