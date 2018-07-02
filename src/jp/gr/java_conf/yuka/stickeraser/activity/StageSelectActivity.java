package jp.gr.java_conf.yuka.stickeraser.activity;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.adapter.StageListRowAdapter;
import jp.gr.java_conf.yuka.stickeraser.code.StickEraserConstants;
import jp.gr.java_conf.yuka.stickeraser.db.GameCountDao;
import jp.gr.java_conf.yuka.stickeraser.model.GameCountModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class StageSelectActivity extends Activity {
	private static final String TAG = "StageSelectActivity";

	private GameCountDao gameCountDao;
	private AlertDialog.Builder dlg;
	private int level;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stage_select);

		// Dao
		gameCountDao = new GameCountDao(this);

		// dialog
		dlg = new AlertDialog.Builder(this);

		// レベルの表示
		Intent menuIntent = getIntent();
		level = menuIntent.getIntExtra("level", 0);

		// 対応する画像を表示
		ImageView levelImage = (ImageView) findViewById(R.id.levelImage);
		levelImage.setImageResource(StickEraserConstants.getLevelImageId(level));

		// ListView
		ListView stageListView = (ListView) findViewById(R.id.stageList);

		// ヘッダの追加
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View countView = inflater.inflate(R.layout.stage_row_header, null);
		stageListView.addHeaderView(countView);

		// クリック時の動作
		stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {

				if (position <= 0) {
					return;
				}

				String[] stageArray = getResources().getStringArray(R.array.stage);
				String stageString = stageArray[position - 1];
				// ダイアログの表示
				dlg.setTitle(stageString)
				.setMessage(R.string.choiceTurn)
				.setPositiveButton(R.string.topTurn, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// ダイアログを閉じる
						dialog.dismiss();

						Intent stickIntent = new Intent(StageSelectActivity.this, StickEraserActivity.class);
						// ステージを受け渡す
						stickIntent.putExtra("stage", position-1);

						// 先攻、後攻を受け渡す
						stickIntent.putExtra("turn", StickEraserConstants.TOP_TURN);

						// レベルを受け渡す
						stickIntent.putExtra("level", level);

						startActivity(stickIntent);
					}
				}).setNeutralButton(R.string.bottomTurn, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// ダイアログを閉じる
						dialog.dismiss();

						Intent stickIntent = new Intent(StageSelectActivity.this, StickEraserActivity.class);
						// ステージを受け渡す
						stickIntent.putExtra("stage", position - 1);

						// 先攻、後攻を受け渡す
						stickIntent.putExtra("turn", StickEraserConstants.BOTTOM_TURN);

						// レベルを受け渡す
						stickIntent.putExtra("level", level);

						startActivity(stickIntent);
					}
				}).setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// ダイアログを閉じる
						dialog.dismiss();
					}
				}).show();
			}
		});

		// 戻るボタン
		Button backButton = (Button) this.findViewById(R.id.back);

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		// ステージ表示
		refleshStageList();

	}

	// Creates the menu items
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.stage_select_menu, menu);
		return true;
	}

	// Handles item selections
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.resetWinCount:
			Log.v(TAG, "resetWinCount selected");
			// ダイアログ開く
			dlg = new AlertDialog.Builder(this);
			dlg.setMessage(R.string.resetWinCountConfirm)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 勝ち数クリア
						gameCountDao.deleteAll(level);

						// 再表示
						refleshStageList();

					}
			}).setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// ダイアログを閉じる
					dialog.dismiss();
				}
			}).show();

			return true;
		}

		return false;
	}

	/**
	 * ステージリストを表示します
	 */
	private void refleshStageList() {
		// ListView
		ListView stageListView = (ListView) findViewById(R.id.stageList);
		List<StageListRow> stages = new ArrayList<StageListRow>();

		String[] stageArray = getResources().getStringArray(R.array.stage);

		for (int i = 0; i < stageArray.length; i++) {
			// DBからデータを取得
			GameCountModel countModel = gameCountDao.select(level, i);
			stages.add(new StageListRow(stageArray[i], countModel.getWinCount()));
		}

		stageListView.setAdapter(new StageListRowAdapter(this, R.layout.stage_row, stages));

	}


	public static class StageListRow {
		private String stageName;
		private int winCount;

		public StageListRow(String stageName, int winCount) {
			super();
			this.stageName = stageName;
			this.winCount = winCount;
		}
		public String getStageName() {
			return stageName;
		}
		public void setStageName(String stageName) {
			this.stageName = stageName;
		}
		public int getWinCount() {
			return winCount;
		}
		public void setWinCount(int winCount) {
			this.winCount = winCount;
		}


	}
}
