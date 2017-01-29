package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class AddComments {
	private String endpoint;
	private String token;
	private String comments;
	private String action = "addcomment";
	private String postId;
	private Callback<AddCommentsJson> callback;
	
	public AddComments(String endpoint, String token, String comments, String postId,
			 Callback<AddCommentsJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.comments = comments;
		this.postId = postId;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.addComments(token, action, postId, comments, callback);
	}
}
