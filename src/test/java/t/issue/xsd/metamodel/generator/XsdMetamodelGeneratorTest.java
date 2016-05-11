package t.issue.xsd.metamodel.generator;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import t.issue.xsd.metamodel.generator.model.ComplexType;
import t.issue.xsd.metamodel.generator.test.model.Model;

/**
 * TODO write comprehensive tests.
 *
 * @author Nezhull
 *
 */
public class XsdMetamodelGeneratorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateMetamodel() throws IOException {
		XsdMetamodelGenerator metamodelGenerator = new XsdMetamodelGenerator();

		ComplexType type = null;

		try (InputStream xsdStream = XsdMetamodelGeneratorTest.class.getResourceAsStream("/schemas/maven-v4_0_0.xsd")) {
			type = metamodelGenerator.generateMetamodel(xsdStream, Model.class);
		}

		Assert.assertNotNull(type);
	}

}
