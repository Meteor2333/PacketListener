package cc.meteormc.packetlistener.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A reflection helper class providing some additional functionalities.
 *
 * @author Meteor23333
 */
public class Reflection {
    /**
     * Finds a field of the specified type name from the given class.
     * <p>
     * <strong>Warning:</strong> This is an unsafe method and should only be used in controlled environments.
     * Otherwise, it is recommended to use the more stable {@link Reflection#findField(Class, Class)} method.
     *
     * @param clazz the class to search for the field
     * @param type the type name of the field to find
     * @return the field matching the specified type name, or {@code null} if none is found
     * @throws SecurityException if the request is denied
     */
    public static @Nullable Field findField(Class<?> clazz, String type) throws SecurityException {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().getSimpleName().equalsIgnoreCase(type)) continue;
            field.setAccessible(true);
            return field;
        }

        // Ensures all fields are searched.
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) return findField(superclass, type);
        else return null;
    }

    /**
     * Finds all fields of the specified type name from the given class.
     * <p>
     * <strong>Warning:</strong> This is an unsafe method and should only be used in controlled environments.
     * Otherwise, it is recommended to use the more stable {@link Reflection#findFields(Class, Class)} method.
     *
     * @param clazz the class to search for fields in
     * @param type the type name of the fields to find
     * @return the fields matching the specified type name, or an {@code empty} array if none is found
     * @throws SecurityException if the request is denied
     */
    public static @NotNull Field[] findFields(Class<?> clazz, String type) throws SecurityException {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().getSimpleName().equalsIgnoreCase(type)) continue;
            field.setAccessible(true);
            fields.add(field);
        }

        // Ensures all fields are searched.
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) fields.addAll(Arrays.asList(findFields(superclass, type)));
        return fields.toArray(new Field[0]);
    }

    /**
     * Finds a field of the specified type from the given class.
     *
     * @param clazz the class to search for the field
     * @param type the type of the field to find
     * @return the field matching the specified type, or {@code null} if none is found
     * @throws SecurityException if the request is denied
     */
    public static @Nullable Field findField(Class<?> clazz, Class<?> type) throws SecurityException {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().equals(type)) continue;
            field.setAccessible(true);
            return field;
        }

        // Ensures all fields are searched.
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) return findField(superclass, type);
        else return null;
    }

    /**
     * Finds all fields of the specified type from the given class.
     *
     * @param clazz the class to search for fields in
     * @param type the type of the fields to find
     * @return the fields matching the specified type, or an {@code empty} array if none is found
     * @throws SecurityException if the request is denied
     */
    public static @NotNull Field[] findFields(Class<?> clazz, Class<?> type) throws SecurityException {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().equals(type)) continue;
            field.setAccessible(true);
            fields.add(field);
        }

        // Ensures all fields are searched.
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) fields.addAll(Arrays.asList(findFields(superclass, type)));
        return fields.toArray(new Field[0]);
    }

    /**
     * This is a helper class and cannot be instantiated!
     */
    private Reflection() {
        throw new UnsupportedOperationException("This is a helper class and cannot be instantiated");
    }
}
