package cc.meteormc.packetlistener.objects;

import cc.meteormc.packetlistener.helper.Reflection;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A network packet wrapper that makes it easy to perform certain actions without relying on NMS.
 *
 * @author Meteor23333
 */
public class Packet {
    private final Object handle;
    private final PacketStage stage;
    private final PacketDirection direction;
    private final String name;

    // Damn it spigot mappings — why are the packet class names so chaotic?
    public static final Pattern PACKET_PATTERN = Pattern.compile("(Clientbound|Serverbound)(.+)Packet");
    public static final Pattern LEGACY_PACKET_PATTERN = Pattern.compile("Packet(Handshaking|Login|Play|Status)(In|Out)(.+)");

    /**
     * Creates a {@link Packet}.
     *
     * @param handle the handle
     * @param stage the packet stage
     * @param direction the packet direction
     * @param name the packet simple name
     */
    private Packet(Object handle, PacketStage stage, PacketDirection direction, String name) {
        this.handle = handle;
        this.stage = stage;
        this.direction = direction;
        this.name = name;
    }

    /**
     * Wraps the given original packet object in a {@link Packet}.
     *
     * @param handle the original packet object to be wrapped
     * @return the wrapped packet, or {@code null} if invalid
     */
    public static @Nullable Packet fromHandle(@NotNull Object handle) {
        Class<?> clazz = handle.getClass();
        String className = clazz.getSimpleName();
        String packageName = clazz.getPackage().getName();
        if (packageName.startsWith("net.minecraft")) {
            Matcher matcher = PACKET_PATTERN.matcher(className);
            if (matcher.matches()) {
                String direction = matcher.group(1);
                String name = matcher.group(2);
                return new Packet(
                        handle,
                        PacketStage.UNKNOWN,
                        PacketDirection.getBySpigotName(direction),
                        name
                );
            }

            Matcher legacyMatcher = LEGACY_PACKET_PATTERN.matcher(className);
            if (legacyMatcher.matches()) {
                String stage = legacyMatcher.group(1);
                String direction = legacyMatcher.group(2);
                String name = legacyMatcher.group(3);
                return new Packet(
                        handle,
                        PacketStage.getBySpigotName(stage),
                        PacketDirection.getBySpigotName(direction),
                        name
                );
            }
        }

        return null;
    }

    /**
     * Gets the original packet object.
     *
     * @return the handle
     */
    public @NotNull Object getHandle() {
        return handle;
    }

    /**
     * Gets the stage of the packet.
     *
     * @return the packet stage
     */
    public @NotNull PacketStage getStage() {
        return stage;
    }

    /**
     * Gets the direction of the packet.
     *
     * @return the packet direction
     */
    public @NotNull PacketDirection getDirection() {
        return direction;
    }

    /**
     * Gets the simple name of the packet.
     *
     * @return the packet simple name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the runtime class name of the packet.
     *
     * @return the packet full name
     */
    public @NotNull String getFullName() {
        return handle.getClass().getSimpleName();
    }

    /**
     * Gets all declared non-static fields of the wrapped packet object,
     * in the order they are declared in the class.
     *
     * @return all declared fields of the wrapped packet class
     */
    public @NotNull Field[] getFields() {
        return FieldUtils.getAllFieldsList(handle.getClass())
                .stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toArray(Field[]::new);
    }

    /**
     * Gets the declared non-static field at the specified position of the wrapped packet object,
     * in the order they are declared in the class.
     *
     * @param position the zero-based index of the field in declaration order
     * @return the field at the specified position
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    public @NotNull Field getField(@Range(from = 0, to = Integer.MAX_VALUE) int position) throws IndexOutOfBoundsException {
        return this.getFields()[position];
    }

    /**
     * Gets the value of the field at the specified position in the wrapped packet object.
     * <p>
     * Fields are scanned in declaration order, excluding static fields.
     * The position starts from {@code 0}.
     * <p>
     * This is useful for accessing fields in obfuscated packet classes where multiple fields
     * may share the same type and can only be identified by type and order.
     * <p>
     * Use this method to retrieve a value of the Nth field regardless of its type.
     *
     * @param position the zero-based index of the field in declaration order
     * @return the value of the field at the specified position
     * @throws IllegalArgumentException if a reflective operation error occurs
     * @throws IndexOutOfBoundsException if the position is out of range
     * @see Packet#getFieldValueOfType(Class, int)
     */
    public <T> @NotNull T getFieldValue(@Range(from = 0, to = Integer.MAX_VALUE) int position) throws IllegalArgumentException, IndexOutOfBoundsException {
        try {
            //noinspection unchecked
            return (T) this.getField(position).get(handle);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot get value at position " + position, e);
        }
    }

