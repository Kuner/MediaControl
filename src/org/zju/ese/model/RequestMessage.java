package org.zju.ese.model;

import java.io.Serializable;

public class RequestMessage implements Serializable {

	/**
	 * 
	 */
	public static final int PLAY = 1;
	public static final int PAUSE = 2;
	public static final int PREVIOUS = 3;
	public static final int NEXT = 4;
	public static final int OPEN = 5;
	public static final int CLOSE = 6;
	public static final int OPENFILE = 7;
	public static final int PICTURE = 8;
	
	private static final long serialVersionUID = 1L;
	private int command;
	private String path;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
}
