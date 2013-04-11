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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
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
   
    /* si esta mostrando preguntas de CL */
    boolean comprensionLectora;
    
    long idMedicion;
    long tiempoInicial;
    
	/*
	 * las preguntas sacadas de la base de datos
	 * inmutable
	 */
	List<Pregunta> preguntas;
	List<Pregunta> preguntasCL;

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
    	comprensionLectora = false;
        
        totalUnidades = 16;
		/*
		 * obtener lista de preguntas
		 */
		try { 
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
            preguntas = DbHandler.getAllPreguntas(myDb);
            preguntasCL = DbHandler.getPreguntasCompLect(myDb);
            
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
		
		esconderSysUI();
		
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
        
//        nroPreguntaActualTextView = (TextView) findViewById(R.id.question_number);
                
        init();
        
        grafo1 = new CuestionarioController(0, unidades, unidadesClon, 4);
        grafo2 = new CuestionarioController(1, unidades, unidadesClon, 11);
        
        fillPregunta(null);
	}


	boolean first = true;
	boolean second = true;
	int grafo = 1;
	
	/* debug, para ver todas las preguntas */
	protected Pregunta getNextPregunta2(Respuesta r) {
		
		return preguntas.remove(0);
	}
	
	protected Pregunta getNextPregunta(Respuesta r) {
		nroPreguntaActual++;
		grafo = (grafo + 1) % 2;
//		Pregunta pgrafo1 = null, pgrafo2 = null; 
		
		if(r != null) {
			if(r.getGrafo() == 0) {
				grafo1.establecerRespuesta(r);
				grafo1.seleccionarSiguienteUnidad();
			} else {
				grafo2.establecerRespuesta(r);
				grafo2.seleccionarSiguienteUnidad();
			}
		}
		
		
		Pregunta siguiente;
		if(grafo == 0) {
			siguiente = grafo1.getNextPregunta();
		} else {
			siguiente = grafo2.getNextPregunta();
		}
		
		if(grafo == 0 && siguiente == null) {
			grafo = 1;
			siguiente = grafo2.getNextPregunta();
		} else if(grafo == 1 && siguiente == null) {
			grafo = 0;
			siguiente = grafo1.getNextPregunta();
		}
		
		return siguiente;		
	}
	
	/* requiere api 11 */ 
	protected void esconderSysUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}

	
	
	protected void fillPregunta(Respuesta r) {
		esconderSysUI();
		
		if(comprensionLectora) {
			if(preguntasCL.size() > 0) {
				nroPreguntaActual++;
				preguntaActual = preguntasCL.remove(0);
			} else {
				preguntaActual = null;
			}
		} else {
			preguntaActual = getNextPregunta(r);
		}
		
		if(comprensionLectora && preguntaActual == null) {
			terminar();
			return;
		}
		
		if(preguntaActual == null) {
			activarComprensionLectora();
			return;
		}
		
				
//		nroPreguntaActualTextView.setText("Pregunta " + nroPreguntaActual);
		
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
		
		alt1.setEnabled(true);
		alt2.setEnabled(true);
		alt3.setEnabled(true);
		alt4.setEnabled(true);
		
		Log.i(TAG, preguntaActual.toString());
	}
	
	protected void activarComprensionLectora() {
		ImageView lectura = (ImageView) findViewById(R.id.lectura_content);
		lectura.setVisibility(View.VISIBLE);
		setImageFromFileName(lectura, "U17/0.PNG");		
		
		/* redimensionar imagen de la pregunta, para que quepa todo en la pantalla */
		LayoutParams paramsPreg = (LayoutParams) preg.getLayoutParams();
		paramsPreg.height = 50;
		// existing height is ok as is, no need to edit it
		preg.setLayoutParams(paramsPreg); 
		
		/* achicar alternativas, para que quepa todo... */
		LayoutParams alts = (LayoutParams) alt1i.getLayoutParams();
		alts.height = 90;
		// existing height is ok as is, no need to edit it
		alt1.setLayoutParams(alts);
		alt1i.setLayoutParams(alts);
		alt2.setLayoutParams(alts);
		alt2i.setLayoutParams(alts);
		alt3.setLayoutParams(alts);
		alt3i.setLayoutParams(alts);
		
		comprensionLectora = true;
		fillPregunta(null); // goto ;D
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
		alt1.setEnabled(false);
		alt2.setEnabled(false);
		alt3.setEnabled(false);
		alt4.setEnabled(false);
		
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
	
	
	
	class DesparecerStatusBar extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while(true) {
				// REQ ANDROID API 11
				CuestionarioActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			
			return null;
		}
	}
	
}
