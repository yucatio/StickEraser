package jp.gr.java_conf.yuka.stickeraser.code;

import jp.gr.java_conf.yuka.stickeraser.activity.R;

public class StickEraserConstants {
	public static final int TOP_TURN = 0;
	public static final int BOTTOM_TURN = 1;

	// MAX勝利数
	public static final int WIN_COUNT_MAX = 999;

	// レベル
	public static final int VS_COM_LEVEl_1 = 0;
	public static final int VS_COM_LEVEl_2 = 1;
	public static final int VS_COM_LEVEl_3 = 2;
	public static final int VS_COM_LEVEl_4 = 3;
	public static final int VS_HUMAN = 99;

	public static int getLevelImageId(int level) {
		switch (level) {
		case VS_COM_LEVEl_1:
			return R.drawable.level_1;
		case VS_COM_LEVEl_2:
			return R.drawable.level_2;
		case VS_COM_LEVEl_3:
			return R.drawable.level_3;
		case VS_COM_LEVEl_4:
			return R.drawable.level_4;
		case VS_HUMAN:
			return R.drawable.vs_human;
		default:
			throw new IllegalArgumentException("存在しないlevelです");
		}
	}
}