package org.openmrs.module.mdrtb.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.LinkExt;

public class MdrtbGutterItem extends LinkExt {
	
	String url = "module/mdrtb/mdrtb.form";
	
	String label = "mdrtb.title";
	
	public String getLabel() {
		return this.label;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
	 * Returns the required privilege in order to see this section. Can be a comma delimited list of
	 * privileges. If the default empty string is returned, only an authenticated user is required
	 * 
	 * @return Privilege string
	 */
	public String getRequiredPrivilege() {
		return "";
	}
	
}
