package org.mem.medesc;

import java.io.BufferedInputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class InicioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        setImageFromFileName(R.id.question_content, "images/test_q2.PNG");
        setImageFromFileName(R.id.alt1, "images/A.PNG");
        setImageFromFileName(R.id.alt2, "images/B.PNG");
        setImageFromFileName(R.id.alt3, "images/C.PNG");        
        setImageFromFileName(R.id.alt4, "images/D.PNG");
    }

    protected void setImageFromFileName(int imageViewId, String image_path) {
    	try {
			BufferedInputStream bf = new BufferedInputStream(getAssets().open(image_path));
			Bitmap bmp = BitmapFactory.decodeStream(bf);
			ImageView question = (ImageView) findViewById(imageViewId);
			question.setImageBitmap(bmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }
    
}
