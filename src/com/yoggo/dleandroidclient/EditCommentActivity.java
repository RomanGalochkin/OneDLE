package com.yoggo.dleandroidclient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.requests.EditComment;

public class EditCommentActivity extends ActionBarActivity implements OnClickListener{
	
	//константы для передачи значений в intent
	public static final String COMMENT = "COMMENT";
	public static final String ID = "ID";
	
	private String id;
	private EditText commentEditText;
	private Button editCommentButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_comment);
		commentEditText = (EditText) findViewById(R.id.edit_comment_editText);
		editCommentButton = (Button) findViewById(R.id.edit_comment_button);
		editCommentButton.setOnClickListener(this);
		setTitle(getResources().getString(R.string.edit_comment));
		getData();
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.edit_comment_button:
			editComments();
			break;
		}
	}
	
	private void getData(){
		Intent intent = getIntent();
		commentEditText.setText(intent.getStringExtra(COMMENT));
		id = intent.getStringExtra(ID);
	}
	
	private void editComments(){
    	AccountSettingsManager account = new AccountSettingsManager(
				this);
		Callback<AddCommentsJson> callback = new Callback<AddCommentsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), "Ошибка при редактировании комментария",
				 Toast.LENGTH_SHORT).show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddCommentsJson list, Response arg1) {
				if (list != null) {
					if(list.result != null){
						Log.d("result", list.result);
					}
					if(list.error != null){
						Toast.makeText(getApplicationContext(),
								list.error, Toast.LENGTH_SHORT).show();
						return;
					}
					//если успешно
					finish();
				
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при редактировании комментария", Toast.LENGTH_SHORT).show();
				}
			}

		};
			new EditComment(account.getSite(), 
					account.getToken(),
					id,
					commentEditText.getText().toString(),
					callback);
    }
}
