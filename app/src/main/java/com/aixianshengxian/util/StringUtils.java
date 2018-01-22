package com.aixianshengxian.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 字符串工具类
 * 
 * @author Administrator
 * 
 */
public class StringUtils {

	/**
	 * 空字符串
	 */
	public static final String STR_EMPTY = "";

	/**
	 * 判断字符串是否为�?
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	/**
	 * 判断字符串是否为null,或�?""、{}、[]
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty2(String str) {
		return str == null || "".equals(str.trim()) || "{}".equals(str)
				|| "[]".equals(str);
	}

	/**
	 * 字符串转stream
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream StringToInputStream(String str) {
		if (StringUtils.isEmpty(str))
			return null;
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	/**
	 * URL编码转换
	 * 
	 * @param src
	 * @return
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	/**
	 * URL解码转换
	 * 
	 * @param src
	 * @return
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * 字符串替�?
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static final String replace(String line, String oldString,
                                       String newString) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * 根据body内容生成完整的HTML内容
	 * 
	 * @param bodyContent
	 * @param encode
	 *            默认为utf-8
	 * @return
	 */
	public static String genHtml(String bodyContent, String encode) {
		if (encode == null || "".equals(encode.trim()))
			encode = "utf-8";

		StringBuffer sb = new StringBuffer();
		sb.append("<html xmlns=http://www.w3.org/1999/xhtml>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset="
				+ encode + "' />\n");
		sb.append("<title>查看消息</title>\n");
		sb.append("</head>\n");
		sb.append("\n");
		sb.append("<body>\n");
		sb.append(bodyContent);
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public static String removeHtml(String htmlStr) {
		String result = "";
		boolean flag = true;
		if (htmlStr == null || "".equals(htmlStr.trim())) {
			return "";
		}

		htmlStr = htmlStr.replace("\"", ""); // 去掉引号

		char[] a = htmlStr.toCharArray();
		int length = a.length;
		for (int i = 0; i < length; i++) {
			if (a[i] == '<') {
				flag = false;
				continue;
			}
			if (a[i] == '>') {
				flag = true;
				continue;
			}
			if (flag == true) {
				result += a[i];
			}
		}
		return result.toString();
	}

	/************************************* Base64 编解�? *********************************************/
	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	/** Base64 encode the given data */
	public static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append(" ");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	/**
	 * Decodes the given Base64 encoded String to a new byte array. The byte
	 * array holding the decoded data is returned.
	 */
	public static byte[] decode(String s) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/************************************* 压缩、解�? ****************************************/

	/**
	 * 压缩方法
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] compress(String str) {
		if (str == null)
			return null;
		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes());
			zout.closeEntry();
			compressed = out.toByteArray();
		} catch (IOException e) {
			compressed = null;
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return compressed;
	}

	/**
	 * 
	 * @param compressed
	 * @return
	 */
	public static final String decompress(byte[] compressed) {

		if (compressed == null)
			return null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed;

		try {

			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			byte[] buffer = new byte[1024];
			int offset = -1;

			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}

			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}

	/**
	 * �?��是否符合手机号码格式
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static boolean checkPhoneNum(String phoneNum) {
		final String regx = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(phoneNum);

		return m.matches();
	}

	public static String format(double value, String chart) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(chart);
		return df.format(value);
	}

	public static String getVolume(String src) {
		if (!src.contains(".")) {
			return src;
		} else {
			String tmp = src.substring(src.indexOf(".") + 1);
			int i = Integer.parseInt(tmp);
			System.out.println(i);
			if (i == 0) {
				return src.substring(0, src.indexOf("."));
			} else {
				return src;
			}
		}
	}

	/**
	 * Unicode转String方法
	 * 
	 * @param str
	 * @return
	 */
	public static String UnicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	/**
	 * 字体大小动�?设置方法
	 * 
	 * @param string
	 *            ,start,end,TextSize
	 * @return
	 */
	public static SpannableString setMyTextType(String string, int start,
                                                int end, int TextSize) {
		SpannableString builder = new SpannableString(string);
		builder.setSpan(new AbsoluteSizeSpan(TextSize), start, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return builder;
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */

		// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		// 鉴于最新4G号码的有47号码段，所以只限定第一位为1
		String telRegex = "[1]\\d{10}";
		if (isEmpty2(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * MD5加密 大写
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		String result_str = md5StrBuff.toString();
		return result_str.toLowerCase();
	}

	/**
	 * 根据距离返回公里数
	 * 
	 * @param d
	 *            单位 m
	 * @return
	 */
	public static String getDistance(float d) {
		if (d < 100) {
			return "100米以内";
		}
		if (d < 200) {
			return "200米以内";
		}
		if (d < 300) {
			return "300米以内";
		}
		if (d < 400) {
			return "400米以内";
		}
		if (d < 500) {
			return "500米以内";
		}
		BigDecimal b = new BigDecimal(d / 1000);
		double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1 + "公里";
	}

	//判断前几位数字是否为0
	public static String getFrontNoZero(String number) {
		int len = number.length();
		int index = 0;
		for (int i = 0;i < len;i ++) {
			String chars = number.substring(i,i + 1);
			if (chars.equals("0")) {//为0
				index = i;
			} else {
				if (chars.equals(".")) {//为小数点
					index = i - 1;
				}
			}
		}
		String newNumber = number.substring(len - index - 1);
		return newNumber;
	}
}
