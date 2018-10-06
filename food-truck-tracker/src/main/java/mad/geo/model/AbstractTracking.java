package mad.geo.model;

import java.util.Calendar;
import java.util.Date;

import mad.geo.model.interfaces.Tracking;

import static mad.geo.utils.DateHelper.dateToString;

/**
 * The abstract class for tracking info
 */
public abstract class AbstractTracking extends AbstractUnique implements Tracking {

    protected String trackingId;
    protected int trackableId;
    protected String title;
    protected Date tarStartTime;
    protected Date tarEndTime;
    protected Date meetTime;
    protected String currLocation;
    protected String meetLocation;

    public AbstractTracking() {
        trackingId = getUniqueStringId();
    }

//    private static String dateTimeToString(Date date) {
//        return DateFormat.getDateTimeInstance(
//                DateFormat.SHORT, DateFormat.MEDIUM).format(date);
//    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
//        if (stringIdSet.add(trackingId)) {
//            stringIdSet.remove(this.trackingId);
//            this.trackingId = trackingId;
//        } else throw new IllegalArgumentException("This ID has existed");
    }

    public int getTrackableId() {
        return trackableId;
    }

    public void setTrackableId(int trackableId) {
        this.trackableId = trackableId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Null title is not allowed.");
        }
        this.title = title;
    }

    public Date getTarStartTime() {
        return tarStartTime;
    }

    public void setTarStartTime(Date tarStartTime) {
        this.tarStartTime = tarStartTime;
    }

    public String getStartTimeStr() {
        return dateToString(tarStartTime);
    }

    public Date getTarEndTime() {
        return tarEndTime;
    }

    public void setTarEndTime(Date tarEndTime) {

        this.tarEndTime = tarEndTime;
    }

    public String getEndTimeStr() {
        return dateToString(tarEndTime);
    }

    public Date getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(Date meetTime) {
        Calendar sourceCal = Calendar.getInstance();
        Calendar targetCalStart = Calendar.getInstance();
        Calendar targetCalEnd = Calendar.getInstance();
        // set the calendars for comparison
        sourceCal.setTime(meetTime);
        targetCalStart.setTime(tarStartTime);
        targetCalEnd.setTime(tarEndTime);
        if (sourceCal.equals(targetCalStart) || sourceCal.equals(targetCalEnd)
                || (sourceCal.after(targetCalStart) && sourceCal.before(targetCalEnd))) {
            this.meetTime = meetTime;
        } else
            throw new IllegalArgumentException("Meet Time should be between Start Time and End Time");

    }

    public String getMeetTimeStr() {
        return dateToString(meetTime);
    }

    public String getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(String currLocation) {
        this.currLocation = currLocation;
    }

    public String getMeetLocation() {
        return meetLocation;
    }

    public void setMeetLocation(String meetLocation) {
        this.meetLocation = meetLocation;
    }

}
