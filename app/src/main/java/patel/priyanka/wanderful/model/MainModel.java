package patel.priyanka.wanderful.model;

import android.support.annotation.Nullable;

public class MainModel {

    private String groupName;
    @Nullable private String groupPlace;
    @Nullable private String groupDate;
    @Nullable private String groupIcon;
    private String groupId;

    public MainModel(String groupName, @Nullable String groupPlace, @Nullable String groupDate, @Nullable String groupIcon) {
        this.groupName = groupName;
        this.groupPlace = groupPlace;
        this.groupDate = groupDate;
        this.groupIcon = groupIcon;
    }

    public MainModel() {
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Nullable
    public String getGroupPlace() {
        return groupPlace;
    }

    public void setGroupPlace(@Nullable String groupPlace) {
        this.groupPlace = groupPlace;
    }

    @Nullable
    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(@Nullable String groupDate) {
        this.groupDate = groupDate;
    }

    @Nullable
    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(@Nullable String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
