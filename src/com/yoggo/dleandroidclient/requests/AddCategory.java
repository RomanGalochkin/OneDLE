package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCategoryJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class AddCategory {
	private String endpoint;
	private String token;
	private String catName;
	private String catAltName;
	private String parentId = "0";
	private String action = "addcat";
	private Callback<AddCategoryJson> callback;
	
	public AddCategory(String endpoint, String token, String catName, String catAltName,
			Callback<AddCategoryJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catName = catName;
		this.catAltName = catAltName;
		doInBackground();
	}
	
	public AddCategory(String endpoint, String token, String catName, String catAltName, String parentId,
			Callback<AddCategoryJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catName = catName;
		this.catAltName = catAltName;
		this.parentId = parentId;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.addCategory(token, action, parentId, catName, catAltName,  callback);
	}
}
