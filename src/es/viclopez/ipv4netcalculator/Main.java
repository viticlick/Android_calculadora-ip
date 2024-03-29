package es.viclopez.ipv4netcalculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import es.viclopez.IPClasses.IP;
import es.viclopez.IPClasses.IP.Base;
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
	
	private Spinner spinnerBase;
	
	private int typeBase;
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
			}else{
				resetLabels();
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

		outAddress.setOnClickListener( copyToClipboard );
		outMask.setOnClickListener( copyToClipboard );
		outWildCard.setOnClickListener(copyToClipboard);
		outNetAddress.setOnClickListener(copyToClipboard);
		outBroadcast.setOnClickListener(copyToClipboard);
		outHosts.setOnClickListener(copyToClipboard);
		
		ipA.requestFocus();
		
		spinnerBase = (Spinner) findViewById( R.id.spinnerBase);
		//Crea un adaptador para rellenar el array
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.bases, 
				android.R.layout.simple_spinner_item);
		//Indica el layout para utilizar cuando aparece la lista
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Aplica el adaptador del spinner
		spinnerBase.setAdapter(adapter);
		spinnerBase.setOnItemSelectedListener(spinnerListener);
		spinnerBase.setSelection( getValueTypeFromPreferences() );
		
		btnReset = (Button) findViewById( R.id.btnReset );
		btnReset.setOnClickListener(resetButton);

		//Código de publicidad admob
		lytMain = (LinearLayout) findViewById(R.id.lytMain);
		adView = new AdView(this, AdSize.BANNER, "a15164b9a92e6e0" );
		lytMain.addView(adView);
		adView.bringToFront();
		adView.loadAd(new AdRequest());


	}

	private OnClickListener copyToClipboard = new OnClickListener() {
		
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onClick(View v) {
			TextView tv = (TextView) v;
			String copyString = tv.getText().toString();
			if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {

//				ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
//				clipboard.setText( copyString ) ;
				
			}else{
				android.content.ClipboardManager clipboard = 
						(android.content.ClipboardManager) getSystemService( 
								getApplicationContext().CLIPBOARD_SERVICE );
				
			    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", copyString );
			    clipboard.setPrimaryClip(clip);
				Toast.makeText( getApplicationContext(), getString( R.string.copy) +"\n" + copyString , Toast.LENGTH_SHORT ).show();
			}
		}
	}; 
	
	private final void calculate() {

		if( enableCalc() ){
			IP ip = new IP( Integer.valueOf( ipA.getText().toString() )
					, Integer.valueOf( ipB.getText().toString() )
					, Integer.valueOf( ipC.getText().toString() )
					, Integer.valueOf( ipD.getText().toString() ) );

			Mask mask = new Mask( Integer.valueOf( ipMask.getText().toString() ) );

			SubNet subNet = new SubNet( ip , mask );


//			int type = getValueTypeFromPreferences();

			printData(ip, mask, subNet, typeBase);
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

		IP.Base base;
		switch( type ){
		case 1:
			base = Base.BINARY;
			break;
		case 2:
			base = Base.OCTAL;
			break;
		case 3:
			base = Base.HEXADECIMAL;
			break;
		default:
			base = Base.DECIMAL;
		}
		outAddress.setText( ip.toString( base ) );
		outMask.setText( mask.toString( base) );
		outWildCard.setText( mask.getWildCard().toString( base ) );
		outNetAddress.setText( subNet.getNetAddres().toString( base ) );
		outBroadcast.setText( subNet.getBroadcastAddres().toString( base ) );

	}

	private final void resetLabels(){
		outAddress.setText("");
		outBroadcast.setText("");
		outHosts.setText("");
		outMask.setText("");
		outNetAddress.setText("");
		outWildCard.setText("");
	}

	@Override
	protected void onResume() {
		calculate();
		spinnerBase.setSelection( getValueTypeFromPreferences() );
		super.onResume();
	}
	
	private final void showErrorAlert( String errorMessage , final int fielId ){
		new AlertDialog.Builder(this).setTitle(
				getString(R.string.error)).setMessage(errorMessage).setNeutralButton(
						getString(R.string.close),
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				findViewById( fielId ).requestFocus();
			}
		}).show();
	}

	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int posicion,
				long id) {
			typeBase = posicion;
			calculate();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private int getValueTypeFromPreferences(){
		
		SharedPreferences sharedPreferences = 
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		String prefValue = sharedPreferences.getString( "base" , "-1" );

		return  Integer.valueOf(prefValue);
	}

}
