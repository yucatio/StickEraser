package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import android.util.Log;

/**
 * 上級用Strategy
 * @author Yuka
 *
 */
public class EraseStrategyImpl03 extends EraseStrategyImpl02 implements EraseStrategy {

	public EraseStrategyImpl03() {
	}

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

		// 1手先でclosingできる or 偶数パターンに持ち込めるか

		//
		int[] randomAccessArray = getRandomAccessIntArray(blockSummary.length);

		// 次の手評価
		int currentScore = 0;

		// forすべての手に対して
		for (int i = 0; i< blockSummary.length; i++) {
			int targetStickNum = randomAccessArray[i] + 1;
			if (blockSummary[targetStickNum -1] == 0) {
				continue;
			}

			// numStickがiであるblockをランダムに探す
			Block block = getTargetBlock(targetStickNum, blockList);
			for (int offset = 0; offset < (block.getJoinNum() - 1) / 2; offset++) {
				int maxStickNum = Math.min(3, block.getJoinNum() - offset);
				for (int stickNum = 1; stickNum <= maxStickNum; stickNum++) {
					int tmpScore = 0;

					ErasePlace tmpErasePlace = new ErasePlace(block.getJoinNum(), offset, stickNum);
					Log.v("blockSummary", "tmpErasePlace" + tmpErasePlace);

					// 消した時のblockSummary
					int[] newBlockSummary = getNewBlockSummary(blockSummary, tmpErasePlace);
					Log.v("blockSummary", "oldBlockSummary" + Arrays.toString(blockSummary));
					Log.v("blockSummary", "newBlockSummary" + Arrays.toString(newBlockSummary));

					// 消した時のoddBlocks
					List<Integer> newOddBlocks = getNewOddBlocks(oddBlocks, tmpErasePlace);
					Log.v("blockSummary", "oldOddBlocks" + oddBlocks.toString());
					Log.v("blockSummary", "newOddBlocks" + newOddBlocks.toString());

					if (getClosingErasePattern(newBlockSummary) == null) {
						// 次でclosingできない
						tmpScore += 4;
					}

					if (getEvenErasePattern(newOddBlocks) == null) {
						// 偶数パターンに持ち込めない
						tmpScore += 2;
					}

					if (getWellKnownErasePattern(newBlockSummary, newOddBlocks) == null) {
						tmpScore += 1;
					}

					if (tmpScore >= currentScore) {
						if (tmpScore == currentScore && random.nextInt(2) < 1) {
							continue;
						}

						currentScore = tmpScore;
						// 対称性を考慮してrandomにする
						if (random.nextInt(2) < 1) {
							erasePlace = tmpErasePlace;
						} else {
							int newOffset = tmpErasePlace.getTargetBlockNum() - tmpErasePlace.getNumStick() - tmpErasePlace.getOffset();
							erasePlace =  new ErasePlace(tmpErasePlace.getTargetBlockNum(), newOffset,  tmpErasePlace.getNumStick());
						}
					}
				}
			}
		}

		if (erasePlace != null) {
			Log.d("getEraseSticks", "next pattern matched.[erasePlace=" + erasePlace.toString() + "]");

			Block block = getBlock(erasePlace, blockList);

			return block;
		}

		// ランダムに線を引く
		Log.d("getEraseSticks", "no pattern matched.");
		// Blockの選択
		Block targetBlock = blockList.get(random.nextInt(blockList.size()));

		// 始点の選択
		int start = random.nextInt(targetBlock.getJoinNum());

		// 終点の選択
		int numStick = Math.min((targetBlock.getJoinNum() - start), random.nextInt(3) + 1);

		Block block = new Block((targetBlock.getStartStickId() + start), numStick);

