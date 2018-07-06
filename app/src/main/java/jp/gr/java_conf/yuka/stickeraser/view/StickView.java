package jp.gr.java_conf.yuka.stickeraser.view;

import jp.gr.java_conf.yuka.stickeraser.activity.StickEraserActivity;
import jp.gr.java_conf.yuka.stickeraser.code.GameState;
import jp.gr.java_conf.yuka.stickeraser.manager.LineManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class StickView extends SurfaceView implements SurfaceHolder.Callback {
	/** アクティブライン始点 */
	private Point startPoint = new Point(-999, -999);

	/** 線情報をもつmanager */
	private LineManager lineManager;

	/** 棒情報をもつmanager */
	private StickManager stickManager;

	public StickView(Context context) {
		this(context, null, 0);
	}

	public StickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setFocusable(true);
		setFocusableInTouchMode(true);
		setClickable(true);
		requestFocus();

		getHolder().addCallback(this);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		stickManager.resize(width, height);

	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 幅の変更
		stickManager.resize(getWidth(), getHeight());
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	private void draw() {
		Canvas canvas = getHolder().lockCanvas();
		// 背景色の設定
		canvas.drawColor(Color.WHITE);

		if (stickManager == null) {
			return;
		}

		// 棒オブジェクトを描画
		stickManager.drawSticks(canvas);

		// 線オブジェクトを描画
		lineManager.drawLines(canvas);

		getHolder().unlockCanvasAndPost(canvas);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		Log.v("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());

		StickEraserActivity activity = (StickEraserActivity) this.getContext();
		activity.hideMessage();

		// ゲーム中でない場合はここでreturn
		if (activity.getGameState() == GameState.OUT_OF_GAME) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			startPoint.set((int) event.getX(), (int) event.getY());

			// 通知
			stickManager.update((int) event.getX(), (int) event.getY(), (int) event.getX(),
					(int) event.getY());

			int color = activity.getLineColor();
			lineManager.newActiveLine((int) event.getX(), (int) event.getY(), (int) event.getX(),
					(int) event.getY(), color);

			break;
		case MotionEvent.ACTION_UP:
			if (startPoint == null || startPoint.x < 0 || startPoint.y < 0) {
				return true;
			}

			// 通知
			stickManager.update(startPoint.x, startPoint.y, (int) event.getX(), (int) event.getY());
			lineManager.updateActiveLine(startPoint.x, startPoint.y, (int) event.getX(), (int) event.getY());

			// 線を消す
			activity.eraseStick(startPoint.x, startPoint.y, (int) event.getX(), (int) event.getY());

			// 通知
			stickManager.update(0, 0, 0, 0);

			startPoint.set(-999, -999);

			break;
		case MotionEvent.ACTION_MOVE:
			if (startPoint == null || startPoint.x < 0 || startPoint.y < 0) {
				return true;
			}

			// 通知
			stickManager.update(startPoint.x, startPoint.y,(int) event.getX(), (int) event.getY());
			lineManager.updateActiveLine(startPoint.x, startPoint.y,(int) event.getX(), (int) event.getY());

			break;
		case MotionEvent.ACTION_CANCEL:
			// 通知
			stickManager.update(-999, -999, -999, -999);
			lineManager.updateActiveLine(-999, -999, -999, -999);
			startPoint.set(-999, -999);

			break;
		}

		draw();
		return true;
	}

	public void setStickManager(StickManager stickManager) {
		this.stickManager = stickManager;
	}

	public void setLineManager(LineManager lineManager) {
		this.lineManager = lineManager;

	}

	public void reset() {
		startPoint.set(-999, -999);
		draw();
	}

	public void restart() {
		startPoint.set(-999, -999);
		draw();
	}

}
