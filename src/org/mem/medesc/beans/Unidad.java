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
//		System.out.println("U"+nroUnidad+" Pregunta al azar de unidad: " + rnd);
		
		/* 
		 * si la pregunta escogida ya fue preguntada
		 * se escoge la otra, sino no se retorna ninguna 
		 */
		if(!preguntas.get(rnd).isPreguntada()) {
//			System.out.println("U"+nroUnidad+" Esta ("+rnd+") no fue preguntada");
			seleccionada = preguntas.get(rnd);
			seleccionada.setPreguntada(true);
		} else if(!preguntas.get((rnd+1)%2).isPreguntada()) {
//			System.out.println("U"+nroUnidad+" La otra ("+((rnd+1)%2)+") no fue preguntada");
			seleccionada = preguntas.get((rnd+1)%2);
			seleccionada.setPreguntada(true);
		}		
		
		System.out.println("U"+nroUnidad+" Seleccionada: " + seleccionada);
		if(seleccionada == null)  {						
			fuePreguntada = true;			
		}
		return seleccionada;
	}
}
