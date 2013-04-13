package es.viclopez.ipv4netcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import es.viclopez.IPClasses.IP;
import es.viclopez.IPClasses.Mask;
import es.viclopez.IPClasses.SubNet;

public class Main extends Activity {

	//Publicidad admob
	private AdView adView;
	private Button btnReset;
	private EditText ipA;
	private EditText ipB;
	private EditText ipC;	
	private EditText ipD;

	private EditText ipMask;
	private final TextWatcher ipTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {

			EditText textBox = (EditText) getCurrentFocus();

			if( s.length() > 0 ){

				if( getCurrentFocus().getId() == R.id.ipMask ){

					if( Integer.valueOf( textBox.getText().toString()) > 31 ){
						showErrorAlert( getString( R.string.errorHighCIDR ) ,
								getCurrentFocus().getId() );
					}else{
						nextFocus();
					}
				}else{

					if( Integer.valueOf( textBox.getText().toString()) > 255 ){
						textBox.setText( s.subSequence(1, 3));
						showErrorAlert( getString( R.string.errorIPpart ) ,
								getCurrentFocus().getId() );
					}

					if( Integer.valueOf( textBox.getText().toString() ) > 25 
							|| Integer.valueOf( textBox.getText().toString() ) == 0){
						nextFocus();
					}
				}
			}
			calculate();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			resetLabels();
		}
	};
	private LinearLayout lytMain;
	private TextView outAddress;
	private TextView outBroadcast;
	private TextView outHosts;

	private TextView outMask;
	private TextView outNetAddress;

	private TextView outWildCard; 

	private final OnClickListener resetButton = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ipA.requestFocus();
			ipA.setText("");
			ipB.setText("");
			ipC.setText("");
			ipD.setText("");
			ipMask.setText("");
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy(){
		if(adView != null)
			adView.destroy();
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch( item.getItemId() ){
		case R.id.about:
			Intent i = new Intent( this , About.class );
			startActivity(i);
			break;
		case R.id.settings:
			Intent i2 = new Intent( this , Preferences.class );
			startActivity(i2);
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false );

		ipA = (EditText) findViewById( R.id.ipA );
		ipB = (EditText) findViewById( R.id.ipB );
		ipC = (EditText) findViewById( R.id.ipC );
		ipD = (EditText) findViewById( R.id.ipD );
		ipMask = (EditText) findViewById( R.id.ipMask );

		ipA.addTextChangedListener(ipTextWatcher);
		ipB.addTextChangedListener(ipTextWatcher);
		ipC.addTextChangedListener(ipTextWatcher);
		ipD.addTextChangedListener(ipTextWatcher);
		ipMask.addTextChangedListener(ipTextWatcher);

		outAddress = (TextView) findViewById( R.id.outAddress );
		outMask = (TextView) findViewById( R.id.outMask );
		outWildCard = (TextView) findViewById( R.id.outWildCard );
		outNetAddress = (TextView) findViewById( R.id.outNetAddress );
		outBroadcast = (TextView) findViewById( R.id.outBroadcast );
		outHosts = (TextView) findViewById( R.id.outHosts );

		ipA.requestFocus();

		btnReset = (Button) findViewById( R.id.btnReset );
		btnReset.setOnClickListener(resetButton);

		//Código de publicidad admob
		lytMain = (LinearLayout) findViewById(R.id.lytMain);
		adView = new AdView(this, AdSize.BANNER, "a15164b9a92e6e0" );
		lytMain.addView(adView);
		adView.bringToFront();
		adView.loadAd(new AdRequest());


	}


	private final void calculate() {

		if( enableCalc() ){
			IP ip = new IP( Integer.valueOf( ipA.getText().toString() )
					, Integer.valueOf( ipB.getText().toString() )
					, Integer.valueOf( ipC.getText().toString() )
					, Integer.valueOf( ipD.getText().toString() ) );

			Mask mask = new Mask( Integer.valueOf( ipMask.getText().toString() ) );

			SubNet subNet = new SubNet( ip , mask );


			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

			String prefValue = sharedPreferences.getString( "base" , "-1" );


			int type = Integer.valueOf(prefValue);

			printData(ip, mask, subNet, type);
		}
	}

	private final boolean enableCalc() {
		return  ipA.getText().length() > 0
				&& Integer.valueOf( ipA.getText().toString() ) <= 255
				&& ipB.getText().length() > 0 
				&& Integer.valueOf( ipB.getText().toString() ) <= 255
				&& ipC.getText().length() > 0
				&& Integer.valueOf( ipC.getText().toString() ) <= 255
				&& ipD.getText().length() > 0
				&& Integer.valueOf( ipD.getText().toString() ) <= 255
				&& ipMask.getText().length() > 0
				&& Integer.valueOf( ipMask.getText().toString() ) <= 31;
	}

	private final void nextFocus() {
		switch( getCurrentFocus().getId() ){
		case R.id.ipA:
			ipB.requestFocus();
			break;
		case R.id.ipB:
			ipC.requestFocus();
			break;
		case R.id.ipC:
			ipD.requestFocus();
			break;
		case R.id.ipD:
			ipMask.requestFocus();
		case R.id.ipMask:
			btnReset.requestFocus();
			break;
		}
	}

	private final void printData(IP ip, Mask mask, SubNet subNet, int type) {

		outHosts.setText( Long.toString(subNet.getHosts() ) );
		switch( type ){

		case 0:
			outAddress.setText( ip.toString( IP.BASE.DECIMAL ) );
			outMask.setText( mask.toString(IP.BASE.DECIMAL) );
			outWildCard.setText( mask.getWildCard().toString(IP.BASE.DECIMAL) );
			outNetAddress.setText( subNet.getNetAddres().toString(IP.BASE.DECIMAL) );
			outBroadcast.setText( subNet.getBroadcastAddres().toString(IP.BASE.DECIMAL) );

			break;
		case 1:
			outAddress.setText( ip.toString( IP.BASE.BINARY ) );
			outMask.setText( mask.toString( IP.BASE.BINARY ) );
			outWildCard.setText( mask.getWildCard().toString( IP.BASE.BINARY ) );
			outNetAddress.setText( subNet.getNetAddres().toString( IP.BASE.BINARY ) );
			outBroadcast.setText( subNet.getBroadcastAddres().toString( IP.BASE.BINARY ) );
			break;
		case 2:
			outAddress.setText( ip.toString( IP.BASE.OCTAL ) );
			outMask.setText( mask.toString( IP.BASE.OCTAL ) );
			outWildCard.setText( mask.getWildCard().toString( IP.BASE.OCTAL ) );
			outNetAddress.setText( subNet.getNetAddres().toString( IP.BASE.OCTAL ) );
			outBroadcast.setText( subNet.getBroadcastAddres().toString( IP.BASE.OCTAL ) );
			break;
		case 3:
			outAddress.setText( ip.toString( IP.BASE.HEXADECIMAL ) );
			outMask.setText( mask.toString( IP.BASE.HEXADECIMAL ) );
			outWildCard.setText( mask.getWildCard().toString( IP.BASE.HEXADECIMAL ) );
			outNetAddress.setText( subNet.getNetAddres().toString( IP.BASE.HEXADECIMAL ) );
			outBroadcast.setText( subNet.getBroadcastAddres().toString( IP.BASE.HEXADECIMAL ) );
			break;

		}
	}

	private final void resetLabels(){
		outAddress.setText("");
		outBroadcast.setText("");
		outHosts.setText("");
		outMask.setText("");
		outNetAddress.setText("");
		outWildCard.setText("");
	}

	private final void showErrorAlert( String errorMessage , final int fielId ){
		new AlertDialog.Builder(this).setTitle(getString(R.string.error)).setMessage(errorMessage).setNeutralButton(getString(R.string.close),
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				findViewById( fielId ).requestFocus();
			}
		}).show();
	}

}
