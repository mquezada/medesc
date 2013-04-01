package org.mem.medesc.beans;

import java.util.List;

public class Unidad {

	public int nroUnidad;
	public List<Pregunta> preguntas;
	public boolean fuePreguntada;	

	public Unidad siguiente;
	public Unidad anterior;
	
	public Pregunta seleccionarPreguntaAlAzar() {
		Pregunta seleccionada = null;
		
		// solo dos preguntas orig/clon por unidad
		int rnd = (int) (2 * Math.random());
		
		/* 
		 * si la pregunta escogida ya fue preguntada
		 * se escoge la otra, sino no se retorna ninguna 
		 */
		if(!preguntas.get(rnd).isPreguntada()) {
			seleccionada = preguntas.get(rnd);
			seleccionada.setPreguntada(true);
		} else if(!preguntas.get((rnd+1)%2).isPreguntada()) {
			seleccionada = preguntas.get((rnd+1)%2);
			seleccionada.setPreguntada(true);
		}
		
		if(seleccionada == null) 
			fuePreguntada = true;
		return seleccionada;
	}
}
