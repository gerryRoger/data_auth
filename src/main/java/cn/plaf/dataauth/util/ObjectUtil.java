package cn.plaf.dataauth.util;/**
 * @Package cn.plaf.dataauth.util
 * @Title:
 * @Description: (用一句话描述该文件做什么)
 * @author luoboy
 * @date 2023/8/3 10:47
 * @version V1.0
 */

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class ObjectUtil {
    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if an Object is empty or null.</p>
     *
     * The following types are supported:
     * <ul>
     * <li>{@link CharSequence}: Considered empty if its length is zero.</li>
     * <li>{@code Array}: Considered empty if its length is zero.</li>
     * <li>{@link Collection}: Considered empty if it has zero elements.</li>
     * <li>{@link Map}: Considered empty if it has zero key-value mappings.</li>
     * </ul>
     *
     * <pre>
     * ObjectUtils.isEmpty(null)             = true
     * ObjectUtils.isEmpty("")               = true
     * ObjectUtils.isEmpty("ab")             = false
     * ObjectUtils.isEmpty(new int[]{})      = true
     * ObjectUtils.isEmpty(new int[]{1,2,3}) = false
     * ObjectUtils.isEmpty(1234)             = false
     * </pre>
     *
     * @param object  the {@code Object} to test, may be {@code null}
     * @return {@code true} if the object has a supported type and is empty or null,
     * {@code false} otherwise
     * @since 3.9
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }
}
