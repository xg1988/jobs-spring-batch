package com.chosu.springbatchbasic.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
@Slf4j
public class DateUtil {
	
	public String getToday(String pattern) {
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat SDF = new SimpleDateFormat(pattern);
		
		String chkDate = SDF.format(calendar.getTime());
		
		//calendar.add(Calendar.DATE, -1);		
		chkDate = SDF.format(calendar.getTime());		
		
		return chkDate;
	}
	
	public String getToday(String pattern, String minusDate) {
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat SDF = new SimpleDateFormat(pattern);
		
		String chkDate = SDF.format(calendar.getTime());	
		calendar.add(Calendar.DATE, Integer.parseInt(minusDate));		
		chkDate = SDF.format(calendar.getTime());		
		
		return chkDate;
	}
	
	// startdate가 enddate를 지났는지 검사
	public boolean isPassDueDate(String startDate, String endDate, String pattern) {
		boolean isPass = false;
		try {
			SimpleDateFormat SDF = new SimpleDateFormat(pattern);
			Date date1 = SDF.parse(startDate);
			Date date2 = SDF.parse(endDate);
			if(date1.compareTo(date2) > 0) {
				isPass = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return isPass;
	}
}
