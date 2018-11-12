package com.example.antonis.rgbstripcontrol;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

	Button sendRequestButton;
	ColorPickerView colorPicker;
	int[] rgb;
	String baseUrl = "http://192.168.1.22:80/";

	private void showSettingsDialog(SettingsFragment.OnSubmitSettingsListener onSubmitSettingsListener) {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction.  We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		SettingsFragment newFragment = new SettingsFragment();
		newFragment.setDefaultIpAddress(baseUrl);
		newFragment.setOnSubmitSettingsListener(onSubmitSettingsListener);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar myToolbar = findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		colorPicker = findViewById(R.id.view_color_picker);
		rgb = new int[3];

		final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


		colorPicker.setColorListener(new ColorListener() {
			@Override
			public void onColorSelected(ColorEnvelope colorEnvelope) {
				if (!Arrays.equals(rgb, colorEnvelope.getColorRGB())) {
					rgb = colorEnvelope.getColorRGB();

					StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + rgb[0] + "&" + rgb[1] + "&" + rgb[2],
							new Response.Listener<String>() {
								@Override
								public void onResponse(String response) {
									// Display the first 500 characters of the response string.
									Log.i("POST Request", "Got response");
								}
							},
							new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Log.i("POST Request", "Error: " + error);
								}
							});
					queue.add(stringRequest);
				}
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		colorPicker.saveData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings)
			showSettingsDialog(new SettingsFragment.OnSubmitSettingsListener() {
				@Override
				public void OnSubmitSettings(String ipAddress) {
					baseUrl = ipAddress;
				}
			});

		return super.onOptionsItemSelected(item);
	}
}
