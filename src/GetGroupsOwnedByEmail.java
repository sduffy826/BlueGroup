
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class gets the groups owned by an email address passed in
 * 
 * @author sduffy
 * @since 2017-05-22
 */
public class GetGroupsOwnedByEmail {

  private static final boolean DEBUGIT = false;
  private static final boolean DEBUGCWA = false;

  /**
   * The mainline, should pass in the email address on invocation
   */
  public ArrayList<GroupOwnerBean> getGroups(String email) {

    // Create array of the beans to return
    ArrayList<GroupOwnerBean> rtnList = new ArrayList<GroupOwnerBean>();

    if (email.length() > 0) {
      // Create object, use flag to determine if tracing should be on
      UserGroups mUserGroups = new UserGroups(DEBUGCWA);

      if (DEBUGIT) System.out.println("\n\nGroups User Is Owner\n=======================");
        
      Vector<String> groupsOwnerOf = mUserGroups.getGroupsUserIsOwnerOf(email);
      for (String aGroup : groupsOwnerOf) {
        GroupOwnerBean groupOwnerBean = new GroupOwnerBean();
        groupOwnerBean.setGroupName(aGroup);
        groupOwnerBean.setOwnerEmail(email);
        rtnList.add(groupOwnerBean);

        if (DEBUGIT) System.out.println(groupOwnerBean.toString());
      }

      if (DEBUGIT) System.out.println("Owner of " + Integer.toString(groupsOwnerOf.size()) + " groups");
      if (DEBUGIT) System.out.println("Done");
    }
    return rtnList;
  }
}