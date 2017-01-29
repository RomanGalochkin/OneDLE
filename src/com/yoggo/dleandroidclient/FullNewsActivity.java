package com.yoggo.dleandroidclient;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.adapters.CommentariesAdapter;
import com.yoggo.dleandroidclient.adapters.NewsAdapter;
import com.yoggo.dleandroidclient.database.Commentaries;
import com.yoggo.dleandroidclient.database.CommentariesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.database.News;
import com.yoggo.dleandroidclient.database.NewsDao;
import com.yoggo.dleandroidclient.fragments.NewsFragment;
import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.json.CommentJson;
import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.requests.AddComments;
import com.yoggo.dleandroidclient.requests.DeleteNews;
import com.yoggo.dleandroidclient.requests.GetComments;
import com.yoggo.dleandroidclient.requests.GetOneNews;
import com.yoggo.dleandroidclient.tools.SelectableMovementMethod;

public class FullNewsActivity extends ActionBarActivity {

	//константы для передачи значений в intent
	public final static String NEWS_ID = "news_id";

	private String newsId;
	private TextView titleTextView;
	private TextView contentTextView;
	private TextView authorTextView;
	private TextView dateTextView;
	private TextView commentsNumberTextView;
	private TextView categoryTextView;
	private EditText commentsFooterEditText;
	private TextView viewsNumberTextView;
	private TextView ratingTextView;
	private ImageButton commentsButton;
	private ListView commentsListView;
	private ImageView imageView;
	private boolean isShowComments;
	private CommentariesAdapter comAdapter;
	private ScrollView scrollView;
	private DatabaseManager dbManager;
	private AccountSettingsManager account;
	private News news;
	private ImageGetter igLoader;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		// получаем id новости из intent'а
		newsId = intent.getStringExtra(NEWS_ID);
		Log.d("fullnews", "id " + newsId);
		account = new AccountSettingsManager(getApplicationContext());
		setContentView(R.layout.full_news_layout);

