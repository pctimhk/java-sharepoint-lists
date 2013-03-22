package com.powroznik.jsplists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.axiom.om.OMElement;

public class JSpListsDefaultConverter implements JSpListsConverter {
	private Map<String, String> fieldTypes;

	private OMElement fieldElem;

	public JSpListsDefaultConverter(Map<String, String> fieldTypes,
			OMElement fieldElem) {
		this.fieldTypes = fieldTypes;
		this.fieldElem = fieldElem;
	}

	@Override
	public String object2String(Object o) {
		if (o instanceof String) {
			o = string2object((String) o);
		}

		if (o instanceof Boolean) {
			return JSpListsUtils.equals(o, Boolean.TRUE) ? "TRUE" : "FALSE";
		}
		if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
				.getAttributeValue(fieldElem, "DisplayName")), "DateTime")) {
			return JSpListsConstants.DATE_FORMAT_1.format(o);
		}
		if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
				.getAttributeValue(fieldElem, "DisplayName")), "Lookup")) {
			StringBuffer sb = new StringBuffer();
			sb.append(JSpListsUtils.toString(o));
			sb.append(";#");
			return sb.toString();
		}
		if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
				.getAttributeValue(fieldElem, "DisplayName")), "LookupMulti")) {
			StringBuffer sb = new StringBuffer();
			for (Long l : (List<Long>) o) {
				if (sb.length() != 0) {
					sb.append(";#");
				}
				sb.append(JSpListsUtils.toString(l));
				sb.append(";#");
			}
			JSpListsUtils.log("XXX" + sb.toString());
			return sb.toString();
		}

		return JSpListsUtils.toString(o);
	}

	@Override
	public Object string2object(String s) {
		try {
			if (JSpListsUtils.isBlank(s)) {
				return null;
			}

			if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
					.getAttributeValue(fieldElem, "DisplayName")), "Boolean")) {
				return JSpListsUtils.equalsIgnoreCase(s, "TRUE");
			}
			if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
					.getAttributeValue(fieldElem, "DisplayName")), "DateTime")) {
				int i = s.indexOf(' ');
				String t = s.substring(0, i) + 'T' + s.substring(i + 1) + 'Z';
				return JSpListsConstants.DATE_FORMAT_1.parseObject(t);
			}
			if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
					.getAttributeValue(fieldElem, "DisplayName")), "Lookup")) {
				int i = s.indexOf(';');
				return Long.parseLong(s.substring(0, i));
			}
			if (JSpListsUtils.equalsIgnoreCase(fieldTypes.get(JSpListsUtils
					.getAttributeValue(fieldElem, "DisplayName")),
					"LookupMulti")) {
				StringTokenizer st = new StringTokenizer(s, ";");
				List<Long> list = new ArrayList<Long>();
				while (st.hasMoreElements()) {
					String s1 = st.nextToken();
					if (s1.charAt(0) == '#') {
						s1 = s1.substring(1);
					}
					list.add(Long.parseLong(s1));
					String s2 = st.nextToken();
				}
				return list;
			}

			return s;
		} catch (Exception e) {
			throw new RuntimeException("Could not convert string " + s
					+ " to object", e);
		}
	}
}
