


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.ibm.bluepages.BluePages;
import com.ibm.bluepages.slaphapi.LDAPAttribute;
import com.ibm.bluepages.slaphapi.LDAPEntry;
import com.ibm.bluepages.slaphapi.SLAPHAPIResults;
import com.ibm.swat.password.ReturnCode;
import com.ibm.swat.password.cwa2;

/*
 * 
 * <p>Example<code>  UserGroups ugObj = new UserGroups(false)
 *                   String userDn = ugObj.getDNForEmail(_emailAddrString);</code></p>   
 */
public class UserGroups {
  private boolean debugIt;
  private cwa2 cwa2Obj;
  
  /** 
   * <p>Perform object initialization, called from the constructors
   * it initializes the cwa2 object used throughout this class
   * and sets debug/trace based on value of 'debugIt'.</p>
   */
  private void initIt() {
    if (debugIt) {
      System.setProperty(cwa2.DEBUG, "true");
      System.setProperty(cwa2.TRACE, "true");
    }
    
    cwa2Obj = new cwa2("bluepages.ibm.com","bluegroups.ibm.com");

    cwa2Obj.setMemberFilterSize(100);
    cwa2Obj.setUseThreaded(true);
  }

  /** 
   * <p>Constructor without args, sets debugging flag off and
   * calls init method</p>
   */
  public UserGroups() {
    debugIt = false;
    initIt();
  }
  
  /** 
   * <p>Constructor with argument identifying debugging flag, it 
   * sets flag and calls init method</p>
   * @param _debugIt  <code>true</code> if want debugging/trace turned
   *                  on <code>false</code> if don't want it
   */
  public UserGroups(boolean _debugIt) {
    debugIt = _debugIt;
    initIt();
  }
  
  /** 
   * <p>Return a string representing the distinguished name
   * for a given email address.</p>
   *
   * @param _emailAddr  the email address to search bluepages for
   * @return            a <code>String</code> representing the 
   *                    distinguished name <code>""</code> if not found
   */
  public String getDNForEmail(String _emailAddr) {    
    if (_emailAddr.isEmpty() == false) {
      if (debugIt ){ System.setProperty(BluePages.DEBUG,"true"); }
      
      LDAPEntry thePerson = getPersonForEmail(_emailAddr);
      if (thePerson != null) {
        return thePerson.getDN();        
      }
    }
    return "";
  }
  
  /** 
   * <p>Method is a wrapper for the call when we want to get the
   * LDAPEntry for a given serial and country code (cnum); the caller
   * can interrogate the ldap entries attributes</p>
   *
   * @param _cnum         cnum to search for (cnum=serial || countrycode)
   * @return              <code>LDAPEntry</code> representing person found
   *                      <code>null</code> if not found
   * @author              S. Duffy
   * @since               2016-12-12
   * @see                 LDAPEntry
   */
  public LDAPEntry getPersonForCnum(String _cnum) {
    return getPersonEntryForKey("uid",_cnum);
  }
  
  /** 
   * <p>Another wrapper for the call when we want to get the
   * LDAPEntry for a given email address.</p>
   *
   * @param _emailAdddr   <code>String</code> representing email address
   *                      to search for.
   * @return              <code>LDAPEntry</code> representing person found
   *                      <code>null</code> if not found
   * @author              S. Duffy
   * @since               2016-12-12
   * @see                 LDAPEntry
   */
  public LDAPEntry getPersonForEmail(String _emailAddr) {
    return getPersonEntryForKey("mail",_emailAddr);
  }
  
  /** 
   * <p>This method returns a person record (LDAPEntry) for a given
   * key.  Note: I only look at the first entry found so you should
   * use something that is unique to one person (i.e. uid (cnum),
   * mail (email address)).</p>
   *
   * @param _searchKey    the tag we should search for i.e. <code>uid</code>
   * @param _searchValue  the search value i.e. <code>SERIAL897</code>
   * @return              <code>LDAPEntry</code> representing person found
   *                      <code>null</code> if not found
   * @author              S. Duffy
   * @since               2016-12-12
   * @see                 LDAPEntry
   */  
  public LDAPEntry getPersonEntryForKey(String _searchKey, String _searchValue) {
    LDAPEntry thePersonEntry = null;
    if (_searchKey.isEmpty() == false && _searchValue.isEmpty() == false) {
      if (debugIt ){ System.setProperty(BluePages.DEBUG,"true"); }
      
      // Empty parms for this call
      String filter = "ibmperson/("+_searchKey+"=" + _searchValue +")";  // .list/bytext?ibm-allgroups (for webcall)
      
      // Make bluepages call
      SLAPHAPIResults results = BluePages.callSLAPHAPI(filter);
      // Make sure the method didn't fail unexpectedly.
      if (results.succeeded()) {               
        // Check to see whether SLAPHAPI returned any results.
        if (results.getSize() > 0) {
          thePersonEntry = results.getEntry();
          if (debugIt) {
            // Show all the attributes                       
            String attrID;            // Attribute Name
            LDAPAttribute attribute;  // Vector to store the values
                        
            for (Enumeration<LDAPAttribute> a = thePersonEntry.getAttributes(); a.hasMoreElements(); ) {
              // Get the attributes.
              attribute = (LDAPAttribute)a.nextElement();
              
              // Get attribute name.
              attrID = attribute.getID();
              
              // Get the values for the current attribute in an enumerator.
              // Iterate through values and display each.
              for (Enumeration<String> v = attribute.getValues(); v.hasMoreElements(); ) {
                  System.out.println("    " + attrID + ": " + v.nextElement());
              }
            }
          }
        }
      }
    }
    return thePersonEntry;
  }
  
