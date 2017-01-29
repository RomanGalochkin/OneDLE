package com.yoggo.dleandroidclient;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.database.Categories;
import com.yoggo.dleandroidclient.database.CategoriesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.fragments.NewsFragment;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.requests.AddNews;
import com.yoggo.dleandroidclient.requests.EditNews;

public class AddNewsActivity extends ActionBarActivity implements OnClickListener{
	
	//константы для передачи значений в intent
	public static final String IS_EDIT = "IS_EDIT";
	public static final String NEWS_ID = "NEWS_ID";
	public static final String NEWS_TITLE = "NEWS_TITLE";
	public static final String NEWS_SHORT = "NEWS_SHORT";
	public static final String NEWS_FULL = "NEWS_FULL";
	public static final String NEWS_CATEGORY = "NEWS_CATEGORY";
	
	
	private EditText newsNameEditText;
	private EditText newsShortEditText;
	private EditText newsFullEditText;
	private Button sendButton;
	private Spinner categoriesSpinner;
	private String[] catNames;
	private boolean isEdit;
	private String newsId;
	
	private AccountSettingsManager accountManager;
	private DatabaseManager dbManager;
	
	SQLiteDatabase db;
	DaoMaster daoMaster;
    DaoSession daoSession;
    DevOpenHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_news);

		categoriesSpinner = (Spinner) findViewById(R.id.add_news_category_spinner);
		newsNameEditText = (EditText) findViewById(R.id.add_news_name_editText);
		newsShortEditText = (EditText) findViewById(R.id.add_news_short_editText);
		newsFullEditText = (EditText) findViewById(R.id.add_news_full_editText);
		sendButton = (Button) findViewById(R.id.add_news_send_button);
		sendButton.setOnClickListener(this);
		
		helper = new DaoMaster.DevOpenHelper(getApplicationContext(), DatabaseManager.DATABASE_NAME, null);
		db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        accountManager = new AccountSettingsManager(
				this);
        
		getCategories();
		
		dbManager = new DatabaseManager(getApplicationContext());
		
		setCategorySpinner();
		Intent intent = getIntent();
		isEdit(intent);
	}
	
	/*
	 * Устанавливаем спиннер с категориями
	 * */
	private void setCategorySpinner(){
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catNames);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoriesSpinner.setAdapter(spinnerAdapter);
	}
	
	/*
	 * Метод для определения - довавляем новую или редактируем существующую новость
	 * */
	public void isEdit(Intent intent){
		isEdit = intent.getBooleanExtra(IS_EDIT, false);
		if(isEdit){
			sendButton.setText(getResources().getString(R.string.edit));
			setTitle(getResources().getString(R.string.edit_news));
			newsNameEditText.setText(intent.getStringExtra(NEWS_TITLE));
			newsShortEditText.setText(intent.getStringExtra(NEWS_SHORT).replaceAll("<br />", "\n"));
			newsFullEditText.setText(intent.getStringExtra(NEWS_FULL).replaceAll("<br />", "\n"));
			newsId = intent.getStringExtra(NEWS_ID);
			Log.d("category id", intent.getStringExtra(NEWS_CATEGORY));
			String catName = getCategoryNameFromId(intent.getStringExtra(NEWS_CATEGORY));
			int pos = 0;
			//выбираем родительскую категорию (если есть)
			for(int i = 0; i <catNames.length; i++){
				if(catNames[i].equals(catName)){
					pos = i;
					break;
				}
			}
			categoriesSpinner.setSelection(pos);
		}else{
			setTitle(getResources().getString(R.string.add_news));
		}
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.add_news_send_button:
			if(isEdit){
				editNews();
			}else{
				addNews();
			}
			break;
		}
	}
	
	/*
	 * Получаем все категории
	 * */
	private void getCategories(){
		CategoriesDao catDao = daoSession.getCategoriesDao();
		List<Categories> cats = catDao.loadAll();
		catNames = new String[cats.size()];
		for(int i = 0; i< cats.size(); i++){
			catNames[i] = cats.get(i).getName();
		}
		
	}
	
	/*
	 * Получаем id категории из имени
	 * */
	private String getCategoryIdFromName(){
		CategoriesDao catDao = daoSession.getCategoriesDao();
		List<Categories> list = catDao.queryBuilder().where(CategoriesDao.Properties.Name.eq((String)categoriesSpinner.getSelectedItem())).limit(1).list();
		String catList = "";
		for(Categories cat : list){
			catList += cat.getId();
		}
		return catList;
	}
	
	/*
	 * Получаем имя категории из id
	 * */
	private String getCategoryNameFromId(String id){
		CategoriesDao catDao = daoSession.getCategoriesDao();
		List<Categories> list = catDao.queryBuilder().where(CategoriesDao.Properties.Id.eq(id)).limit(1).list();
		String catList = "";
		for(Categories cat : list){
			catList = cat.getName();
		}
		return catList;
	}
	
	/*
	 * Метод добавления новости
	 * */
	private void addNews(){

		Callback<AddNewsJson> callback = new Callback<AddNewsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), "Ошибка при добавлении новости",
				 200).show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddNewsJson list, Response arg1) {
				if (list != null) {
					if(list.newsId != null){
						Log.d("news_id", list.newsId);
					}
					if(list.result != null){
						Log.d("result", list.result);
					}
					//проверяем модерацию
					if(list.moderation != null){
						Log.d("moderation", list.moderation);
						if(list.moderation.equals("yes")){
							Toast.makeText(getApplicationContext(),
									"Новость отправлена на модерацию", 200).show();
							finish();
						}else{
							NewsFragment.uploadNews(null, String.valueOf(list.newsId));
							finish();
						}
					}
					if(list.error != null){
						Log.d("error", list.error);
						Toast.makeText(getApplicationContext(),
								list.error, 200).show();
						return;
					}
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при добавлении новости", 200).show();
				}
			}

		};
			new AddNews(accountManager.getSite(), 
					accountManager.getToken(), 
					newsNameEditText.getText().toString(),
					newsShortEditText.getText().toString(),
					newsFullEditText.getText().toString(),
					getCategoryIdFromName(),
					callback);
    }
	
	/*
	 * Метод редактирования новости
	 * */
	private void editNews(){
    	AccountSettingsManager account = new AccountSettingsManager(
				this);
		Callback<AddNewsJson> callback = new Callback<AddNewsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), "Ошибка при редактировании новости",
				 Toast.LENGTH_SHORT).show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddNewsJson list, Response arg1) {
				if (list != null) {
					if(list.newsId != null){
						Log.d("news_id", list.newsId);
					}
					if(list.result != null){
						Log.d("result", list.result);
					}
					//обновляем запись в локальной базе
					dbManager.updateNewsFromLocalDB(Long.valueOf(newsId), newsNameEditText.getText().toString(),
							newsShortEditText.getText().toString(),
							newsFullEditText.getText().toString());
					if(list.error != null){
						Log.d("error", list.error);
						Toast.makeText(getApplicationContext(),
								list.error, 200).show();
						return;
					}
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при редактировании новости", 200).show();
				}
			}
		};
			 new EditNews(account.getSite(), 
					account.getToken(),
					newsId,
					newsNameEditText.getText().toString(),
					newsShortEditText.getText().toString(),
					newsFullEditText.getText().toString(),
					getCategoryIdFromName(),
					callback);
    }

}
