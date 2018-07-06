package jp.gr.java_conf.yuka.stickeraser.adapter;

import java.util.List;

import jp.gr.java_conf.yuka.stickeraser.activity.R;
import jp.gr.java_conf.yuka.stickeraser.activity.StageSelectActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StageListRowAdapter extends ArrayAdapter<StageSelectActivity.StageListRow> {
	private int resourceId;
	private List<StageSelectActivity.StageListRow> items;
	private LayoutInflater inflater;
	OnClickListener listener;

	public StageListRowAdapter(Context context, int resourceId, List<StageSelectActivity.StageListRow> items) {
		this(context, resourceId, items, null);
	}

	public StageListRowAdapter(Context context, int resourceId, List<StageSelectActivity.StageListRow> items, OnClickListener listener) {
		super(context, resourceId, items);
		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = inflater.inflate(resourceId, null);
		}

		StageSelectActivity.StageListRow stageRow = items.get(position);

		// stageの名前をセット
		TextView stageString = (TextView)view.findViewById(R.id.stageString);
		stageString.setText(stageRow.getStageName());

		// 勝利数をセット
		TextView winCount = (TextView)view.findViewById(R.id.winCount);
		winCount.setText(String.valueOf(stageRow.getWinCount()));

		return view;

	}
}
