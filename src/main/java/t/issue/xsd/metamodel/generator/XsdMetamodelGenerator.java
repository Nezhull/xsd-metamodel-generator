package t.issue.xsd.metamodel.generator;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroup.Compositor;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;

import t.issue.xsd.metamodel.generator.model.ComplexProperty;
import t.issue.xsd.metamodel.generator.model.ComplexType;
import t.issue.xsd.metamodel.generator.model.CompositeProperty;
import t.issue.xsd.metamodel.generator.model.CompositionType;
import t.issue.xsd.metamodel.generator.model.SimpleProperty;
import t.issue.xsd.metamodel.generator.model.SimpleTypeConstraint;
import t.issue.xsd.metamodel.generator.utils.ReflectionUtils;

/**
 * Xml schema definition metamodel generator class
 *
 * @author Pavel
 */
public class XsdMetamodelGenerator {
    private static Log log = LogFactory.getLog(XsdMetamodelGenerator.class);

    private Map<Class<?>, ComplexType> complexTypes = new HashMap<Class<?>, ComplexType>();

    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;

    private boolean throwExceptions = true;

    private XSSchemaSet schemaSet = null;

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public boolean isThrowExceptions() {
        return throwExceptions;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }

    /**
     * Method generates jaxb metamodel, that maps xsd schema with java classes. If schema imports other schemas, it is required
     * to set {@link EntityResolver} that resolves schema names to physical files. If required it is possible to provide custom
     * {@link ErrorHandler} to handle internal generator errors
     *
     * @param xsdSchemaStream {@link InputStream} pointing to xsd schema
     * @param objectType      {@link Class} of jaxb object representing xml
     * @return {@link ComplexType} containing full metamodel of concrete xsd complex type
     * @throws MetamodelGenerationException if not possible to map jaxb object to provided xsd schema
     */
    public ComplexType generateMetamodel(InputStream xsdSchemaStream, Class<?> objectType) throws MetamodelGenerationException {
        if (xsdSchemaStream == null) {
            processError("Argument schemaStream is required");
        }

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);

        XSOMParser parser = new XSOMParser(saxParserFactory);
        parser.setEntityResolver(entityResolver);
        parser.setErrorHandler(errorHandler);

        InputSource source = new InputSource(xsdSchemaStream);
        source.setSystemId("xsd-stream");

        try {
            parser.parse(source);
            schemaSet = parser.getResult();
        } catch (SAXException e) {
            processError(e);
        }

        if (schemaSet == null) {
            processError("No schemas in stream");
        }

        XSComplexType mainType = getMainType(objectType);
        if (mainType != null) {
            try {
                return buildType(objectType, mainType, null);
            } catch (SecurityException e) {
                processError(e);
            }
        } else {
            processError("Can not detect main xml type in class: " + objectType.getName());
        }

