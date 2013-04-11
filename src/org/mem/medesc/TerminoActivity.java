package org.mem.medesc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TerminoActivity extends Activity {

	
	/* requiere api 11 */ 
	protected void esconderSysUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termino);
		getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		
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
