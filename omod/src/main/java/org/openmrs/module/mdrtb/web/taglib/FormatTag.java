/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsUtil;

public class FormatTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	protected static final Log log = LogFactory.getLog(FormatTag.class);
	
	//***** PROPERTIES *****
	
	private Object obj;
	
	private String separator;
	
	private String defaultVal;
	
	private String nameType; // for concepts, specifies the concept name type to use when fetching the concept name; default is short
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Does the actual formatting of each object
	 */
	public String formatObject(Object o) {
		if (ObjectUtil.notNull(o)) {
			if (o instanceof Regimen) {
				Regimen r = (Regimen) o;
				return RegimenUtils.formatRegimenGenerics(r, separator, ObjectUtil.nvlStr(defaultVal, "mdrtb.none"));
			} else if (o instanceof Concept) {
				Concept concept = (Concept) o;
				ConceptName name = null;
				
				if (StringUtils.isNotBlank(nameType)) {
					name = MdrtbUtil.getConceptName(concept, Context.getLocale().getLanguage(),
					    ConceptNameType.valueOf(nameType.toUpperCase()));
				}
				
				// if we haven't found a name yet, just get the best short name
				if (name == null) {
					name = MdrtbUtil.getConceptName(concept, Context.getLocale().getLanguage(),
					    ConceptNameType.FULLY_SPECIFIED);
				}
				
				return name != null ? name.getName() : "";
			} else if (o instanceof Obs) {
				Obs obs = (Obs) o;
				return obs.getValueAsString(Context.getLocale());
			} else if (o instanceof String) {
				// turn line breaks into <br/> for HTML display
				String str = (String) o;
				str = str.replace("\r\n", "<br/>");
				str = str.replace("\n", "<br/>");
				str = str.replace("\r", "<br/>");
				return str;
			} else {
				return o.toString();
			}
		}
		return MessageUtil.translate(defaultVal);
	}
	
	/**
	 * @see Tag#doStartTag()
	 */
	@SuppressWarnings("unchecked")
	public int doStartTag() {
		
		String ret = (ObjectUtil.notNull(defaultVal) ? MessageUtil.translate(defaultVal) : "");
		if (obj != null) {
			if (obj instanceof Collection<?>) {
				Collection<?> l = (Collection<?>) obj;
				if (!l.isEmpty()) {
					Object o = l.iterator().next();
					if (o instanceof Concept) {
						ret = RegimenUtils.formatConcepts((Collection<Concept>) l, separator, defaultVal);
					} else if (o instanceof DrugOrder) {
						ret = RegimenUtils.formatDrugOrders(((Collection<DrugOrder>) l), separator, defaultVal);
					} else {
						List<String> s = new ArrayList<>();
						for (Object item : l) {
							s.add(formatObject(item));
						}
						ret = OpenmrsUtil.join(s, separator);
					}
				}
			} else {
				ret = formatObject(obj);
			}
		}
		try {
			if (ret != null) {
				JspWriter w = pageContext.getOut();
				w.println(ret);
			}
		}
		catch (IOException ex) {
			log.error("Error while starting duration tag", ex);
		}
		return SKIP_BODY;
	}
	
	/**
	 * @see Tag#doEndTag()
	 */
	public int doEndTag() {
		obj = null;
		separator = null;
		defaultVal = null;
		nameType = null;
		return EVAL_PAGE;
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}
	
	/**
	 * @param obj the obj to set
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	/**
	 * @return the defaultVal
	 */
	public String getDefaultVal() {
		return defaultVal;
	}
	
	/**
	 * @param defaultVal the defaultVal to set
	 */
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	
	public String getNameType() {
		return nameType;
	}
	
	public void setNameType(String nameType) {
		this.nameType = nameType;
	}
}
