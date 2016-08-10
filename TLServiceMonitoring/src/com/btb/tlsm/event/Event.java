package com.btb.tlsm.event;

public class Event {
	
	public static final int IDLE = 0;
	public static final int MMM_RESULT = 1;

	public static final int SHUTDOWN = 9999; // 메시지 루프를 종료시킨다.

	public int id;
	public Object param;
	public Object param2;

	public Event(int id) {
		this.id = id;
		this.param = null;
		this.param2 = null;
	}

	public Event(int id, Object param) {
		this.id = id;
		this.param = param;
		this.param2 = null;
	}

	public Event(int id, Object param, Object param2) {
		this.id = id;
		this.param = param;
		this.param2 = param2;
	}
}
