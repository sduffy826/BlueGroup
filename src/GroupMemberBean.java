
/**
 * Bean with bluegroup attributes: groupName, groupOwner and members
 * @author sduffy
 *
 */
public class GroupMemberBean {
  private String groupName;
  private String ownerEmail;
  private String memberEmail;
  
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
  public String getMemberEmail() {
    return memberEmail;
  }
  public void setMemberEmail(String memberEmail) {
    this.memberEmail = memberEmail;
  }
  @Override
  public String toString() {
    return "Group: " + getGroupName() + " Owner: " + getOwnerEmail() + " Member: " + getMemberEmail();
  }
  
  
}
