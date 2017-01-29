package com.yoggo.dleandroidclient.requests;

import retrofit.Callback;
import retrofit.RestAdapter;

import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.serverapi.OneDLEApi;

public class EditComment {
	private String endpoint;
	private String token;
	private String id;
	private String commText;
	private String action = "editcomment";
	private Callback<AddCommentsJson> callback;
	
	public EditComment(String endpoint, String token, String id, String commText, Callback<AddCommentsJson>  callback) {
		this.token = token;
		this.callback = callback;
		this.endpoint = endpoint;
		this.id = id;
		this.commText = commText;
		doInBackground();
	}
	
	protected void doInBackground() {
		RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint)
				.build();
		OneDLEApi api = adapter.create(OneDLEApi.class);
		api.editComments(token, action, id, commText, callback);
	}
}
