package com.trs.om.bean;

/**
 * 封装用户绑定的加密锁信息.
 */
public class UserKey {

	private Long id;
	private Long userId;
	private String keySn;
	private String hmacKey;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getKeySn() {
		return keySn;
	}
	public void setKeySn(String keySn) {
		this.keySn = keySn;
	}
	public String getHmacKey() {
		return hmacKey;
	}
	public void setHmacKey(String hmacKey) {
		this.hmacKey = hmacKey;
	}

}
