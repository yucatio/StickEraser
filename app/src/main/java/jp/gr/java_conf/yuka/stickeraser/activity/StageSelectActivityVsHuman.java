package jp.gr.java_conf.yuka.stickeraser.activity;

import java.text.MessageFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class StageSelectActivityVsHuman extends Activity {
	private AdView mAdView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stage_select_vs_human);

		// Ad
		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
			}
		});
		mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		// ListView
		ListView stageListView = (ListView) findViewById(R.id.stageList);

		// ヘッダの追加
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View countView = inflater.inflate(R.layout.stage_row_header_simple, null);
		stageListView.addHeaderView(countView);

		// 行の追加
		String[] stageArray = getResources().getStringArray(R.array.stage);
		ArrayAdapter<String> stageListAdapter = new ArrayAdapter<String>(this,R.layout.stage_row_vs_human, stageArray);
		stageListView.setAdapter(stageListAdapter);

		stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {

				// 入力チェック
				if (! validate()) {
					return;
				}

				if (position <= 0) {
					return;
				}

				Intent stickIntent = new Intent(StageSelectActivityVsHuman.this, StickEraserActivity.class);
				// ステージを受け渡す
				stickIntent.putExtra("stage", position-1);

				// palyer1の名前を受け渡す
				EditText player1NameEdit = (EditText) findViewById(R.id.player1Name);
				String player1Name = player1NameEdit.getText().toString();
				stickIntent.putExtra("player1Name", player1Name);

				// player2の名前を受け渡す
				EditText player2NameEdit = (EditText) findViewById(R.id.player2Name);
				String player2Name = player2NameEdit.getText().toString();
				stickIntent.putExtra("player2Name", player2Name);

				// レベルを受け渡す
				Intent menuIntent = getIntent();
				int level = menuIntent.getIntExtra("level", 0);
				stickIntent.putExtra("level", level);

				startActivity(stickIntent);
			}

			private boolean validate() {
				// player1
				EditText player1NameEdit = (EditText) findViewById(R.id.player1Name);
				String player1Name = player1NameEdit.getText().toString();

				if ("".equals(player1Name)) {
					Toast.makeText(StageSelectActivityVsHuman.this, MessageFormat.format(getString(R.string.ERR_MSG_ZERO_LENGTH), new Object[]{getString(R.string.topTurn)}), Toast.LENGTH_SHORT).show();
					return false;
				}

				// player2
				EditText player2NameEdit = (EditText) findViewById(R.id.player2Name);
				String player2Name = player2NameEdit.getText().toString();

				if ("".equals(player2Name)) {
					Toast.makeText(StageSelectActivityVsHuman.this, MessageFormat.format(getString(R.string.ERR_MSG_ZERO_LENGTH), new Object[]{getString(R.string.bottomTurn)}), Toast.LENGTH_SHORT).show();
					return false;
				}

				// 相関チェック
				if (player1Name.equals(player2Name)) {
					Toast.makeText(StageSelectActivityVsHuman.this, MessageFormat.format( getString(R.string.ERR_MSG_SAME_NAME), new Object[]{getString(R.string.topTurn), getString(R.string.bottomTurn)}), Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			}
		});

		// 戻るボタン
		Button backButton = (Button) this.findViewById(R.id.back);

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}
}
