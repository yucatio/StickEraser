package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

/**
 * game_count テーブルに対応するモデルです
 * @author Yuka
 *
 */
public class GameCountModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private int level;
	private int stage;
	private int winCount;

	public GameCountModel(int level, int stage, int winCount) {
		super();
		this.level = level;
		this.stage = stage;
		this.winCount = winCount;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public int getWinCount() {
		return winCount;
	}
	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

}
