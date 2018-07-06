package jp.gr.java_conf.yuka.stickeraser.model;

public class ErasePlace {
	private int targetBlockNum;
	private int offset;
	private int numStick;

	public ErasePlace(int targetBlockNum, int offset, int numStick) {
		super();
		this.targetBlockNum = targetBlockNum;
		this.offset = offset;
		this.numStick = numStick;
	}

	public int getTargetBlockNum() {
		return targetBlockNum;
	}

	public int getOffset() {
		return offset;
	}

	public int getNumStick() {
		return numStick;
	}

	@Override
	public String toString() {
		return "ErasePlace [targetBlockNum=" + targetBlockNum + ", offset="
				+ offset + ", numStick=" + numStick + "]";
	}

}
