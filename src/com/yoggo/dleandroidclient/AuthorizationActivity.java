package com.yoggo.dleandroidclient;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.utils.Idn;
import org.apache.http.client.utils.Punycode;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.json.TokenJson;
import com.yoggo.dleandroidclient.requests.GetToken;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class AuthorizationActivity extends Activity {
	
	private Button logInButton;
	private EditText loginEditText;
	private EditText passwordEditText;
	private EditText siteEditText;
	private AccountSettingsManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		accountManager = new AccountSettingsManager(getApplicationContext());
		if(accountManager.getToken() != null && !accountManager.getToken().equals("")){
			Intent intent = new Intent(AuthorizationActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		
		setContentView(R.layout.activity_authorization);

		logInButton = (Button) findViewById(R.id.authorization_log_in_button);
		loginEditText = (EditText) findViewById(R.id.authorization_login);
		passwordEditText = (EditText) findViewById(R.id.authorization_password);
		siteEditText = (EditText) findViewById(R.id.authorization_site_address);
		logInButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				   //получаем токен
				   new GetToken(loginEditText.getText().toString(), 
						passwordEditText.getText().toString(), OneDLEApi.mobileKey ,getLang(siteEditText.getText().toString()), new Callback<TokenJson>(){

					@Override
					public void failure(RetrofitError arg0) {
						Toast.makeText(getApplicationContext(), "Ошибка авторизации", 200).show();
					}

					@Override
					public void success(TokenJson arg0, Response arg1) {
						if(arg0 != null && arg0.token != null && !arg0.token.equals("")){
							Toast.makeText(getApplicationContext(), "Авторизация прошла успешно", 200).show();
							//устанавливаем токен в аккаунт
							accountManager.setToken(arg0.token);
							//показываем главное меню
							Intent intent = new Intent(getApplicationContext(), MainActivity.class);
							accountManager.setName(loginEditText.getText().toString());
							accountManager.setSite(getLang(siteEditText.getText().toString()));
							startActivity(intent);
							finish();
						}else{
							Toast.makeText(getApplicationContext(), "Ошибка авторизации", 200).show();
						}	
					}
				});
			}
		});
		
	}
	
	
	@SuppressLint("NewApi")
	public static String utfToASCII(String stringUrl) {
		IDN.toASCII(stringUrl);
	    Log.d("punycode", IDN.toASCII(stringUrl));
	    return IDN.toASCII(stringUrl);
	}
	
	private String formatSiteAddress(String address){
		String http = "http://";
		if(address.contains(http)){
			return address;
		}else{
			Log.d("debug", http + address);
			return http + address;
		}
	}
	
	/*
	 * Проверяем язык
	 * */
	private String getLang(String string) {
		if (string.matches("[a-zA-Z.\\-_\\s]+")) {
			accountManager.setAccountLang("EN");
			return formatSiteAddress(siteEditText.getText().toString());
		} else if (string.matches("[а-яА-Я.\\-_\\s]+")) {
			accountManager.setAccountLang("RU");
			accountManager.setAccountSiteRu(string);
			return formatSiteAddress(utfToASCII(siteEditText.getText().toString()));
		} else {
			return formatSiteAddress(siteEditText.getText().toString());
		}
	}
}
