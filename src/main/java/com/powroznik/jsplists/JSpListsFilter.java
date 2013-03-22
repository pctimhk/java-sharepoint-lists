package com.powroznik.jsplists;

public class JSpListsFilter {
	private String displayName;

	private int comparison;

	private Object value;

	public JSpListsFilter(String displayName, int comparison, Object value) {
		this.displayName = displayName;
		this.comparison = comparison;
		this.value = value;
	}

	public int getComparison() {
		return comparison;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Object getValue() {
		return value;
	}
}
