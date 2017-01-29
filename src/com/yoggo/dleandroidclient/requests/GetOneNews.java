package com.yoggo.dleandroidclient.requests;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import android.util.Log;

import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetOneNews {
	private String endpoint;
	private String token;    
	private String action = "getonenews";
	private String newsId;
	private String columns = "news_id,title,short_story,full_story,rating,news_read,date,autor,comm_num,user_id";
	private Callback<List<NewsJson>> callback;
	
	
	public GetOneNews(String endpoint, String token, String newsId, Callback<List<NewsJson>> callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.newsId = newsId;
		doInBackground();
	}

	protected void doInBackground() {
		Log.d("token", token);
		Log.d("news_id", newsId);
		Log.d("endpoint", endpoint);
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getOneNews(token, action, newsId, columns, callback);
	}

}
