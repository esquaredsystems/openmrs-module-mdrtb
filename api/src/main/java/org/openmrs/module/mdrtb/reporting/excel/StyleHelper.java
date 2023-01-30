package org.openmrs.module.mdrtb.reporting.excel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.openmrs.util.OpenmrsUtil;

public class StyleHelper {
	
	HSSFWorkbook wb;
	
	Map<String, HSSFFont> fonts = new HashMap<String, HSSFFont>();
	
	Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>();
	
	Collection<String> fontAttributeNames = new HashSet<String>();
	
	Collection<String> fontAttributeStarting = new ArrayList<String>();
	
	short dateDataFormat;
	
	static Map<String, Short> backgroundColors = new HashMap<String, Short>();
	static {
		backgroundColors.put("grey", new Short(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex()));
	}
	
	public StyleHelper(HSSFWorkbook wb) {
		this.wb = wb;
		fontAttributeNames.add("bold");
		fontAttributeNames.add("italic");
		fontAttributeNames.add("underline");
		fontAttributeStarting.add("size=");
		HSSFDataFormat df = wb.createDataFormat();
		dateDataFormat = df.getFormat("d-mmm-yy");
	}
	
	public HSSFFont getFont(String s) {
		SortedSet<String> att = new TreeSet<String>();
		for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens();) {
			String str = st.nextToken().trim().toLowerCase();
			if (str.equals("")) {
				continue;
			}
			att.add(str);
		}
		String descriptor = OpenmrsUtil.join(att, ",");
		if (styles.containsKey(descriptor)) {
			return (HSSFFont) fonts.get(descriptor);
		} else {
			HSSFFont font = wb.createFont();
			for (Iterator<String> i = att.iterator(); i.hasNext();) {
				String str = (String) i.next();
				if (str.equals("bold")) {
					font.setBold(true);
				} else if (str.equals("italic")) {
					font.setItalic(true);
				} else if (str.equals("underline")) {
					font.setUnderline(HSSFFont.U_SINGLE);
				} else if (str.startsWith("size=")) {
					str = str.substring(5);
					font.setFontHeightInPoints(Short.parseShort(str));
				}
			}
			fonts.put(descriptor, font);
			return font;
		}
	}
	
	// pass a comma-separated string containing attributes:
	//    bold
	//    italic
	//    underline
	//    size=##
	//    wraptext
	//    border=all | bottom | top | left | right
	//    align=center | left | right | fill
	//    date
	//    bgcolor=grey
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HSSFCellStyle getStyle(String s) {
		SortedSet att = new TreeSet();
		SortedSet fontAtts = new TreeSet();
		for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens();) {
			String str = st.nextToken().trim().toLowerCase();
			if (str.equals("")) {
				continue;
			}
			boolean isFont = false;
			if (fontAttributeNames.contains(str)) {
				isFont = true;
			} else {
				for (Iterator i = fontAttributeStarting.iterator(); i.hasNext();) {
					if (str.startsWith((String) i.next())) {
						isFont = true;
						break;
					}
				}
			}
			(isFont ? fontAtts : att).add(str);
		}
		SortedSet allAtts = new TreeSet();
		allAtts.addAll(att);
		allAtts.addAll(fontAtts);
		String descriptor = OpenmrsUtil.join(allAtts, ",");
		if (styles.containsKey(descriptor)) {
			return (HSSFCellStyle) styles.get(descriptor);
		} else {
			HSSFCellStyle style = wb.createCellStyle();
			if (fontAtts.size() > 0) {
				HSSFFont font = getFont(OpenmrsUtil.join(fontAtts, ","));
				style.setFont(font);
			}
			for (Iterator<?> i = att.iterator(); i.hasNext();) {
				helper(style, (String) i.next());
			}
			styles.put(descriptor, style);
			return style;
		}
	}
	
	public HSSFCellStyle getAugmented(HSSFCellStyle style, String s) {
		String desc = null;
		for (Iterator<?> i = styles.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			if (e.getValue().equals(style)) {
				desc = (String) e.getKey();
			}
		}
		if (desc == null) {
			throw new IllegalArgumentException(
			        "StyleHelper.getAugmented() can only take a style registered with this StyleHelper");
		}
		if (desc.equals("")) {
			desc = s;
		} else {
			desc += "," + s;
		}
		return getStyle(desc);
	}
	
	private void helper(HSSFCellStyle style, String s) {
		if (s.equals("wraptext")) {
			style.setWrapText(true);
		} else if (s.startsWith("align=")) {
			s = s.substring(6);
			if (s.equals("left")) {
				style.setAlignment(HorizontalAlignment.LEFT);
			} else if (s.equals("center")) {
				style.setAlignment(HorizontalAlignment.CENTER);
			} else if (s.equals("right")) {
				style.setAlignment(HorizontalAlignment.RIGHT);
			} else if (s.equals("fill")) {
				style.setAlignment(HorizontalAlignment.FILL);
			}
		} else if (s.startsWith("border=")) {
			s = s.substring(7);
			BorderStyle borderWeight = BorderStyle.THIN;
			if (s.indexOf("thick") != -1) {
				borderWeight = BorderStyle.THICK;
			} else if (s.indexOf("medium") != -1) {
				borderWeight = BorderStyle.MEDIUM;
			}
			if (s.indexOf("all") != -1) {
				style.setBorderTop(borderWeight);
				style.setBorderBottom(borderWeight);
				style.setBorderLeft(borderWeight);
				style.setBorderRight(borderWeight);
			} else if (s.indexOf("top") != -1) {
				style.setBorderTop(borderWeight);
			} else if (s.indexOf("bottom") != -1) {
				style.setBorderBottom(borderWeight);
			} else if (s.indexOf("left") != -1) {
				style.setBorderLeft(borderWeight);
			} else if (s.indexOf("right") != -1) {
				style.setBorderRight(borderWeight);
			}
		} else if (s.equals("date")) {
			style.setDataFormat(dateDataFormat);
		} else if (s.startsWith("date=")) {
			s = s.substring(5);
			HSSFDataFormat df = wb.createDataFormat();
			dateDataFormat = df.getFormat(s);
			style.setDataFormat(dateDataFormat);
		} else if (s.startsWith("bgcolor=")) {
			s = s.substring(8);
			if (backgroundColors.containsKey(s)) {
				style.setFillForegroundColor(((Short) backgroundColors.get(s)).shortValue());
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
		}
	}
}
