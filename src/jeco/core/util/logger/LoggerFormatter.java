/*
 * Copyright (C) 2010 José Luis Risco Martín <jlrisco@ucm.es>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *  - José Luis Risco Martín
 */
package jeco.core.util.logger;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for the logger. It formats the log records in the following way:
 * [LEVEL-THREAD|TIME]: MESSAGE
 * where LEVEL is the level of the log record, THREAD is the name of the thread
 * that generated the log record, TIME is the time elapsed since the start of the
 * program and MESSAGE is the message of the log record.
 */
public class LoggerFormatter extends Formatter {

  /**
   * Message format for the log records.
   */
  private static final MessageFormat messageFormat = new MessageFormat("[{0}-{1}|{2}]: {3} \n");
  /**
   * Singleton instance of the formatter.
   */
  public static final LoggerFormatter formatter = new LoggerFormatter();
  /**
   * Time when the program started.
   */
  protected long startTime;

  /**
   * Constructs a new formatter.
   */
  public LoggerFormatter() {
    super();
    startTime = System.currentTimeMillis();
  }

  @Override
  public String format(LogRecord record) {
    Object[] arguments = new Object[4];
    arguments[0] = record.getLevel();
    arguments[1] = Thread.currentThread().getName();
    arguments[2] = getElapsedTime(record.getMillis());
    arguments[3] = record.getMessage();
    return messageFormat.format(arguments);
  }

  /**
   * Returns the time elapsed since the start of the program in the format
   * HH:MM:SS.mmm.
   * @param currentTime the current time.
   * @return the time elapsed since the start of the program.
   */
  public String getElapsedTime(long currentTime) {
    long elapsedTime = currentTime - startTime;
    long elapsedTimeInSeconds = elapsedTime / 1000;
    String format1 = String.format("%%0%dd", 2);
    String format2 = String.format("%%0%dd", 3);
    String seconds = String.format(format1, elapsedTimeInSeconds % 60);
    String minutes = String.format(format1, (elapsedTimeInSeconds % 3600) / 60);
    String hours = String.format(format1, elapsedTimeInSeconds / 3600);
    String millis = String.format(format2, elapsedTime % 1000);
    String time = hours + ":" + minutes + ":" + seconds + "." + millis;
    return time;
  }
}
