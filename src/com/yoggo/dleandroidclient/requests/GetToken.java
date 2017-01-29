package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.yoggo.dleandroidclient.json.TokenJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetToken{

	private String login;
	private String password;
	private String androidPassw;
	private String endpoint;
	private Callback<TokenJson> callback;
	
	public GetToken(String login, String password, String androidPassw, String endpoint, Callback<TokenJson> callback) {
		this.login = login;
		this.password = password;
		this.androidPassw = androidPassw;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}

	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getToken(login, password, androidPassw, callback);
		
		
	}

	
}
