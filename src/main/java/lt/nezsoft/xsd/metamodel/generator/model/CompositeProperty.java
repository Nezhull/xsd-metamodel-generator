package lt.nezsoft.xsd.metamodel.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing xsd composition
 * 
 * @author Pavel
 *
 */
public class CompositeProperty extends Property {
	private static final long serialVersionUID = 8057725641366663776L;
	
	private String parentTypeName;
	private List<Property> properties = new ArrayList<Property>();
	private CompositionType type;
	
	/**
	 * Gets internal properties of this composition
	 * 
	 * @return {@link List} containing internal properties
	 */
	public List<Property> getProperties() {
		return properties;
	}
	
	/**
	 * Gets type of this composition
	 * 
	 * @return composition type as {@link CompositionType} enumeration value
	 */
	public CompositionType getType() {
		return type;
	}
	
	/**
	 * Gets parent complex type name of this composition
	 * 
	 * @return parent complex type name
	 */
	public String getParentTypeName() {
		return parentTypeName;
	}
	
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	public void setType(CompositionType type) {
		this.type = type;
	}

	public void setParentTypeName(String parentTypeName) {
		this.parentTypeName = parentTypeName;
	}
	
}
