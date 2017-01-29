package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCategoryJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class DeleteCategory {
	private String endpoint;
	private String token;
	private String catId;
	private String action = "delcat";
	private Callback<AddCategoryJson> callback;
	
	public DeleteCategory(String endpoint, String token, String catId,
			Callback<AddCategoryJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catId = catId;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.deleteCategory(token, action, catId,  callback);
	}
}
