package com.yoggo.dleandroidclient.requests;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetNews {

	private String endpoint;
	private String token;
	private String action = "getnews";
	private String columnOrder = "id";
	private String order = "DESC";
	private String start = "0";
	private String limit = "10";
	private String columns = "news_id,title,short_story,date,autor,comm_num,category";
	private Callback<List<NewsJson>> callback;
	
	
	public GetNews(String endpoint, String token, Callback<List<NewsJson>> callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}
	
	public GetNews(String endpoint, String token, String start, Callback<List<NewsJson>> callback) {
		this.token = token;
		this.start = start;
		this.callback = callback;
		this.endpoint = endpoint;
		doInBackground();
	}

	public void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getNews(token, action,
				columnOrder,
				order,
				start,
				limit,
				columns,
				callback);
	}

	
}
