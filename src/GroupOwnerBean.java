
/**
 * Bean with bluegroup attributes: groupName, and groupOwner
 * @author sduffy
 *
 */
public class GroupOwnerBean {
  private String groupName;
  private String ownerEmail;
  
  public String getGroupName() {
    return groupName;
  }
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
  public String getOwnerEmail() {
    return ownerEmail;
  }
  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }
  
  @Override
  public String toString() {
    return "Group: " + getGroupName() + " Owner: " + getOwnerEmail();
  }
  
  
}
