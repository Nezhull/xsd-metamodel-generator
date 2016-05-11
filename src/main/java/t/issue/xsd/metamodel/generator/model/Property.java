package t.issue.xsd.metamodel.generator.model;

import java.io.Serializable;

public abstract class Property implements Serializable {
	private static final long serialVersionUID = -1090034906384203861L;
	
	private boolean required = true;
	private Long minCount = Long.valueOf(1);
	private Long maxCount = Long.valueOf(1);

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}

	public Long getMaxCount() {
		return maxCount;
	}

	public void setMinCount(Long minCount) {
		this.minCount = minCount;
	}

	public Long getMinCount() {
		return minCount;
	}
	
}
