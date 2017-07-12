package com.trs.otm.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	private static final byte chrsz = 8;
	private static final boolean hexcase = false;
	private static final int mode = 32;

	public static JSArray<Integer> str2binl(String D) {
		JSArray<Integer> C = new JSArray<Integer>();
		int A = ((1 << chrsz) - 1);
		for (int B = 0; B < D.length() * chrsz; B += chrsz) {
			int index = B >> 5;
			int temp = JSUtil.getIntegerValue(C.get(index));
			C.set(index, temp | ((D.codePointAt(B / chrsz) & A) << (B % 32)));
		}
		return C;
	}

	public static int bit_rol(int A, int B) {
		int left = A << B;
		int right = A >>> (32 - B);
		int result = (left) | (right);
		return result;
	}

	public static int safe_add(int A, int D) {
		int C = (A & 65535) + (D & 65535);
		int B = (A >> 16) + (D >> 16) + (C >> 16);
		int result = (B << 16) | (C & 65535);
		return result;
	}

	public static int md5_cmn(int F, int C, int B, int A, int E, int D) {
		return safe_add(bit_rol(safe_add(safe_add(C, F), safe_add(A, D)), E), B);
	}

	public static int md5_ff(int C, int B, int G, int F, int A, int E, int D) {
		return md5_cmn((B & G) | ((~B) & F), C, B, A, E, D);
	}

	public static int md5_gg(int C, int B, int G, int F, int A, int E, int D) {
		return md5_cmn((B & F) | (G & (~F)), C, B, A, E, D);
	}

	public static int md5_hh(int C, int B, int G, int F, int A, int E, int D) {
		return md5_cmn(B ^ G ^ F, C, B, A, E, D);
	}

	public static int md5_ii(int C, int B, int G, int F, int A, int E, int D) {
		return md5_cmn(G ^ (B | (~F)), C, B, A, E, D);
	}

	public static JSArray<Integer> core_md5(JSArray<Integer> K, int F) {
		int index = F >> 5;
		K.set(index, JSUtil.getIntegerValue(K.get(index)) | (128 << (F % 32)));
		index = (((F + 64) >>> 9) << 4) + 14;
		K.set(index, F);
		int J = 1732584193;
		int I = -271733879;
		int H = -1732584194;
		int G = 271733878;
		for (int C = 0; C < K.size(); C += 16) {
			int E = J;
			int D = I;
			int B = H;
			int A = G;
			J = md5_ff(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 0)), 7,
					-680876936);
			G = md5_ff(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 1)), 12,
					-389564586);
			H = md5_ff(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 2)), 17,
					606105819);
			I = md5_ff(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 3)), 22,
					-1044525330);
			J = md5_ff(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 4)), 7,
					-176418897);
			G = md5_ff(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 5)), 12,
					1200080426);
			H = md5_ff(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 6)), 17,
					-1473231341);
			I = md5_ff(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 7)), 22,
					-45705983);
			J = md5_ff(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 8)), 7,
					1770035416);
			G = md5_ff(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 9)), 12,
					-1958414417);
			H = md5_ff(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 10)), 17,
					-42063);
			I = md5_ff(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 11)), 22,
					-1990404162);
			J = md5_ff(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 12)), 7,
					1804603682);
			G = md5_ff(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 13)), 12,
					-40341101);
			H = md5_ff(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 14)), 17,
					-1502002290);
			I = md5_ff(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 15)), 22,
					1236535329);
			J = md5_gg(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 1)), 5,
					-165796510);
			G = md5_gg(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 6)), 9,
					-1069501632);
			H = md5_gg(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 11)), 14,
					643717713);
			I = md5_gg(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 0)), 20,
					-373897302);
			J = md5_gg(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 5)), 5,
					-701558691);
			G = md5_gg(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 10)), 9,
					38016083);
			H = md5_gg(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 15)), 14,
					-660478335);
			I = md5_gg(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 4)), 20,
					-405537848);
			J = md5_gg(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 9)), 5,
					568446438);
			G = md5_gg(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 14)), 9,
					-1019803690);
			H = md5_gg(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 3)), 14,
					-187363961);
			I = md5_gg(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 8)), 20,
					1163531501);
			J = md5_gg(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 13)), 5,
					-1444681467);
			G = md5_gg(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 2)), 9,
					-51403784);
			H = md5_gg(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 7)), 14,
					1735328473);
			I = md5_gg(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 12)), 20,
					-1926607734);
			J = md5_hh(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 5)), 4,
					-378558);
			G = md5_hh(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 8)), 11,
					-2022574463);
			H = md5_hh(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 11)), 16,
					1839030562);
			I = md5_hh(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 14)), 23,
					-35309556);
			J = md5_hh(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 1)), 4,
					-1530992060);
			G = md5_hh(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 4)), 11,
					1272893353);
			H = md5_hh(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 7)), 16,
					-155497632);
			I = md5_hh(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 10)), 23,
					-1094730640);
			J = md5_hh(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 13)), 4,
					681279174);
			G = md5_hh(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 0)), 11,
					-358537222);
			H = md5_hh(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 3)), 16,
					-722521979);
			I = md5_hh(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 6)), 23,
					76029189);
			J = md5_hh(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 9)), 4,
					-640364487);
			G = md5_hh(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 12)), 11,
					-421815835);
			H = md5_hh(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 15)), 16,
					530742520);
			I = md5_hh(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 2)), 23,
					-995338651);
			J = md5_ii(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 0)), 6,
					-198630844);
			G = md5_ii(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 7)), 10,
					1126891415);
			H = md5_ii(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 14)), 15,
					-1416354905);
			I = md5_ii(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 5)), 21,
					-57434055);
			J = md5_ii(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 12)), 6,
					1700485571);
			G = md5_ii(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 3)), 10,
					-1894986606);
			H = md5_ii(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 10)), 15,
					-1051523);
			I = md5_ii(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 1)), 21,
					-2054922799);
			J = md5_ii(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 8)), 6,
					1873313359);
			G = md5_ii(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 15)), 10,
					-30611744);
			H = md5_ii(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 6)), 15,
					-1560198380);
			I = md5_ii(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 13)), 21,
					1309151649);
			J = md5_ii(J, I, H, G, JSUtil.getIntegerValue(K.get(C + 4)), 6,
					-145523070);
			G = md5_ii(G, J, I, H, JSUtil.getIntegerValue(K.get(C + 11)), 10,
					-1120210379);
			H = md5_ii(H, G, J, I, JSUtil.getIntegerValue(K.get(C + 2)), 15,
					718787259);
			I = md5_ii(I, H, G, J, JSUtil.getIntegerValue(K.get(C + 9)), 21,
					-343485551);
			J = safe_add(J, E);
			I = safe_add(I, D);
			H = safe_add(H, B);
			G = safe_add(G, A);
		}
		if (mode == 16) {
			JSArray<Integer> result = new JSArray<Integer>();
			result.set(0, I);
			result.set(1, H);
			return result;
		} else {
			JSArray<Integer> result = new JSArray<Integer>();
			result.set(0, J);
			result.set(1, I);
			result.set(2, H);
			result.set(3, G);
			return result;
		}
	}

	public static String binl2hex(JSArray<Integer> C) {
		String B = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
		String D = "";
		for (int A = 0; A < C.size() * 4; A++) {
			int index1 = ((C.get(A >> 2) >> ((A % 4) * 8 + 4)) & 15);
			int index2 = ((C.get(A >> 2) >> ((A % 4) * 8)) & 15);
			D += B.charAt((int) index1) + "" + B.charAt((int) index2);
		}
		return D;
	}

	public static String md5_3(String B) {
		JSArray<Integer> A = core_md5(str2binl(B), B.length() * chrsz);
		A = core_md5(A, 16 * chrsz);
		A = core_md5(A, 16 * chrsz);
		return binl2hex(A);
	}

	public static String hex_md5(String A) {
		return binl2hex(core_md5(str2binl(A), A.length() * chrsz));
	}

	public static String md5(String A) {
		return hex_md5(A);
	}

	public static String getBytesString(byte[] bytes) {
		String outString = "";
		for (int i = 0; i < bytes.length; i++) {
			outString += String.format("%1$X", bytes[i]);
		}
		return outString;
	}

	public static String _md5(String str) throws NoSuchAlgorithmException {
		MessageDigest alg = MessageDigest.getInstance("MD5");
		byte[] input = str.getBytes();
		alg.update(input);
		byte[] hash = alg.digest();
		return getBytesString(hash);
	}

	public static String _md5_3(String str) throws NoSuchAlgorithmException {
		MessageDigest alg = MessageDigest.getInstance("MD5");
		// first
		alg.reset();
		JSArray<Integer> src = str2binl(str);
		for (int i = 0; i < src.size(); i++) {
			byte byteData = (byte) JSUtil.getIntegerValue(src.get(i));
			alg.update(byteData);
		}
		byte[] temp = alg.digest();
		// second
		alg.reset();
		alg.update(temp);
		temp = alg.digest();
		// third
		alg.reset();
		alg.update(temp);
		temp = alg.digest();
		return getBytesString(temp);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(MD5.md5("jqccadmin"));
//	     long time1 = System.currentTimeMillis();
//			int n = 100000000;
//			boolean[] primes = new boolean[n+1];
//			for(int i=2;i<=n;++i){
//				primes[i] = true;
//			}
//			for(int j=2;j<=n;++j){
//				if(primes[j]==true){
//					for(int m=2;j*m<=n;++m)
//						primes[j*m] = false;
//				}
//			}
//
//			for(int k=2;k<=n;++k){
//				if(primes[k]==true){
//					System.out.println(k+"是素数");
//				}
//			}
			
//			System.out.println((System.currentTimeMillis() - time1)/1000f +"秒");
	}

	public static String digestPassword(String pw, String vc) {
		return md5(md5_3(pw) + vc.toUpperCase());
	}
}
