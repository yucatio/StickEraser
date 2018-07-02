package jp.gr.java_conf.yuka.stickeraser.model;

import android.content.Context;
import jp.gr.java_conf.yuka.stickeraser.code.StickEraserConstants;
import jp.gr.java_conf.yuka.stickeraser.db.GameCountDao;

public class HumanPlayer extends Player {

	private static final long serialVersionUID = 1L;

	private GameCountDao gameCountDao;

	public HumanPlayer(String name) {
		super(name);
	}

	public HumanPlayer(String name, int lineColor) {
		super(name, lineColor);
	}

	@Override
	public void gameWin(Context context, GameInfoModel gameInfo) {
		if (gameCountDao == null) {
			// Dao
			gameCountDao = new GameCountDao(context);
		}
		// 勝利数をカウントアップする
		if (gameInfo.getLevel() != StickEraserConstants.VS_HUMAN) {
			gameCountDao.updateWinCount(gameInfo.getLevel(), gameInfo.getStage());
		}
	}

}