  /** 
   * <p>Return a vector of the groups a user is in, this one returns
   * the full DN of the group</p>
   *
   * @param _emailAddr   <code>String</code> email address of person to get groups for
   * @return             <code>Vector&lt;String&gt;</code> groups user is in 
   * @author             S. Duffy
   * @since              2016-12-13 
   */
  public Vector<String> getFullGroupsUserIsIn(String _emailAddr) {
    Vector<String> groupsIn = new Vector<String>();
    if (_emailAddr.isEmpty() == false) {
      if (debugIt ){ System.setProperty(BluePages.DEBUG,"true"); }
      
      // Empty parms for this call
      Map<String, Object> parms = new HashMap<String, Object>();
      String filter = "ibmperson/(mail=" + _emailAddr +")";  // .list/bytext?ibm-allgroups (for webcall)
      String[] attributes = { "ibm-allgroups" };             // elements to pull
      
      // Make bluepages call
      SLAPHAPIResults results = BluePages.callSLAPHAPI(filter, attributes, parms);
      // Make sure the method didn't fail unexpectedly.
      if (results.succeeded()) {               
        // Check to see whether SLAPHAPI returned any results.
        if (results.getSize() > 0) {
          String attrID;
          LDAPEntry entry;          // Hashtable to store LDAPAttribute instances
          LDAPAttribute attribute;  // Vector to store the values
        
          // Iterate over values
          for (Enumeration<LDAPEntry> e = results.getEntries(); e.hasMoreElements(); ) {
            entry = e.nextElement();
            
            // Get an enumerator for the attributes returned for the current entry.
            // Iterate through attributes and get values for each attribute.
            for (Enumeration<LDAPAttribute> a = entry.getAttributes(); a.hasMoreElements(); ) {
              // Get the attributes.
              attribute = a.nextElement();
                
              // Get attribute name.
              attrID = attribute.getID();
                
              // Get the values for the current attribute in an enumerator.
              // Iterate through values and display each.
              for (Enumeration<String> v = attribute.getValues(); v.hasMoreElements(); ) {
                groupsIn.add(v.nextElement());
              }
            }           
          }
        }
      } 
    }
    return groupsIn;
  }

  /** 
   * <p>Get the group administrators for a group</p>
   *
   * @param _group   <code>String</code> the group name
   * @return         <code>Vector&lt;String&gt;</code> the admins (email address)
   * @author         S. Duffy
   * @since          2016-12-12 
   */
  public Vector<String> getGroupAdmins(String _group) {
    Vector<String> list = new Vector<String>();
    ReturnCode theRc = cwa2Obj.listAdmins(_group,3,list,"mail");
    if (theRc.getCode() != 0) {
      list = null;
    }
    return list;
  }
  
  /** 
   * <p>Get the groups that a email address is the owner or admin of, argument
   * passed in determines which type</p>
   *
   * @param _searchTag  pass "owner" or "admin" for this, it dictates whether
   *                    what type's of groups are returned (where emailAddr is owner
   *                    or admin of)
   * @param _emailAddr  <code>String</code> the persons email address
   * @return            <code>Vector&lt;String&gt;</code> list of groups
   * @author            S. Duffy
   * @since             2016-12-13
   */
  public Vector<String> getGroupsAdminOrOwnerOf(String _searchTag, String _emailAddr) 
                                                                  throws IllegalArgumentException {
    Vector<String> groups2Return = new Vector<String>();
    
    // Need the dn for the search so get it
    String dnForEmail = getDNForEmail(_emailAddr);
        
    // Set search filter
    String searchFilter;
    if (_searchTag.compareToIgnoreCase("owner") == 0) {
      searchFilter = "(owner=" + dnForEmail + ")";
    }
    else if (_searchTag.compareToIgnoreCase("admin") == 0) {
      searchFilter = "(admin=" + dnForEmail + ")";
    }
    else throw new IllegalArgumentException("Invalid argument");
        
    // We're going to specify a different base to use (needed for admin/owner searches
    String base2Use = "ou=metadata,ou=ibmgroups,o=ibm.com/";
    
    // Only want the common name attribute
    String[] attr2Return = new String[] { "cn" };  
      
    // Tell it we're override the base search argument, do this in parm
    Map<String, Object> parms = new HashMap<String, Object>();
    parms.put(BluePages.SLAPHAPI_SEARCH_BASE, "base"); 
    
    // Make bluepages call
    SLAPHAPIResults results = BluePages.callSLAPHAPI(base2Use+searchFilter,attr2Return,parms);
    // Make sure the method didn't fail unexpectedly.
    if (results.succeeded()) {               
      // Check to see whether SLAPHAPI returned any results.
      if (results.getSize() > 0) {
        LDAPEntry entry;      
        // Iterate over values
        for (Enumeration<LDAPEntry> e = results.getEntries(); e.hasMoreElements(); ) {
          entry = e.nextElement();        
          try {
            // Only attribute we asked for is common name so add it to vector we'll be returning
            groups2Return.add(entry.getAttribute("cn").getValue());
          }
          catch(NullPointerException nullPtrE) { /* Just to show how to catch null ptr */ }
          catch(Exception exc) { }                   
        }
      }
    }
    return groups2Return;
  }  
  
