package in.eko.service.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class OperationHelper {
	
	private static Logger logger = Logger
			.getLogger(OperationHelper.class);

	private static OperationHelper operationHelper = null;

	public static OperationHelper getInstance() {

		if (operationHelper == null) {
			synchronized (OperationHelper.class) {
				operationHelper = new OperationHelper();
			}
		}
		return operationHelper;
	}
	
	public Date getDateAfterXMinutes(Date date, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, +min);
        return calendar.getTime();
        
    }
	
	public Date getDateBeforeXMinutes(Date date, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -min);
        return calendar.getTime();
        
    }
	
	public Date getDateOfBeforeXdays(int x) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - x);
		Date myDate = cal.getTime();
		return myDate;
	}
	
	public Date getDateOfAfterXdays(int x) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + x);
        Date myDate = cal.getTime();
        return myDate;
    }
	
	public Date getCurdate() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		now.set(Calendar.HOUR_OF_DAY,0);
		Date myDate = now.getTime();
		return myDate;
	}

}
