package com.yeksefr.khayam;


public class Helper
{
	public static byte[] get2Byte(short i)
	{
		return new byte[] {
				(byte)((i >> 0) & 0xff),
				(byte)((i >> 8) & 0xff),
		};
	}
	
	public static byte[] get4Byte(int i)
	{
		return new byte[] {
			(byte)((i >> 0) & 0xff),
			(byte)((i >> 8) & 0xff),
			(byte)((i >> 16) & 0xff),
			(byte)((i >> 24) & 0xff),
		};
	}
	
	public static byte[] get8Byte(long data)
	{
		return new byte[] {
			(byte)((data >> 0) & 0xff),
			(byte)((data >> 8) & 0xff),
			(byte)((data >> 16) & 0xff),
			(byte)((data >> 24) & 0xff),
			(byte)((data >> 32) & 0xff),
			(byte)((data >> 40) & 0xff),
			(byte)((data >> 48) & 0xff),
			(byte)((data >> 56) & 0xff),
		};
	}
	
	public static byte[] get8Byte(double data) {
		return get8Byte(Double.doubleToLongBits(data));
	}
	
	public static short toShort(byte b1, byte b2)
	{
		return (short)(((0xff & b2) << 8) | ((0xff & b1)));
	}
	
	public static int toInt(byte b1, byte b2, byte b3, byte b4)
	{
		return ((b4 & 0xff) << 24) | ((b3 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b1 & 0xff);
	}

	public static long toLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8)
	{
		return (long)(
			(long)(0xff & b8) << 56 |
			(long)(0xff & b7) << 48 |
			(long)(0xff & b6) << 40 |
			(long)(0xff & b5) << 32 |
			(long)(0xff & b4) << 24 |
			(long)(0xff & b3) << 16 |
			(long)(0xff & b2) << 8 |
			(long)(0xff & b1) << 0
		);
	}
	
	public static double toDouble(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8)
	{
		return Double.longBitsToDouble(toLong(b1, b2, b3, b4, b5, b6, b7, b8));
	}
	
	
	
	
	////////////////
	
    static String en_num = "0123456789";
    static String fa_num = "\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u06F8\u06F9";

	public static String convertEnglishNumbersToParsi(String s)
	{
		for(int j = 0; j < s.length(); j++)
		{
			char c = s.charAt(j);
			int i = en_num.indexOf(c);
			if (i != -1)
			{
				s = s.replace(c, fa_num.charAt(i));
			}
		}
		
		return s;
	}
	
	public static String convertEnglishNumbersToParsiCammaSeparated(String s)
	{
		StringBuffer sb = new StringBuffer();
		int len = s.length();
		for(int j = 0; j < len; j++)
		{
			if ((len - j) % 3 == 0 && j != 0)
				sb.append(',');
				
			char c = s.charAt(j);
			int i = en_num.indexOf(c);
			if (i != -1)
			{
				sb.append(fa_num.charAt(i));
			} else
			{
				sb.append(en_num.charAt(i));
			}
		}
		
		return sb.toString();
	}
}