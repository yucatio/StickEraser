package jp.gr.java_conf.yuka.stickeraser.db;

import jp.gr.java_conf.yuka.stickeraser.code.StickEraserConstants;
import jp.gr.java_conf.yuka.stickeraser.model.GameCountModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameCountDao {
	private static final String TABLE_NAME = "game_count";
	private GameCountDbHelper dbHelper;
	private SQLiteDatabase db;

	private static final String SELECT_SQL =
			"SELECT win_count " +
			"FROM " + TABLE_NAME + " " +
			"WHERE level=? AND stage=?";

	private static final String UPDATE_WIN_COUNT_SQL =
			"UPDATE " + TABLE_NAME + " " +
			"SET win_count = win_count + 1 " +
			"WHERE level=? AND stage=?";

	public GameCountDao(Context context) {
		dbHelper = new GameCountDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public GameCountModel select(int level, int stage) {
		Cursor cursor = null;

		int winCount =0;

		try {
			cursor = db.rawQuery(SELECT_SQL, new String[]{String.valueOf(level), String.valueOf(stage)});

			if (cursor.moveToFirst()) {
				// データあり
				winCount = cursor.getInt(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return new GameCountModel(level, stage, winCount);
	}

	/**
	 * 勝ち数を1増加させます
	 * @param level
	 * @param stage
	 */
	public void updateWinCount(int level, int stage) {
		// いまデータがあるかどうか

		Cursor cursor = null;

		try {
			cursor = db.rawQuery(SELECT_SQL, new String[]{String.valueOf(level), String.valueOf(stage)});

			if (cursor.moveToFirst()) {
				// データがある場合
				int count = cursor.getInt(0);

				if (count < StickEraserConstants.WIN_COUNT_MAX) {
					// MAX_COUNTより小さい場合
					// win_countをインクリメント
					Log.v("GameCountDao", "update data.");

					db.execSQL(UPDATE_WIN_COUNT_SQL, new String[]{String.valueOf(level), String.valueOf(stage)});
				}
			} else {
				// データがない場合
				// 初期値1でinsert
				Log.v("GameCountDao", "insert data.");

				ContentValues values = new ContentValues();
				values.put("level", level);
				values.put("stage", stage);
				values.put("win_count", 1);

				db.insert(TABLE_NAME, null, values);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

	}

	/**
	 * levelの勝ち数をクリアします
	 * @param level
	 */
	public void deleteAll(int level) {
		db.delete(TABLE_NAME, "level=?", new String[]{"" + level});
	}

	/**
	 * すべての対戦結果を削除します
	 */
	public void deleteAll() {
		db.delete(TABLE_NAME, null, null);
	}

}