    /**
     * Sets the value of the field at the specified position in the wrapped packet object.
     * <p>
     * Fields are scanned in declaration order, excluding static fields.
     * The position starts from {@code 0}.
     * <p>
     * This is useful for modifying fields in obfuscated packet classes where multiple fields
     * may share the same type and can only be identified by type and order.
     * <p>
     * Use this method to assign a value to the Nth field regardless of its type.
     *
     * @param position the zero-based index of the field in declaration order
     * @param value the value to set for the field at the specified position
     * @throws IllegalArgumentException if a reflective operation error occurs
     * @throws IndexOutOfBoundsException if the position is out of range
     * @see Packet#setFieldValueOfType(Class, int, Object)
     */
    public <T> void setFieldValue(@Range(from = 0, to = Integer.MAX_VALUE) int position, T value) throws IllegalArgumentException, IndexOutOfBoundsException {
        try {
            this.getField(position).set(handle, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot set value at position " + position, e);
        }
    }

    /**
     * Gets the value of the {@code position}-th field whose type exactly matches {@code type} in the wrapped packet object.
     * <p>
     * Fields are scanned in declaration order, excluding static fields.
     * Only fields whose declared type is exactly equal to {@code type} are considered.
     * The position starts from {@code 0}.
     * <p>
     * This is useful for accessing fields in obfuscated packet classes where multiple fields
     * may share the same type and can only be identified by type and order.
     * <p>
     * Use this method to retrieve a value of the Nth field of a specific type.
     *
     * @param type the type to match against declared field types
     * @param position the zero-based index among all fields of the specified type
     * @return the value of the {@code position}-th field of the specified type
     * @param <T> the expected type of the field value
     * @throws IllegalArgumentException if a reflective operation error occurs
     * @throws IndexOutOfBoundsException if the position is out of range
     * @see Packet#getFieldValue(int)
     */
    public <T> @NotNull T getFieldValueOfType(@NotNull Class<T> type, @Range(from = 0, to = Integer.MAX_VALUE) int position) throws IllegalArgumentException, IndexOutOfBoundsException {
        try {
            //noinspection unchecked
            return (T) Reflection.findFields(handle.getClass(), type)[position].get(handle);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot get value at position " + position, e);
        }
    }

    /**
     * Sets the value of the {@code position}-th field whose type exactly matches {@code type} in the wrapped packet object.
     * <p>
     * Fields are scanned in declaration order, excluding static fields.
     * Only fields whose declared type is exactly equal to {@code type} are considered.
     * The position starts from {@code 0}.
     * <p>
     * This is useful for modifying fields in obfuscated packet classes where multiple fields
     * may share the same type and can only be identified by type and order.
     * <p>
     * Use this method to assign a value of the Nth field of a specific type.
     *
     * @param type the type to match against declared field types
     * @param position the zero-based index among all fields of the specified type
     * @param value the value to set for the {@code position}-th field of the specified type
     * @param <T> the expected type of the field value
     * @throws IllegalArgumentException if a reflective operation error occurs
     * @throws IndexOutOfBoundsException if the position is out of range
     * @see Packet#setFieldValue(int, Object)
     */
    public <T> void setFieldValueOfType(@NotNull Class<T> type, @Range(from = 0, to = Integer.MAX_VALUE) int position, T value) throws IllegalArgumentException, IndexOutOfBoundsException {
        try {
            Reflection.findFields(handle.getClass(), type)[position].set(handle, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot set value at position " + position, e);
        }
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format(
                "%s{handle=%s, stage=%s, direction=%s, name=%s}",
                this.getClass().getSimpleName(),
                this.getFullName(),
                this.stage,
                this.direction,
                this.name
        );
    }
}
