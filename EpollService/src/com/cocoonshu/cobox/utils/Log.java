package com.cocoonshu.cobox.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class Log {

	private static final String      LOG_FILE_NAME    = "Log_%04d%02d%02d_%02d%02d%02d.log";
	private static final String      LOG_DIR_NAME     = "Log";
	private static       boolean     sIsDebugable     = false;
	private static       boolean     sIsConsoleOutput = true;
	private static       boolean     sIsLogFileOutput = true;
	private static       PrintStream sLogStream       = null;
	
	private static void openLogFile(String dir) {
		closeLogFile();
		Calendar calendar = Calendar.getInstance();
		try {
			String localePath = null;
			if (dir == null) {
				localePath = System.getProperty("java.class.path") + "/";
				String[] localePaths = localePath.split(";");
				if (localePaths != null || localePath.length() > 0) {
					localePath = localePaths[0] + "/";
				}
			} else {
				localePath = dir + "/";
			}
			
			localePath = LOG_DIR_NAME + "/";
			
			File outputFile = new File(String.format(localePath + LOG_FILE_NAME,
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND)));
			if (!outputFile.exists()) {
				System.out.println("[I] Create log file on " + outputFile.getAbsolutePath());
				outputFile.getParentFile().mkdirs();
				outputFile.createNewFile();
			}
			
			sLogStream = new PrintStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sLogStream = null;
		} catch (IOException e) {
			e.printStackTrace();
			sLogStream = null;
		}
	}
	
	private static void closeLogFile() {
		if (sLogStream != null) {
			sLogStream.close();
			sLogStream = null;
		}
	}
	
	public static void setConsoleOutput(boolean enabled) {
		sIsConsoleOutput = enabled;
	}
	
	public static void setLogFileOutput(boolean enabled, String logDirectory) {
		sIsLogFileOutput = enabled;
		if (sIsLogFileOutput) {
			openLogFile(logDirectory);
		} else {
			closeLogFile();
		}
	}
	
	public static void setLogFileOutput(boolean enabled) {
		sIsLogFileOutput = enabled;
		if (sIsLogFileOutput) {
			openLogFile(null);
		} else {
			closeLogFile();
		}
	}
	
	public static void i(String tag, String msg) {
		String log = "[I][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			System.out.println(log);
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(log);
		}
	}
	
	public static void i(String tag, String msg, boolean noNewLine) {
		String log = "[I][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			if (noNewLine) {
				System.out.print(log);
			} else {
				System.out.println(log);
			}
		}
		if (sIsLogFileOutput && sLogStream != null) {
			if (noNewLine) {
				sLogStream.print(log);
			} else {
				sLogStream.println(log);
			}
		}
	}
	
	public static void w(String tag, String msg) {
		String log = "[W][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			System.out.println(log);
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(log);
		}
	}
	
	public static void w(String tag, String msg, boolean noNewLine) {
		String log = "[W][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			if (noNewLine) {
				System.out.print(log);
			} else {
				System.out.println(log);
			}
		}
		if (sIsLogFileOutput && sLogStream != null) {
			if (noNewLine) {
				sLogStream.print(log);
			} else {
				sLogStream.println(log);
			}
		}
	}
	
	public static void e(String tag, String msg) {
		String log = "[E][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			System.out.println(log);
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(log);
		}
	}
	
	public static void e(String tag, String msg, Throwable throwable) {
		String log = "[E][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			if (sIsDebugable) {
				System.out.println(log);
				if (throwable != null) {
					throwable.printStackTrace();
				}
			} else {
				System.out.println(log + (throwable != null ? "\n\t" + throwable.getMessage() : ""));
			}
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(log);
			if (throwable != null) {
				throwable.printStackTrace(sLogStream);
			}
		}
	}
	
	public static void print(String msg) {
		if (sIsConsoleOutput) {
			System.out.print(msg);
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.print(msg);
		}
	}
	
	public static void println(String msg) {
		if (sIsConsoleOutput) {
			System.out.println(msg);
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(msg);
		}
	}
	
	public static void println() {
		if (sIsConsoleOutput) {
			System.out.println();
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println();
		}
	}
	
	public static void stack(String tag, String msg) {
		String log = "[S][" + tag + "] " + msg;
		if (sIsConsoleOutput) {
			System.out.println(log);
			new Throwable().printStackTrace();
		}
		if (sIsLogFileOutput && sLogStream != null) {
			sLogStream.println(log);
			new Throwable().printStackTrace(sLogStream);
		}
	}
	
}
