package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

public class GameInfoModel implements Serializable {
	private int level;
	private int stage;

	public GameInfoModel(int level, int stage) {
		super();
		this.level = level;
		this.stage = stage;
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


}
