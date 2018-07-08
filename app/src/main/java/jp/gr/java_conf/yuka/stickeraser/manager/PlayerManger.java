package jp.gr.java_conf.yuka.stickeraser.manager;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.model.Player;

public class PlayerManger {
	private List<Player> players = new ArrayList<Player>();

	/** 現在のプレーヤ */
	private int nowPlayerIndex = -1;

	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * 次のプレイヤーを返します
	 * @return
	 */
	public Player getNextPlayer() {
		nowPlayerIndex = (nowPlayerIndex + 1) % players.size();
		return players.get(nowPlayerIndex);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getNowPlayer() {
		return players.get(nowPlayerIndex);
	}

	public void reset() {
		nowPlayerIndex = -1;
	}
}
