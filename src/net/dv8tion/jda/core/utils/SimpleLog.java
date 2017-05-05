/*
 *     Copyright 2015-2017 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.core.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import com.tinytimrob.common.LogWrapper;

/** Replaces JDA's logging implementation with Log4j2 (sigh) */
public class SimpleLog
{
	private static final Map<String, SimpleLog> LOGS = new HashMap<>();
	private final Logger logger;

	public SimpleLog(String name)
	{
		this.logger = LogWrapper.getLogger(name);
	}

	/**
	 * Will get the LOG with the given LOG-name or create one if it didn't exist
	 *
	 * @param name the name of the LOG
	 * @return SimpleLog with given LOG-name
	 */
	public static SimpleLog getLog(String name)
	{
		synchronized (LOGS)
		{
			if (!LOGS.containsKey(name.toLowerCase()))
			{
				LOGS.put(name.toLowerCase(), new SimpleLog(name));
			}
		}
		return LOGS.get(name.toLowerCase());
	}

	/**
	 * Will LOG a message with given LOG-level
	 *
	 * @param level The level of the Log
	 * @param msg   The message to LOG
	 */
	public void log(Level level, Object msg)
	{
		this.logger.log(level.equivalentLevel, msg);
	}

	public void log(Throwable ex)
	{
		this.logger.log(org.apache.logging.log4j.Level.INFO, "Encountered an error", ex);
	}

	/**
	 * Will LOG a message with trace level.
	 *
	 * @param msg the object, which should be logged
	 */
	public void trace(Object msg)
	{
		this.log(Level.TRACE, msg);
	}

	/**
	 * Will LOG a message with debug level
	 *
	 * @param msg the object, which should be logged
	 */
	public void debug(Object msg)
	{
		this.log(Level.DEBUG, msg);
	}

	/**
	 * Will LOG a message with info level
	 *
	 * @param msg the object, which should be logged
	 */
	public void info(Object msg)
	{
		this.log(Level.INFO, msg);
	}

	/**
	 * Will LOG a message with warning level
	 *
	 * @param msg the object, which should be logged
	 */
	public void warn(Object msg)
	{
		this.log(Level.WARNING, msg);
	}

	/**
	 * Will LOG a message with fatal level
	 *
	 * @param msg the object, which should be logged
	 */
	public void fatal(Object msg)
	{
		this.log(Level.FATAL, msg);
	}

	/**
	 * Enum containing all the LOG-levels
	 */
	public enum Level
	{
		ALL("Finest", 0, false, org.apache.logging.log4j.Level.ALL), TRACE("Trace", 1, false, org.apache.logging.log4j.Level.TRACE), DEBUG("Debug", 2, false, org.apache.logging.log4j.Level.DEBUG), INFO("Info", 3, false, org.apache.logging.log4j.Level.INFO), WARNING("Warning", 4, true, org.apache.logging.log4j.Level.WARN), FATAL("Fatal", 5, true, org.apache.logging.log4j.Level.FATAL), OFF("NO-LOGGING", 6, true, org.apache.logging.log4j.Level.OFF);

		private final String msg;
		private final int pri;
		private final boolean isError;
		public final org.apache.logging.log4j.Level equivalentLevel;

		Level(String message, int priority, boolean isError, org.apache.logging.log4j.Level equivalentLevel)
		{
			this.msg = message;
			this.pri = priority;
			this.isError = isError;
			this.equivalentLevel = equivalentLevel;
		}

		/**
		 * Returns the Log-Tag (e.g. Fatal)
		 *
		 * @return the logTag
		 */
		public String getTag()
		{
			return this.msg;
		}

		/**
		 * Returns the numeric priority of this loglevel, with 0 being the lowest
		 *
		 * @return the level-priority
		 */
		public int getPriority()
		{
			return this.pri;
		}

		/**
		 * Returns whether this LOG-level should be treated like an error or not
		 *
		 * @return boolean true, if this LOG-level is an error-level
		 */
		public boolean isError()
		{
			return this.isError;
		}
	}
}