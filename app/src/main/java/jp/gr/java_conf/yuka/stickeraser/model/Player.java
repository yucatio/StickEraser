package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

import jp.gr.java_conf.yuka.stickeraser.code.ColorSettings;
import android.content.Context;

public abstract class Player implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int lineColor;

	public Player(String name) {
		this(name, ColorSettings.LINE_COLOR_DEFAULT);
	}

	public Player(String name, int lineColor) {
		super();
		this.name = name;
		this.lineColor = lineColor;
	}

	public String getName() {
		return name;
	}


	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * ゲームに勝った時に呼び出されるメソッド
	 * @param gameInfo ゲーム情報
	 */
	public abstract void gameWin(Context context, GameInfoModel gameInfo);

}
