package com.indivar.edubold3.UniversalKit.Utility.Util;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Null-ish checks for common Java/Spring types.
 * Java 21 compatible.
 */
public final class Nulls {

    private Nulls() { }

    /**
     * Returns true if the value is considered "null-ish":
     *  - null
     *  - Optional.empty() (recursively unwrapped)
     *  - CharSequence that is blank or equalsIgnoreCase("null")
     *  - empty arrays, Collections, Maps
     *  - empty Iterables / Iterators
     */
    public static boolean isMissing(Object value) {
        Object v = unwrapOptionals(value);
        if (v == null) return true;

        // Text: blank or the literal "null"
        if (v instanceof CharSequence cs) {
            String s = cs.toString().trim();
            return s.isEmpty() || "null".equalsIgnoreCase(s);
        }

        // Arrays
        if (v.getClass().isArray()) {
            return Array.getLength(v) == 0;
        }

        // Collections / Maps
        if (v instanceof Collection<?> c) {
            return c.isEmpty();
        }
        if (v instanceof Map<?, ?> m) {
            return m.isEmpty();
        }

        // Iterable / Iterator (covers many custom containers)
        if (v instanceof Iterable<?> it) {
            return !it.iterator().hasNext();
        }
        if (v instanceof Iterator<?> it) {
            return !it.hasNext();
        }

        // Spring’s ObjectUtils covers: CharSequence length 0, Array length 0, Collection/Map empty.
        // We already handled those, but keep as a conservative check for edge cases.
        return ObjectUtils.isEmpty(v);
    }

    /** Convenience inverse. */
    public static boolean isPresent(Object value) {
        return !isMissing(value);
    }

    private static Object unwrapOptionals(Object value) {
        Object v = value;
        // Unwrap nested Optionals: Optional.of(Optional.empty()) → null
        while (v instanceof Optional<?> opt) {
            if (opt.isEmpty()) return null;
            v = opt.get();
        }
        // Spring also has ObjectUtils.unwrapOptional, but this loop handles nesting.
        return v;
    }
}
