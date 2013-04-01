package org.mem.medesc.utils;

public class Pair<U,V> {

	private U u;
	private V v;
	
	public Pair(U u, V v) {
		this.u = u;
		this.v = v;
	}

	public U getFirst() {
		return u;
	}

	public void setFirst(U u) {
		this.u = u;
	}

	public V getSecond() {
		return v;
	}

	public void setSecond(V v) {
		this.v = v;
	}
	
	public void clean() {
		u = null;
		v = null;
	}

}