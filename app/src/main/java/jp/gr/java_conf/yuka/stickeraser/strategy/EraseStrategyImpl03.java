package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import jp.gr.java_conf.yuka.stickeraser.util.ArrayUtil;

import android.util.Log;

import static jp.gr.java_conf.yuka.stickeraser.strategy.Tactics.*;

/**
 * 上級用Strategy
 * @author Yuka
 *
 */
public class EraseStrategyImpl03 implements EraseStrategy {

	public EraseStrategyImpl03() {
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
		block = getBlockOfEvenErasePattern(blockList, blockSummary);
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
