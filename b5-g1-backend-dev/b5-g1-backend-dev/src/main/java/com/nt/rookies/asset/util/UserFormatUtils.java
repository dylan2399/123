package com.nt.rookies.asset.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoField;

import org.springframework.stereotype.Component;


@Component
public class UserFormatUtils {
	
	public String capitalizeName(String name){
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	public boolean isFirstNameContainWhiteSpace(String fName){
		return fName.contains(" ");
	}

	public String formatName(String name){
		String[] strs = name.replaceAll("\\s\\s+", " ").trim().split(" ");
		String result = "";
		for (String s: strs){
			result += capitalizeName(s) + " ";
		}
		return result.trim();
	}

	public int userAge(LocalDate dob) {
		LocalDate currentDate = LocalDate.now();
		if (dob != null && currentDate != null) {
			return Period.between(dob, currentDate).getYears();
		}
		return 0;
	}

	public boolean joinedDateIsValid(LocalDate dob, LocalDate joinedDate) {
		int years = Period.between(dob, joinedDate).getYears();
		if(years < 18){
			return false;
		}
		return true;
	}

	public boolean compareDate(LocalDate dob, LocalDate joinedDate) {
		return joinedDate.isAfter(dob);
	}

	public boolean isWeekend(LocalDate joinedDate) {
		DayOfWeek day = DayOfWeek.of(joinedDate.get(ChronoField.DAY_OF_WEEK));
		return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
	}
}
