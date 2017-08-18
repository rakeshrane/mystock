package util

import java.util.Calendar
import java.util.Date

object DateUtil {
  def removeTime(date: Date ): Date= {
        val cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.getTime();
    }
}