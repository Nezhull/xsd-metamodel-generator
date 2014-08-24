package lt.nezsoft.xsd.metamodel.generator.model;


/**
 * Class representing simple type property
 * 
 * @author Pavel
 *
 */
public class SimpleProperty extends Property {
	private static final long serialVersionUID = -6124160118765852754L;
	
	private SimpleTypeConstraint constraints;
	private String fieldName;
	private String typeName;
	private Class<?> fieldType;

	/**
	 * Gets constraints of simple type
	 * 
	 * @return {@link SimpleTypeConstraint} containing constraints of simple type
	 */
	public SimpleTypeConstraint getConstraints() {
		return constraints;
	}

	/**
	 * Gets field name containing this simple type
	 * 
	 * @return field name
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * Gets name of this simple type
	 * 
	 * @return type name
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Gets field type
	 * 
	 * @return {@link Class} representing field type
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setConstraints(SimpleTypeConstraint constraints) {
		this.constraints = constraints;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
