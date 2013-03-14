package org.mem.medesc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.mem.medesc.data.DbHandler;
import org.mem.medesc.utils.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import au.com.bytecode.opencsv.CSVWriter;

public class TerminoActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termino);
		
		Button volver = (Button) findViewById(R.id.volverb);
		volver.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, InicioActivity.class);
		startActivity(i);
		this.finish();
	}
	
}
