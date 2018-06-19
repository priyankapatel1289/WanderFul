package patel.priyanka.wanderful.model;

public class MainModel {

    private String groupName;
    private String groupPlace;
    private String groupDate;
    private String groupIcon;
    private String groupId;

    public MainModel(String groupName, String groupPlace, String groupDate, String groupIcon) {
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

    public String getGroupPlace() {
        return groupPlace;
    }

    public void setGroupPlace(String groupPlace) {
        this.groupPlace = groupPlace;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
