package my.uum;

public class Cafe {
    private String cafeCode;
    private String name;
    private String inasisName;
    private String officeTelNo;
    private String mobileTelNo;
    private String location;
    private String locationLink;
    private String openTime;
    private String closeTime;
    private String holidayStatus;
    private String description;
    private String emailAdmin;

    public Cafe(String cafeCode, String name, String inasisName, String officeTelNo, String mobileTelNo, String location,
                String locationLink, String openTime, String closeTime, String holidayStatus, String description,
                String emailAdmin) {
        this.cafeCode = cafeCode;
        this.name = name;
        this.inasisName = inasisName;
        this.officeTelNo = officeTelNo;
        this.mobileTelNo = mobileTelNo;
        this.location = location;
        this.locationLink = locationLink;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.holidayStatus = holidayStatus;
        this.description = description;
        this.emailAdmin = emailAdmin;
    }

    public String getCafeCode() {
        return cafeCode;
    }

    public void setCafeCode(String cafeCode) {
        this.cafeCode = cafeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInasisName() {
        return inasisName;
    }

    public void setInasisName(String inasisName) {
        this.inasisName = inasisName;
    }

    public String getOfficeTelNo() {
        return officeTelNo;
    }

    public void setOfficeTelNo(String officeTelNo) {
        this.officeTelNo = officeTelNo;
    }

    public String getMobileTelNo() {
        return mobileTelNo;
    }

    public void setMobileTelNo(String mobileTelNo) {
        this.mobileTelNo = mobileTelNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationLink() {
        return locationLink;
    }

    public void setLocationLink(String locationLink) {
        this.locationLink = locationLink;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getHolidayStatus() {
        return holidayStatus;
    }

    public void setHolidayStatus(String holidayStatus) {
        this.holidayStatus = holidayStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEmailAdmin() {
        return emailAdmin;
    }

    public void setEmailAdmin(String emailAdmin) {
        this.emailAdmin = emailAdmin;
    }

}
