package lt.nezsoft.xsd.metamodel.generator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Class representing xsd complex type
 * 
 * @author Pavel
 *
 */
public class ComplexType implements Serializable {
	private static final long serialVersionUID = -6398777892344854896L;
	
	private List<SimpleProperty> attributes = new ArrayList<SimpleProperty>();
	private CompositeProperty contents;
	private Class<?> typeClass;
	private String typeName;
	private SimpleProperty value;
	
	/**
	 * Gets complex type attributes
	 * 
	 * @return {@link List} of {@link SimpleProperty} attributes
	 */
	public List<SimpleProperty> getAttributes() {
		return attributes;
	}
	
	/**
	 * Gets contents of complex type
	 * 
	 * @return {@link CompositeProperty} containing contents of complex type
	 */
	public CompositeProperty getContents() {
		return contents;
	}

	/**
	 * Gets jaxb class of this complex type
	 * 
	 * @return jaxb object {@link Class}
	 */
	public Class<?> getTypeClass() {
		return typeClass;
	}

	/**
	 * Gets value of complex type
	 * 
	 * @return value of complex type as {@link SimpleProperty}
	 */
	public SimpleProperty getValue() {
		return value;
	}
	
	/**
	 * Gets xsd complex type name defined in the xsd
	 * 
	 * @return complex type name defined in the xsd
	 */
	public String getTypeName() {
		return typeName;
	}

	public void setAttributes(List<SimpleProperty> attributes) {
		this.attributes = attributes;
	}

	public void setContents(CompositeProperty contents) {
		this.contents = contents;
	}

	public void setTypeClass(Class<?> typeClass) {
		this.typeClass = typeClass;
	}

	public void setValue(SimpleProperty value) {
		this.value = value;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
