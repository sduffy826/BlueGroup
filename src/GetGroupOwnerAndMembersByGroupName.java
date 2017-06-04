import java.util.Vector;
import java.util.ArrayList;

public class GetGroupOwnerAndMembersByGroupName {

  private static final boolean DEBUGIT = false;
  private static final boolean DEBUGCWA = false;

  /**
   * Mainline to test with, should pass the groupname into this program
   */

  public ArrayList<GroupMemberBean> getMembers(String groupName) {
    ArrayList<GroupMemberBean> rtnList = new ArrayList<GroupMemberBean>();

    if (groupName.length() > 0) {

      // Create object we'll turn on debug info :)
      UserGroups mUserGroups = new UserGroups(DEBUGCWA);

      // Get owner
      String ownersEmail = mUserGroups.getGroupOwnersEmail(groupName);

      // Get members
      Vector<String> members = mUserGroups.getGroupMembers(groupName);

      if (DEBUGIT) System.out.println("Group passed in: " + groupName + " Owner: " + ownersEmail);
      
      for (String memberEmail : members) {
        GroupMemberBean groupMemberBean = new GroupMemberBean();
        groupMemberBean.setGroupName(groupName);
        groupMemberBean.setOwnerEmail(ownersEmail);
        groupMemberBean.setMemberEmail(memberEmail);
        rtnList.add(groupMemberBean);

        if (DEBUGIT) System.out.println(groupMemberBean.toString());
      }

    }
    if (DEBUGIT) System.out.println("Done");
    
    return rtnList;
  }
}
