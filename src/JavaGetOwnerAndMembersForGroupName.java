//***************************************************************************
// Encode column called uriEncode, all other columns are passed to output
// stage.
//***************************************************************************
// package com.ibm.is.cc.javastage.rs_utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.ibm.is.cc.javastage.api.Configuration;
import com.ibm.is.cc.javastage.api.InputLink;
import com.ibm.is.cc.javastage.api.InputRecord;
import com.ibm.is.cc.javastage.api.Link;
import com.ibm.is.cc.javastage.api.OutputLink;
import com.ibm.is.cc.javastage.api.OutputRecord;
import com.ibm.is.cc.javastage.api.RejectRecord;
import com.ibm.is.cc.javastage.api.Processor;
import com.ibm.is.cc.javastage.api.Capabilities;


/**
 * <code>JavaGetGroupsFormEmail</code> is a program for bean-based transformer stage.
 * It will get the groups for a given email address.
 *
 * @version 1.0
 */
public class JavaGetOwnerAndMembersForGroupName extends Processor
{
   private InputLink m_inputLink;
   private OutputLink m_outputLink;
   private OutputLink m_rejectLink;

   public JavaGetOwnerAndMembersForGroupName() {
      super();
   }

   /**
    * Overrides default values of capabilities.
    * It will check whether this program can be run with the current job design.
    *
    * <li> Minimum number of input links is "1"
    * <li> Maximum number of input links is "1"
    * <li> Minimum number of output stream links is "1"
    * <li> Maximum number of output stream links is "1"
    * <li> Maximum number of reject links is "1"
    * <li> Is Wave Generator : false
    *
    * @return Capabilities
    */
   public Capabilities getCapabilities() {
      Capabilities capabilities = new Capabilities();
      // Set minimum number of input links to 1
      capabilities.setMinimumInputLinkCount(1);
      // Set maximum number of input links to 1
      capabilities.setMaximumInputLinkCount(1);
      // Set minimum number of output stream links to 1
      capabilities.setMinimumOutputStreamLinkCount(1);
      // Set maximum number of output stream links to 1
      capabilities.setMaximumOutputStreamLinkCount(1);
      // Set maximum number of reject links to 1
      capabilities.setMaximumRejectLinkCount(1);
      // Set is Wave Generator to false
      capabilities.setIsWaveGenerator(false);
      return capabilities;
   }

   /**
    * Specifies the current configuration (number and types of links)
    *
    * @param configuration The current configuration(number and types of links, environment)
    * @param isRuntime <code>true</code> if runtime, otherwise <code>false</code>.
    * @return <code>false</code> if found problems with the configurations. Otherwise, returns <code>true</code>.
    */
   public boolean validateConfiguration(Configuration configuration, 
                                        boolean isRuntime) throws Exception
   {
      // Specify current link configurations.
      m_inputLink = configuration.getInputLink(0);
      m_outputLink = configuration.getOutputLink(0);
      
      if (configuration.getRejectLinkCount() == 1) {
         m_rejectLink = m_inputLink.getAssociatedRejectLink();
      }
      
      return true;
   }

   /**
    * Read records from the input and send alternate rows to the reject link, or 
    * stream output link.
    *
    * If the value of the firstname column contains "*" character, mark reject flag and 
    * send the record to reject link. In rejected record, "ERRORTEXT" and "ERRORCODE"
    * fields are added to show the rejected reason.
    *
    * @exception Exception if error occurred during processing records.
    */ 
   public void process() throws Exception
   {
      OutputRecord outputRecord = m_outputLink.getOutputRecord();

      boolean rejectIt;
      // Loop until there is no more input data
      do
      {
         InputRecord record = m_inputLink.readRecord();
        
         if (record == null) {
            // End of data
            break;
         }

         // Get the object from the input row.
         GroupNameBean inputBean = (GroupNameBean) record.getObject();
         
         rejectIt = false;
         
         // Instantiate object and get owner/members
         GetGroupOwnerAndMembersByGroupName grpInfo = new GetGroupOwnerAndMembersByGroupName();
         ArrayList<GroupMemberBean> groupInfoList = grpInfo.getMembers(inputBean.getGroupName());
         
         if (groupInfoList.size() > 0) {
           for (GroupMemberBean grpMemberBean : groupInfoList) {
             outputRecord.putObject(grpMemberBean);
             m_outputLink.writeRecord(outputRecord);
           }
         } 
         else {
           rejectIt = true;
         }
         
         if (rejectIt && m_rejectLink != null)
         {
            // Reject record.  This transfers the row to the reject link.
            // The same kind of forwarding is also possible for regular stream
            // links.
            RejectRecord rejectRecord = m_rejectLink.getRejectRecord(record);

            // Reject record can contain additional columns "ERRORTEXT" and "ERRORCODE".
            // The field will be shown as columns in rejected output records.
            rejectRecord.setErrorText("Error encoding text");
            rejectRecord.setErrorCode(123);
            m_rejectLink.writeRecord(rejectRecord);

         }
      } 
      while (true);
   }

   /**
    * Returns the bean class corresponding to <code>inputLink</code>.
    *
    * @param inputLink {@link Link}
    * @return A {@link Class} of bean class.
    */
   public Class<GroupNameBean> getBeanForInput(Link inputLink)
   {
      return GroupNameBean.class;
   }

   /**
    * Returns the bean class corresponding to <code>outputLink</code>.
    *
    * @param outputLink {@link Link}
    * @return A {@link Class} of bean class.
    */
   public Class<GroupMemberBean> getBeanForOutput(Link outputLink)
   {
      return GroupMemberBean.class;
   }
}