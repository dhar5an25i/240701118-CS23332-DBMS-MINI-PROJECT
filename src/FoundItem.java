public class FoundItem {
    private String itemName, location, foundDate, description, contactInfo;

    public FoundItem(String itemName, String location, String foundDate, String description, String contactInfo) {
        this.itemName = itemName;
        this.location = location;
        this.foundDate = foundDate;
        this.description = description;
        this.contactInfo = contactInfo;
    }

    public String getItemName() { return itemName; }
    public String getLocation() { return location; }
    public String getFoundDate() { return foundDate; }
    public String getDescription() { return description; }
    public String getContactInfo() { return contactInfo; }
}