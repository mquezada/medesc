package org.mem.medesc;

import org.mem.medesc.beans.Pregunta;
import org.mem.medesc.beans.Respuesta;
import org.mem.medesc.beans.Unidad;
import org.mem.medesc.utils.Pair;

public class CuestionarioController {

	static final int B = 0;
	static final int X = 1;
	static final int Y = 2;
	static final int D = 3;
	
	static final int TOTAL_PREGUNTAS = 32;
	static final int TOTAL_UNIDADES = 16;
	static final int MAX_DUMMIES = 3;
	static final int MAX_FALLIDAS = 2;
	static final int TOTAL_ANOS = 4;
	
	static int TOTAL_PREGUNTAS_TEST = 4;
	
	private Pair<Respuesta, Respuesta> respuestaAnterior;
	private Pair<Respuesta, Respuesta> respuestaActual;
	
	private int unidadActual;
	private int totalDummies;
	private int totalFallidas;
	private int totalPreguntasAcum;
	
	private int grafoId;	
	private Unidad[] unidades;
	private Unidad[] unidadesClon;
	
	private boolean clon;
	
	public CuestionarioController(int grafoId, Unidad[] unidades, 
			Unidad[] unidadesClon, int unidadInicial) {
		this.grafoId = grafoId;
		this.unidades = unidades;
		this.unidadesClon = unidadesClon;
		
		unidadActual = unidadInicial;		
		totalDummies = 0;
		totalFallidas = 0;
		totalPreguntasAcum = 0;
		
		respuestaAnterior = new Pair<Respuesta, Respuesta>(null, null);
		respuestaActual = new Pair<Respuesta, Respuesta>(null, null);
		
		clon = false;
	}
	
	public Pregunta getNextPregunta() {
		if(totalDummies == 3) {
			return null;
		}
		
		if(totalPreguntasAcum == TOTAL_PREGUNTAS_TEST) {
			return null;
		}
		
		totalPreguntasAcum++;
		if(clon) {
			return unidadesClon[unidadActual].seleccionarPreguntaAlAzar();
		} else {
			return unidades[unidadActual].seleccionarPreguntaAlAzar();
		}
	}
	
	public void establecerRespuesta(Respuesta r) {
		if(respuestaActual.getFirst() != null) {
			if(respuestaActual.getSecond() != null) {				
				respuestaAnterior.setFirst(respuestaActual.getFirst());
				respuestaAnterior.setSecond(respuestaActual.getSecond());
				
				respuestaActual.clean();
				respuestaActual.setFirst(r);

			} else {
				respuestaActual.setSecond(r);
			}
		} else {
			respuestaActual.setFirst(r);
		}		 
	}
	
