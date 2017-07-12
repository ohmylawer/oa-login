/*
 * Created on 2009-11-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.trs.om.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import sense.Living1.*;

/**
 * @author zengk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SrvVerify {

	/**
	 * 
	 */
	public SrvVerify() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		/*cn.com.sense.living.SrvVerify test = new cn.com.sense.living.SrvVerify();
		
		byte[] bInfo = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E','F', '1', '2', '3', '4', '5'};
		boolean flag = test.MD5Verify(bInfo,"1111111111111");
		System.out.println("success flag="+flag);
		Living1 liv1 = new Living1();
		byte[] digestLocal = new byte[20];
		byte[] key = {0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,
			      0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b,0x0b};
		byte[] text = ((String)"1234567890123456789012345678901234567890").getBytes();
		
		int res = liv1.LIV_hmac_software(text, text.length, key, digestLocal);
		if (res != Living1.LIV_SUCCESS) {
			System.out.print("\t取本地计算的hmac响应失败， 错误代码是： " + res);
			return;
		}
		System.out.println("\t取本地计算的hmac响应成功 ");
		for(int i=0;i<20;i++)
		{
			System.out.println("digestLocal["+i+"] = "+ digestLocal[i]);
		}*/
	}
	
	public boolean HmacVerify(byte[] bStrRand,byte[] key,String result)
	{
		boolean is_success = false;
		
		try{
		byte[] digestLocal = new byte[20];
		//Living1 liv1 = new Living1();
		/*int res = liv1.LIV_hmac_software(bStrRand, bStrRand.length, key, digestLocal);
		if (res != Living1.LIV_SUCCESS) {
			is_success = false;
		}*/
		byte[] hash = new byte[20];
		byte[] k_ipad = new byte[64];
		byte[] k_opad = new byte[64];
		for (int i=0;i<20;i++)
		{
			k_ipad[i] = key[i];
			k_opad[i] = key[i];
		}
	    for (int i = 0; i < 64; i++) {
	        k_ipad[i] ^= 0x36;
	        k_opad[i] ^= 0x5c;
	    }
		try{
			MessageDigest sha1 = MessageDigest.getInstance("sha-1");
			sha1.update(k_ipad);
			sha1.update(bStrRand);
			hash = sha1.digest();
			MessageDigest sha1_a = MessageDigest.getInstance("sha-1");
			sha1_a.update(k_opad);
			sha1_a.update(hash);
			digestLocal = sha1_a.digest();			
		}catch(NoSuchAlgorithmException e)
		{
			System.out.println(" MessageDigest  digest error! ");
		}
		
		
		//
		String  bStrhmac = null;
		StringBuffer strhmac = new StringBuffer(digestLocal.length*2);
		String  bStr;
		
		//将生成的hmac值转换成字符串传给StringBuffer
		for(int iX = 0; iX < digestLocal.length; iX++ )
		{
			bStr = Integer.toHexString(digestLocal[iX]);
			// byte是两个字节的，而上面的Integer.toHexString会把字节扩展为4个字节
			if(1 == bStr.length())
			{
				strhmac.append("0");
			}
			strhmac.append(bStr.length() > 2 ? bStr.substring(6,8) : bStr); 
		}
		
		//将MD5值转换成String
		bStrhmac = strhmac.toString();
		
		//将字符串中的小写字母全部转换成大写字母
		bStrhmac = bStrhmac.toUpperCase();
		
		//判断客户端生成的MD5值与服务器端生成的MD5值是否相等
		is_success = result.equals(bStrhmac);
		
		}catch (Exception e)
		{
			is_success = false;
		}
		
		return is_success;
	}//HmacVerify
	
	public boolean MD5Verify(byte[] key,String MD5Str)
	{
		boolean is_success = false;
		
		try{
			//使用MD5密码学算法
			java.security.MessageDigest alga = java.security.MessageDigest.getInstance("MD5");
			
			byte[] bDiegst = alga.digest( key );
			//MD5值计算完成
	
			String  bStrMd5 = null;
			StringBuffer strMd5 = new StringBuffer(bDiegst.length*2);
			String  bStr;
			
			//将生成的MD5值转换成字符串传给StringBuffer
			for(int iX = 0; iX < bDiegst.length; iX++ )
			{
				bStr = Integer.toHexString(bDiegst[iX]);
				// byte是两个字节的，而上面的Integer.toHexString会把字节扩展为4个字节
				if(1 == bStr.length())
				{
					strMd5.append("0");
				}
				strMd5.append(bStr.length() > 2 ? bStr.substring(6,8) : bStr); 
			}
			
			//将MD5值转换成String
			bStrMd5 = strMd5.toString();
			
			//将字符串中的小写字母全部转换成大写字母
			bStrMd5 = bStrMd5.toUpperCase();
			
			//判断客户端生成的MD5值与服务器端生成的MD5值是否相等
			is_success = MD5Str.equals(bStrMd5);
			
		}catch (NoSuchAlgorithmException e)
		{
			is_success = false;
		}
		return is_success;
	}//MD5Verify

}
