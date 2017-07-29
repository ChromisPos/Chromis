
package uk.chromis.pos.util;

import uk.chromis.format.Formats;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {
    
    
      public static int getTotalMinute(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int hourToMinute = (hour * 60) + minute ;
       return hourToMinute;
    }  
     
      
     public static String getAppDateStartHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);  
    }  
     
     
      public static String getAppDateStartHour(Date d , int amount ) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();    
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
     public static String getAppDateLastHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
     
     public static String getAppDateLastHour(Date d, int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
  
     
     
     public static String getFirstMonthDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, 0);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
     
     public static String getFirstMonthDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.MONTH, amount);
        calendar.set(Calendar.DAY_OF_MONTH, 0);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
       public static String getLastMonthDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
       
       
     public static String getLastMonthDay(Date d  , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.MONTH, amount);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
       
     
     
        public static String getFirstYearDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_YEAR, 1);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
        
        public static String getFirstYearDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.YEAR, amount);
        calendar.set(Calendar.DAY_OF_YEAR, 1);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
     
     
    
  
    
    
    public static String getLastYearDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
    
    
    public static String getLastYearDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.YEAR, amount);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);  
    }  
    
    
    public static int getHour(Date d){
         SimpleDateFormat sdf1 = new SimpleDateFormat("HH");  
//        "Feb 1, 2013 12:00:00 AM"
        return Integer.parseInt(sdf1.format(d));  
    }
    
    
    public static String getDayOfTheWeek(Date d){
      SimpleDateFormat sdf1=new SimpleDateFormat("EEEE"); 
       return  sdf1.format(d);
    }
    
     public static String getDayOfTheMonth(Date d){
      SimpleDateFormat sdf1=new SimpleDateFormat("dd"); 
       String dateResult=  sdf1.format(d);
       return dateResult;
    }
     
     public static String getLastDayOfTheMonth(Date d){
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
//        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");  
//        "Feb 1, 2013 12:00:00 AM"
        return sdf1.format(dddd);  
    }
    
    
       
        public static Date getDateFirstHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return dddd;  
    }
        
        
     public static Date getDateLastHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,11);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return dddd;  
    }
        
   public static void main(String[] args){
        try {
            System.out.println(getFirstYearDay(new Date()));
            System.out.println(getLastYearDay(new Date()));
            System.out.println(getFirstYearDay(new Date(),-1));
            System.out.println(getLastYearDay(new Date(),-1));
            System.out.println();
            
            System.out.println(getFirstMonthDay(new Date()));
            System.out.println(getLastMonthDay(new Date()));
            System.out.println(getFirstMonthDay(new Date(),0));
            System.out.println(getLastMonthDay(new Date(),-1));
            System.out.println();
            
            System.out.println(getAppDateStartHour(new Date()));
            System.out.println(getAppDateLastHour(new Date()));
            System.out.println("Yesterday:"+getAppDateStartHour(new Date(),-1));
            System.out.println("Yesterday:"+getAppDateLastHour(new Date(),-1));
            
//            System.out.println(getDayOfTheWeek(new Date()));
//            System.out.println(getDayOfTheMonth(new Date()));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        
        
   }
}
