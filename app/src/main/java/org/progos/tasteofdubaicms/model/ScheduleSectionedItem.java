package org.progos.tasteofdubaicms.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/30/2015.
 */
public class ScheduleSectionedItem implements Serializable {

    String scheduleId;
    String heading;
    String title;
    String content;
    ArrayList<ScheduleItem> scheduleItems;

    public ScheduleSectionedItem(String scheduleId, String heading, String title, String content, ArrayList<ScheduleItem> scheduleItems) {
        this.scheduleId = scheduleId;
        this.heading = heading;
        this.title = title;
        this.content = content;
        this.scheduleItems = scheduleItems;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(ArrayList<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }
}
