/**
 * CodeUtils.java
 */
package freelancer.worldvideo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @function: 字符编码
 * @author jiangbing
 * @data: 2014-3-14
 */
public class CodeUtils {
	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String s) {
		String str = "";
		try {
			int length = s.length();
		
	        for (int i = 0; i < length; i++)
	        {
	            if (s.substring(i, i + 1).matches("[\u4e00-\u9fa5]+"))
	            {
	            	int ch = (int) s.charAt(i);
	    			if (ch > 255)
	    				str += "\\u" + Integer.toHexString(ch);
	            }
	            else
	            {
	            	str += s.substring(i, i + 1);
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;
	}

	public static String decode(String str) {
		try {
			Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
			Matcher matcher = pattern.matcher(str);
			char ch;
			while (matcher.find()) {
				ch = (char) Integer.parseInt(matcher.group(2), 16);
				str = str.replace(matcher.group(1), ch + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;

	}
}
