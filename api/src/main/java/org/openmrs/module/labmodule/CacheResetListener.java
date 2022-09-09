package org.openmrs.module.labmodule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;


public class CacheResetListener implements GlobalPropertyListener {

	protected final Log log = LogFactory.getLog(getClass());
	

    public boolean supportsPropertyName(String property) {   	
	    return property != null && property.equals("mdrtb.colorMap");
    }
	
	
	public void globalPropertyChanged(GlobalProperty property) {
		if (property.getProperty() != null) {
			if (property.getProperty().equals("mdrtb.colorMap")) {
				Context.getService(TbService.class).resetColorMapCache();
			}
		}
	    
    }

    public void globalPropertyDeleted(String property) {
	    throw new MdrtbAPIException("Required global property has been deleted.");
    }

}
