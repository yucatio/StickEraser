package jp.gr.java_conf.yuka.stickeraser.manager;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.code.StickState;
import jp.gr.java_conf.yuka.stickeraser.model.Line;
import jp.gr.java_conf.yuka.stickeraser.model.Stick;
import android.graphics.Point;

/**
 * ピラミッド型に棒を配置するクラスです
 * @author Yuka
 *
 */
public class PyramidalStickManager extends StickManager {
	private static final float MARGIN_RETIO = 0.05f;
	private static final float LINE_MARGIN_RETIO = 0.1f;

	private static final int INDEX_START_X = 0;
	private static final int INDEX_START_Y = 1;
	private static final int INDEX_END_X = 2;
	private static final int INDEX_END_Y = 3;

	StickAllocator allocator;

	public PyramidalStickManager(int size, int canvasWidth, int canvasHeight) {
		super(size, canvasWidth, canvasHeight);
	}

	@Override
	protected ArrayList<Stick> generateSticks(int canvasWidth, int canvasHeight) {

		// stick数
		int numSticks = getSize() * (getSize() + 1) / 2;

		ArrayList<Stick> sticks = new ArrayList<Stick>(numSticks);

		allocator = new StickAllocator(canvasWidth, canvasHeight);

		for (int i = 0; i < numSticks; i++) {

			int[] position = allocator.getPosition(i);
			sticks.add(i, new Stick(position[INDEX_START_X], position[INDEX_START_Y], position[INDEX_END_X], position[INDEX_END_Y]));
		}

		return sticks;
	}

	@Override
	public void resize(int width, int height) {
		// stick数
		int numSticks = getSize() * (getSize() + 1) / 2;

		allocator.resize(width, height);

		for(int i = 0; i < numSticks; i++) {
			Stick stick = getStick(i);

			int[] position = allocator.getPosition(i);

			stick.setStartX(position[INDEX_START_X]);
			stick.setStartY(position[INDEX_START_Y]);
			stick.setEndX(position[INDEX_END_X]);
			stick.setEndY(position[INDEX_END_Y]);
		}
	}

	@Override
	public int getRow(int id) {
		return  (int)Math.round(Math.floor((-1 + Math.sqrt(1 + 8 * id))/2));
	}

	@Override
	protected boolean belongToSameRow(ArrayList<Integer> stickIds) {
		int firstRow = getRow(stickIds.get(0));
		for (int i = 1; i < stickIds.size(); i++) {
			int row =  getRow(stickIds.get(i));
			if (row != firstRow) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected boolean isNeighborSticks(ArrayList<Integer> stickIds) {
		if (! belongToSameRow(stickIds)) {
			return false;
		}

		for (int i = 1; i < stickIds.size(); i++) {
			// idの差分が1だった場合、隣り合っている
			if (Math.abs(stickIds.get(i) - stickIds.get(i-1)) != 1) {
				return false;
			}
		}

		return true;
	}

	@Override
	public List<Block> getBlockList() {
		List<Block> blockList = new ArrayList<Block>();

		for (int row = 0; row < getSize(); row++) {
			// 連続して空いている数
			int joinCount = 0;

			// 段の初めのindex
			int first = (row * (row + 1))/2;
			for (int i = first + row; i >= first; i--) {
				if (getStick(i).getStatus() == StickState.NOT_ERASED) {
					// まだ消されていない
					joinCount++;
				} else {
					if (joinCount > 0) {
						// ここで連続性が断たれた
						StickManager.Block block = new StickManager.Block(i+1, joinCount);
						blockList.add(block);

						joinCount = 0;
					}
				}
			}
			if (joinCount > 0) {
				// ここで連続性が断たれた
				StickManager.Block block = new StickManager.Block(first, joinCount);
				blockList.add(block);
			}
		}
		return blockList;
	}

	@Override
	public Line getEraseLine(Block block) {
		// 始点棒の中心の座標
		Stick startStick = getStick(block.getStartStickId());
		Point startPoint = new Point((startStick.getStartX() + startStick.getEndX())/2, (startStick.getStartY() + startStick.getEndY())/2);
		startPoint.x -= allocator.getxWidth()/3;
		startPoint.y += Math.abs(startStick.getStartY() - startStick.getEndY()) / 8;

		// 終点棒の中心
		Stick endStick = getStick(block.getStartStickId() + block.getJoinNum() - 1);
		Point endPoint = new Point((endStick.getStartX() + endStick.getEndX())/2, (endStick.getStartY() + endStick.getEndY())/2);
		endPoint.x += allocator.getxWidth()/3;
		endPoint.y -=  Math.abs(endStick.getStartY() - endStick.getEndY()) / 8;

		Line line = new Line(startPoint, endPoint);

		return line;
	}

	private class StickAllocator {
		// margin
		private int margin;
		// 棒と棒の間隔(y軸)
		private int lineMargin;
		// 真ん中
		private int centerX;
		// sick間隔(x軸)
		private int xWidth;
		// 1段の高さ
		private int lineHeight;

		public StickAllocator(int width, int height) {
			resize(width, height);
		}

		public void resize(int width, int height) {
			margin = (int) (width * MARGIN_RETIO);
			lineMargin = (int) (lineHeight * LINE_MARGIN_RETIO);
			centerX = width / 2;
			xWidth = width / getSize();
			lineHeight = (height - margin * 2) / getSize();
		}

		public int[] getPosition(int index) {
			// 段数(1番上の段が第0段)
			int row = getRow(index);
			// row段目の一番初めの数
			int first = (row * (row+1))/2;
			// 中心からどれだけずれているか
			// 第n段目には(n+1)本の棒を立てる
			float distance = (index - first) - row / 2.0f;

			int[] position = new int[4];

			position[INDEX_START_X] = centerX + (int)(xWidth * distance);
			position[INDEX_START_Y] = margin + lineHeight * row;
			position[INDEX_END_X] = centerX + (int)(xWidth * distance);
			position[INDEX_END_Y] = margin + lineHeight * (row + 1) - lineMargin;

			return position;
		}

		public int getMargin() {
			return margin;
		}

		public int getLineMargin() {
			return lineMargin;
		}

		public int getCenterX() {
			return centerX;
		}

		public int getxWidth() {
			return xWidth;
		}

		public int getLineHeight() {
			return lineHeight;
		}
	}

}
