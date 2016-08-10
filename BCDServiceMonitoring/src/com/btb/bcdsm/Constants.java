package com.btb.bcdsm;

/**
 * @author iskwon
 */
public class Constants {

	// Default Properties
	public static final String DEFAULT_LOG_FILE = "log/BCDSM.log";
	
	/* 
	 * 평일은 21시 ~ 04시 30분까, 주말은 21시 ~ 08시 30분까지 코스콤 회선 정보가 들어오지 않는다.
	 * 따라서 이 시간동안은 서비스 확인을 하지 않는다.
	 */
	public static final String EXCLUDE_FROM_TIME 	= "21:00:00";
	public static final String TWENTYFOUR_HOUR 		= "24:00:00";
	public static final String ZERO_HOUR			= "00:00:00";
	public static final String EXCLUDE_TO_TIME 		= "08:30:00";
	
}
