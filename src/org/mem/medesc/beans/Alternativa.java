package org.mem.medesc.beans;

public class Alternativa {
	
	long id;
	long idPregunta;
	String imgPath;
	int tipo;
	
	public Alternativa() { }
	
	public Alternativa(long id, long idPregunta, String imgPath, int tipo) {
		super();
		this.id = id;
		this.idPregunta = idPregunta;
		this.imgPath = imgPath;
		this.tipo = tipo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdPregunta() {
		return idPregunta;
	}

	public void setIdPregunta(long idPregunta) {
		this.idPregunta = idPregunta;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "Alternativa [id=" + id + ", idPregunta=" + idPregunta
				+ ", imgPath=" + imgPath + ", tipo=" + tipo + "]";
	}
	
	

}
