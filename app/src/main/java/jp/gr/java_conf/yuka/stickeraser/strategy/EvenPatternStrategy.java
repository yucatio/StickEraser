package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;

import static jp.gr.java_conf.yuka.stickeraser.strategy.Tactics.*;

/**
 * 偶数パターンを探索するStrategy
 * @author Yuka
 *
 */
public class EvenPatternStrategy implements EraseStrategy {

	@Override
	public Block getEraseSticks(StickManager stickManager) {
		// Block情報
		List<StickManager.Block> blockList = stickManager.getBlockList();

		// サマリ情報
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

		// ランダムに線を引く
		return getRandomBlock(blockList);
	}



}
