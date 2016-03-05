package org.kamusi.kamusifidgetapp;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	final String tokenURL = "http://dev.kamusi.leaves.telamenta.com/services/session/token";
	final String registerURL = "http://kamusi.org/user/register";
	final String loginURL = "http://dev.kamusi.leaves.telamenta.com/api/user/login";
			
	
	Button registerButton;
	Button loginButton;
	String CSRFtoken = "";	
	EditText usernameTextBox;
	EditText passwordTextBox;
	TextView error;
	
	String loginAnswer = "";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 Globals g = Globals.getInstance();
		 if(!g.getLogin()){
		setContentView(R.layout.activity_main);
		error = (TextView)findViewById(R.id.textView1);
		error.setVisibility(View.GONE);
		loginButtons();
		 } else {
			 setContentView(R.layout.logged);			 
		 }		
		getCSRFToken();
	}
	
	private Handler loginHandler = new Handler()
	{
	    @Override
	    public void handleMessage(Message msg)
	    {
	        if (msg.what == 1)
	        {
	            login();
	        }
	    }
	};
	
//	public void widgetButtons(){
//		ImageView profile = (ImageView) findViewById(R.id.profile);	
//		profile.setOnClickListener(new OnClickListener() {
//			
//			@Override
//            public void onClick(View v) {            
//				 Intent uiIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//				 uiIntent.setFlags(1);
//				 MainActivity.this.sendBroadcast(uiIntent);		        	
//            }
//        });
//		
//	}
	
	public void loginButtons(){		
		
		registerButton = (Button) findViewById(R.id.button2);
		loginButton = (Button) findViewById(R.id.button1);
		usernameTextBox = (EditText) findViewById(R.id.editText1);
		passwordTextBox = (EditText) findViewById(R.id.editText2);		
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
            public void onClick(View v) {                            
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(registerURL));
                startActivity(browserIntent);            	
            }
        });
		
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
            public void onClick(View v) {										        	 
				
				if(usernameTextBox.getText().length() == 0 || passwordTextBox.getText().length() == 0){
				 error.setVisibility(View.VISIBLE);
				 error.setText("username or password missing");
				} else {
				
					// REMOTE LOGIN
					postHTTP(usernameTextBox.getText().toString(),passwordTextBox.getText().toString());					
				
					
					/* REDIRECT TO ADD WIDGET
					Intent widgetIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
					Context context = getApplicationContext();
					ComponentName name = new ComponentName(context, KamusiWidgetProvider.class);
					int [] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
					widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids);
					startActivityForResult(widgetIntent,0);*/
				}
			}
        });
	}
	
	private void login(){
		if(loginAnswer.equals("")){
			 error.setVisibility(View.VISIBLE);
			 error.setText("Connection Problem");
		}
		
		 // LAYOUT ACTION ON ANSWER
		else if(loginAnswer.contains("blocked")){
			 error.setVisibility(View.VISIBLE);
			 error.setText("This user is blocked");
		 }
		 else if(loginAnswer.contains("Wrong")){
			 error.setVisibility(View.VISIBLE);
			 error.setText("Wrong username or password");
		 } else if(loginAnswer.contains("session_name")){						 
			 Globals g = Globals.getInstance();
			 g.setLogin(true);
			 g.setUsername(usernameTextBox.getText().toString());
			 Intent uiIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);										   										    				
			 MainActivity.this.sendBroadcast(uiIntent);				
			// CHANGE THIS LAYOUT
			setContentView(R.layout.logged);			
			TextView loginmessage = (TextView)findViewById(R.id.textView2);
			loginmessage.setText("Logged in as " + g.getUsername());

			// TODO: ACTIVATE LOGOUT OPTION
			// TODO: PARSE ANSWER
			// TODO: RETRIEVE USER DATA
			// TODO: Locally SAVE LOGIN DATA
			
			
			// TODO: REQUEST LIST OF WORDS
			// TODO: Locally SAVE LIST OF WORDS
		 }	
	}
	
	private void getCSRFToken(){		
		new Thread(new Runnable() {
	        public void run() {		
		try {		
			HttpClient httpclient = new DefaultHttpClient();			
		    HttpResponse response = httpclient.execute(new HttpGet(tokenURL));
		    //content = response.getEntity().getContent();
		    String token = EntityUtils.toString(response.getEntity());
		    CSRFtoken = token.substring(3);
		    Log.d("CSRF",CSRFtoken);
		  } catch (Exception e) {
		    Log.e("[CSRF GET REQUEST]", "Network exception", e);
		  }
	        }
	    }).start();
	}
	
	private void postHTTP(final String username, final String password){		
		 new Thread(new Runnable() {
		        public void run() {	
		try {	
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(loginURL);	
		JSONObject login = writeJSON(username,password);
		StringEntity se = new StringEntity(login.toString());
		httppost.setEntity(se);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");	
		httppost.setHeader("X-CSRF-Token", CSRFtoken);			
		HttpResponse response  = httpclient.execute(httppost);
		String answer = EntityUtils.toString(response.getEntity()); 
		loginAnswer = answer;
		Log.d("LOGIN",loginAnswer);
		
		// Because we're in a different thread we need to send a message to Main Activity
		// in order to resume the login procedure.
		Message theMessage = loginHandler.obtainMessage(1); // get a basic message of type 1
		// TODO: SEND loginAnswer as a theMessage argument
		loginHandler.sendMessage(theMessage);// send the message to the main activity handler.
		
		} catch (Exception e) {
			loginAnswer = "";
		    Log.e("[LOGIN POST REQUEST]", "Network exception: " + e.toString(), e);		   
		  }
		        }
		    }).start();		
	}
	
	public JSONObject writeJSON(String username, String password) {
		  JSONObject object = new JSONObject();
		  try {
			object.put("password", password);
		    object.put("username", username);		   
		  } catch (JSONException e) {
		    e.printStackTrace();
		  }
		  return object;
		} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
