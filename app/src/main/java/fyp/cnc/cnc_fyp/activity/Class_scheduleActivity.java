package fyp.cnc.cnc_fyp.activity;
import com.alamkanak.weekview.WeekViewEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Class_scheduleActivity extends Class_scheduleAbstract {
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Calendar startTime;
        Calendar endTime;
        WeekViewEvent event;

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 2);
        startTime.set(Calendar.HOUR_OF_DAY, 11);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 2);
        endTime.set(Calendar.HOUR_OF_DAY, 13);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(1, "COMP S382F - P02 - C0411", startTime, endTime);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 5);
        startTime.set(Calendar.HOUR_OF_DAY, 9);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 5);
        endTime.set(Calendar.HOUR_OF_DAY, 11);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(1, "COMP S382F - L02 - G29", startTime, endTime);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 2);
        startTime.set(Calendar.HOUR_OF_DAY, 14);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 2);
        endTime.set(Calendar.HOUR_OF_DAY, 16);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(10, "COMP S380F - P03 - C0415", startTime, endTime);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 6);
        startTime.set(Calendar.HOUR_OF_DAY, 9);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 6);
        endTime.set(Calendar.HOUR_OF_DAY, 11);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(10, "COMP S380F - L01 - G29", startTime, endTime);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 3);
        startTime.set(Calendar.HOUR_OF_DAY, 11);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 3);
        endTime.set(Calendar.HOUR_OF_DAY, 13);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(10, "COMP S412F - P02 - C0411", startTime, endTime);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, 6);
        startTime.set(Calendar.HOUR_OF_DAY, 14);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_WEEK, 5);
        endTime.set(Calendar.HOUR_OF_DAY, 16);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(10, "COMP S412F - L01 - C0311", startTime, endTime);
        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 4);
//        startTime.set(Calendar.MINUTE, 20);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 5);
//        endTime.set(Calendar.MINUTE, 0);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        startTime.add(Calendar.DATE, 1);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 15);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, 15);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        //AllDay event
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 0);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 23);
//        event = new WeekViewEvent(7, getEventTitle(startTime),null, startTime, endTime);
//        events.add(event);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 8);
//        startTime.set(Calendar.HOUR_OF_DAY, 2);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.DAY_OF_MONTH, 10);
//        endTime.set(Calendar.HOUR_OF_DAY, 23);
//        event = new WeekViewEvent(8, getEventTitle(startTime),null, startTime, endTime);
//        events.add(event);
//
//        // All day event until 00:00 next day
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 10);
//        startTime.set(Calendar.HOUR_OF_DAY, 0);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.SECOND, 0);
//        startTime.set(Calendar.MILLISECOND, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.DAY_OF_MONTH, 11);
//        event = new WeekViewEvent(8, getEventTitle(startTime), null, startTime, endTime);
//        events.add(event);

        return events;
    }
}
