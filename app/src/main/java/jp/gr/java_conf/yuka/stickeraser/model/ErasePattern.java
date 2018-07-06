package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

public class ErasePattern implements Serializable, Comparable<ErasePattern> {
	private static final long serialVersionUID = 61959703771575617L;

	private int targetBlockNum;
	private int offset;
	private int numStick;

	public ErasePattern(int targetBlockNum, int offset, int numStick) {
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
		return "ErasePattern [targetBlockNum=" + targetBlockNum + ", offset="
				+ offset + ", numStick=" + numStick + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numStick;
		result = prime * result + offset;
		result = prime * result + targetBlockNum;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErasePattern other = (ErasePattern) obj;
		if (numStick != other.numStick)
			return false;
		if (offset != other.offset)
			return false;
		if (targetBlockNum != other.targetBlockNum)
			return false;
		return true;
	}

	public int compareTo(ErasePattern another) {
		if (another == null) {
			return 1;
		}

		if(targetBlockNum != another.targetBlockNum) {
			return targetBlockNum - another.targetBlockNum;
		}

		if (offset != another.offset) {
			return offset - another.offset;
		}

		if (numStick != another.numStick) {
			return numStick - another.numStick;
		}

		return 0;
	}

}
