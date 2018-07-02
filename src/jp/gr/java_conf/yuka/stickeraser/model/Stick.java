package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

import jp.gr.java_conf.yuka.stickeraser.code.ColorSettings;
import jp.gr.java_conf.yuka.stickeraser.code.StickState;
import android.graphics.Paint;

/**
 * 1つのstickを表すモデルです
 * stickの位置を保持します
 */
public class Stick implements Serializable {
	private static final long serialVersionUID = 5215351555853425585L;

	private static final int LINE_WIDTH = 8;

	private int startX;
	private int startY;
	private int endX;
	private int endY;

	private StickState status = StickState.NOT_ERASED;

	private Paint paint = PAINT_NOT_ERASED;

	/** 消されていないときのpaint */
	private static Paint PAINT_NOT_ERASED = new Paint();
	/** 消された時のpaint */
	private static Paint PAINT_ERASED = new Paint();
	/** 線がオーバーラップしたときのpaint */
	private static Paint PAINT_INTERSECTED = new Paint();
	/** 消されているかつオーバーラップしたときのpaint */
	private static Paint PAINT_ERASED_INTERSECTED = new Paint();

	static {
		PAINT_NOT_ERASED.setAntiAlias(true);
		PAINT_NOT_ERASED.setColor(ColorSettings.STICK_COLOR_NOT_ERASED);
		PAINT_NOT_ERASED.setStrokeWidth(LINE_WIDTH);

		PAINT_ERASED.setAntiAlias(true);
		PAINT_ERASED.setColor(ColorSettings.STICK_COLOR_ERASED);
		PAINT_ERASED.setStrokeWidth(LINE_WIDTH);

		PAINT_INTERSECTED.setAntiAlias(true);
		PAINT_INTERSECTED.setColor(ColorSettings.STICK_COLOR_INTERSECTED);
		PAINT_INTERSECTED.setStrokeWidth(LINE_WIDTH);

		PAINT_ERASED_INTERSECTED.setAntiAlias(true);
		PAINT_ERASED_INTERSECTED.setColor(ColorSettings.STICK_COLOR_ERASED_INTERSECTED);
		PAINT_ERASED_INTERSECTED.setStrokeWidth(LINE_WIDTH);
	}

	public Stick(int startX, int startY, int endX, int endY) {
		super();
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	/**
	 * 与えられた線分と自身の状態によって、
	 * 状態を遷移します
	 *
	 * @param aX
	 * @param aY
	 * @param bX
	 * @param bY
	 */
	public void update(int aX, int aY, int bX, int bY) {
		if (hasIntersectionWith(aX, aY, bX, bY)) {
			// 交差する
			if (status == StickState.NOT_ERASED) {
				// 色を設定する
				paint = PAINT_INTERSECTED;
			} else if(status == StickState.ERASED) {
				// 色を設定する
				paint = PAINT_ERASED_INTERSECTED;

			}
		} else {
			// 交差しない
			if (status == StickState.NOT_ERASED) {
				// 色を設定する
				paint = PAINT_NOT_ERASED;
			} else if (status == StickState.ERASED) {
				// 色を設定する
				paint = PAINT_ERASED;
			}
		}
	}

	/**
	 * 与えられた線分と交差する場合、
	 * 自身の状態を書き換えます
	 *
	 * @param aX
	 * @param aY
	 * @param bX
	 * @param bY
	 */
	public void erase(int aX, int aY, int bX, int bY) {
		if (hasIntersectionWith(aX, aY, bX, bY)) {
			erase();
		}
	}

	/**
	 * 自身の状態を消去済みに書き換えます
	 */
	public void erase() {
		status = StickState.ERASED;
	}

	/**
	 * 与えられた線分と自身が交差するか返します
	 * @param aX 始点x座標
	 * @param aY 始点y座標
	 * @param bX 終点x座標
	 * @param bY 終点y座標
	 * @return
	 */
	public boolean hasIntersectionWith(int aX, int aY, int bX, int bY) {
		// 誤差範囲
		float epsilon = 0.0001f;

		// 線分は
		// -(bY - aY) * x + (bX - aX) * y = bX * aY - aX * bY
		// と
		// -(endY - startY) * x + (endX - startX) * y = endX * startY - startX * endY

		// {-(bY - aY)      , bX - aX     }{x} = {bX * aY - aX * bY}
		// {-(endY - startY),endX - startX}{y} = {endX * startY - startX * endY}
		// これを解いて、
		// {x} = { endX - startX * (bX * aY - aX * bY) - (bX - aX ) * (endX * startY - startX * endY)} * 1/{-(bY - aY) * (endX - startX)
		// {y}   { endY - startY * (bX * aY - aX * bY) - (bY - aY)  * (endX * startY - startX * endY)}         + (bX - aX) * (endY - startY)}

		float d = -1 * (bY - aY) * (endX - startX) + (bX - aX) * (endY - startY);

		if (Math.abs(d) < epsilon){
			// 線分は平行
			return false;
		}

		// 交差する点
		float x = ((endX - startX) * (bX * aY - aX * bY) - (bX - aX ) * (endX * startY - startX * endY)) / d;
		float y = ((endY - startY) * (bX * aY - aX * bY) - (bY - aY)  * (endX * startY - startX * endY)) / d;

		// 与えられた線分の内部か
		int maxX = Math.max(aX, bX);
		int minX = Math.min(aX, bX);

		if (x < minX - epsilon || maxX + epsilon < x) {
			// 線分の範囲外
			return false;
		}

		maxX = Math.max(startX, endX);
		minX = Math.min(startX, endX);

		if (x < minX - epsilon || maxX + epsilon < x) {
			// 線分の範囲外
			return false;
		}

		int argsMaxY = Math.max(aY, bY);
		int argsMinY = Math.min(aY, bY);

		if (y < argsMinY - epsilon || argsMaxY + epsilon < y) {
			// 線分の範囲外
			return false;
		}

		int thisMaxY = Math.max(startY, endY);
		int thisMinY = Math.min(startY, endY);

		if (y < thisMinY - epsilon || thisMaxY + epsilon < y) {
			// 線分の範囲外
			return false;
		}

		return true;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public StickState getStatus() {
		return status;
	}

	public Paint getPaint() {
		return paint;
	}

	public void reset() {
		status = StickState.NOT_ERASED;
		paint = PAINT_NOT_ERASED;
	}
}
