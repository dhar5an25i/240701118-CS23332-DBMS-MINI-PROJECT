public class LostItem {
    private String itemName, location, lostDate, description, contactInfo;

    public LostItem(String itemName, String location, String lostDate, String description, String contactInfo) {
        this.itemName = itemName;
        this.location = location;
        this.lostDate = lostDate;
        this.description = description;
        this.contactInfo = contactInfo;
    }

    public String getItemName() { return itemName; }
    public String getLocation() { return location; }
    public String getLostDate() { return lostDate; }
    public String getDescription() { return description; }
    public String getContactInfo() { return contactInfo; }
}