		dbManager = new DatabaseManager(getApplicationContext());
		// ссылки на view
		setViews();
		//изначально фокус вверху
		scrollView.fullScroll(ScrollView.FOCUS_UP);
		// работа с БД, получаем новость по заданному id
		try {
			showNews();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// получаем все "подробности" с сервера
		getFullNews(); 
		// назначаем listener на кнопку комментов
		commentsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.comments_button:
					// показываем/скрываем комментарии
					showCommentsToggle();
					break;
				}
			}
		});
	}
	
	private void setViews(){
		titleTextView = (TextView) findViewById(R.id.title_textView);
		contentTextView = (TextView) findViewById(R.id.content_textView);
		authorTextView = (TextView) findViewById(R.id.author_textView);
		dateTextView = (TextView) findViewById(R.id.date_textView);
		commentsButton = (ImageButton) findViewById(R.id.comments_button);
		commentsListView = (ListView) findViewById(R.id.commentsListView);
		commentsNumberTextView = (TextView) findViewById(R.id.comments_number_textView);
		viewsNumberTextView = (TextView) findViewById(R.id.views_number_textView);
		ratingTextView = (TextView) findViewById(R.id.rating_number_textView);
		imageView = (ImageView) findViewById(R.id.imageView2);
		categoryTextView = (TextView) findViewById(R.id.full_news_category_textView);
		scrollView = (ScrollView) findViewById(R.id.full_news_scrollView);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getFullNews();
		uploadComments();
	}


	public void showNews() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "onedle.db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		news = newsDao.load(Long.valueOf(newsId));
		if (newsDao != null) {
			// заполняем view
			if (news.getTitle() != null) {
				titleTextView.setText(news.getTitle());
			}

			igLoader = new Html.ImageGetter() {
				public Drawable getDrawable(String source) {
					
					if(news.getImage() != null){
						Bitmap btm = BitmapFactory.decodeByteArray(news.getImage(), 0, news.getImage().length);
						imageView.setImageBitmap(btm);
						return new BitmapDrawable(getResources());
					}
					
					// Если рисунок существует в кеше, то просто устанавливаем
					// его и
					// ничего не делаем дальше
					if (NewsAdapter.mDrawableCache.containsKey(source)) {
						imageView.setImageDrawable(NewsAdapter.mDrawableCache
								.get(source).get());
						return new BitmapDrawable(getResources());
					}
					return new BitmapDrawable(getResources());
				}
			};

			
			if (news.getFullStory() != null) {
				contentTextView.setMovementMethod(new SelectableMovementMethod());
				contentTextView.setText(Html.fromHtml(news.getFullStory(),
						igLoader, null));
			}

			if (news.getNewsRead() != null) {
				viewsNumberTextView.setText(Html.fromHtml(news.getNewsRead()));
			}

			if (news.getRating() != null) {
				ratingTextView.setText(Html.fromHtml(news.getRating()));
			}

			if (news.getAuthor() != null) {
				authorTextView.setText(Html.fromHtml(news.getAuthor()) + " | ");
			}

			if (news.getDate() != null) {
				dateTextView.setText(news.getDate());
			}

			if (news.getTitle() != null) {
				setTitle(news.getTitle());
			}
			if (news.getCommNum() != null) {
				commentsNumberTextView.setText(news.getCommNum());
			}

			if (news.getCategory() != null) {
				categoryTextView.setText(dbManager.getCategoryNameById(news
						.getCategory()) + " | ");
			}
		}

	}

	public void getFullNews() {
		new GetOneNews(account.getSite(),
				account.getToken(), newsId, new Callback<List<NewsJson>>() {
					@Override
					public void failure(RetrofitError arg0) {
						Toast.makeText(getApplicationContext(),
								"Ошибка при загрузке новости", 200).show();
					}

					@Override
					public void success(List<NewsJson> list, Response arg1) {
						if (list != null) {
							for (NewsJson oneNews : list) {
								dbManager.insertToNews(oneNews);
							}
							showNews();
						} else {
							Toast.makeText(getApplicationContext(),
									"Ошибка при загрузке новости", 200).show();
						}
						//обновляем меню
						supportInvalidateOptionsMenu();
					}
				});
	}

	private void addComments() {
		
		Callback<AddCommentsJson> callback = new Callback<AddCommentsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(),
						"Ошибка при добавлении комментария", Toast.LENGTH_SHORT).show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddCommentsJson list, Response arg1) {
				if (list != null) {
					if (list.result != null) {
						Log.d("result", list.result);
					}
					if (list.error != null) {
						Log.d("error", list.error);
						Toast.makeText(getApplicationContext(), list.error, 200)
								.show();
						return;
					}
					//если успешно - обновляем комменты
					uploadComments();
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при добавлении комментария", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		if (commentsFooterEditText != null) {
			new AddComments(account.getSite(),
					account.getToken(), commentsFooterEditText.getText()
							.toString(), newsId, callback);
		}
	}

	/*
	 * показываем/скрываем комментарии
	 */
	public void showCommentsToggle() {
		if (isShowComments == false) {
			showComments(commentsListView, newsId, true);
			isShowComments = true;
		} else {
			removeComments(commentsListView);
			isShowComments = false;
		}
	}

	public void showComments(ListView view, String newsId, boolean isNeedUpload) {
		// инициализируем linearLayout для списка комментариев
		LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// работа с БД, заполняем таблицу с комментами(для тестов)
		CommentariesDao comDao = daoSession.getCommentariesDao();
		List<Commentaries> coms = comDao.queryBuilder()
				.where(CommentariesDao.Properties.NewsId.eq(newsId)).list();
		comAdapter = new CommentariesAdapter(this, coms);
		comAdapter.setNewsId(Long.valueOf(newsId));
		// проверка на только один футер
		if (commentsListView.getFooterViewsCount() == 0
				&& account.isUserCanAddComments()) {
			LayoutInflater lInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View footer = lInflater.inflate(R.layout.comments_footer, null);
			Button commentsButton = (Button) footer
					.findViewById(R.id.comments_footer_button);
			commentsFooterEditText = (EditText) footer
					.findViewById(R.id.comments_footer_editText);
			commentsButton.setTag(Integer.valueOf(newsId));
			commentsButton.setOnClickListener(commentsClick);
			commentsListView.addFooterView(footer);
		}
		
		// установка адаптера
		view.setAdapter(comAdapter);
		//устанавливаем высоту списка
		setListViewHeightBasedOnItems(view);
		view.setVisibility(View.VISIBLE);
		if (isNeedUpload) {
			uploadComments();
		}
	}
	
	/*
	 * Метод для определения высоты списка
	 * */
	public boolean setListViewHeightBasedOnItems(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter != null) {
	        int numberOfItems = listAdapter.getCount();
	        // Получаем высоту всех элементов.
	        int totalItemsHeight = 0;
	        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
	            View item = listAdapter.getView(itemPos, null, listView);
	            float px = (300) * (listView.getResources().getDisplayMetrics().density);
	            item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	            totalItemsHeight += item.getMeasuredHeight();
	        }
	        int totalDividersHeight = listView.getDividerHeight() *
	                (numberOfItems - 1);
	        // Получаем отступ
	        int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

	        // Выставляем списку высоту
	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalItemsHeight + totalDividersHeight + totalPadding;
	        listView.setLayoutParams(params);
	        listView.requestLayout();
	        return true;
	    } else {
	        return false;
	    }

	}

	/*
	 * Метод для загрузки комментариев
	 * */
	private void uploadComments() {
		new GetComments(account.getSite(),
				account.getToken(), newsId, new Callback<List<CommentJson>>() {

					@Override
					public void failure(RetrofitError arg0) {
						if(isShowComments){
							scrollView.post(new Runnable() {
								@Override
								public void run() {
									scrollView.fullScroll(ScrollView.FOCUS_DOWN);
								}
							});
						}
					}

					@Override
					public void success(List<CommentJson> list, Response arg1) {
						if (list != null) {
							//если успешно то обновляем локальную БД
							dbManager.insertToComments(list, newsId);
							if (isShowComments) {
								showComments(commentsListView, newsId, false);
								//фокус вниз
								scrollView.post(new Runnable() {
									@Override
									public void run() {
										scrollView
												.fullScroll(ScrollView.FOCUS_DOWN);
									}
								});
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"Ошибка при обновлении комментариев", 200)
									.show();
						}
					}
				});
	}

	/*
	 * listener для показа комментариев
	 */
	OnClickListener commentsClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			addComments();
			//очищаем футер
			if (commentsFooterEditText != null) {
				commentsFooterEditText.setText("");
			}
		}
	};

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	// удаляем комменты
	public void removeComments(ListView view) {
		view.setAdapter(null);
		view.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if ((news.getUserId() != null
				&& news.getUserId().equals(account.getUserId())
				&& account.isUserCanAddNews())
				|| account.isAdmin()) {
			getMenuInflater().inflate(R.menu.menu_edit_news, menu);
		}
		if ((news.getUserId() != null
				&& news.getUserId().equals(account.getUserId())
				&& account.isUserCanAllEdit())
				|| account.isAdmin()) {
			getMenuInflater().inflate(R.menu.menu_del_news, menu);
		}
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_edit_news) {
			Intent intent = new Intent(FullNewsActivity.this,
					AddNewsActivity.class);
			//передаем параметры новости для редактирования
			intent.putExtra(AddNewsActivity.IS_EDIT, true);
			intent.putExtra(AddNewsActivity.NEWS_TITLE, news.getTitle());
			intent.putExtra(AddNewsActivity.NEWS_SHORT, news.getShortStory());
			intent.putExtra(AddNewsActivity.NEWS_FULL, news.getFullStory());
			intent.putExtra(AddNewsActivity.NEWS_CATEGORY, news.getCategory());
			intent.putExtra(AddNewsActivity.NEWS_ID, newsId);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_del_news) {
			deleteNews();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void deleteNews(){
    	AccountSettingsManager account = new AccountSettingsManager(
				this);
		Callback<AddNewsJson> callback = new Callback<AddNewsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), "Ошибка при удалении новости",
				 Toast.LENGTH_SHORT).show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddNewsJson list, Response arg1) {
				if (list != null) {
					if(list.result != null){
						Log.d("result", list.result);
					}
					if(list.error != null){
						Log.d("error", list.error);
						Toast.makeText(getApplicationContext(),
								list.error, 200).show();
						return;
					}
					
					Toast.makeText(getApplicationContext(),
							"Новость была удалена", Toast.LENGTH_SHORT).show();
					//удаляем с локальной БД
					dbManager.deleteNewsFromLocalDB(Long.valueOf(newsId));
					NewsFragment.uploadNews(null, null);
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при удалении новости", 200).show();
				}
			}

		};
			new DeleteNews(account.getSite(), 
					account.getToken(), 
					newsId,
					callback);
		}
}
