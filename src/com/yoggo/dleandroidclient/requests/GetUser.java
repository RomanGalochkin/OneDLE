package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.GroupContainerJson;
import com.yoggo.dleandroidclient.json.UserJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetUser {
	private String endpoint;
	private String token;
	private String action = "getuser";
	private String userId = "me";
	private String columns = "user_group,user_id";
	private Callback<UserJson> callback;
	
	public GetUser(String endpoint, String token, Callback<UserJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getUser(token, action, columns, userId,
				callback);
	}
}
