package com.yoggo.dleandroidclient.adapters;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yoggo.dleandroidclient.AddNewsActivity;
import com.yoggo.dleandroidclient.AuthorizationActivity;
import com.yoggo.dleandroidclient.EditCommentActivity;
import com.yoggo.dleandroidclient.MainActivity;
import com.yoggo.dleandroidclient.R;
import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.database.Commentaries;
import com.yoggo.dleandroidclient.database.CommentariesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.requests.DeleteCommentary;

public class CommentariesAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Commentaries> commentaries;
	private AccountSettingsManager accountManager;
	private DatabaseManager dbManager;
	private ListView listView;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;

	private long newsId;

	public CommentariesAdapter(Context context, List<Commentaries> commentaries) {
		this.context = context;
		this.commentaries = commentaries;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		accountManager = new AccountSettingsManager(context);
		dbManager = new DatabaseManager(context);
	}

	public void setNewsId(long newsId) {
		this.newsId = newsId;
	}

	/*
	 * Вызов меню по долгому тапу
	 * */
	OnItemLongClickListener longItemClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder != null) {
				openPopupMenu(view, holder);
			}
			return false;
		}
	};

	public void openPopupMenu(View view, final ViewHolder holder) {
		PopupMenu popup = new PopupMenu(context, view);
		if ((holder.title.getText().toString().equals(accountManager.getName())
				|| accountManager.isUserCanAllEdit())
				|| accountManager.isAdmin()) {
			popup.getMenuInflater().inflate(R.menu.popup_menu_edit_comment,
					popup.getMenu());
			popup.getMenuInflater().inflate(R.menu.popup_menu_delete_comment,
					popup.getMenu());
		}
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				int id = item.getItemId();
				if (id == R.id.action_edit_comment) {
					Intent edit = new Intent(context, EditCommentActivity.class);
					//передаем параметры для редактирования коммента
					edit.putExtra(EditCommentActivity.COMMENT, holder.content
							.getText().toString());
					edit.putExtra(EditCommentActivity.ID,
							String.valueOf(holder.id));
					context.startActivity(edit);
					return true;
				} else if (id == R.id.action_delete_comment) {
					deleteComments(holder.id);
					return true;
				}
				return true;
			}
		});
		popup.show();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (listView == null) {
			listView = (ListView) parent;
			listView.setOnItemLongClickListener(longItemClick);
		}
		// используем созданные, но не используемые view
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.comment_layout, parent, false);
			holder.title = (TextView) view.findViewById(R.id.title_textView);
			holder.date = (TextView) view.findViewById(R.id.date_textView);
			holder.content = (TextView) view
					.findViewById(R.id.content_textView);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Commentaries com = (Commentaries) getItem(position);

		holder.id = Long.valueOf(com.getId());

		holder.title.setText(com.getAuthor());
		holder.date.setText(com.getDate());
		holder.content.setText(Html.fromHtml(com.getContent()));

		return view;
	}

	private void deleteComments(final long comId) {
		Callback<AddCommentsJson> callback = new Callback<AddCommentsJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(context, "Ошибка при удалении комментария", Toast.LENGTH_SHORT)
						.show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddCommentsJson list, Response arg1) {
				if (list != null) {
					if (list.result != null) {
						Log.d("result", list.result);
					}
					if (list.error != null) {
						Toast.makeText(context, list.error, 200).show();
						return;
					}
					//удаляем из локальной БД
					dbManager.deleteCommentsFromLocalDB(comId);
					DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
							"onedle.db", null);
					db = helper.getWritableDatabase();
					daoMaster = new DaoMaster(db);
					daoSession = daoMaster.newSession();
					CommentariesDao comDao = daoSession.getCommentariesDao();
					List<Commentaries> coms = comDao
							.queryBuilder()
							.where(CommentariesDao.Properties.NewsId.eq(newsId))
							.list();
					commentaries = coms;
					notifyDataSetChanged();
					notifyDataSetInvalidated();

				} else {
					Toast.makeText(context, "Ошибка при удалении комментария", Toast.LENGTH_SHORT)
					.show();
				}
			}

		};
		 new DeleteCommentary(accountManager.getSite(),
				 accountManager.getToken(), String.valueOf(comId), callback);
	}

	class ViewHolder {
		long id;
		long userId;
		TextView title;
		TextView content;
		TextView date;
		EditText comment;
	}

	@Override
	public int getCount() {
		return commentaries.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return commentaries.get(position);
	}

	// id по позиции
	@Override
	public long getItemId(int position) {
		return position;
	}

}
