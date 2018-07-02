package jp.gr.java_conf.yuka.stickeraser.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help);

		// 戻るボタン
		Button backButton = (Button) this.findViewById(R.id.back);

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

}
