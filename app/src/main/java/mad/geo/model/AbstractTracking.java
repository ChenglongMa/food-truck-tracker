package mad.geo.model;

import java.util.Date;

import mad.geo.model.interfaces.Tracking;

/**
 * GeoTracking
 *
 * @author : Charles Ma
 * @date : 30-08-2018
 * @time : 11:47
 * @description :
 */
public abstract class AbstractTracking implements Tracking {
    protected String trackingId;
    protected int trackableId;
    protected String title;
    protected Date tarStartTime;
    protected Date tarEndTime;
    protected Date meetTime;
    protected String currLocation;
    protected String meetLocation;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
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
        this.title = title;
    }

    public Date getTarStartTime() {
        return tarStartTime;
    }

    public void setTarStartTime(Date tarStartTime) {
        this.tarStartTime = tarStartTime;
    }

    public Date getTarEndTime() {
        return tarEndTime;
    }

    public void setTarEndTime(Date tarEndTime) {
        this.tarEndTime = tarEndTime;
    }

    public Date getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(Date meetTime) {
        this.meetTime = meetTime;
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
