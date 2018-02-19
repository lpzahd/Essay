package com.lpzahd;

import com.lpzahd.base.NoInstance;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public final class Strings extends NoInstance {

    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof CharSequence) {
            return '"' + printableToString(obj.toString()) + '"';
        }

        Class<?> cls = obj.getClass();
        if (Byte.class == cls) {
            return byteToString((Byte) obj);
        }

        if (cls.isArray()) {
            return arrayToString(cls.getComponentType(), obj);
        }
        return obj.toString();
    }

    public static String printableToString(String string) {
        int length = string.length();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ) {
            int codePoint = string.codePointAt(i);
            switch (Character.getType(codePoint)) {
                case Character.CONTROL:
                case Character.FORMAT:
                case Character.PRIVATE_USE:
                case Character.SURROGATE:
                case Character.UNASSIGNED:
                    switch (codePoint) {
                        case '\n':
                            builder.append("\\n");
                            break;
                        case '\r':
                            builder.append("\\r");
                            break;
                        case '\t':
                            builder.append("\\t");
                            break;
                        case '\f':
                            builder.append("\\f");
                            break;
                        case '\b':
                            builder.append("\\b");
                            break;
                        default:
                            builder.append("\\u").append(String.format("%04x", codePoint).toUpperCase(Locale.US));
                            break;
                    }
                    break;
                default:
                    builder.append(Character.toChars(codePoint));
                    break;
            }
            i += Character.charCount(codePoint);
        }
        return builder.toString();
    }

    public static String arrayToString(Class<?> cls, Object obj) {
        if (byte.class == cls) {
            return byteArrayToString((byte[]) obj);
        }
        if (short.class == cls) {
            return Arrays.toString((short[]) obj);
        }
        if (char.class == cls) {
            return Arrays.toString((char[]) obj);
        }
        if (int.class == cls) {
            return Arrays.toString((int[]) obj);
        }
        if (long.class == cls) {
            return Arrays.toString((long[]) obj);
        }
        if (float.class == cls) {
            return Arrays.toString((float[]) obj);
        }
        if (double.class == cls) {
            return Arrays.toString((double[]) obj);
        }
        if (boolean.class == cls) {
            return Arrays.toString((boolean[]) obj);
        }
        return arrayToString((Object[]) obj);
    }

    /**
     * A more human-friendly version of Arrays#toString(byte[]) that uses hex representation.
     */
    public static String byteArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(byteToString(bytes[i]));
        }
        return builder.append(']').toString();
    }

    public static String byteToString(Byte b) {
        if (b == null) {
            return "null";
        }
        return "0x" + String.format("%02x", b).toUpperCase(Locale.US);
    }

    public static String arrayToString(Object[] array) {
        StringBuilder buf = new StringBuilder();
        arrayToString(array, buf, new HashSet<Object[]>());
        return buf.toString();
    }

    public static void arrayToString(Object[] array, StringBuilder builder, Set<Object[]> seen) {
        if (array == null) {
            builder.append("null");
            return;
        }

        seen.add(array);
        builder.append('[');
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }

            Object element = array[i];
            if (element == null) {
                builder.append("null");
            } else {
                Class elementClass = element.getClass();
                if (elementClass.isArray() && elementClass.getComponentType() == Object.class) {
                    Object[] arrayElement = (Object[]) element;
                    if (seen.contains(arrayElement)) {
                        builder.append("[...]");
                    } else {
                        arrayToString(arrayElement, builder, seen);
                    }
                } else {
                    builder.append(toString(element));
                }
            }
        }
        builder.append(']');
        seen.remove(array);
    }

    public static String join(CharSequence... cs) {
        if (cs == null) return null;
        StringBuilder builder = new StringBuilder(cs.length);
        for (CharSequence c : cs) {
            builder.append(c);
        }
        return builder.toString();
    }

    public static boolean empty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean nonEmpty(CharSequence str) {
        return !empty(str);
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return Objects.equals(a, b) || a != null && b != null && (a.length() == b.length()) && a.equalsIgnoreCase(b);
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.CHINA, format, args);
    }

    public static byte[] getBytesIso8859_1(String string) {
        return getBytesUnchecked(string, "ISO-8859-1");
    }

    public static byte[] getBytesUsAscii(String string) {
        return getBytesUnchecked(string, "US-ASCII");
    }

    public static byte[] getBytesUtf16(String string) {
        return getBytesUnchecked(string, "UTF-16");
    }

    public static byte[] getBytesUtf16Be(String string) {
        return getBytesUnchecked(string, "UTF-16BE");
    }

    public static byte[] getBytesUtf16Le(String string) {
        return getBytesUnchecked(string, "UTF-16LE");
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytesUnchecked(string, "UTF-8");
    }

    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if(string == null) {
            return null;
        } else {
            try {
                return string.getBytes(charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    public static String newString(byte[] bytes, String charsetName) {
        if(bytes == null) {
            return null;
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static String newStringIso8859_1(byte[] bytes) {
        return newString(bytes, "ISO-8859-1");
    }

    public static String newStringUsAscii(byte[] bytes) {
        return newString(bytes, "US-ASCII");
    }

    public static String newStringUtf16(byte[] bytes) {
        return newString(bytes, "UTF-16");
    }

    public static String newStringUtf16Be(byte[] bytes) {
        return newString(bytes, "UTF-16BE");
    }

    public static String newStringUtf16Le(byte[] bytes) {
        return newString(bytes, "UTF-16LE");
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, "UTF-8");
    }

}
