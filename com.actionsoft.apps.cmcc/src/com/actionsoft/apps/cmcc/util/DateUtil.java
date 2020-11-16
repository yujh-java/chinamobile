package com.actionsoft.apps.cmcc.util;
/**
 * 日期处理
 * @author nch
 * @date 20170622
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.actionsoft.bpms.util.UtilString;

public class DateUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 比较两日期大小
	 * @param date1
	 * @param date2
	 * @return date1 > date2 返回false
	 * @throws ParseException
	 * 
	 */
	public static boolean compareDates(String date1,String date2) throws ParseException{
		boolean bol = true;
		if(UtilString.isEmpty(date2) || UtilString.isEmpty(date1)){
			bol = false;
		}else{
			long date1Times = sdf.parse(date1).getTime();
			long date2Times = sdf.parse(date2).getTime();
			if(date1Times > date2Times){
				bol = false;
			}
		}
		return bol;
	}
	/**
	 * 增加DataUtil处理类扩展方法
	 * sunk
	 * 2017-05-23
	 */
	protected static String datePattern = "yyyy-MM-dd";
	private static final Calendar calendar = Calendar.getInstance();
	
	public static String getSysMonth() {
		return getDateStr("yyyy-MM");
	}
	
	public static String getSysDate() {
		return getDateStr("yyyy-MM-dd");
	}

	public static String getSysDateTime() {
		return getDateStr("yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getSysDateTimeMillis() {
		return getDateStr("yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static String getDateStr(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		
		return formatter.format(new Date());
	}
	
	public static String format(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * 按照格式返回日期
	 * 
	 * @param args
	 */
	public static Date parseFormatDate(String aDateStr)  {
		return parseFormatDate(aDateStr, datePattern);
	}

	/**
	 * 按照格式返回日期
	 * 
	 * @param args
	 */
	public static Date parseFormatDate(String aDateStr, String aDateFmtStr) {
		SimpleDateFormat smt = new SimpleDateFormat(aDateFmtStr);
		Date ret;
		if (aDateStr == null || aDateStr.equals(""))
			return null;
		try {
			ret = smt.parse(aDateStr.trim());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	/**
	 * 取得指定月份的第一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	public static String getMonthBegin(String strdate) {
		Date date = parseFormatDate(strdate);
		return formatDateByFormat(date, "yyyy-MM") + "-01";
	}

	/**
	 * 取得指定月份的最后一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	public static String getMonthEnd(String strdate) {
		Date date = parseFormatDate(getMonthBegin(strdate));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return formatDate(calendar.getTime());
	}

	/**
	 * 常用的格式化日期
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	public static String formatDate(Date date) {
		return formatDateByFormat(date, datePattern);
	}


	/**
	 * 以指定的格式来格式化日期
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String
	 */
	public static String formatDateByFormat(Date date, String format) {
		String result = "";
		if (date != null&&!"".equals(date)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				// LOGGER.info("date:" + date);
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 得到当月倒数第i天的最后时间,精确到秒
	 * @param date
	 */
	public static Date getLastDateOfMonth(Date date,int i) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -i);
		c.set(Calendar.HOUR, 0);
		c.add(Calendar.SECOND, -1);

		return c.getTime();
	}
	
	/**
	 * 得到上月倒数第i天的最后时间,精确到秒
	 * @param date
	 */
	public static Date getLastDateOfLastMonth(Date date,int i) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -i);
		c.set(Calendar.HOUR, 0);
		c.add(Calendar.SECOND, -1);

		return c.getTime();
	}
	/**
	 * 得到月中的指定某天的日期
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date getDateOfMonth(Date date,int i){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		c.set(Calendar.DAY_OF_MONTH, i);

		return c.getTime();
	}
	/**
	 * 得到年中的指定的某月
	 * lu
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date getDateOfYear(Date date,int i){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		c.set(Calendar.MONTH, i-1);
		return c.getTime();
	}
	
	/**
	 * 得到所给时间当前季度的开始和结束时间
	 * @param date
	 * @return
	 */
	public static Date[] getSeason(Date date) {
		if (date != null) {
			Date[] season = new Date[2];
			try {
				int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
				String year = new SimpleDateFormat("yyyy").format(date);
				switch (month) {
					case 1:
					case 2:
					case 3:
						season[0] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-1-1");
						season[1] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-3-31");
						break;
					case 4:
					case 5:
					case 6:
						season[0] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-4-1");
						season[1] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-6-30");
						break;
					case 7:
					case 8:
					case 9:
						season[0] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-7-1");
						season[1] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-9-30");
						break;
					case 10:
					case 11:
					case 12:
						season[0] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-10-1");
						season[1] = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-12-31");
				}

			} catch (Exception e) {
			}

			return season;
		}
		return null;
	}
	
	/**
	 * 得到所给日期的当月第一天和最后一天,精确到天
	 * @param date
	 * @return
	 */
	public static Date[] getMonth(Date date) {
		Date[] dates = new Date[2];
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		
		c.set(Calendar.DAY_OF_MONTH,1);
		dates[0] = c.getTime();
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		dates[1] = c.getTime();
		return dates;
	}
	
	/**
	 * 得到相差relative天的日期,style决定了 取到的精度
	 * @param now
	 * @param relative
	 * @param style
	 * @return
	 */
	public static Date getRelativeDate(Date now,int relative,String style){
		SimpleDateFormat sdf=new SimpleDateFormat(style);
		Calendar c = Calendar.getInstance();
		try{
			c.setTime(sdf.parse(sdf.format(now)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		if(relative != 0){
			c.add(Calendar.DAY_OF_MONTH,relative);
		}

		return c.getTime();
	}
	
	/**
	 * @param now
	 * @param relative
	 * @param style
	 * @return
	 */
	public static Date getRelativeMonth(Date now,int relative){
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		if(relative != 0){
			c.add(Calendar.MONTH,relative);
		}
		
		return c.getTime();
	}
	/**
	 * @param now
	 * @param relative
	 * @param style
	 * @return
	 */
	public static String getRelativeMonth(Date now,int relative,String style){
		SimpleDateFormat sdf=new SimpleDateFormat(style);
		Calendar c = Calendar.getInstance();
		try{
			c.setTime(sdf.parse(sdf.format(now)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		if(relative != 0){
			c.add(Calendar.MONTH,relative);
		}
		
		return sdf.format(c.getTime());
	}
	/**
	 * @param now
	 * @param relative
	 * @param style
	 * @return
	 */
	public static String getRelativeMonth(String now,int relative,String style){
		SimpleDateFormat sdf=new SimpleDateFormat(style);
		Calendar c = Calendar.getInstance();
		try{
			c.setTime(sdf.parse(now));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		if(relative != 0){
			c.add(Calendar.MONTH,relative);
		}
		
		return sdf.format(c.getTime());
	}
	

	public static Integer getIntervalDays(Date startday, Date endday) {
		long sl = startday.getTime();
		long el = endday.getTime();

		long ei = el - sl;
		// 根据毫秒数计算间隔天数
		return (int) (ei / (1000 * 60 * 60 * 24));
	}
	
	public static Integer getIntervalMonth(Date startDate, Date endday) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int s = calendar.get(Calendar.MONTH);
		calendar.setTime(endday);
		
		return s - calendar.get(Calendar.MONTH);
	}

	public static List<String> getMonthsBetweenTwoDays(String startDateStr, String endDateStr) {
		Date startDate = parseFormatDate(startDateStr, "yyyy-MM");
		Date endDate = parseFormatDate(endDateStr, "yyyy-MM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		
		List<String> dateList = new ArrayList<String>();
		
		while(startDate.before(endDate)) {
			dateList.add(formatDateByFormat(startDate, "yyyyMM"));
			calendar.add(Calendar.MONTH, 1);
			startDate = calendar.getTime();
		}

		dateList.add(formatDateByFormat(endDate, "yyyyMM"));
		
		return dateList;
	}
	
	public static List<String> getMonthsBetweenTwoDaysDesc(String startDateStr, String endDateStr) {
		Date startDate = parseFormatDate(startDateStr, "yyyy-MM");
		Date endDate = parseFormatDate(endDateStr, "yyyy-MM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		
		List<String> dateList = new ArrayList<String>();
		
		while(endDate.after(startDate)) {
			dateList.add(formatDateByFormat(endDate, "yyyyMM"));
			calendar.add(Calendar.MONTH, -1);
			endDate = calendar.getTime();
		}

		dateList.add(formatDateByFormat(startDate, "yyyyMM"));
		
		return dateList;
	}
	/**
	 * yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static int getMonthEndInt(String date){
		int endDayInt = -1;
		String endDay = getMonthEnd(date);
		if(null != endDay && !"".equals(endDay)){
			endDayInt = Integer.parseInt(endDay.substring(endDay.lastIndexOf("-")+1));
		}
		return endDayInt;
		
		
	}
	
	/*public static Date getDatePossible(String datestr) {
		String[] fmt={"MM/dd/yyyy","yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"};
		
		Date ret=null;
		try {
			ret = DateUtils.parseDate(datestr, fmt);
		} catch (ParseException e) {
			ret=null;
			
			//e.printStackTrace();
		}
		return ret;
	}*/
	
	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static String strToFormat(String strDate) {
		if (null == strDate || strDate.equals("")) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			strDate = formatter.format(formatter.parse(strDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static String strToFormat(String strDate,String format) {
		if (null == strDate || strDate.equals("")) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			strDate = formatter.format(formatter.parse(strDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 上一天
	 */
	public static Date getLastDay(Date date){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try{
			c.setTime(sdf.parse(sdf.format(date)));
		}catch (Exception e) { 
			e.printStackTrace();
		}
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}
	
	 /**
     * 将查询起始日期，设置为00:00:00
     * @param startDate
     * @return
     */
    public static Date getQueStartDate(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        //cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        startDate = cal.getTime();

        return startDate;
    }

	/**
	 * 将查询截止日期，设置为23:59:59
	 * @param endDate
	 */
	public static Date getQueEndDate(Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		//cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		endDate = cal.getTime();
		
		return endDate;
	}
	
	/**
	 * 获取相对时间
	 * @param ms
	 * @return
	 */
	public static Date getDate(long ms){
		return new Date(new Date().getTime()+ms);
	}
  	//输入一个发送申诉时间判断是否与系统当前时间超过7天，超过7天返回true否则返回false
  	public static  boolean getLastTimeBool(String oldTime){
		Date old=DateUtil.parseFormatDate(oldTime, "yyyy-MM-dd HH:mm:ss");
		long long_old=old.getTime();
		long long_now=System.currentTimeMillis();
		long cha=long_now-long_old;
		System.out.println(cha);
		if(cha<604800000){
			return false;
		}else{
			return true;
		}
	}
  	/**
  	 * 
  		 * 描述： 获取下i个月的今天的昨天
  		 * 创建者：吴士伟
  		 * 创建时间：2014-10-10下午5:09:32
  		 * 说明：
  		 * 
  		 * @return
  	 * @throws ParseException 
  	 */
    public static String getPreDayOfNextXMonth(String thedate,int Xmonth) throws ParseException {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = new GregorianCalendar();
		Date date = simpleDateFormat.parse(thedate);
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + Xmonth);
		calendar.set(Calendar.HOUR, -24);
		Date date2 = calendar.getTime();
		String youDate = simpleDateFormat.format(date2);
		return youDate;
    }
    /**
     * 
    	 * 描述： 
    	 * 创建者：吴士伟
    	 * 创建时间：2014-10-10下午5:16:01
    	 * 说明：
    	 * 
    	 * @return
     */
    public static String getDayOfNextXMonth(String thedate,int Xmonth) throws ParseException {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = new GregorianCalendar();
		Date date = simpleDateFormat.parse(thedate);
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + Xmonth);
		Date date2 = calendar.getTime();
		String youDate = simpleDateFormat.format(date2);
		return youDate;
    }
    
    /**
	 * 得到当前年份
	 */
	public static int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 得到当前月份
	 */
	public static int getMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}
	/**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }
    
    public static String formatStringLength(Object value,int length,Object changer) {
    	StringBuffer sb=new StringBuffer();
    	for (int i = value.toString().length(); i <length; i++) {
			sb.append(changer);
		}
		sb.append(value);
    	return sb.toString();
	}
}
