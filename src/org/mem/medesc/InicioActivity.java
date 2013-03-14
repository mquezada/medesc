package org.mem.medesc;

import org.mem.medesc.data.DbHandler;
import org.mem.medesc.utils.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class InicioActivity extends Activity implements OnClickListener {

	AutoCompleteTextView nombre_alumno;
	static public final String TAG = "InicioActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        
        Button comenzar = (Button) findViewById(R.id.comenzarb);
        comenzar.setOnClickListener(this);
        
        Button admin = (Button) findViewById(R.id.adminb);
        admin.setOnClickListener(this);
        
        nombre_alumno = (AutoCompleteTextView) findViewById(R.id.nombre_et);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.comenzarb:
			String nombreAlumno = nombre_alumno.getText().toString();
			
			if(nombreAlumno.trim().equals("")) {
				Toast.makeText(this, "Debe ingresar el nombre del alumno.", Toast.LENGTH_LONG).show();
			} else {
			
				DatabaseHelper myDbHelper;
		        SQLiteDatabase myDb = null;
	
		        myDbHelper = new DatabaseHelper(this);
		        /*
		         * Database must be initialized before it can be used. This will ensure
		         * that the database exists and is the current version.
		         */
		         myDbHelper.initializeDataBase();
	
		         try { 
		            // A reference to the database can be obtained after initialization.
		            myDb = myDbHelper.getWritableDatabase();
		            /*
		             * Place code to use database here.
		             */
		            
		            long idMedicion = DbHandler.addMedicion(myDb, nombreAlumno);
		            Intent i = new Intent(this, CuestionarioActivity.class);
		            i.putExtra("medicion_id", idMedicion);
		            Log.i(TAG, "Guardando medicion id " + idMedicion + ". Nombre: " + nombreAlumno);
		            startActivity(i);
		            this.finish();
		            
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
		         
			}
			
			break;
		case R.id.adminb:
			Intent i = new Intent(this, AdminActivity.class);
			startActivity(i);
			break;
		}
		
	}
    
    
}
