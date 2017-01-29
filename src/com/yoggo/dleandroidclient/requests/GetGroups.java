package com.yoggo.dleandroidclient.requests;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.CategoryJson;
import com.yoggo.dleandroidclient.json.GroupContainerJson;
import com.yoggo.dleandroidclient.json.GroupJson;
import com.yoggo.dleandroidclient.json.GroupMainContainer;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetGroups {
	private String endpoint;
	private String token;
	private String action = "getgroups";
	private Callback<List<GroupJson>> callback;
	
	public GetGroups(String endpoint, String token, Callback<List<GroupJson>>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getGroups(token, action,
				callback);
	}
}
