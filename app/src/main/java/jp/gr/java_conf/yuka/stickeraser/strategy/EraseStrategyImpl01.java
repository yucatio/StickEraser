package jp.gr.java_conf.yuka.stickeraser.strategy;

import java.util.List;
import java.util.Random;

import android.util.Log;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;

/**
 * 初級用Strategy
 * @author Yuka
 *
 */
public class EraseStrategyImpl01 implements EraseStrategy {

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

	protected Block getBlock(ErasePlace erasePlace, List<Block> blockList) {
		int[] randomAccessArray = getRandomAccessIntArray(blockList.size());

		Block targetBlock = null;
		for (int i = 0; i< blockList.size(); i++) {
			Block block = blockList.get(randomAccessArray[i]);
			if (erasePlace.getTargetBlockNum() == block.getJoinNum()) {
				targetBlock = block;
				break;
			}
		}

		Block block = new Block((targetBlock.getStartStickId() + erasePlace.getOffset()), erasePlace.getNumStick());
		return block;
	}

	/**
	 * closingできる場合のerasePatternを返します
	 * @param blockSummary
	 * @return closingできる場合、erasePattern。できない場合null
	 */
	protected ErasePlace getClosingErasePattern(int[] blockSummary) {
		// 1個しかない場合
		if (blockSummary.length == 1) {
			return new ErasePlace(1, 0, 1);
		}

		// 5より多い場合はreturn
		if  (blockSummary.length > 5) {
			return null;
		}

		// blockSummary[blockSummary.length - 1] が 1 でない場合はreturn
		if (blockSummary[blockSummary.length - 1] != 1) {
			return null;
		}

		// 2以上で棒が存在する最小のindexを探す
		int minIdx = 1;
		while (blockSummary[minIdx] == 0) {
			minIdx ++;
		}

		// 最小 + 1 == blockSummary.length でない場合はreturn
		if (minIdx + 1 != blockSummary.length) {
			return null ;
		}

		Random random = new Random();

		if (blockSummary[0] % 2 == 1) {
			// 1の数が奇数
			// 0または2本残して消す

			if (blockSummary.length == 5 || blockSummary.length == 4) {
				// 5か4の場合は2本残し
				return new ErasePlace(blockSummary.length, 1, blockSummary.length-2);
			} else if (blockSummary.length == 3) {
				// 3の場合は0または2本残し
				if (random.nextInt(2) < 1) {
					return new ErasePlace(blockSummary.length, 1, blockSummary.length-2);
				} else {
					return new ErasePlace(blockSummary.length, 0, blockSummary.length);
				}
			} else {
				// 2の場合は全部消し
				return new ErasePlace(blockSummary.length, 0, blockSummary.length);
			}
		} else {
			// 1の数が偶数

			// 5の場合は不可
			if (blockSummary.length == 5) {
				return null;
			}

			// 1本残して消す
			if (random.nextInt(2) < 1) {
				return new ErasePlace(blockSummary.length, 0, blockSummary.length-1);
			} else {
				return new ErasePlace(blockSummary.length, 1, blockSummary.length-1);
			}
		}

	}


	/**
	 * 0からsizeまでが格納された配列を返します。値の順番はランダムです
	 * @param size 配列サイズ
	 * @return 値の順番はランダムな、0からsizeまでが格納された配列
	 */
	protected int[] getRandomAccessIntArray(int size) {
		int[] array = new int[size];

		// 初期化
		for (int i = 0; i < size; i++) {
			array[i] = i;
		}

		// 入れ替え
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			int randomInt = random.nextInt(size);
			int tmp = array[i];
			array[i] = array[randomInt];
			array[randomInt] = tmp;
		}

		return array;
	}
}
