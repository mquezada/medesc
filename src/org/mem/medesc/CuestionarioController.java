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
	
	int TOTAL_PREGUNTAS_TEST;
	
	private Pair<Respuesta, Respuesta> respuestaAnterior;
	private Pair<Respuesta, Respuesta> respuestaActual;
	
	private int unidadActual;
	static int totalDummies;
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
		
		TOTAL_PREGUNTAS_TEST = 4;
	}
	
	public Pregunta getNextPregunta() {
		if(totalDummies == 3) {
			System.out.println("G"+grafoId+ " " + "Total dummies 3");
			System.out.println("G"+grafoId+ " " + "Total preguntas acumulado = " + totalPreguntasAcum);	
			return null;
		}
		
		if(!clon && totalPreguntasAcum == TOTAL_PREGUNTAS_TEST) {
			System.out.println("G"+grafoId+ " " + "Total preguntas acumulado = " + totalPreguntasAcum);			
			System.out.println("G"+grafoId+ " " + "Total preguntas del test alcanzado: " + TOTAL_PREGUNTAS_TEST);
			return null;
		}
		
		
		Pregunta siguiente = null;
		if(clon) {
			Respuesta preguntaOrig = respuestaActual.getFirst();
			int offset = (int) (preguntaOrig.getIdPregunta() % 2);
			int unidad = preguntaOrig.getUnidad()-1;
			
			System.out.println("G"+grafoId+ " " + "Pregunta clon");
			System.out.print("G"+grafoId+ " ");
			
			siguiente = unidadesClon[unidad].preguntas.get((offset+1)%2);
			
			System.out.println("U"+unidad+" Seleccionada: " + siguiente);
			
//			siguiente = unidadesClon[unidadActual].seleccionarPreguntaAlAzar();
//			
//			int direccion = 1;
//			int signo = -1;
//			
//			/*
//			 * si es unidad 16, parte altiro buscando en la unidad 15
//			 */
//			if(unidadActual == TOTAL_UNIDADES - 1) {
//				direccion = -1;
//			}
//			
//			while(siguiente == null) {
//				if(unidadActual + direccion > unidadesClon.length-1 || unidadActual + direccion < 0)
//					break;
//				
//				if(unidadesClon[unidadActual + direccion] != null) {
//					System.out.print("G"+grafoId+ " ");
//					siguiente = unidadesClon[unidadActual + direccion].seleccionarPreguntaAlAzar();
//				} else { 
//					break;
//				}
//				
//				if(unidadActual == TOTAL_UNIDADES - 1) {
//					direccion--;
//				} else if (unidadActual == 0) {
//					direccion++;
//				} else {
//					if(direccion < 0)
//						direccion = (Math.abs(direccion) + 1) * signo;
//					else
//						direccion = signo * direccion;
//					
//					signo = -signo;
//				}
//			}
			
			
		} else {
			System.out.println("G"+grafoId+ " " + "Pregunta original");
			System.out.print("G"+grafoId+ " ");
			siguiente = unidades[unidadActual].seleccionarPreguntaAlAzar();
			 
			int direccion = 1;
			int signo = -1;
			
			/*
			 * si es unidad 16, parte altiro buscando en la unidad 15
			 */
			if(unidadActual == TOTAL_UNIDADES - 1) {
				direccion = -1;
			}
			
			while(siguiente == null) {
				if(unidadActual + direccion > unidades.length-1 || unidadActual + direccion < 0)
					break;
				
				if(unidades[unidadActual + direccion] != null) {
					System.out.print("G"+grafoId+ " ");
					siguiente = unidades[unidadActual + direccion].seleccionarPreguntaAlAzar();
				} else { 
					break;
				}
				
				if(unidadActual == TOTAL_UNIDADES - 1) {
					direccion--;
				} else if (unidadActual == 0) {
					direccion++;
				} else {
					if(direccion < 0)
						direccion = (Math.abs(direccion) + 1) * signo;
					else
						direccion = signo * direccion;
					
					signo = -signo;
				}
			}		
			
		}
		
		return siguiente;
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
		
		System.out.println("G"+grafoId+ " " + "Par respuesta actual: " + respuestaActual.getFirst() + " " + respuestaActual.getSecond());
		System.out.println("G"+grafoId+ " " + "Par respuesta anterior: " + respuestaAnterior.getFirst() + " " + respuestaAnterior.getSecond());
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
				System.out.println("G"+grafoId+ " " + "Buena/Buena");
				Respuesta a1 = respuestaAnterior.getFirst();
				Respuesta a2 = respuestaAnterior.getSecond();
				
				/*
				 * Si no hubo anterior (o sea es el primer par de preguntas) , 
				 * se avanza en (16-U)/2 unidades redondeando hacia arriba.
				 */
				if(a1 == null || a2 == null) {
					System.out.println("G"+grafoId+ " " + "U = U + (16-U)/2 [U=" + (unidadActual+1) + "]");
					// == 8 + U/2
					//unidadActual = (int) Math.ceil(Math.abs((TOTAL_UNIDADES - unidadActual)/2));
					unidadActual = Math.min(TOTAL_UNIDADES - 1, TOTAL_UNIDADES/2 + (int) ((unidadActual)/2.0));
				/*
				 * Si la anterior fue buena / buena, 
				 * se avanza en (16-U)/2 unidades, redondeando hacia arriba.
				 */
				} else if(a1.getTipo() == B && a2.getTipo() == B) {
					System.out.println("G"+grafoId+ " " + "U = U + (16-U)/2 [U=" + (unidadActual+1) + "]");					
					//unidadActual = (int) Math.ceil(Math.abs((TOTAL_UNIDADES - unidadActual)/2));
					unidadActual = Math.min(TOTAL_UNIDADES - 1, TOTAL_UNIDADES/2 + (int) ((unidadActual)/2.0));
				/*
				 * Si la anterior (unidad A) fue distinta a buena/buena 
				 * se avanza en (A-U)/2 unidades, redondeando hacia arriba
				 */
				} else if(a1.getTipo() != B || a2.getTipo() != B) {
					int unidadAnterior = a1.getUnidad();
					System.out.println("G"+grafoId+ " " + "U = U + (A-U)/2 [U=" + (unidadActual+1) + ", A="+(unidadAnterior+1)+"]");
					// == U/2 + A/2
					//unidadActual = Math.min(TOTAL_UNIDADES - 1, unidadActual + (int) Math.ceil(Math.abs(a1.getUnidad() - unidadActual)/2.0));
					unidadActual = Math.min(TOTAL_UNIDADES - 1, (int) (((unidadActual) / 2.0) + ((unidadAnterior) / 2.0)));
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
				System.out.println("G"+grafoId+ " " + "B/M o M/B");
				/*
				 * Se considera que es informaci�n �confusa� 
				 * y se contabiliza esta �pregunta fallida�.
				 * 
				 * Si hay menos de 2 �preguntas fallidas� previas, 
				 * se agrega una pregunta extra al grafo.
				 */
				if(totalFallidas < 2) {
					System.out.println("G"+grafoId+ " " + "Total Fallidas < 2");
					TOTAL_PREGUNTAS_TEST++;
				}				
				totalFallidas++;
				
				if(r1.getTipo() == D || r2.getTipo() == D) {
					totalDummies++;
				}				
				
				/* 
				 * Se redirige a una pregunta perteneciente a una unidad 
				 * del mismo a�o acad�mico que U. ( 1-4 / 5-8 / 9-12 / 13-16 )
				 */
				int unidadesPorAno = TOTAL_UNIDADES / TOTAL_ANOS;
				int min = (int) ((unidadActual/TOTAL_ANOS) * TOTAL_ANOS);
				unidadActual = (int) (Math.random() * unidadesPorAno + min) - 1;
				
				if(unidadActual < 0)
					unidadActual = 0;
				
			// X(D) / Y(D)
			} else if( 	(r1.getTipo() == X && r2.getTipo() == Y) ||				
						(r1.getTipo() == Y && r2.getTipo() == X) ||						
						(r1.getTipo() == D && r2.getTipo() == X) ||
						(r1.getTipo() == D && r2.getTipo() == Y) ||
						(r1.getTipo() == X && r2.getTipo() == D) ||
						(r1.getTipo() == Y && r2.getTipo() == D) ||				
						(r1.getTipo() == D && r2.getTipo() == D)) {
				System.out.println("G"+grafoId+ " " + "X(D) / Y(D)");
				/*
				 * contabilizar las dummies
				 */
				if (r1.getTipo() == D || r2.getTipo() == D) {
					System.out.println("G"+grafoId+ " " + "Dummy, se suma 1");
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
					
					System.out.println("G"+grafoId+ " " + "U = U - U/2 [U=" + (unidadActual+1) + "]");
					//unidadActual = Math.min(1, Math.abs(unidadActual - unidadActual/2));
					unidadActual = unidadActual/2;
				/*
				 * Si la respuesta (par original/clon) anterior fue buena/buena, 
				 * se retrocede en (U-A)/2 unidades, redondeando hacia abajo.
				 */
				} else if(a1.getTipo() == B && a2.getTipo() == B) {
										
					int unidadAnterior = a1.getUnidad();
					System.out.println("G"+grafoId+ " " + "U = U - (U-A)/2 [U=" + (unidadActual+1) + ", A=" + (unidadAnterior+1) + "]");
					
					unidadActual = Math.max(1, Math.abs(unidadActual - Math.abs(unidadActual - unidadAnterior)/2));
				}
				
			// X(Y) / X(Y)
			} else if((r1.getTipo() == X && r2.getTipo() == X) ||				
						(r1.getTipo() == Y && r2.getTipo() == Y)) {
				System.out.println("G"+grafoId+ " " + "X(Y) / X(Y)");
				/*
				 * El salto es directamente hacia la unidad indexada 
				 * en la base de datos de respuestas.
				 *   
				 */
				unidadActual = r1.getContestada().getApuntaA() - 1;
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
			totalPreguntasAcum++;
		}
		System.out.println("G"+grafoId+ " " + "Siguiente unidad: " + (unidadActual+1));
	}

	public int getGrafoId() {
		return grafoId;
	}

	public void setGrafoId(int grafoId) {
		this.grafoId = grafoId;
	}
	
	
	
}
