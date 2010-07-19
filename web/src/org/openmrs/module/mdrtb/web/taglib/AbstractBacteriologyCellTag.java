package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.module.mdrtb.MdrtbUtil;


public abstract class AbstractBacteriologyCellTag extends TagSupport {

    private static final long serialVersionUID = 6161336882128086538L;
    
	private final Log log = LogFactory.getLog(getClass());
   
    protected void renderCell(Concept result) {
    	String ret = null;
    	
    	if(result != null) {	
    		String color = MdrtbUtil.getColorForConcept(result);
    		if (color == null) {
    			color = "black";
    		}
    		
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;background-color:" + color + ";\">&nbsp</td>";
    	}
    	else {
    		// handle the null case
    		ret = "<td style=\"padding:0px;border:0px;margin:0px;\"/>";
    	}
    		
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting tag", ex);
		}
    }
}
