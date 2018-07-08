package jp.gr.java_conf.yuka.stickeraser.manager;

import java.util.ArrayList;

import jp.gr.java_conf.yuka.stickeraser.code.ColorSettings;
import jp.gr.java_conf.yuka.stickeraser.model.Line;
import android.graphics.Canvas;
import android.graphics.Point;

public class LineManager {

	/** すでに描画された線を格納するリスト */
	private ArrayList<Line> lines = new ArrayList<Line>();

	/** 現在操作中の線 */
	private Line activeLine;

	public void newActiveLine(int startX, int startY, int endX, int endY, int color) {
		Point start = new Point(startX, startY);
		Point end = new Point(endX, endY);

		activeLine = new Line(start, end, color);

	}

	public void updateActiveLine(int startX, int startY, int endX, int endY) {
		if (activeLine == null) {
			return;
		}

		activeLine.getStart().set(startX, startY);
		activeLine.getEnd().set(endX, endY);
	}

	public void clearActiveLine() {
		activeLine = null;
	}

	/**
	 * 線の確定を行う
	 */
	public void settleActiveLine() {
		int newColor = ColorSettings.darken(activeLine.getPaint().getColor());
		activeLine.getPaint().setColor(newColor);
		add(activeLine);
	}

	private void add(Line line) {
		lines.add(line);
	}

	public void reset() {
		activeLine = null;
		lines.clear();
	}

	public void drawLines(Canvas canvas) {
		for (Line line : lines) {
			canvas.drawLine(line.getStart().x, line.getStart().y, line.getEnd().x,
					line.getEnd().y, line.getPaint());
		}

		if (activeLine != null) {
			canvas.drawLine(activeLine.getStart().x, activeLine.getStart().y, activeLine.getEnd().x,
					activeLine.getEnd().y, activeLine.getPaint());
		}
	}


}
