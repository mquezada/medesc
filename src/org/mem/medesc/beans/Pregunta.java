package org.mem.medesc.beans;

import java.util.List;

public class Pregunta {
	
	long id;
	String imgPath;
	int unidad;
	int tipo;
	List<Alternativa> alternativas;
	
	public Pregunta() { }

	public Pregunta(long id, String imgPath, int unidad, int tipo,
			List<Alternativa> alternativas) {
		super();
		this.id = id;
		this.imgPath = imgPath;
		this.unidad = unidad;
		this.tipo = tipo;
		this.alternativas = alternativas;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getUnidad() {
		return unidad;
	}

	public void setUnidad(int unidad) {
		this.unidad = unidad;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public List<Alternativa> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(List<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}

	@Override
	public String toString() {
		return "Pregunta [id=" + id + ", imgPath=" + imgPath + ", unidad="
				+ unidad + ", tipo=" + tipo + ", alternativas=" + alternativas
				+ "]";
	}
	
	
	
}
