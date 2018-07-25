package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;

import android.util.Log;

import static jp.gr.java_conf.yuka.stickeraser.strategy.Tactics.*;

/**
 * 相手の1手先まで読むStrategy
 * @author Yuka
 *
 */
public class ReadOpponentStrategy implements EraseStrategy {

	public ReadOpponentStrategy() {
	}

	public Block getEraseSticks(StickManager stickManager) {
		// Block情報
		List<StickManager.Block> blockList = stickManager.getBlockList();

		int[] blockSummary = getBlockSummary(blockList);

		// closingできるか
		Block block = getBlockOfClosingErasePattern(blockList, blockSummary);
		if (block != null) {
			return block;
		}

		// 偶数パターンに持ち込めるか
		block = getBlockOfEvenPattern(blockList, blockSummary);
		if (block != null) {
			return block;
		}

		// 相手がでclosingできない、 偶数パターンに持ち込めない消し方を見つける
		block = getBlockOfAvoidingOpponentWinPattern(blockList, blockSummary);
		if (block != null) {
			return block;
		}

		// ランダムに線を引く
		Log.d("getEraseSticks", "no pattern matched.");
		return getRandomBlock(blockList);
	}




}
