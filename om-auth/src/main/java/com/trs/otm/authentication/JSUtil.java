package com.trs.otm.authentication;

public class JSUtil {
	public static byte getByteValue(Byte b){
		if(b==null)
			return 0;
		else
			return b.byteValue();
	}
	public static long getLongValue(Long l){
		if(l==null)
			return 0;
		else
			return l.longValue();
	}
	public static int getIntegerValue(Integer i){
		if(i==null)
			return 0;
		else
			return i.intValue();
	}
}
