/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cn.plaf.dataauth.util;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author liuzh
 * @since 4.1.0
 */
public class StringUtil {
	public static final int INDEX_NOT_FOUND = -1;
	public static final String EMPTY = "";
	
	public static final String PLACE_HOLDER_STR =" ? ";
	public static final String SPLIT_STR =" , ";
	
	

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
	public static boolean hasLength( CharSequence str) {
		return (str != null && str.length() > 0);
	}
	
	public static boolean hasText( CharSequence str) {
		return (str != null && str.length() > 0 && containsText(str));
	}
	private static boolean containsText(CharSequence str) {
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
    
    
    /** 
     * @Title: getMethodName 
     * @Description: 获取对象类型属性的get方法名 
     * @param propertyName 
     *            属性名 
     * @return String "get"开头且参数(propertyName)值首字母大写的字符串 
     */  
    public static String convertToReflectGetMethod(String propertyName) {  
        return "get" + toFirstUpChar(propertyName);  
    }  
  
    /** 
     * @Title: convertToReflectSetMethod 
     * @Description: 获取对象类型属性的set方法名 
     * @param propertyName 
     *            属性名 
     * @return String "set"开头且参数(propertyName)值首字母大写的字符串 
     */  
    public static String convertToReflectSetMethod(String propertyName) {  
        return "set" + toFirstUpChar(propertyName);  
    }  
  
    /** 
     * @Title: toFirstUpChar 
     * @Description: 将字符串的首字母大写 
     * @param target 
     *            目标字符串 
     * @return String 首字母大写的字符串 
     */  
    public static String toFirstUpChar(String target) {  
        StringBuilder sb = new StringBuilder(target);  
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
        return sb.toString();  
    }  
  
    /** 
     * @Title: getFileSuffixName 
     * @Description: 获取文件名后缀 
     * @param fileName 
     *            文件名 
     * @return String 文件名后缀。如：jpg 
     */  
    public static String getFileSuffixName(String fileName) {  
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();  
    }  
  
  
    /**    
     * @Title: getPackageByPath    
     * @Description 通过指定文件获取类全名   
     * @param classFile 类文件 
     * @return String 类全名 
     */  
    public static String getPackageByPath(File classFile, String exclude){  
        if(classFile == null || classFile.isDirectory()){  
            return null;  
        }  
          
        String ta  = classFile.getAbsolutePath();
        String path = classFile.getAbsolutePath().replace("/","\\");
  
        path = path.substring(path.indexOf(exclude) + exclude.length()+1).replace('\\', '.');  
        if(path.startsWith(".")){  
            path = path.substring(1);  
        }  
        if(path.endsWith(".")){  
            path = path.substring(0, path.length() - 1);  
        }  
  
        return path.substring(0, path.lastIndexOf('.'));  
    }
    
    /**
     * 字符串替换
     * @param text
     * @param searchString
     * @param replacement
     * @return
     */
    public static String replace(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }
    
    public static String replace(final String text, final String searchString, final String replacement, final int max) {
        return replace(text, searchString, replacement, max, false);
    }
    
    private static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        String searchText = text;
        if (ignoreCase) {
            searchText = text.toLowerCase();
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }
    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }
    
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtils.join(null, *, *, *)                = null
     * StringUtils.join([], *, *, *)                  = ""
     * StringUtils.join([null], *, *, *)              = ""
     * StringUtils.join(["a", "b", "c"], "--", 0, 3)  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], "--", 1, 3)  = "b--c"
     * StringUtils.join(["a", "b", "c"], "--", 2, 3)  = "c"
     * StringUtils.join(["a", "b", "c"], "--", 2, 2)  = ""
     * StringUtils.join(["a", "b", "c"], null, 0, 3)  = "abc"
     * StringUtils.join(["a", "b", "c"], "", 0, 3)    = "abc"
     * StringUtils.join([null, "", "a"], ',', 0, 3)   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.
     * @param endIndex the index to stop joining from (exclusive).
     * @return the joined String, {@code null} if null array input; or the empty string
     * if {@code endIndex - startIndex <= 0}. The number of joined entries is given by
     * {@code endIndex - startIndex}
     * @throws ArrayIndexOutOfBoundsException ife<br>
     * {@code startIndex < 0} or <br>
     * {@code startIndex >= array.length()} or <br>
     * {@code endIndex < 0} or <br>
     * {@code endIndex > array.length()}
     */
    public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = newStringBuilder(noOfItems);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
    
    private static StringBuilder newStringBuilder(final int noOfItems) {
        return new StringBuilder(noOfItems * 16);
    }

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
    
    
    public static String getPlaceholder(final String column,final Object object) {
    	if(isEmpty(object)) {
    		return null;
    	}
        if (object.getClass().isArray()) {
        	return arrange(column,Arrays.asList(object));
        }
        if(object instanceof Collection<?>) {
        	
        	return arrange(column,((Collection<?>) object));
        }
    	
    	return null;
    }
    
    private static String arrange(final String column,Collection<?> c) {
    	StringBuffer sb  = new StringBuffer();
		for(int i=0,len=c.size();i<len;i++) {
			if(i< len-1) {
				sb.append(PLACE_HOLDER_STR).append(SPLIT_STR);
			}else {
				sb.append(PLACE_HOLDER_STR);
			}
		}
		return sb.toString();
    }
    
    
}
