package org.jcommon.scheduling;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogManager {
	public static enum Type {
		STARTED(800),
		COMPLETED(800),
		ERROR(900),
		INFO(800);
		
		private int level;
		
		Type(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
	};
	
	public static final void log(Task task, Type type, String message) {
		Logger.getLogger("jcommon").log(new CustomLevel(type.name(), type.getLevel()), message);
	}
	
	public static final void log(Task task, Type type, Throwable t) {
		Logger.getLogger("jcommon").log(new CustomLevel(type.name(), type.getLevel()), "", t);
	}
}

class CustomLevel extends Level {
	private static final long serialVersionUID = 1L;

	public CustomLevel(String s, int i) {
		super(s, i);
	}
}