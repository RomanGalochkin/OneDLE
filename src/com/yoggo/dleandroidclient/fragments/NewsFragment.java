package com.yoggo.dleandroidclient.fragments;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.yoggo.dleandroidclient.FullNewsActivity;
import com.yoggo.dleandroidclient.MainActivity;
import com.yoggo.dleandroidclient.R;
import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.adapters.NewsAdapter;
import com.yoggo.dleandroidclient.database.CommentariesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.News;
import com.yoggo.dleandroidclient.database.NewsDao;
import com.yoggo.dleandroidclient.interfaces.DrawerListUpdateCallback;
import com.yoggo.dleandroidclient.interfaces.OldNewsLoadListener;
import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.requests.GetNews;

public class NewsFragment extends Fragment implements OnRefreshListener,
		OldNewsLoadListener, DrawerListUpdateCallback {
	
	//переменные дл€ восстановлени€ положени€ скролла
	private static int index = 0;
	private static int top = 0;

	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String COMMENTS_TAG = "comments_tag";
	private static final String OPEN_COMMENTS_TAG = "comments_tag";
	public static final int NEWS_FRAGMENT = 1;

	private static ListView newsListView;
	private static NewsAdapter newsAdapter;
	private static DatabaseManager dbManager;
	private static SwipeRefreshLayout swipeLayout;
	private static Activity activity;
	
	private DaoMaster daoMaster;
	private static DaoSession daoSession;
	private SQLiteDatabase db;
	
	private static NewsFragment fragment;
	
	private View root;

	public static NewsFragment newInstance() {
		if(fragment == null){
			fragment = new NewsFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, NEWS_FRAGMENT);
			fragment.setArguments(args);
			return fragment;
		}else{
			return fragment;
		}
		
	}

	public NewsFragment() {
	}
	
	@Override
	public void onResume() {
		if(newsAdapter != null){
			dbManager = new DatabaseManager(getActivity());
			
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "onedle.db", null);
	        db = helper.getWritableDatabase();
	        daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
	        
	        //провер€ем, если новостей в базе ноль, то подгружаем с сервера, иначе
	        //ждЄм когда пользователь сам это сделает
			NewsDao comDao = daoSession.getNewsDao();
			try{
				List<News> coms = comDao.queryBuilder().orderDesc(NewsDao.Properties.Date).list();
				newsAdapter = new NewsAdapter(getActivity(), coms);
				newsAdapter.setOldNewsLoadListener(this);
				newsListView.setAdapter(newsAdapter);
				newsListView.setSelectionFromTop(index, top);;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		super.onResume();
	}
	
	@Override
	public void onPause(){
		index = newsListView.getFirstVisiblePosition();
		View v = newsListView.getChildAt(0);
		top = (v == null) ? 0 : (v.getTop() - newsListView.getPaddingTop());
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_news, container,
				false);
		root = rootView;
		activity = getActivity();
		
		dbManager = new DatabaseManager(getActivity());
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "onedle.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        //провер€ем, если новостей в базе ноль, то подгружаем с сервера, иначе
        //ждЄм когда пользователь сам это сделает
		NewsDao comDao = daoSession.getNewsDao();
		try{
			List<News> coms = comDao.queryBuilder().orderDesc(NewsDao.Properties.Date).limit(10).list();
			if(coms.size() == 0){
				uploadNews(null, null);
			}

			newsAdapter = new NewsAdapter(getActivity(), coms);
			newsAdapter.setOldNewsLoadListener(this);
			newsListView = (ListView) rootView.findViewById(R.id.newsListView);
			swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.news_refresh);
			swipeLayout.setOnRefreshListener(this);
			//настройка свайп бара
			swipeLayout.setProgressViewOffset(false, FullNewsActivity.dpToPx(35),
					FullNewsActivity.dpToPx(90));
			newsListView.setAdapter(newsAdapter);
			//при скролле пр€чем скроллбар
			newsListView.setOnScrollListener(new OnScrollListener() {
				int mLastFirstVisibleItem = 0;
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}

				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (view.getId() == newsListView.getId()) {
						ActionBarActivity aba = (ActionBarActivity) getActivity();
						ActionBar ab = aba.getSupportActionBar();
						int currentFirstVisibleItem = newsListView
								.getFirstVisiblePosition() - 1;

						if (currentFirstVisibleItem > mLastFirstVisibleItem
								&& ab.isShowing()) {
							// getSherlockActivity().getSupportActionBar().hide();
							ab.hide();

						} else if (currentFirstVisibleItem < mLastFirstVisibleItem
								&& !ab.isShowing()) {
							// getSherlockActivity().getSupportActionBar().show();
							ab.show();
						}

						boolean enable = false;
						if (newsListView != null
								&& newsListView.getChildCount() > 0) {
							boolean firstItemVisible = newsListView
									.getFirstVisiblePosition() == 0;
							boolean topOfFirstItemVisible = newsListView
									.getChildAt(0).getTop() == 0;
							enable = firstItemVisible && topOfFirstItemVisible;
						}
						swipeLayout.setEnabled(enable);

						mLastFirstVisibleItem = currentFirstVisibleItem;
					}
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
		return rootView;
	}

	public static void uploadNews(final String start, final String openFull) {
		AccountSettingsManager account = new AccountSettingsManager(
				activity);
		Callback<List<NewsJson>> callback = new Callback<List<NewsJson>>() {

			@Override
			public void failure(RetrofitError arg0) {
				if (swipeLayout.isShown()) {
					swipeLayout.setRefreshing(false);
				}
				if(arg0.getResponse() == null ||
						arg0.getResponse().getReason() == null ||
						!arg0.getResponse().getReason().equals("OK")){
					 Toast.makeText(activity, "ќшибка при загрузке новостей",
							 Toast.LENGTH_SHORT).show();
					 return;
				}
			}

			@Override
			public void success(List<NewsJson> list, Response arg1) {
				if (list != null) {
					try{
						dbManager.insertToNews(list);
						NewsDao newsDao = daoSession.getNewsDao();
						List<News> news = newsDao.queryBuilder().orderDesc(NewsDao.Properties.Date).list();
						newsAdapter.changeNewsId(news);
						//обновл€ем адаптер
						newsAdapter.notifyDataSetChanged();
						if (swipeLayout.isShown()) {
							swipeLayout.setRefreshing(false);
						}
						
						if(openFull != null){
							//открывем полную новость
							Intent intent = new Intent(activity, FullNewsActivity.class);
							intent.putExtra(FullNewsActivity.NEWS_ID,
									openFull);
							activity.startActivity(intent);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				} else {
					Toast.makeText(activity,
							"ќшибка при загрузке новостей", Toast.LENGTH_SHORT).show();
				}
			}
		};
		//start - точка загрузки новостей, т.е. либо подгружаем по скроллу, либо новые
		if (start == null) {
			 new GetNews(account.getSite(),
					account.getToken(), callback);
		} else {
			new GetNews(account.getSite(),
					account.getToken(), start, callback);
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));

	}

	@Override
	public void onRefresh() {
		uploadNews(null, null);

	}

	@Override
	public void loadNews() {
		NewsDao comDao = daoSession.getNewsDao();
		String start = String.valueOf(comDao.count());
		Log.d("start", start);
			uploadNews(start, null);
		
		

	}

	@Override
	public void updateList() {
		try{
			NewsDao comDao = daoSession.getNewsDao();
			List<News> coms = comDao.queryBuilder().orderDesc(NewsDao.Properties.Date).list();
			//обновл€ем адаптер
			newsAdapter.changeNewsId(coms);
			newsAdapter.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
