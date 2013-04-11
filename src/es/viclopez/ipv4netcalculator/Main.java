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

	private Button btnCalculateNetwork;
	private Button btnReset;
	private EditText ipA;
	private EditText ipB;
	private EditText ipC;
	private EditText ipD;	
	private EditText ipMask;

	private TextView outMask;
	private TextView outAddress;
	private TextView outWildCard;
	private TextView outNetAddress;
	private TextView outBroadcast;
	private TextView outHosts;
	
	//Publicidad admob
	private AdView adView;
	private LinearLayout lytMain;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
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

		btnCalculateNetwork = (Button) findViewById( R.id.btCalculateNetwork );
		btnCalculateNetwork.setEnabled(false);
		btnCalculateNetwork.setOnClickListener( calculateButton );
		
		btnReset = (Button) findViewById( R.id.btnReset );
		btnReset.setOnClickListener(resetButton);

		//Código de publicidad admob
		lytMain = (LinearLayout) findViewById(R.id.lytMain);
		adView = new AdView(this, AdSize.BANNER, "a15164b9a92e6e0" );
		lytMain.addView(adView);
		adView.bringToFront();
		adView.loadAd(new AdRequest());


	}

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
	
	
	private final OnClickListener calculateButton = new OnClickListener() {

		@Override
		public void onClick(View v) {

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

		private final void printData(IP ip, Mask mask, SubNet subNet, int type) {

			outHosts.setText( Long.toString(subNet.getHosts() ) );
			switch( type ){

			case 0:
				outAddress.setText( ip.toDecimalString() );
				outMask.setText( mask.toDecimalString() );
				outWildCard.setText( mask.getWildCard().toDecimalString() );
				outNetAddress.setText( subNet.getNetAddres().toDecimalString() );
				outBroadcast.setText( subNet.getBroadcastAddres().toDecimalString() );

				break;
			case 1:
				outAddress.setText( ip.toBinaryString() );
				outMask.setText( mask.toBinaryString() );
				outWildCard.setText( mask.getWildCard().toBinaryString() );
				outNetAddress.setText( subNet.getNetAddres().toBinaryString() );
				outBroadcast.setText( subNet.getBroadcastAddres().toBinaryString() );
				break;
			case 2:
				outAddress.setText( ip.toOctalString() );
				outMask.setText( mask.toOctalString() );
				outWildCard.setText( mask.getWildCard().toOctalString() );
				outNetAddress.setText( subNet.getNetAddres().toOctalString() );
				outBroadcast.setText( subNet.getBroadcastAddres().toOctalString() );
				break;
			case 3:
				outAddress.setText( ip.toHexString() );
				outMask.setText( mask.toHexString() );
				outWildCard.setText( mask.getWildCard().toHexString() );
				outNetAddress.setText( subNet.getNetAddres().toHexString() );
				outBroadcast.setText( subNet.getBroadcastAddres().toHexString() );
				break;

			}
		}
	};

	private final TextWatcher ipTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			resetLabels();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

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
			enableButton();
		}
	};

	private final void enableButton() {
		btnCalculateNetwork.setEnabled( ipA.getText().length() > 0
				&& Integer.valueOf( ipA.getText().toString() ) <= 255
				&& ipB.getText().length() > 0 
				&& Integer.valueOf( ipB.getText().toString() ) <= 255
				&& ipC.getText().length() > 0
				&& Integer.valueOf( ipC.getText().toString() ) <= 255
				&& ipD.getText().length() > 0
				&& Integer.valueOf( ipD.getText().toString() ) <= 255
				&& ipMask.getText().length() > 0
				&& Integer.valueOf( ipMask.getText().toString() ) <= 31);
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
			btnCalculateNetwork.requestFocus();
			break;
		}
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
	
	private final void resetLabels(){
		outAddress.setText("");
		outBroadcast.setText("");
		outHosts.setText("");
		outMask.setText("");
		outNetAddress.setText("");
		outWildCard.setText("");
	}
	
	@Override
	public void onDestroy(){
	      if(adView != null)
	         adView.destroy();
	      super.onDestroy();
	}

}
