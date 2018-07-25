package jp.gr.java_conf.yuka.stickeraser.activity;

import jp.gr.java_conf.yuka.stickeraser.code.StickEraserConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity  extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.menu);

		// 初級ボタン
		Button level1Button = (Button) this.findViewById(R.id.vsComLevel1);
		level1Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stageSelectIntent = new Intent(MenuActivity.this, StageSelectActivity.class);
				stageSelectIntent.putExtra("level", StickEraserConstants.VS_COM_LEVEl_1);
				startActivity(stageSelectIntent);
			}
		});

		// 中級ボタン
		Button level2Button = (Button) this.findViewById(R.id.vsComLevel2);
		level2Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stageSelectIntent = new Intent(MenuActivity.this, StageSelectActivity.class);
				stageSelectIntent.putExtra("level", StickEraserConstants.VS_COM_LEVEl_2);
				startActivity(stageSelectIntent);
			}
		});

		// 上級ボタン
		Button level3Button = (Button) this.findViewById(R.id.vsComLevel3);
		level3Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stageSelectIntent = new Intent(MenuActivity.this, StageSelectActivity.class);
				stageSelectIntent.putExtra("level", StickEraserConstants.VS_COM_LEVEl_3);
				startActivity(stageSelectIntent);
			}
		});

		// 最上級ボタン
		Button level4Button = (Button) this.findViewById(R.id.vsComLevel4);
		level4Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stageSelectIntent = new Intent(MenuActivity.this, StageSelectActivity.class);
				stageSelectIntent.putExtra("level", StickEraserConstants.VS_COM_LEVEl_4);
				startActivity(stageSelectIntent);
			}
		});

		// 2人対戦
		Button vsHumanButton = (Button) this.findViewById(R.id.vsHuman);
		vsHumanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent stageSelectIntent = new Intent(MenuActivity.this, StageSelectActivityVsHuman.class);
				stageSelectIntent.putExtra("level", StickEraserConstants.VS_HUMAN);
				startActivity(stageSelectIntent);
			}
		});

		// ヘルプボタン
		Button helpButton = (Button) this.findViewById(R.id.help);
		helpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent helpIntent = new Intent(MenuActivity.this, HelpActivity.class);
				startActivity(helpIntent);
			}
		});
	}
}
