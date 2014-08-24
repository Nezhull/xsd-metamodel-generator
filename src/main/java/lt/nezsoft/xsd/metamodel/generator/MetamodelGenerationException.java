package lt.nezsoft.xsd.metamodel.generator;

public class MetamodelGenerationException extends RuntimeException {
	private static final long serialVersionUID = 9188703278887272926L;

	public MetamodelGenerationException() {
		super();
	}

	public MetamodelGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetamodelGenerationException(String message) {
		super(message);
	}

	public MetamodelGenerationException(Throwable cause) {
		super(cause);
	}
	
}
