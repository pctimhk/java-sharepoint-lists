package com.powroznik.jsplists;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

public class JSpListsUtils {
	private static final Logger L = Logger.getLogger("JSpLists");

	private static final OMFactory OM_FACTORY = OMAbstractFactory
			.getOMFactory();

	public static void addAttribute(OMElement omElement, String name,
			String value) {
		omElement.addAttribute(name, value, null);
	}

	public static boolean contains(Object[] os, Object o1) {
		for (Object o2 : os) {
			if (equals(o2, o1)) {
				return true;
			}
		}
		return false;
	}

	public static OMElement createElement(OMElement omElement1, String name) {
		OMElement omElement2 = createElement(name);
		omElement1.addChild(omElement2);
		return omElement2;
	}

	public static OMElement createElement(String name) {
		return OM_FACTORY.createOMElement(new QName(name));
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	public static boolean equalsIgnoreCase(String s, String t) {
		if (s == null && t == null) {
			return true;
		}
		if (s == null || t == null) {
			return false;
		}
		return s.equalsIgnoreCase(t);
	}

	public static String getAttributeValue(OMElement omElement, String name) {
		if (omElement == null) {
			return null;
		}
		return omElement.getAttributeValue(new QName(name));
	}

	public static String getComparisonElemName(int comparison) {
		if (comparison == JSpListsConstants.COMPARISON_EQ) {
			return "Eq";
		}
		if (comparison == JSpListsConstants.COMPARISON_GE) {
			return "Geq";
		}
		if (comparison == JSpListsConstants.COMPARISON_GT) {
			return "Gt";
		}
		if (comparison == JSpListsConstants.COMPARISON_LE) {
			return "Leq";
		}
		if (comparison == JSpListsConstants.COMPARISON_LT) {
			return "Lt";
		}
		throw new RuntimeException();
	}

	public static OMElement getFirstElement(OMElement omElement) {
		if (omElement == null) {
			return null;
		}
		return omElement.getFirstElement();
	}

	public static OMElement getFirstElement(OMElement omElement1, String name) {
		// log("omElement1 = " + omElement1);
		if (omElement1 == null) {
			return null;
		}
		Iterator<OMElement> iter = omElement1.getChildElements();
		while (iter.hasNext()) {
			OMElement omElement2 = iter.next();
			if (equalsIgnoreCase(omElement2.getQName().getLocalPart(), name)) {
				return omElement2;
			}
		}
		return null;

	}

	public static String getText(OMElement elem) {
		if (elem == null) {
			return null;
		}
		return elem.getText();
	}

	public static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static void log(String msg) {
		L.info(msg);
	}

	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}
}
