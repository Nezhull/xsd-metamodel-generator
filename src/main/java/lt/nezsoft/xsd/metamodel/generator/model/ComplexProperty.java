package lt.nezsoft.xsd.metamodel.generator.model;

/**
 * Class representing complex type property
 * 
 * @author Pavel
 *
 */
public class ComplexProperty extends Property {
	private static final long serialVersionUID = 825691398576146543L;

	private ComplexType complexType;
	private String fieldName;
	
	/**
	 * Gets type of current property
	 * 
	 * @return {@link ComplexType} of current property
	 */
	public ComplexType getComplexType() {
		return complexType;
	}
	
	/**
	 * Gets class field name
	 * 
	 * @return field name
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	public void setComplexType(ComplexType complexType) {
		this.complexType = complexType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
