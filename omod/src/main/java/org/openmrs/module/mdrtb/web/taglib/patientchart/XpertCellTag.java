package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;

public class XpertCellTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private List<Xpert> xperts;
	
	//private List<Regimen> regimens;
	
	private String style;
	
	private String clazz;
	
	private String parameters;
	
	// private Concept drug;
	
	private String showTooltip;
	
	public int doStartTag() {
		
		String titleString = "";
		
		String colorString = "";
		
		StringBuilder resultString = new StringBuilder();
		
		String ret = "";
		
		if (xperts == null || xperts.isEmpty()) {
			// handle the null case
			ret = "<td style=\"" + this.style + "\""
			        + ((this.clazz != null && !this.clazz.isEmpty()) ? "class=\"" + this.clazz + "\"" : "") + "/>";
		} else {
			// initialize the rankings used by calculate what color to display in the cell
			Map<Concept, Integer> resultRankings = null;
			
			// used to track the "overall" result, which determines how we decide what color to display
			Concept overallResult = null;
			
			// loop through all the bacteriologies in this set and fetch the result for each of them
			for (Xpert xpert : xperts) {
				if (xpert != null) {
					
					// if there's a valid result....
					if (xpert.getResult() != null) {
						
						// fetch the scanty bacilli/colony number, if relevant
						Concept rifResult = xpert.getRifResistance();
						
						// append the appropriate result to the result list
						resultString.append(xpert.getResult().getName(Context.getLocale()).getName()).append("/")
						        .append(rifResult.getName(Context.getLocale()).getName());
						
						// append the appropriate title to the title list
						titleString = titleString.concat(xpert.getResult().getName(Context.getLocale()).getName() + " - "
						        + xpert.getLab().getDisplayString() + "<br/>");
						
						// now figure the overall result for the purpose of determining the color of the cell
						// TODO: some error handling here if we get a result we don't expect?
						Concept detected = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED);
						Concept notDetected = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED);
						Concept error = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR);
						
						overallResult = xpert.getResult();
						
						if (overallResult.equals(detected)) {
							colorString = "lightcoral";
						}
						
						else if (overallResult.equals(notDetected)) {
							colorString = "lightgreen";
						}
						
						else if (overallResult.equals(error)) {
							colorString = "lightgrey";
						}
						
						else {
							colorString = "lightgrey";
						}
					}
					// if there isn't a result, add the status here
					else {
						titleString = titleString.concat(TestStatusRenderer.renderStandardStatus(xpert) + " - "
						        + xpert.getLab().getDisplayString() + "<br/>");
					}
				}
			}
			
			// set the color based on the overall result
			if (overallResult == null) {
				// if there is some sort of bac here, set the default color to gray (if there is no actual result)
				colorString = "lightgray"; // TODO: make this configurable?
			}
			
			// now create the actual string to render
			// TODO: this is operating on the assumption that all the bacs are from the same specimen
			ret = "<td onmouseover=\"document.body.style.cursor = \'pointer\'\" onmouseout=\"document.body.style.cursor = \'default\'\" "
			        + "onclick=\"window.location = \'../specimen/specimen.form?specimenId="
			        + xperts.get(0).getSpecimenId()
			        + "&testId="
			        + xperts.get(0).getId()
			        + this.parameters
			        + "\'\""
			        + ("true".equalsIgnoreCase(this.showTooltip) ? " title=\"" + titleString + "\"" : "")
			        + "style=\";background-color:"
			        + colorString
			        + ";"
			        + this.style
			        + "\" "
			        + ((this.clazz != null && !this.clazz.isEmpty()) ? "class=\"" + this.clazz + "\"" : "")
			        + ">&nbsp;"
			        + resultString + "&nbsp;</td>";
		}
		
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting tag", ex);
		}
		
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		xperts = null;
		return EVAL_PAGE;
	}
	
	public void setXperts(List<Xpert> xperts) {
		this.xperts = xperts;
	}
	
	public List<Xpert> getXperts() {
		return xperts;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	public String getParameters() {
		return parameters;
	}
	
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public void setShowMouseover(String showTooltip) {
		this.showTooltip = showTooltip;
	}
	
	public String getShowMouseover() {
		return showTooltip;
	}
	
}
