package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import android.util.Log;

/**
 * 中級用Strategy
 * @author Yuka
 *
 */
public class EraseStrategyImpl02 extends EraseStrategyImpl01 implements
		EraseStrategy {

	@Override
	public Block getEraseSticks(StickManager stickManager) {
		// Block情報
		List<StickManager.Block> blockList = stickManager.getBlockList();

		Random random = new Random();

		// サマリ情報
		// 最大のjoin数
		int maxJoin = 0;
		for (Block block : blockList) {
			if (block.getJoinNum() > maxJoin) {
				maxJoin = block.getJoinNum();
			}
		}

		int[] blockSummary = new int[maxJoin];
		for (Block block : blockList) {
			blockSummary[block.getJoinNum()-1]++;
		}

		// closingできるか
		ErasePlace erasePlace = getClosingErasePattern(blockSummary);

		if (erasePlace != null) {
			Log.d("getEraseSticks", "closing pattern matched.[erasePlace=" + erasePlace.toString() + "]");

			Block block = getBlock(erasePlace, blockList);

			return block;
		}

		// サマリ情報2
		// 本数が同じブロックが奇数個の、本数
		List<Integer> oddBlocks = new ArrayList<Integer>();

		for (int i=0; i < blockSummary.length; i++) {
			if (blockSummary[i] % 2 == 1) {
				// 奇数ブロックの場合の本数
				oddBlocks.add(i+1);
			}
		}

		erasePlace = getEvenErasePattern(oddBlocks);

		if (erasePlace != null) {
			Log.d("getEraseSticks", "even pattern matched.[erasePlace=" + erasePlace.toString() + "]");

			Block block = getBlock(erasePlace, blockList);

			return block;
		}

		// ランダムに線を引く
		// Blockの選択
		Block targetBlock = blockList.get(random.nextInt(blockList.size()));

		// 始点の選択
		int start = random.nextInt(targetBlock.getJoinNum());

		// 終点の選択
		int numStick = Math.min((targetBlock.getJoinNum() - start), random.nextInt(3) + 1);

		Block block = new Block((targetBlock.getStartStickId() + start), numStick);

		return block;

	}

	/**
	 * oddBlocksをなくすようなerasePatternを返します
	 * @param oddBlocks
	 * @return oddBlocksをなくすことができる場合、oddBlocksをなくすようなerasePattern。なくすことができない場合はnull
	 */
	protected ErasePlace getEvenErasePattern(List<Integer> oddBlocks) {
		if (oddBlocks.size() > 3) {
			// 奇数の個数が4つ以上のときはoddBlockにできない
			return null;
		}

		Random random = new Random();

		// 本数が同じブロックをすべて偶数個にできるか
		if (oddBlocks.size() == 1) {
			int joinNum = oddBlocks.get(0);
			if(joinNum % 2 == 1) {
				// blockに含まれる本数が奇数
				if (joinNum == 1 || random.nextInt(2) < 1) {
					// 1本消す
					return new ErasePlace(joinNum, joinNum/2, 1);
				} else {
					// 3本消す
					return new ErasePlace(joinNum, joinNum/2 - 1, 3);
				}

			} else {
				// blockに含まれる本数が偶数
				// 2本消す
				return new ErasePlace(joinNum, joinNum/2 - 1, 2);
			}

		} else if (oddBlocks.size() == 2) {
			int max = Math.max(oddBlocks.get(0), oddBlocks.get(1));
			int min = Math.min(oddBlocks.get(0), oddBlocks.get(1));
			int diff = max - min;

			if (diff <= 3) {
				if (random.nextInt(2) < 1) {
					return new ErasePlace(max, 0, diff);
				} else {
					return new ErasePlace(max, max - diff, diff);
				}
			}

		} else if (oddBlocks.size() == 3) {
			// 整列
			Collections.sort(oddBlocks);

			int min1 = oddBlocks.get(0);
			int min2 = oddBlocks.get(1);
			int max = oddBlocks.get(2);

			int diff = max - (min1 + min2);

			if (1 <= diff && diff <= 3) {
				if (random.nextInt(2) < 1) {
					// min1 + diff + min2 に分割
					return new ErasePlace(max, min1, diff);
				} else {
					// min2 + diff + min1 に分割
					return new ErasePlace(max, min2, diff);
				}
			}

		}

		return null;
	}


}