	public void seleccionarSiguienteUnidad() {
		/* 
		 * determinar salto y condiciones de borde 
		 * usando respuestas anteriores
		 */ 
		Respuesta r1 = respuestaActual.getFirst();
		Respuesta r2 = respuestaActual.getSecond();
		if(r1 != null && r2 != null) {
			// B / B
			if(r1.getTipo() == B && r2.getTipo() == B) {
			
				Respuesta a1 = respuestaAnterior.getFirst();
				Respuesta a2 = respuestaAnterior.getSecond();
				
				/*
				 * Si no hubo anterior (o sea es el primer par de preguntas) , 
				 * se avanza en (16-U)/2 unidades redondeando hacia arriba.
				 */
				if(a1 == null || a2 == null) {
					unidadActual = (int) Math.ceil(Math.abs((TOTAL_UNIDADES - unidadActual)/2));
				/*
				 * Si la anterior fue buena / buena, 
				 * se avanza en (16-U)/2 unidades, redondeando hacia arriba.
				 */
				} else if(a1.getTipo() == B && a2.getTipo() == B) {
					unidadActual = (int) Math.ceil(Math.abs((TOTAL_UNIDADES - unidadActual)/2));
				/*
				 * Si la anterior (unidad A) fue distinta a buena/buena 
				 * se avanza en (A-U)/2 unidades, redondeando hacia arriba
				 */
				} else if(a1.getTipo() != B || a2.getTipo() != B) {
					unidadActual = (int) Math.ceil(Math.abs(a1.getUnidad() - unidadActual)/2);
				}
				
			// B / M || M / B
			} else if(
				(   r1.getTipo() == B && (
						r2.getTipo() == X || 
						r2.getTipo() == Y ||
						r2.getTipo() == D)) 
				|| ((			
						r1.getTipo() == X ||
						r1.getTipo() == Y ||
						r1.getTipo() == D) && 
					r2.getTipo() == B)) {
				
				/*
				 * Se considera que es información “confusa” 
				 * y se contabiliza esta “pregunta fallida”.
				 * 
				 * Si hay menos de 2 “preguntas fallidas” previas, 
				 * se agrega una pregunta extra al grafo.
				 */
				if(totalFallidas < 2) {
					TOTAL_PREGUNTAS_TEST++;
				}				
				totalFallidas++;
				
				/* 
				 * Se redirige a una pregunta perteneciente a una unidad 
				 * del mismo año académico que U. ( 1-4 / 5-8 / 9-12 / 13-16 )
				 */
				int unidadesPorAno = TOTAL_UNIDADES / TOTAL_ANOS;
				int min = (int) ((unidadActual/TOTAL_ANOS) * TOTAL_ANOS) + 1;
				unidadActual = (int) (Math.random() * unidadesPorAno + min);
				
			// X(D) / Y(D)
			} else if( 	(r1.getTipo() == X && r2.getTipo() == Y) ||				
						(r1.getTipo() == Y && r2.getTipo() == X) ||						
						(r1.getTipo() == D && r2.getTipo() == X) ||
						(r1.getTipo() == D && r2.getTipo() == Y) ||
						(r1.getTipo() == X && r2.getTipo() == D) ||
						(r1.getTipo() == Y && r2.getTipo() == D) ||				
						(r1.getTipo() == D && r2.getTipo() == D)) {
				
				/*
				 * contabilizar las dummies
				 */
				if (r1.getTipo() == D || r2.getTipo() == D) {
					totalDummies++;
				}
				
				Respuesta a1 = respuestaAnterior.getFirst();
				Respuesta a2 = respuestaAnterior.getSecond();
				
				/*
				 * Si la respuesta (par original/clon) anterior fue mala/mala 
				 * o no hay anterior, se retrocede en U/2 unidades, redondeando hacia abajo. 
				 */
				if((a1 == null || a2 == null) ||
					((a1.getTipo() == X || a1.getTipo() == Y || a1.getTipo() == D) &&
					(a2.getTipo() == X || a2.getTipo() == Y || a2.getTipo() == D))) {
					
					unidadActual = Math.min(1, Math.abs(unidadActual - unidadActual/2));
				/*
				 * Si la respuesta (par original/clon) anterior fue buena/buena, 
				 * se retrocede en (U-A)/2 unidades, redondeando hacia abajo.
				 */
				} else if(a1.getTipo() == B && a2.getTipo() == B) {
					
					int unidadAnterior = a1.getUnidad();
					unidadActual = Math.min(1, Math.abs(unidadActual - Math.abs(unidadActual - unidadAnterior)/2));
				}
				
			// X(Y) / X(Y)
			} else if((r1.getTipo() == X && r2.getTipo() == X) ||				
						(r1.getTipo() == Y && r2.getTipo() == Y)) {
				/*
				 * El salto es directamente hacia la unidad indexada 
				 * en la base de datos de respuestas.
				 *   
				 */
				unidadActual = r1.getContestada().getApuntaA();
			}
			/*
			 * en este punto, la unidad que se entregue no debe ser clon			
			 */
			clon = false;
			
		/*
		 * si no, es que se hizo solo la pregunta original, la siguiente es clon
		 */
		} else {
			clon = true;
			// unidadActual = unidadActual
		}
	}

	public int getGrafoId() {
		return grafoId;
	}

	public void setGrafoId(int grafoId) {
		this.grafoId = grafoId;
	}
	
	
	
}
