package com.trs.otm.authentication;

import java.util.TreeMap;

public class JSArray<T> {
	private TreeMap<Integer,T> map=new TreeMap<Integer,T>();
	public void set(int index, T obj) {
		map.put(index, obj);
	}
	public T get(int index){
		return map.get(index);
	}
	public int size(){
		return map.lastKey()+1;
	}
}