		return block;
	}

	private Block getTargetBlock(int numSticks, List<Block> blockList) {
		int[] randomAccessArray = getRandomAccessIntArray(blockList.size());

		Block targetBlock = null;
		for (int i = 0; i< blockList.size(); i++) {
			Block block = blockList.get(randomAccessArray[i]);
			if (numSticks == block.getJoinNum()) {
				targetBlock = block;
				break;
			}
		}

		return targetBlock;
	}

	/**
	 * erasePlaceで削除したときの新しいblockSummeryを返します
	 * @param blockSummary 古いblockSummary
	 * @param erasePlace
	 * @return erasePlaceで削除したときのblockSummery
	 */
	private int[] getNewBlockSummary(int[] blockSummary,
			ErasePlace erasePlace) {
		// 古い配列をコピー
		int[] newBlockSummary = Arrays.copyOf(blockSummary, blockSummary.length);

		// 削除した後の左右の数
		int rightNum = erasePlace.getOffset();
		int leftNum = erasePlace.getTargetBlockNum() - erasePlace.getOffset() - erasePlace.getNumStick();

		//更新
		newBlockSummary[erasePlace.getTargetBlockNum() - 1]--;

		if (rightNum > 0) {
			newBlockSummary[rightNum - 1]++;
		}
		if (leftNum > 0) {
			newBlockSummary[leftNum - 1]++;
		}
		return newBlockSummary;
	}

	/**
	 * erasePlaceで削除したときの新しいoddBlocksを返します
	 * @param oddBlocks 古いoddBlocks
	 * @param erasePlace
	 * @return erasePlaceで削除したときのoddBlocks
	 */
	private List<Integer> getNewOddBlocks(List<Integer> oddBlocks,
			ErasePlace erasePlace) {
		// 古いListをコピー
		List<Integer> newOddBlocks = new ArrayList<Integer>(oddBlocks);

		// 削除した後の左右の数
		Integer rightNum = erasePlace.getOffset();
		Integer leftNum = erasePlace.getTargetBlockNum() - erasePlace.getOffset() - erasePlace.getNumStick();

		if (newOddBlocks.contains(erasePlace.getTargetBlockNum())) {
			newOddBlocks.remove(Integer.valueOf(erasePlace.getTargetBlockNum()));
		} else {
			newOddBlocks.add(Integer.valueOf(erasePlace.getTargetBlockNum()));
		}

		if (rightNum > 0) {
			if (newOddBlocks.contains(rightNum)) {
				newOddBlocks.remove(rightNum);
			} else {
				newOddBlocks.add(rightNum);
			}
		}
		if (leftNum > 0) {
			if (newOddBlocks.contains(leftNum)) {
				newOddBlocks.remove(leftNum);
			} else {
				newOddBlocks.add(leftNum);
			}
		}

		return newOddBlocks;
	}

	private ErasePlace getWellKnownErasePattern(int[] blockSummary,
			List<Integer> oddBlocks) {

		// 1-2-3パターン
		if (blockSummary[0] == 0 ||  blockSummary[1] == 0 || blockSummary[2] == 0) {
			return null;
		}

		// oddBlocksをコピー
		List<Integer> tmpOddBlocks = new ArrayList<Integer>(oddBlocks);

		if (tmpOddBlocks.contains(Integer.valueOf(1))) {
			tmpOddBlocks.remove(Integer.valueOf(1));
		} else {
			tmpOddBlocks.add(Integer.valueOf(1));
		}

		if (tmpOddBlocks.contains(Integer.valueOf(2))) {
			tmpOddBlocks.remove(Integer.valueOf(2));
		} else {
			tmpOddBlocks.add(Integer.valueOf(2));
		}

		if (tmpOddBlocks.contains(Integer.valueOf(3))) {
			tmpOddBlocks.remove(Integer.valueOf(3));
		} else {
			tmpOddBlocks.add(Integer.valueOf(3));
		}

		ErasePlace erasePlace = getEvenErasePattern(tmpOddBlocks);

		if (erasePlace != null && blockSummary[erasePlace.getTargetBlockNum() - 1] != 0) {
			return erasePlace;
		}

		return null;
	}

}
