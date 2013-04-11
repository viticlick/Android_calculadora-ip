package es.viclopez.ipv4netcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class About extends Activity {
    /** Called when the activity is first created. */

	private Button btnExit;
	
    @Override public void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        
        btnExit = (Button) findViewById(R.id.exit);
        btnExit.setOnClickListener( new OnClickListener( ) {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
    }
}
