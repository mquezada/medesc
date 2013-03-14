package org.mem.medesc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

import org.mem.medesc.beans.Alternativa;
import org.mem.medesc.beans.Pregunta;
import org.mem.medesc.beans.Respuesta;
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
	Button alt1, alt2, alt3, alt4;
	DatabaseHelper myDbHelper;
    SQLiteDatabase myDb = null;
    List<Pregunta> preguntas;
    ImageView preg, alt1i, alt2i, alt3i, alt4i;
    
    TextView nroPreguntaActualTextView;
    
    Pregunta preguntaActual;
    int nroPreguntaActual;
    
    long idMedicion;
    long tiempoInicial;
    
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
        nroPreguntaActual = 0;
        
        try { 
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
            /*
             * Place code to use database here.
             */
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
		
        tiempoInicial = System.currentTimeMillis();
        fillPregunta();
	}
	
	protected Pregunta getNextPregunta() {
		
		if(preguntas.size() > 0) {			
			nroPreguntaActual++;
			return preguntas.remove(0); 
		} else {
			return null;
		}
		
	}
	
	protected void fillPregunta() {
		preguntaActual = getNextPregunta();
		
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
		
		Respuesta r = new Respuesta(idMed, idPreg, idAlt, tipo, orden, duracion);
		
		
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
		
		fillPregunta();
	}
}
