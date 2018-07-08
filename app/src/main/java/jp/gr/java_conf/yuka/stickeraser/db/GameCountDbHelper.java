package jp.gr.java_conf.yuka.stickeraser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameCountDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "stick_earser.db";
	private static final int DB_VERSION = 1;
	private static final String CREATE_TABLE_SQL =
			"CREATE TABLE game_count " +
			"(level integer not null," +	// 対戦相手のレベル
			" stage integer not null," +	// 棒の配置パターンのID
			" win_count integer not null," +	// 勝ち数
			"PRIMARY KEY(level, stage))";

	public GameCountDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// 実装なし
	}

}
