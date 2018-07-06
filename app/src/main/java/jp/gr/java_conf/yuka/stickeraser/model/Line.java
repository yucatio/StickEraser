package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

import jp.gr.java_conf.yuka.stickeraser.code.ColorSettings;
import android.graphics.Paint;
import android.graphics.Point;

public class Line implements Serializable {
	private static final long serialVersionUID = 631969388434111060L;

	private static final int DEFAULT_LINE_WIDTH = 5;

	private Point start;
	private Point end;

	private Paint paint;

	public Line(Point start, Point end) {
		this(start, end, ColorSettings.LINE_COLOR_DEFAULT);
	}

	public Line(Point start, Point end, int color) {
		this.start = start;
		this.end = end;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(DEFAULT_LINE_WIDTH);

		this.paint = paint;
	}

	public Line(Point start, Point end, Paint paint) {
		super();
		this.start = start;
		this.end = end;
		this.paint = paint;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

}
