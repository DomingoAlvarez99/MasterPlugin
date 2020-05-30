package com.masterplugin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomDate {

	public static String getCurrentDate() {
		return LocalDateTime.of(LocalDate.now(), LocalTime.now()).toString().substring(0,
				LocalDateTime.of(LocalDate.now(), LocalTime.now()).toString().length() - 4);
	}

}