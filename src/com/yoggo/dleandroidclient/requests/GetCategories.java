package com.yoggo.dleandroidclient.requests;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.CategoryContainerJson;
import com.yoggo.dleandroidclient.json.CategoryJson;
import com.yoggo.dleandroidclient.json.CommentJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetCategories {
	
	private String endpoint;
	private String token;
	private String action = "getcats";
	private String columns = "id,text,autor,date";
	private Callback<List<CategoryJson>> callback;
	
	public GetCategories(String endpoint, String token, Callback<List<CategoryJson>> callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getCats(token, action,
				callback);
	}
}
