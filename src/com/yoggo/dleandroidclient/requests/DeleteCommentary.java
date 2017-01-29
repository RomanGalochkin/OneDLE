package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class DeleteCommentary {
	private String endpoint;
	private String token;
	private String id;
	private String action = "delcomment";
	private Callback<AddCommentsJson> callback;
	
	public DeleteCommentary(String endpoint, String token, String id, 
			 Callback<AddCommentsJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.id = id;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.deleteComments(token, action, id, callback);
	}
}
