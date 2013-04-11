package org.mem.medesc;

import org.mem.medesc.data.DbHandler;
import org.mem.medesc.utils.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InicioActivity extends Activity implements OnClickListener {

	AutoCompleteTextView nombre_alumno;
	static public final String TAG = "InicioActivity";
	
	/* requiere api 11 */ 
	protected void esconderSysUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        
        esconderSysUI();
        
        Button comenzar = (Button) findViewById(R.id.comenzarb);
        comenzar.setOnClickListener(this);
        
        Button admin = (Button) findViewById(R.id.adminb);
        admin.setOnClickListener(this);
        
        nombre_alumno = (AutoCompleteTextView) findViewById(R.id.nombre_et);
    }

	@Override
	public void onClick(View v) {
		esconderSysUI();
		
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
			final AlertDialog.Builder editalert = new AlertDialog.Builder(this);
	        editalert.setTitle("Ingresar Contraseña");
//	        editalert.setMessage("");
	        
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        final String password = prefs.getString("password", "");
//	        System.out.println(password);
	        
	        final EditText input = new EditText(this);
	        input.setSingleLine();
	        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
	        	        
	        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	                LinearLayout.LayoutParams.FILL_PARENT,
	                LinearLayout.LayoutParams.FILL_PARENT);
	        input.setLayoutParams(lp);
	        editalert.setView(input);

	        editalert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	if(input.getText().toString().equals(password)) {	            	
	            		Intent i = new Intent(InicioActivity.this, AdminActivity.class);
	            		startActivity(i);
	            	} else {
	            		Toast.makeText(InicioActivity.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
	            	}
	            }
	        });
	        
	        editalert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();					
				}
			});

	        editalert.show();
	        
			
		}
		
	}
    
    
}