  /** 
   * <p>Get the members of the group passed in, this one returns the email address
   * of the people, if the caller wants a different attribute then call the method with
   * the other signature</p>
   *
   * @param _groupName  <code>String</code> group to get members of (the cn)
   * @return            <code>Vector&lt;String&gt;</code> list emails in the group
   * @author            S. Duffy
   * @since             2016-12-13 
   */
  public Vector<String> getGroupMembers(String _groupName) {
    return getGroupMembers(_groupName,"mail");  // Default to returning email address
  }
  
  /** 
   * <p>Get the members of the group passed in, the caller also specifies the
   * attribute they want returned (look at bp schema for valid attributes (i.e. callupName))</p>
   *
   * @param _groupName   <code>String</code> group to get members of (the cn)
   * @param _bpAttribute <code>String</code> the bluepages attribute to return
   * @return             <code>Vector&lt;String&gt;</code> of _bpAttributes 
   * @author             S. Duffy
   * @since              2016-12-13 
   */
  public Vector<String> getGroupMembers(String _groupName, String _bpAttribute) {
    Vector<String> memberList = new Vector<String>();
    ReturnCode rc = cwa2Obj.listMembers(_groupName, memberList, _bpAttribute); //"callupName");
    
    return memberList;    
  }
  
  /** 
   * <p>Get group owner for a group</p>
   *
   * @param _group   <code>String</code> the group name
   * @return         <code>String</code> the owner (full DN format)
   * @author         S. Duffy
   * @since          2016-12-12 
   */
  public String getGroupOwner(String _group) {
    return cwa2Obj.getOwner(_group);
  }
      
  /** 
   * <p>Get group owners email address for a group</p>
   *
   * @param _group   <code>String</code> the group name
   * @return         <code>String</code> the email address of the owner, null if not found
   * @author         S. Duffy
   * @since          2016-12-12 
   */
  public String getGroupOwnersEmail(String _group) {
    // Get the distinguished name for group owner
    String dnOfOwner = getGroupOwner(_group);
    String rtnValue = null;
    // Pull the uid value out of the RDN value.
    String uid = LdapHelper.getRdnValue(dnOfOwner,"uid");
    if (uid.length() > 0) {     
      LDAPEntry thePerson = getPersonForCnum(uid);
      if (thePerson != null) {
        rtnValue = thePerson.getAttribute("mail").getValue();
      }
    }
    return rtnValue;
  }
    
  
  /** 
   * <p>Get the groups the email address passed in is an administrator
   * of; this is really a wrapper for the getGroupsAdminOrOwnerOf method</p>
   *
   * @param _emailAddr  <code>String</code> the persons email address
   * @return            <code>Vector&lt;String&gt;</code> list of groups person is admin of,
   *                    it contains the common name of the group (cn)
   * @author            S. Duffy
   * @since             2016-12-13 
   */
  public Vector<String> getGroupsUserIsAdminOf(String _emailAddr) {
    try {
      return getGroupsAdminOrOwnerOf("admin", _emailAddr);
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }
    
  /** 
   * <p>Return a vector of groups that the user (email) is in, this one is really
   * a wrapper for the method that does the work (getFullGroupsUserIsIn)</p>
   *
   * @param _emailAddr   <code>String</code> email address of person to get groups for
   * @return             <code>Vector&lt;String&gt;</code> groups user is in 
   * @author             S. Duffy
   * @since              2016-12-13 
   */
  public Vector<String> getGroupsUserIsIn(String _emailAddr) {
    Vector<String> groups = getFullGroupsUserIsIn(_emailAddr);
    
    for(int i=0; i < groups.size(); i++) {
      // Replace vector element with just the rdn 'cn' value
      groups.set(i,LdapHelper.getRdnValue(groups.get(i),"cn"));
    }
    return groups;
  }
    
  /**
   * <p>This returns a vector of groups that the user is an owner of, this is really
   * a wrapper for the method that does the work (getGroupsAdminOrOwnerOf)</p>
   * 
   * @param _emailAddr  <code>String</code> the email address of the user
   * @return            <code>Vector&lt;String&gt;</code> groups user is in, it
   *                    contains the common name (cn) 
   * @author            S. Duffy
   * @since             2016-12-13
   */
  public Vector<String> getGroupsUserIsOwnerOf(String _emailAddr) {
    try {
      return getGroupsAdminOrOwnerOf("owner", _emailAddr);
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }
}