        return null;
    }

    private ComplexType buildType(Class<?> clazz, XSType xtype, String elementName) {
        if (complexTypes.containsKey(clazz)) {
            return complexTypes.get(clazz);
        } else {
            ComplexType type = new ComplexType();
            type.setTypeClass(clazz);
            XSType someType = xtype != null ? xtype : getType(clazz, elementName);
            if (someType != null && someType.isComplexType()) {
                XSModelGroup modelGroup = extractModelGroup(someType.asComplexType());
                XSComplexType complexType = xtype.asComplexType();
                if (modelGroup != null) {
                    type.setContents(buildCompositProperty(clazz, modelGroup, complexType.getName()));
                }
                type.setTypeName(complexType.getName() != null ? complexType.getName() : elementName);
                for (Field field : ReflectionUtils.getAllFields(clazz)) {
                    XmlValue value = field.getAnnotation(XmlValue.class);
                    if (value != null) {
                        XSSimpleType simpleType = complexType.getBaseType().asSimpleType();
                        if (simpleType != null) {
                            SimpleProperty simpleProperty = buildBasicSimpleProperty(field, simpleType);
                            type.setValue(simpleProperty);
                        }
                    }
                }
                for (XSAttributeUse attributeUse : complexType.getAttributeUses()) {
                    XSAttributeDecl attrDecl = attributeUse.getDecl();
                    Field field = getAttributeField(clazz, attrDecl.getName());
                    if (field != null) {
                        SimpleProperty simpleProperty = buildBasicSimpleProperty(field, attrDecl.getType());
                        type.getAttributes().add(simpleProperty);
                    }
                }
            }
            complexTypes.put(clazz, type);
            return type;
        }
    }

    private CompositeProperty buildCompositProperty(Class<?> clazz, XSModelGroup modelGroup, String parentTypeName) {
        CompositeProperty compositeProperty = new CompositeProperty();
        compositeProperty.setType(resolveCompositionType(modelGroup.getCompositor()));
        compositeProperty.setParentTypeName(parentTypeName);
        for (XSParticle particle : modelGroup.getChildren()) {
            XSTerm term = particle.getTerm();
            if (term != null && term.isElementDecl()) {
                if (term.asElementDecl().getType().isComplexType()) {
                    compositeProperty.getProperties().add(buildComplexProperty(clazz, term.asElementDecl(), particle.getMinOccurs(), particle.getMaxOccurs()));
                } else if (term.asElementDecl().getType().isSimpleType()) {
                    compositeProperty.getProperties().add(buildSimpleProperty(clazz, term.asElementDecl(), particle.getMinOccurs(), particle.getMaxOccurs()));
                }
            } else if (term != null && term.isModelGroup()) {
                compositeProperty.getProperties().add(buildCompositProperty(clazz, term.asModelGroup(), parentTypeName));
            }
        }
        return compositeProperty;
    }

    private CompositionType resolveCompositionType(Compositor compositor) {
        if (compositor == null) {
            return null;
        }
        switch (compositor) {
            case ALL:
                return CompositionType.ALL;
            case CHOICE:
                return CompositionType.CHOISE;
            case SEQUENCE:
                return CompositionType.SEQUENCE;
            default:
                return null;
        }
    }

    private ComplexProperty buildComplexProperty(Class<?> clazz, XSElementDecl elementDecl, BigInteger minCount, BigInteger maxCount) {
        XSComplexType complexType = elementDecl.getType().asComplexType();
        Field field = getElementField(clazz, elementDecl.getName() != null && !elementDecl.getName().isEmpty() ? elementDecl.getName() : complexType.getName());
        if (field != null) {
            ComplexProperty complexProperty = new ComplexProperty();
            complexProperty.setFieldName(field.getName());
            complexProperty.setMinCount(minCount.longValue());
            complexProperty.setMaxCount(maxCount.longValue() != -1 ? maxCount.longValue() : null);
            complexProperty.setRequired(complexProperty.getMinCount() != 0);
            if (complexProperty.getMaxCount() == null || complexProperty.getMaxCount() > 1) {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                complexProperty.setComplexType(buildType((Class<?>) type.getActualTypeArguments()[0], complexType, elementDecl.getName()));
            } else {
                complexProperty.setComplexType(buildType(field.getType(), complexType, elementDecl.getName()));
            }
            return complexProperty;
        }
        return null;
    }

    private Field getElementField(Class<?> clazz, String elementName) {
        if (clazz != null) {
            List<Field> fields = ReflectionUtils.getAllFields(clazz);
            for (Field field : fields) {
                XmlElement element = field.getAnnotation(XmlElement.class);
                if (element != null && element.name().equals(elementName)) {
                    return field;
                }
            }
            for (Field field : fields) {
                if (field.getName().equals(elementName)) {
                    return field;
                }
            }
        }
        return null;
    }

    private Field getAttributeField(Class<?> clazz, String attributeName) {
        if (clazz != null) {
            List<Field> fields = ReflectionUtils.getAllFields(clazz);
            for (Field field : fields) {
                XmlAttribute element = field.getAnnotation(XmlAttribute.class);
                if (element != null && element.name().equals(attributeName)) {
                    return field;
                }
            }
            for (Field field : fields) {
                if (field.getName().equals(attributeName)) {
                    return field;
                }
            }
        }
        return null;
    }

    private SimpleProperty buildSimpleProperty(Class<?> clazz, XSElementDecl elementDecl, BigInteger minCount, BigInteger maxCount) {
        XSSimpleType simpleType = elementDecl.getType().asSimpleType();
        Field field = getElementField(clazz, elementDecl.getName() != null && !elementDecl.getName().isEmpty() ? elementDecl.getName() : simpleType.getName());
        if (field != null) {
            SimpleProperty simpleProperty = buildBasicSimpleProperty(field, simpleType);
            simpleProperty.setMinCount(minCount.longValue());
            simpleProperty.setMaxCount(maxCount.longValue() != -1 ? maxCount.longValue() : null);
            simpleProperty.setRequired(simpleProperty.getMinCount() != 0);
            if (simpleProperty.getMaxCount() == null || simpleProperty.getMaxCount() > 1) {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                simpleProperty.setFieldType((Class<?>) type.getActualTypeArguments()[0]);
            }
            return simpleProperty;
        }
        return null;
    }

    private SimpleProperty buildBasicSimpleProperty(Field field, XSSimpleType simpleType) {
        SimpleProperty simpleProperty = new SimpleProperty();
        simpleProperty.setFieldName(field.getName());
        simpleProperty.setTypeName(simpleType.getName());
        simpleProperty.setFieldType(field.getType());
        simpleProperty.setConstraints(buildConstraint(simpleType));
        return simpleProperty;
    }

    public SimpleTypeConstraint buildConstraint(XSSimpleType simpleType) {
        SimpleTypeConstraint constraint = new SimpleTypeConstraint();
        List<String> enumeration = new ArrayList<String>();
        for (XSFacet facet : extractAllFasets(simpleType)) {
            if (facet.getName().equals(XSFacet.FACET_ENUMERATION)) {
                enumeration.add(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_MAXINCLUSIVE)) {
                constraint.setMaxValue(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_MININCLUSIVE)) {
                constraint.setMinValue(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_MAXEXCLUSIVE)) {
                constraint.setMaxValue(String.valueOf(Integer.parseInt(facet.getValue().value) - 1));
            } else if (facet.getName().equals(XSFacet.FACET_MINEXCLUSIVE)) {
                constraint.setMinValue(String.valueOf(Integer.parseInt(facet.getValue().value) + 1));
            } else if (facet.getName().equals(XSFacet.FACET_LENGTH)) {
                constraint.setLength(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_MAXLENGTH)) {
                constraint.setMaxLength(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_MINLENGTH)) {
                constraint.setMinLength(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_PATTERN)) {
                constraint.setPattern(facet.getValue().value);
            } else if (facet.getName().equals(XSFacet.FACET_TOTALDIGITS)) {
                constraint.setTotalDigits(facet.getValue().value);
            }
        }
        if (enumeration.size() > 0) {
            constraint.setEnumeration(enumeration.toArray(new String[]{}));
        }
        return constraint;
    }

    private List<XSFacet> extractAllFasets(XSSimpleType simpleType) {
        List<XSFacet> facets = new ArrayList<XSFacet>();
        List<XSSimpleType> allTypes = new ArrayList<XSSimpleType>();
        allTypes.add(simpleType);
        XSSimpleType lastType = simpleType;
        while (lastType.getSimpleBaseType() != null) {
            allTypes.add(lastType.getSimpleBaseType());
            lastType = lastType.getSimpleBaseType();
        }
        ListIterator<XSSimpleType> iterator = allTypes.listIterator(allTypes.size());
        while (iterator.hasPrevious()) {
            XSRestrictionSimpleType restriction = iterator.previous().asRestriction();
            if (restriction != null) {
                facets.addAll(restriction.getDeclaredFacets());
            }
        }
        return facets;
    }

    private XSModelGroup extractModelGroup(XSComplexType complexType) {
        XSParticle particle = complexType.getContentType().asParticle();
        if (particle != null) {
            XSTerm term = particle.getTerm();
            if (term != null) {
                if (term.isModelGroup()) {
                    return term.asModelGroup();
                }
            }
        }
        return null;
    }

    private XSType getType(Class<?> clazz, String elementName) {
        XmlType type = clazz.getAnnotation(XmlType.class);
        if (type != null) {
            if (type.name() != null && !type.name().isEmpty()) {
                return getType(type.name());
            }
        }
        if ("##default".equals(elementName)) {
            elementName = clazz.getSimpleName();
        }
        XSElementDecl element = getElement(elementName);
        if (element != null) {
            return element.getType();
        } else {
            return null;
        }
    }

    private XSComplexType getMainType(Class<?> objectType) {
        XmlType type = objectType.getAnnotation(XmlType.class);
        XmlRootElement root = objectType.getAnnotation(XmlRootElement.class);
        if (type != null) {
            String mainTypeName = type.name();
            if (mainTypeName != null && !mainTypeName.isEmpty()) {
                XSType mainType = getType(mainTypeName);
                return mainType != null && mainType.isComplexType() ? mainType.asComplexType() : null;
            } else {
                if (root != null) {
                    if ("##default".equals(root.name())) {
                        mainTypeName = objectType.getSimpleName();
                    } else {
                        mainTypeName = root.name();
                    }
                    XSElementDecl element = getElement(mainTypeName);
                    if (element != null) {
                        XSType mainType = element.getType();
                        return mainType != null && mainType.isComplexType() ? mainType.asComplexType() : null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private XSType getType(String name) {
        for (XSSchema schema : schemaSet.getSchemas()) {
            XSType type = schema.getType(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    private XSElementDecl getElement(String name) {
        for (XSSchema schema : schemaSet.getSchemas()) {
            XSElementDecl element = schema.getElementDecl(name);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    private void processError(String message) throws MetamodelGenerationException {
        MetamodelGenerationException ex = new MetamodelGenerationException(message);
        log.error(message, ex);
        if (throwExceptions) {
            throw ex;
        }
    }

    private void processError(Throwable th) throws MetamodelGenerationException {
        MetamodelGenerationException ex = new MetamodelGenerationException(th.getMessage());
        log.error(th.getMessage(), ex);
        if (throwExceptions) {
            throw ex;
        }
    }

}
