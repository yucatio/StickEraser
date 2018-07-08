package jp.gr.java_conf.yuka.stickeraser.model;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.strategy.EraseStrategy;
import jp.gr.java_conf.yuka.stickeraser.view.StickView;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;

public class ComputerPlayer extends Player {
	private static final long serialVersionUID = 1L;

	// 1回に書く長さ
	private static final int LENGTH_PER_FRAME = 5;

	private EraseStrategy strategy;

	public ComputerPlayer(String name, EraseStrategy strategy) {
		super(name);
		this.strategy = strategy;
	}

	public ComputerPlayer(String name, int lineColor, EraseStrategy strategy) {
		super(name, lineColor);
		this.strategy = strategy;
	}

	/**
	 * 棒けしの線をひきます
	 *
	 * @param stickManager
	 */
	public void drawLine(StickManager stickManager, StickView view) {
		// Strategyで消す場所の決定
		StickManager.Block block = strategy.getEraseSticks(stickManager);

		// 消す場所に対応する線の座標
		Line eraseLine = stickManager.getEraseLine(block);
//		Log.v("drawLine",
//				"(" + eraseLine.getStart().x + "," + eraseLine.getStart().y
//						+ "),(" + eraseLine.getEnd().x + ","
//						+ eraseLine.getEnd().y + ")");

		// アニメーション
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
				eraseLine.getStart().x, eraseLine.getStart().y, 0);
		view.onTouchEvent(event);

		// アニメーション
		// 線の長さ
		int lineLength = (int) Math.sqrt(Math.pow(eraseLine.getStart().x - eraseLine.getEnd().x, 2)
				+ Math.pow(eraseLine.getStart().y - eraseLine.getEnd().y, 2));
		// 分割数
		int time = lineLength / LENGTH_PER_FRAME + 1;
		for (int i = 0; i < time; i++) {
			event = MotionEvent.obtain(SystemClock.uptimeMillis(),
					SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
					(1 - 1.0f * i / time) * eraseLine.getStart().x + (1.0f * i / time)
							* eraseLine.getEnd().x, (1 - 1.0f * i / time) * eraseLine.getStart().y + (1.0f * i / time) * eraseLine.getEnd().y, 0);
			view.onTouchEvent(event);
			SystemClock.sleep(10);
		}

		event = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
				eraseLine.getEnd().x, eraseLine.getEnd().y, 0);
		view.onTouchEvent(event);
	}

	public EraseStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(EraseStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void gameWin(Context context, GameInfoModel gameInfo) {
		// nothing to do
	}

}
