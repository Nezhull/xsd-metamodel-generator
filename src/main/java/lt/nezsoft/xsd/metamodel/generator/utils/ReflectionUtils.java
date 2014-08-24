package lt.nezsoft.xsd.metamodel.generator.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel
 *
 */
public class ReflectionUtils {
	
	/**
	 * Gets all fields declared in the {@code clazz} {@link Class} or its subclasses
	 * 
	 * @param clazz {@link Class} to get fields from
	 * 
	 * @return {@link List} containing all fields declared in the {@code clazz} or its subclasses
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		Class<?> cls = clazz;
		List<Field> fields = new ArrayList<Field>();
		while (!Object.class.equals(cls)) {
			fields.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass() != null ? cls.getSuperclass() : Object.class;
		}
		return fields;
	}
	
	/**
	 * Finds first occurrence of {@link Field} with name provided by {@code name} argument in the {@code clazz} {@link Class}
	 * or its subclasses and sets field as accessible
	 * 
	 * @param name field name
	 * @param fields {@link List} of fields
	 * 
	 * @return found field or {@code null} if not found
	 */
	public static Field findField(String name, Class<?> clazz) {
		return findField(name, clazz, true);
	}
	
	/**
	 * Finds first occurrence of {@link Field} with name provided by {@code name} argument in the {@code clazz} {@link Class}
	 * or its subclasses
	 * 
	 * @param name field name
	 * @param fields {@link List} of fields
	 * @param accessible if to set field as accessible
	 * 
	 * @return found field or {@code null} if not found
	 */
	public static Field findField(String name, Class<?> clazz, boolean accessible) {
		for (Field field : getAllFields(clazz)) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * Finds first occurrence of {@link Field} with name provided by {@code name} argument in the {@code fields} {@link List}
	 * and sets field as accessible
	 * 
	 * @param name field name
	 * @param fields {@link List} of fields
	 * 
	 * @return found field or {@code null} if not found
	 */
	public static Field findField(String name, List<Field> fields) {
		return findField(name, fields, true);
	}
	
	/**
	 * Finds first occurrence of {@link Field} with name provided by {@code name} argument in the {@code fields} {@link List}
	 * 
	 * @param name field name
	 * @param fields {@link List} of fields
	 * @param accessible if to set field as accessible
	 * 
	 * @return found field or {@code null} if not found
	 */
	public static Field findField(String name, List<Field> fields, boolean accessible) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				field.setAccessible(accessible);
				return field;
			}
		}
		return null;
	}
	
}
