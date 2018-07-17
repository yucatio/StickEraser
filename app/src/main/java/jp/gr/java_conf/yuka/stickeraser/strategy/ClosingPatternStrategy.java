package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.List;
import java.util.Random;

import android.util.Log;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import jp.gr.java_conf.yuka.stickeraser.util.ArrayUtil;

import static jp.gr.java_conf.yuka.stickeraser.strategy.Tactics.*;

/**
 * 1本の塊のみにして勝てるパターンを探索するStrategy
 * @author Yuka
 *
 */
public class ClosingPatternStrategy implements EraseStrategy {

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

		// ランダムに線を引く
		return getRandomBlock(blockList);
	}


}
