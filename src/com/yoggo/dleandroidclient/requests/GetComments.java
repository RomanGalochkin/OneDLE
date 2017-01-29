package com.yoggo.dleandroidclient.requests;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.CommentJson;
import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class GetComments {
	
	private String endpoint;
	private String token;
	private String newsId;
	private String action = "getcomments";
	private String columnOrder = "id";
	private String order = "DESC";
	private String start = "0";
	private String limit = "10";
	private String columns = "id,text,autor,date";
	private Callback<List<CommentJson>> callback;
	
	public GetComments(String endpoint, String token, String newsId, Callback<List<CommentJson>> callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.newsId = newsId;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.getComments(token,newsId, action,
				columnOrder,
				order,
				start,
				limit,
				columns,
				callback);
	}
}
