package org.mem.medesc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.mem.medesc.data.DbHandler;
import org.mem.medesc.utils.DatabaseHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class AdminActivity extends PreferenceActivity {
	
	
	class ExportTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog;		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(AdminActivity.this, "", 
		            "Exportando csv...", true);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			 DatabaseHelper myDbHelper;
             SQLiteDatabase myDb = null;

             myDbHelper = new DatabaseHelper(AdminActivity.this);
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
                 
                 List<String[]> resultados = DbHandler.getResultados(myDb);
                 
                 CSVWriter writer = null;
                 try {
                     writer = new CSVWriter(new FileWriter("/sdcard/resultados.csv"), ',');
                     writer.writeAll(resultados);
                     writer.close();
                 } 
                 catch (IOException e) {
                     e.printStackTrace();
                 }
                 
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
             return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			Toast.makeText(AdminActivity.this, "Archivo CSV guardado en tarjeta de memoria", Toast.LENGTH_LONG).show();
		}
	}
	
	protected void sendFileAsAttachment() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    emailIntent.setType("text/csv");
//	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"me@gmail.com"}); 
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Resultados medición"); 
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Se adjunta archivo csv");
	    
	    Log.v(getClass().getSimpleName(), "sPhotoUri=" + Uri.parse("file://"+ "/sdcard/resultados.csv"));
	    
	    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ "/sdcard/resultados.csv"));
	    startActivity(Intent.createChooser(emailIntent, "Compartir resultados..."));
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.admin);
        
        Preference myPref = (Preference) findPreference("export");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	         public boolean onPreferenceClick(Preference preference) {
	        	 new ExportTask().execute();	        	
	        	 return true;
	         }
	     });
        
        myPref = (Preference) findPreference("share");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	         public boolean onPreferenceClick(Preference preference) {
	        	 new ExportTask().execute();
	        	 sendFileAsAttachment();
	        	 return true;
	         }
	     });
        
    }
}
