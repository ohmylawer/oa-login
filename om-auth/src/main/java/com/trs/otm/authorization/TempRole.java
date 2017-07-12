package com.trs.otm.authorization;

public class TempRole {
	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		if(this==obj)
			return true;
		if(obj instanceof TempRole){
			TempRole other=(TempRole)obj;
			if(this.name.equals(other.name))
				return true;
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
