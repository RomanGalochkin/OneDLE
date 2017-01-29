package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class EditNews {
	private String endpoint;
	private String token;
	private String id;
	private String title;
	private String catList;
	private String shortNews;
	private String fullNews;
	private String action = "editnews";
	private Callback<AddNewsJson> callback;
	
	public EditNews(String endpoint, String token, String id, String title, String shortStory, String fullStory,
			String catList, Callback<AddNewsJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.catList = catList;
		this.shortNews = shortStory;
		this.fullNews = fullStory;
		this.title = title;
		this.id = id;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.editNews(token, action, id,  shortNews, fullNews, catList, title, callback);
	}
}
