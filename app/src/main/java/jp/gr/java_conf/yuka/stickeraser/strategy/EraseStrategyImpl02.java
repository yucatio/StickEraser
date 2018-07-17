package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import android.util.Log;

import static jp.gr.java_conf.yuka.stickeraser.strategy.Tactics.*;

/**
 * 中級用Strategy
 * @author Yuka
 *
 */
public class EraseStrategyImpl02 implements EraseStrategy {

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
		block = getBlockOfEvenErasePattern(blockList, blockSummary);
		if (block != null) {
			return block;
		}

		// ランダムに線を引く
		return getRandomBlock(blockList);
	}



}
