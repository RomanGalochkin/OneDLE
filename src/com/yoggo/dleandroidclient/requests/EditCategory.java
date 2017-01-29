package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCategoryJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class EditCategory {
	private String endpoint;
	private String token;
	private String catName;
	private String catAltName;
	private String catId;
	private String parentId = "0";
	private String action = "editcat";
	private Callback<AddCategoryJson> callback;
	
	public EditCategory(String endpoint, String token, String catName, String catAltName, String catId,
			Callback<AddCategoryJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catName = catName;
		this.catAltName = catAltName;
		doInBackground();
	}
	
	public EditCategory(String endpoint, String token, String catName, String catAltName, String catId, String parentId,
			Callback<AddCategoryJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catName = catName;
		this.catAltName = catAltName;
		this.parentId = parentId;
		this.catId = catId;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.editCategory(token, action, parentId, catId, catName, catAltName,  callback);
	}
}
