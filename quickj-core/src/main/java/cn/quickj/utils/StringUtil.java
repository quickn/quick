package cn.quickj.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class StringUtil {
	public static final char[] HexDigits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	static URLCodec urlCoder = new URLCodec();

	public static final Pattern PTitle = Pattern.compile(
			"<title>(.+?)</title>", 34);

	public static Pattern patternHtmlTag = Pattern.compile("<[^<>]+>", 32);

	public static final Pattern PLetterOrDigit = Pattern.compile("^\\w*$", 34);

	public static final Pattern PLetter = Pattern.compile("^[A-Za-z]*$", 34);

	public static final Pattern PDigit = Pattern.compile("^\\d*$", 34);

	private static Pattern chinesePattern = Pattern.compile("[^一-龥]+", 34);

	private static Pattern idPattern = Pattern.compile("[\\w\\_\\.\\,]*", 34);

	public static byte[] md5(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(src.getBytes());
			return md;
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] md5(byte[] src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(src);
			return md;
		} catch (Exception e) {
		}
		return null;
	}

	public static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(src.getBytes());
			return hexEncode(md);
		} catch (Exception e) {
		}
		return null;
	}

	public static String hexEncode(byte[] bs) {
		return new String(new Hex().encode(bs));
	}

	public static byte[] hexDecode(String str) {
		try {
			if (str.endsWith("\n")) {
				str = str.substring(0, str.length() - 1);
			}
			char[] cs = str.toCharArray();
			return Hex.decodeHex(cs);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String byteToBin(byte[] bs) {
		char[] cs = new char[bs.length * 9];
		for (int i = 0; i < bs.length; ++i) {
			byte b = bs[i];
			int j = i * 9;
			cs[j] = (((b >>> 7 & 0x1) == 1) ? 49 : '0');
			cs[(j + 1)] = (((b >>> 6 & 0x1) == 1) ? 49 : '0');
			cs[(j + 2)] = (((b >>> 5 & 0x1) == 1) ? 49 : '0');
			cs[(j + 3)] = (((b >>> 4 & 0x1) == 1) ? 49 : '0');
			cs[(j + 4)] = (((b >>> 3 & 0x1) == 1) ? 49 : '0');
			cs[(j + 5)] = (((b >>> 2 & 0x1) == 1) ? 49 : '0');
			cs[(j + 6)] = (((b >>> 1 & 0x1) == 1) ? 49 : '0');
			cs[(j + 7)] = (((b & 0x1) == 1) ? 49 : '0');
			cs[(j + 8)] = ',';
		}
		return new String(cs);
	}

	public static boolean isUTF8(byte[] bs) {
		if (hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {
			return true;
		}
		int lLen = bs.length;
		for (int i = 0; i < lLen;) {
			byte b = bs[(i++)];
			if (b >= 0) {
				continue;
			}
			if ((b < -64) || (b > -3)) {
				return false;
			}
			int c = (b > -32) ? 2 : (b > -16) ? 3 : (b > -8) ? 4 : (b > -4) ? 5
					: 1;
			if (i + c > lLen) {
				return false;
			}
			for (int j = 0; j < c; ++i) {
				if (bs[i] >= -64)
					return false;
				++j;
			}

		}

		return true;
	}

	public static String javaEncode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "\\", "\\\\");
		txt = replaceEx(txt, "\r\n", "\n");
		txt = replaceEx(txt, "\n", "\\n");
		txt = replaceEx(txt, "\"", "\\\"");
		txt = replaceEx(txt, "'", "\\'");
		return txt;
	}

	public static String javaDecode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "\\\\", "\\");
		txt = replaceEx(txt, "\\n", "\n");
		txt = replaceEx(txt, "\\r", "\r");
		txt = replaceEx(txt, "\\\"", "\"");
		txt = replaceEx(txt, "\\'", "'");
		return txt;
	}

	@SuppressWarnings("unchecked")
	public static String[] splitEx(String str, String spilter) {
		if (str == null) {
			return null;
		}
		if ((spilter == null) || (spilter.equals(""))
				|| (str.length() < spilter.length())) {
			String[] t = { str };
			return t;
		}
		ArrayList al = new ArrayList();
		char[] cs = str.toCharArray();
		char[] ss = spilter.toCharArray();
		int length = spilter.length();
		int lastIndex = 0;
		for (int i = 0; i <= str.length() - length;) {
			boolean notSuit = false;
			for (int j = 0; j < length; ++j) {
				if (cs[(i + j)] != ss[j]) {
					notSuit = true;
					break;
				}
			}
			if (!(notSuit)) {
				al.add(str.substring(lastIndex, i));
				i += length;
				lastIndex = i;
			} else {
				++i;
			}
		}
		if (lastIndex <= str.length()) {
			al.add(str.substring(lastIndex, str.length()));
		}
		String[] t = new String[al.size()];
		for (int i = 0; i < al.size(); ++i) {
			t[i] = ((String) al.get(i));
		}
		return t;
	}

	public static String replaceEx(String str, String subStr, String reStr) {
		if (str == null) {
			return null;
		}
		if ((subStr == null) || (subStr.equals(""))
				|| (subStr.length() > str.length()) || (reStr == null)) {
			return str;
		}
		StringBuffer sb = new StringBuffer();
		String tmp = str;
		int index = -1;
		while (true) {
			index = tmp.indexOf(subStr);
			if (index < 0) {
				break;
			}
			sb.append(tmp.substring(0, index));
			sb.append(reStr);
			tmp = tmp.substring(index + subStr.length());
		}

		sb.append(tmp);
		return sb.toString();
	}

	public static String replaceAllIgnoreCase(String source, String oldstring,
			String newstring) {
		Pattern p = Pattern.compile(oldstring, 34);
		Matcher m = p.matcher(source);
		return m.replaceAll(newstring);
	}

	public static String urlEncode(String str) {
		return urlEncode(str, "GBK");
	}

	public static String urlDecode(String str) {
		return urlDecode(str, "GBK");
	}

	public static String urlEncode(String str, String charset) {
		try {
			return urlCoder.encode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String urlDecode(String str, String charset) {
		try {
			return urlCoder.decode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String htmlEncode(String txt) {
		return StringEscapeUtils.escapeHtml(txt);
	}

	public static String htmlDecode(String txt) {
		txt = replaceEx(txt, "&#8226;", "·");
		return StringEscapeUtils.unescapeHtml(txt);
	}

	public static String quotEncode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "&", "&amp;");
		txt = replaceEx(txt, "\"", "&quot;");
		return txt;
	}

	public static String quotDecode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "&quot;", "\"");
		txt = replaceEx(txt, "&amp;", "&");
		return txt;
	}

	public static String escape(String src) {
		StringBuffer sb = new StringBuffer();
		sb.ensureCapacity(src.length() * 6);
		for (int i = 0; i < src.length(); ++i) {
			char j = src.charAt(i);
			if ((Character.isDigit(j)) || (Character.isLowerCase(j))
					|| (Character.isUpperCase(j))) {
				sb.append(j);
			} else if (j < 256) {
				sb.append("%");
				if (j < '\16') {
					sb.append("0");
				}
				sb.append(Integer.toString(j, 16));
			} else {
				sb.append("%u");
				sb.append(Integer.toString(j, 16));
			}
		}
		return sb.toString();
	}

	public static String unescape(String src) {
		StringBuffer sb = new StringBuffer();
		sb.ensureCapacity(src.length());
		int lastPos = 0;
		int pos = 0;

		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				char ch;
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src
							.substring(pos + 2, pos + 6), 16);
					sb.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src
							.substring(pos + 1, pos + 3), 16);
					sb.append(ch);
					lastPos = pos + 3;
				}
			} else if (pos == -1) {
				sb.append(src.substring(lastPos));
				lastPos = src.length();
			} else {
				sb.append(src.substring(lastPos, pos));
				lastPos = pos;
			}
		}

		return sb.toString();
	}

	public static String leftPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();

		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < iMax; ++i) {
			sb.append(c);
		}
		sb.append(srcString);
		return sb.toString();
	}

	public static String subString(String src, int length) {
		if (src == null) {
			return null;
		}
		int i = src.length();
		if (i > length) {
			return src.substring(0, length);
		}
		return src;
	}

	/**
	 * 测量一个字符串长度，其中汉字作为2个长度，西方字母作为一个长度。
	 * 
	 * @param src
	 * @return
	 */
	public static int lengthEx(String src) {
		if (src == null)
			return 0;
		try {
			return src.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public static void main(String[] args) {
		System.out.println(lengthEx("中文123abc"));
	}
	public static String rightPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();

		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		StringBuffer sb = new StringBuffer();
		sb.append(srcString);
		for (int i = 0; i < iMax; ++i) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String rightTrim(String src) {
		if (src != null) {
			char[] chars = src.toCharArray();
			for (int i = chars.length - 1; i > 0; --i) {
				if (chars[i] == ' ')
					continue;
				if (chars[i] != '\t') {
					return new String(ArrayUtils.subarray(chars, 0, i + 1));
				}
			}
		}
		return src;
	}

	@SuppressWarnings("unchecked")
	public static void printStringWithAnyCharset(String str) {
		Map map = Charset.availableCharsets();
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; ++i) {
			System.out.println(keys[i]);
			for (int j = 0; j < keys.length; ++j) {
				System.out.print("\t");
				try {
					System.out.println("From "
							+ keys[i]
							+ " To "
							+ keys[j]
							+ ":"
							+ new String(str.getBytes(keys[i].toString()),
									keys[j].toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String toSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; ++i)
			if (c[i] == ' ') {
				c[i] = 12288;
			} else {
				if ((c[i] > '@') && (c[i] < '['))
					continue;
				if ((c[i] > '`') && (c[i] < '{')) {
					continue;
				}

				if (c[i] < '')
					c[i] = (char) (c[i] + 65248);
			}
		return new String(c);
	}

	public static String toNSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; ++i) {
			if (c[i] == ' ') {
				c[i] = 12288;
			} else if (c[i] < '')
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; ++i) {
			if (c[i] == 12288) {
				c[i] = ' ';
			} else if ((c[i] > 65280) && (c[i] < 65375))
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String getHtmlTitle(String html) {
		Matcher m = PTitle.matcher(html);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}

	public static String getTextFromHtml(String html) {
		String text = patternHtmlTag.matcher(html).replaceAll("");
		if (isEmpty(text)) {
			return "";
		}
		return text.replaceAll("[\\s　]{2,}", " ");
	}

	public static boolean isEmpty(String str) {
		return ((str != null) && (str.length() != 0));
	}

	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}

	public static final String noNull(String string, String defaultString) {
		return ((isEmpty(string)) ? defaultString : string);
	}

	public static final String noNull(String string) {
		return noNull(string, "");
	}

	public static String join(Object[] arr) {
		return join(arr, ",");
	}

	public static String join(Object[][] arr) {
		return join(arr, ",");
	}

	public static String join(Object[] arr, String spliter) {
		if (arr == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static String join(Object[][] arr, String spliter) {
		if (arr == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(join(arr[i], spliter));
		}
		return sb.toString();
	}

	public static int count(String str, String findStr) {
		int lastIndex = 0;
		int length = findStr.length();
		int count = 0;
		int start = 0;
		while ((start = str.indexOf(findStr, lastIndex)) >= 0) {
			lastIndex = start + length;
			++count;
		}
		return count;
	}

	public static boolean isLetterOrDigit(String str) {
		return PLetterOrDigit.matcher(str).find();
	}

	public static boolean isLetter(String str) {
		return PLetter.matcher(str).find();
	}

	public static boolean isDigit(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return PDigit.matcher(str).find();
	}

	public static boolean containsChinese(String str) {
		return (chinesePattern.matcher(str).matches());
	}

	public static boolean checkID(String str) {
		if (isEmpty(str)) {
			return true;
		}

		return (!(idPattern.matcher(str).matches()));
	}

	public static String getURLExtName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf(63);
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf(46, index1);
		if (index2 == -1) {
			return null;
		}
		int index3 = url.indexOf(47, 8);
		if (index3 == -1) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		if (ext.matches("[^\\/\\\\]*")) {
			return ext;
		}
		return null;
	}

	public static String getURLFileName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf(63);
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf(47, index1);
		if ((index2 == -1) || (index2 < 8)) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		return ext;
	}

	public static byte[] GBKToUTF8(String chenese) {
		char[] c = chenese.toCharArray();
		byte[] fullByte = new byte[3 * c.length];
		for (int i = 0; i < c.length; ++i) {
			int m = c[i];
			String word = Integer.toBinaryString(m);
			StringBuffer sb = new StringBuffer();
			int len = 16 - word.length();
			for (int j = 0; j < len; ++j) {
				sb.append("0");
			}
			sb.append(word);
			sb.insert(0, "1110");
			sb.insert(8, "10");
			sb.insert(16, "10");
			String s1 = sb.substring(0, 8);
			String s2 = sb.substring(8, 16);
			String s3 = sb.substring(16);
			byte b0 = Integer.valueOf(s1, 2).byteValue();
			byte b1 = Integer.valueOf(s2, 2).byteValue();
			byte b2 = Integer.valueOf(s3, 2).byteValue();
			byte[] bf = new byte[3];
			bf[0] = b0;
			fullByte[(i * 3)] = bf[0];
			bf[1] = b1;
			fullByte[(i * 3 + 1)] = bf[1];
			bf[2] = b2;
			fullByte[(i * 3 + 2)] = bf[2];
		}
		return fullByte;
	}
	
	/**
	 * 判断是否是数字
	 * @param string
	 * @return
	 */
	public static boolean isNumeric(String strValue) {
		try {
			Long temp = Long.valueOf(strValue);
			if (temp != null && temp >= 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * 判断是否是Email格式
	 * @param strValue
	 * @return
	 */
	public static boolean isEmail(String strValue){
		Pattern pattern = Pattern.compile("\\w+?\\@\\w+?\\.\\w+?");
		Matcher m = pattern.matcher(strValue);
		if(m.find()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断是否是Tel (家庭电话等) 如：0751-88208578
	 * @param strValue
	 * @return
	 */
	public static boolean isTel(String strValue){
		Pattern pattern = Pattern.compile("^\\d{1,9}\\-\\d{3,12}$");
		Matcher m = pattern.matcher(strValue);
		if(m.find()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断是否是国内手机 11位的手机号码
	 * @param strValue
	 * @return
	 */
	public static boolean isMobile(String strValue){
		Pattern pattern = Pattern.compile("^1[358]\\d{9}$");
		Matcher m = pattern.matcher(strValue);
		if(m.find()){
			return true;
		}else{
			return false;
		}
	}
	public static String exceptionMsg(Exception e){
		StringWriter writer = new StringWriter();
		PrintWriter w = new PrintWriter(writer);
		e.printStackTrace(w);
		return writer.toString();
	}
}