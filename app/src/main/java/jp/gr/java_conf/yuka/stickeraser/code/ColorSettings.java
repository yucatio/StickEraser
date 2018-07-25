package jp.gr.java_conf.yuka.stickeraser.code;

import android.graphics.Color;

public class ColorSettings {
	public static final int LINE_COLOR_DEFAULT = Color.GRAY;
	public static final int LINE_COLOR_PLAYER1 = Color.BLUE;
	public static final int LINE_COLOR_PLAYER2 = Color.GREEN;

	public static final int STICK_COLOR_NOT_ERASED  = Color.BLACK;
	public static final int STICK_COLOR_ERASED      = Color.GRAY;
	public static final int STICK_COLOR_INTERSECTED = Color.RED;
	public static final int STICK_COLOR_ERASED_INTERSECTED = 0xff7f6069;

	/**
	 * 暗くなった色を返す
	 * (とりあえず必要なものだけ)
	 * @param color
	 * @return
	 */
	public static int darken(int color) {
		switch (color) {
		case Color.BLUE:
			return 0xff000099;
		case Color.GREEN:
			return 0xff009900;
		default:
			return color;
		}
	}
}
