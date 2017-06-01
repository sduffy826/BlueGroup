
import javax.naming.InvalidNameException;
import javax.naming.ldap.*;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * This is a helper class to process an LdapName object, and LdapName is
 * a Distinguished Name object (like cn=John Doe,ou=xyz.com,o=us).  For
 * ref the 'cn=John Doe' is called a relative distinguished item (rdn), the
 * cn is the rdn type, the John Doe is called the rdn value.
 * 
 * @author S. Duffy
 *
 */
public class LdapHelper {
  
  /**
   * This method returns a list where each row is an rdn type/value pair, this
   * is useful if you want the type/values in a list, in the original order of the
   * DN.
   * 
   * @param _ldapString  <code>String</code> representing the DN to process
   * @return             <code>List&lt;Entry&lt;String,String&gt;&gt;</code> containing
   *                     the result
   *                     
   * @author S. Duffy
   * @since  2016-12-10                     
   */
  public static List<Entry<String,String>> getList(String _ldapString) {
    LdapName ldapNameObj;
    Rdn rdnObj;
    List<Entry<String,String>> listMap = new ArrayList<Entry<String, String>>();
    
    try {
      ldapNameObj = new LdapName(_ldapString);
     
      // This goes left to right (rdn(0) is furthest to right)
      for (int i = ldapNameObj.size()-1; i >= 0; i--) {
        rdnObj = ldapNameObj.getRdn(i);                     
        listMap.add(new SimpleEntry<String,String>(rdnObj.getType(), rdnObj.getValue().toString()));               
      }
    } catch (InvalidNameException e) {
      e.printStackTrace();
    }
    return listMap;
  }
  
  /**
   * Return a map for the given DN, the rdn type is the key in
   * the map, the rdn value is the value (obviously).
   * 
   * @param _ldapString  <code>String</code> the DN to evaluate
   * @return             <code>Map&lt;String,String&gt; the map
   *                     with the key/value pairs.
   *                     
   * @author S. Duffy
   * @since  2016-12-10
   */
  public static Map<String,String> getMap(String _ldapString) {
    LdapName ldapNameObj;
    Rdn rdnObj;
    Map<String,String> theMap = new HashMap<String,String>();
    
    try {
      ldapNameObj = new LdapName(_ldapString);
     
      // This goes left to right (rdn(0) is furthest to right)
      for (int i = ldapNameObj.size()-1; i >= 0; i--) {
        rdnObj = ldapNameObj.getRdn(i);
        theMap.put(rdnObj.getType(), rdnObj.getValue().toString());        
      }
    } catch (InvalidNameException e) {
      e.printStackTrace();
    }
    return theMap;
  }
  /**
   * This method returns the rdn value for a specific rdn type, this isn't really
   * efficient but wanted to show how you could iterate over the ldapname object.
   *    * 
   * @param _ldapString  <code>String</code> representing the DN
   * @param _rdnType     <code>String</code> the rdn type you want
   * @return             <code>String</code> the rdn value
   * 
   * @author S. Duffy
   * @since  2016-12-10
   */
  public static String getRdnValue(String _ldapString, String _rdnType) {
    LdapName ldapNameObj;
    Rdn rdnObj;
    
    try {
      ldapNameObj = new LdapName(_ldapString);
      // This goes left to right (rdn(0) is furthest to right)
      for (int i = ldapNameObj.size()-1; i >= 0; i--) {
        rdnObj = ldapNameObj.getRdn(i);
        if ( rdnObj.getType().compareToIgnoreCase(_rdnType) == 0 ) {
          return rdnObj.getValue().toString();
        }
      }
    } catch (InvalidNameException e) {
      e.printStackTrace();
    }
    return "";
  }
  
  /** 
   * Get the rdn values out of the DN separated by comma's, did this
   * for testing
   * 
   * @param _ldapString <code>String</code> the string with the DN
   * @return            <code>String</code> the DN with the 'rdntype=' removed
   *                    and values separated by comma's
   *                    
   * @author S. Duffy
   * @since  2016-12-10 
   */
  public static String getRdnValues(String _ldapString) {
    return getRdnValuesSeparated(_ldapString,",");
  }
  
  /**
   * <p>Return the DN without all the rdn type indicators, i.e. cn=John Doe,ou=xyz.com,o=abc
   * would return John Doe/xyz.com/abc (if the separator passed in is /).
   * 
   * @param _ldapString <code>String</code> the DN to parse
   * @param _separator  <code>String</code> the separator to use (i.e. /)
   * 
   * @return            <code>String</code> with the 'rdnType=' values removed and separated
   *                    by the separator
   * @author S. Duffy
   * @since  2016-12-10
   */
  public static String getRdnValuesSeparated(String _ldapString, String _separator) {
    LdapName ldapNameObj;
        
    try {
      ldapNameObj = new LdapName(_ldapString);
      StringBuffer strBuff = new StringBuffer();
      // This goes left to right (rdn(0) is furthest to right)
      for (int i = ldapNameObj.size()-1; i >= 0; i--) {
        strBuff.append(ldapNameObj.getRdn(i).getValue().toString());
        if (i > 0) strBuff.append(_separator);
      }
      return strBuff.toString();
    } catch (InvalidNameException e) {
      e.printStackTrace();
    }
    return "";   
  }
}
