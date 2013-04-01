package org.mem.medesc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mem.medesc.beans.Alternativa;
import org.mem.medesc.beans.Pregunta;
import org.mem.medesc.beans.Respuesta;
import org.mem.medesc.beans.Unidad;
import org.mem.medesc.data.DbHandler;
import org.mem.medesc.utils.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CuestionarioActivity extends Activity implements OnClickListener {
	
	static final public String TAG = "CuestionarioActivity";
	
	/* usados para generar la interfaz */
	Button alt1, alt2, alt3, alt4;    
    ImageView preg, alt1i, alt2i, alt3i, alt4i;    
    TextView nroPreguntaActualTextView;
        
    DatabaseHelper myDbHelper;
    SQLiteDatabase myDb = null;
    
    Pregunta preguntaActual;
    int nroPreguntaActual;
   
    long idMedicion;
    long tiempoInicial;
    
	/*
	 * las preguntas sacadas de la base de datos
	 * inmutable
	 */
	List<Pregunta> preguntas;

	/*
	 * grupos de preguntas
	 */
	Unidad[] unidades;
	Unidad[] unidadesClon;
	
	/*
	 * 
	 */
	int totalUnidades;
	
	/*
	 * Grafos 1 y 2
	 */
	CuestionarioController grafo1;
	CuestionarioController grafo2;
	
	/**
	 * inicializa variables y obtiene lista
	 * con preguntas
	 */
	private void init() {
		nroPreguntaActual = 0;
        tiempoInicial = System.currentTimeMillis();
    		
        totalUnidades = 16;
		/*
		 * obtener lista de preguntas
		 */
		try { 
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
            preguntas = DbHandler.getAllPreguntas(myDb);
            
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                myDb.close();
            }
        }
		
		/*
		 * grupos de preguntas
		 */
		unidades = new Unidad[totalUnidades];
		unidadesClon = new Unidad[totalUnidades];
		
		for(Pregunta p : preguntas) {			
			int unidad = p.getUnidad() - 1;
			
			if(p.getTipo() == 0) {
				if(unidades[unidad] == null) {
					unidades[unidad] = new Unidad();
					unidades[unidad].nroUnidad = unidad + 1;
					unidades[unidad].fuePreguntada = false;
					unidades[unidad].preguntas = new ArrayList<Pregunta>();								
				}
				unidades[unidad].preguntas.add(p);
			} else {
				if(unidadesClon[unidad] == null) {
					unidadesClon[unidad] = new Unidad();
					unidadesClon[unidad].nroUnidad = unidad + 1;
					unidadesClon[unidad].fuePreguntada = false;
					unidadesClon[unidad].preguntas = new ArrayList<Pregunta>();								
				}
				unidadesClon[unidad].preguntas.add(p);
			}
		}
		
		unidades[0].anterior = null;
		unidadesClon[0].anterior = null;
		unidades[totalUnidades-1].siguiente = null;
		unidadesClon[totalUnidades-1].siguiente = null;
		for(int i = 1; i < totalUnidades - 1; i++) {
			unidades[i].anterior = unidades[i-1];
			unidades[i].siguiente = unidades[i+1];
			
			unidadesClon[i].anterior = unidadesClon[i-1];
			unidadesClon[i].siguiente = unidadesClon[i+1];
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cuestionario);
		
		idMedicion = getIntent().getLongExtra("medicion_id", 0); 
		
		alt1 = (Button) findViewById(R.id.alt1b);
		alt2 = (Button) findViewById(R.id.alt2b);
		alt3 = (Button) findViewById(R.id.alt3b);
		alt4 = (Button) findViewById(R.id.alt4b);
		
		alt1.setOnClickListener(this);
		alt2.setOnClickListener(this);
		alt3.setOnClickListener(this);
		alt4.setOnClickListener(this);
		
		preg = (ImageView) findViewById(R.id.question_content);
		alt1i = (ImageView) findViewById(R.id.alt1);
		alt2i = (ImageView) findViewById(R.id.alt2);
		alt3i = (ImageView) findViewById(R.id.alt3);
		alt4i = (ImageView) findViewById(R.id.alt4);
		
        myDbHelper = new DatabaseHelper(this);
        myDbHelper.initializeDataBase();
        
        nroPreguntaActualTextView = (TextView) findViewById(R.id.question_number);
                
        init();
        
        grafo1 = new CuestionarioController(0, unidades, unidadesClon, 4);
        grafo2 = new CuestionarioController(1, unidades, unidadesClon, 11);
        
        fillPregunta(null);
	}
	

	boolean first = true;
	boolean second = true;
	int grafo = 1;
	
	protected Pregunta getNextPregunta(Respuesta r) {
		grafo = (grafo + 1) % 2;
		Pregunta pgrafo1 = null, pgrafo2 = null; 
		
		// grafo == 0
		if(first) {
			first = false;
			pgrafo1 = grafo1.getNextPregunta();
		} else
		
		// grafo == 1
		if(second) {
			grafo1.establecerRespuesta(r);
			grafo1.seleccionarSiguienteUnidad();
			
			second = false;
			pgrafo2 = grafo2.getNextPregunta();
		} else
		
		// grafo == 0
		if(!first && r.getGrafo() == (grafo+1)%2) {
			grafo2.establecerRespuesta(r);
			grafo2.seleccionarSiguienteUnidad();
			pgrafo1 = grafo1.getNextPregunta();
		} else
		
		// grafo == 1
		if(!second && r.getGrafo() == (grafo+1)%2) {
			grafo1.establecerRespuesta(r);
			grafo1.seleccionarSiguienteUnidad();
			pgrafo2 = grafo2.getNextPregunta();			
		} 
		
		/** @TODO PROBAR ESTO! (probar todo, en realidad) */
		if(grafo == 0) {
			if(pgrafo1 == null) {
				grafo = (grafo+1)%2;
				return pgrafo2;
			} else {
				return pgrafo1;
			}
		} else {
			if(pgrafo2 == null) {
				grafo = (grafo+1)%2;
				return pgrafo1;
			} else {
				return pgrafo2;
			}
		}	
	}
		
	protected void fillPregunta(Respuesta r) {
		preguntaActual = getNextPregunta(r);
		
		if(preguntaActual == null) {
			terminar();
			return;
		}
		
		nroPreguntaActualTextView.setText("Pregunta " + nroPreguntaActual);
		
		setImageFromFileName(preg, preguntaActual.getImgPath());
		List<Alternativa> alternativas = preguntaActual.getAlternativas();
		
		setImageFromFileName(alt1i, alternativas.get(0).getImgPath());
		setImageFromFileName(alt2i, alternativas.get(1).getImgPath());
		setImageFromFileName(alt3i, alternativas.get(2).getImgPath());
		
		if(alternativas.size() < 4) {
			alt4.setVisibility(View.GONE);
			alt4i.setVisibility(View.GONE);			
		} else {
			alt4.setVisibility(View.VISIBLE);
			alt4i.setVisibility(View.VISIBLE);
			
			setImageFromFileName(alt4i, alternativas.get(3).getImgPath());
		}
		
		Log.i(TAG, preguntaActual.toString());
	}
	
	@Override
	public void onBackPressed() {
		// no debe pasar nada hasta terminar el quiz
	}
	
	protected void terminar() {
		Intent i = new Intent(this, TerminoActivity.class);
		startActivity(i);
		this.finish();
	}
	
	protected void setImageFromFileName(ImageView imageView, String image_path) {
		try {
			BufferedInputStream bf = new BufferedInputStream(getAssets().open("images/" + image_path));
			Bitmap bmp = BitmapFactory.decodeStream(bf);			
			imageView.setImageBitmap(bmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public void onClick(View v) {
		long idMed = idMedicion;
		long idPreg = preguntaActual.getId();
		long idAlt = 0;
		int tipo = 0;
		int orden = nroPreguntaActual;
		long duracion = (long) (System.currentTimeMillis() - tiempoInicial)/1000;
		
		List<Alternativa> alts = preguntaActual.getAlternativas();
		Alternativa escogida = null;
		
		switch(v.getId()) {
		case R.id.alt1b:
			escogida = alts.get(0);
			break;
		case R.id.alt2b:
			escogida = alts.get(1);
			break;
		case R.id.alt3b:
			escogida = alts.get(2);
			break;
		case R.id.alt4b:
			escogida = alts.get(3);
			break;			
		}
		
		Log.i(TAG, "Escogida: " + escogida);
		idAlt = escogida.getId();
		tipo = escogida.getTipo();
		
		Respuesta r = new Respuesta(idMed, idPreg, idAlt, tipo, orden, duracion, grafo, preguntaActual.getUnidad(), escogida);
		
		
		try { 
			// A reference to the database can be obtained after initialization.
			myDb = myDbHelper.getWritableDatabase();
			/*
			 * Place code to use database here.
			 */
			long id = DbHandler.addRespuesta(myDb, r);
			Log.i(TAG, "Guardando respuesta id " + id  + ": " + r);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				myDbHelper.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				myDb.close();
			}
		}
		
		fillPregunta(r);
	}
}
