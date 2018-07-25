package jp.gr.java_conf.yuka.stickeraser.manager;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

import jp.gr.java_conf.yuka.stickeraser.activity.R;
import jp.gr.java_conf.yuka.stickeraser.code.StickEraseResultCode;
import jp.gr.java_conf.yuka.stickeraser.code.StickState;
import jp.gr.java_conf.yuka.stickeraser.model.Line;
import jp.gr.java_conf.yuka.stickeraser.model.Stick;
import jp.gr.java_conf.yuka.stickeraser.model.StickEraseResultModel;
import jp.gr.java_conf.yuka.stickeraser.observer.LineObserver;

public abstract class StickManager implements LineObserver {
	private ArrayList<Stick> sticks;

	private int size;

	public StickManager(int size, int canvasWidth, int canvasHeight) {
		this.size = size;
		sticks = generateSticks(canvasWidth, canvasHeight);
	}

	protected abstract ArrayList<Stick> generateSticks(int canvasWidth, int canvasHeight);

	public void update(int aX, int aY, int bX, int bY) {
		// 各点に通知
		for (Stick stick : sticks) {
			stick.update(aX, aY, bX, bY);
		}

	}

	public StickEraseResultModel eraseStick(int aX, int aY, int bX, int bY) {
		StickEraseResultModel result = new StickEraseResultModel(StickEraseResultCode.SUCCESS);

		// 交差しているStickの集合
		ArrayList<Integer> interStickIds = new ArrayList<Integer>();

		// 交点取得
		for (int i=0; i < sticks.size(); i++) {
			if (sticks.get(i).hasIntersectionWith(aX, aY, bX, bY)) {
				interStickIds.add(i);
			}
		}

		// 0点交差
		if (interStickIds.size() <= 0) {
			result.setCode(StickEraseResultCode.ZERO_INTERSECTION);
//			result.setMessageId(R.string.stickEraseResult_ZERO_INTERSECTION);
			return result;
		}

		// 同じ段か
		if (! belongToSameRow(interStickIds)) {
			result.setCode(StickEraseResultCode.ACROSS_ROW);
			result.setMessageId(R.string.stickEraseResult_ACROSS_ROW);
			return result;
		}

		// すでに消されたstickに交差してないか
		for (int i : interStickIds) {
			if (sticks.get(i).getStatus() == StickState.ERASED) {
				result.setCode(StickEraseResultCode.INTERSECT_ERASED_STICK);
				result.setMessageId(R.string.stickEraseResut_INTERSECT_ERASED_STICK);
				return result;
			}
		}

		// 4点以上で交差
		if (interStickIds.size() >= 4) {
			result.setCode(StickEraseResultCode.OVER_INTERSECTION);
			result.setMessageId(R.string.stickEraseResult_OVER_INTERSECTION);
			return result;
		}

		// 隣り合っているか
		if (! isNeighborSticks(interStickIds)) {
			result.setCode(StickEraseResultCode.NOT_NEIGHBOR_STICK);
			result.setMessageId(R.string.stickEraseResult_NOT_NEIGHBOR_STICK);
			return result;
		}

		// 消す
		for (int i : interStickIds) {
			sticks.get(i).erase();
		}
		result.setCode(StickEraseResultCode.SUCCESS);
		return result;
	}

	/**
	 * Id が属する段数を返します
	 * @param i stickId
	 * @return stickが属する段数
	 */
	protected abstract int getRow(int i);

	/**
	 * 与えられた棒のIDがすべて同じ段に属するかどうかを返します
	 * @param stickIds 棒のIDのリスト
	 * @return 与えられた棒のIDがすべて同じ段に属する場合にtrue
	 */
	protected abstract boolean belongToSameRow(ArrayList<Integer> stickIds);

	/**
	 * 与えられたidのstickがすべて隣り合っているかどうか返します。stickIdはソートされている必要があります。
	 * @param stickIds stickIdのList
	 * @return 与えられたidのstick がすべて隣り合っているときにtrue
	 */
	protected abstract boolean isNeighborSticks(ArrayList<Integer> stickIds);

	/**
	 * 画面サイズを変更します
	 * @param width 画面横幅
	 * @param height 画面縦幅
	 */
	public abstract void resize(int width, int height);


	/**
	 * すべての棒が消されているかどうか返します
	 * @return すべての棒が消されているときにtrue
	 */
	public boolean isAllErased() {
		for (Stick stick : sticks) {
			if (stick.getStatus() != StickState.ERASED) {
				return false;
			}
		}
		return true;
	}

	public int getNumOfNotErasedSticks() {
		int count = 0;
		for (Stick stick : sticks) {
			if (stick.getStatus() == StickState.NOT_ERASED) {
				count++;
			}
		}
		return count;
	}

	public int getSize() {
		return size;
	}

	public Stick getStick(int id) {
		return sticks.get(id);
	}

	public void reset() {
		for (Stick stick : sticks) {
			stick.reset();
		}
	}

	/**
	 * 削除可能なBlockの一覧を返します
	 * @return
	 */
	public abstract List<Block> getBlockList();

	/**
	 * Blockを削除するために引く線の座標を返します
	 * @param block
	 * @return
	 */
	public abstract Line getEraseLine(Block block);

	public void drawSticks(Canvas canvas) {
		for (Stick stick : sticks) {
			canvas.drawLine(stick.getStartX(), stick.getStartY(),
					stick.getEndX(), stick.getEndY(), stick.getPaint());
		}
	}

	public static class Block {
		// 始点
		private int startStickId;
		// 本数
		private int joinNum;

		public Block(int startStickId, int joinNum) {
			super();
			this.startStickId = startStickId;
			this.joinNum = joinNum;
		}

		public int getStartStickId() {
			return startStickId;
		}
		public void setStartStickId(int stickId) {
			this.startStickId = stickId;
		}
		public int getJoinNum() {
			return joinNum;
		}
		public void setJoinNum(int stickNum) {
			this.joinNum = stickNum;
		}

	}

}
