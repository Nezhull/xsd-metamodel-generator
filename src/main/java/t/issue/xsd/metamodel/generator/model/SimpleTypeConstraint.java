package t.issue.xsd.metamodel.generator.model;

import java.io.Serializable;

/**
 * Class representing {@link SimpleProperty} constraints
 * 
 * @author Pavel
 *
 */
public class SimpleTypeConstraint implements Serializable {
	private static final long serialVersionUID = -7571915497288400745L;
	
	private String[] enumeration = null;
	private String length = null;
	private String maxLength = null;
	private String maxValue = null;
	private String minLength = null;
	private String minValue = null;
	private String pattern = null;
	private String totalDigits = null;

	public String[] getEnumeration() {
		return enumeration;
	}

	public String getLength() {
		return length;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public String getMinLength() {
		return minLength;
	}

	public String getMinValue() {
		return minValue;
	}

	public String getPattern() {
		return pattern;
	}

	public String getTotalDigits() {
		return totalDigits;
	}

	public void setEnumeration(String[] enumeration) {
		this.enumeration = enumeration;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setTotalDigits(String totalDigits) {
		this.totalDigits = totalDigits;
	}
	
}
