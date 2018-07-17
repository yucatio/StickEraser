package jp.gr.java_conf.yuka.stickeraser.activity;

import java.text.MessageFormat;

import jp.gr.java_conf.yuka.stickeraser.code.ColorSettings;
import jp.gr.java_conf.yuka.stickeraser.code.GameState;
import jp.gr.java_conf.yuka.stickeraser.code.StickEraseResultCode;
import jp.gr.java_conf.yuka.stickeraser.code.StickEraserConstants;
import jp.gr.java_conf.yuka.stickeraser.db.GameCountDao;
import jp.gr.java_conf.yuka.stickeraser.manager.LineManager;
import jp.gr.java_conf.yuka.stickeraser.manager.PlayerManger;
import jp.gr.java_conf.yuka.stickeraser.manager.PyramidalStickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.model.ComputerPlayer;
import jp.gr.java_conf.yuka.stickeraser.model.GameInfoModel;
import jp.gr.java_conf.yuka.stickeraser.model.HumanPlayer;
import jp.gr.java_conf.yuka.stickeraser.model.Player;
import jp.gr.java_conf.yuka.stickeraser.model.StickEraseResultModel;
import jp.gr.java_conf.yuka.stickeraser.strategy.EraseStrategyImpl01;
import jp.gr.java_conf.yuka.stickeraser.strategy.EraseStrategyImpl02;
import jp.gr.java_conf.yuka.stickeraser.strategy.EraseStrategyImpl03;
import jp.gr.java_conf.yuka.stickeraser.view.StickView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class StickEraserActivity extends Activity {
	private static final long COMPUTER_DELAY_MS = 200;

	private static final int NEW_GAME = 0;
	private static final int RESTART_GAME = 1;

	private static final String TAG = "StickEraserActivity";

	private Handler mHandler = new Handler(new StickEraserHandlerCallback());
	private StickManager stickManager;
	private LineManager lineManager;
	private PlayerManger playerManger;
	private Toast toast;

	private GameState gameState = GameState.OUT_OF_GAME;

	private GameInfoModel gameInfo;

	// 呼び出し中フラグ
	private boolean called = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate called.");

		setContentView(R.layout.main);

		// Stage Id
		Intent stageSelectIntent = getIntent();

		// 盤面サイズ
		int size = 5;

		int stage = stageSelectIntent.getIntExtra("stage", 0);

		// stickManager作成
		if (stage == 0) {
			size = 5;
		} else if (stage == 1) {
			size = 6;
		} else if (stage == 2) {
			size = 7;
		} else if (stage == 3) {
			size = 8;
		} else {
			throw new IllegalArgumentException("stage size not found.");
		}

		// この時点では画面サイズ取れないので、仮の値をセット
		stickManager = new PyramidalStickManager(size, 1, 1);

		// playerManager作成
		playerManger = new PlayerManger();

		// Player作成
		String player1Name = stageSelectIntent.getStringExtra("player1Name");
		if (player1Name == null) {
			player1Name = getString(R.string.you);
		}

		Player player1 = new HumanPlayer(player1Name, ColorSettings.LINE_COLOR_PLAYER1);
		Player player2 = null;

		int level = stageSelectIntent.getIntExtra("level", StickEraserConstants.VS_COM_LEVEl_1);

		if (level == StickEraserConstants.VS_COM_LEVEl_1) {
			player2 = new ComputerPlayer(getString(R.string.computer), ColorSettings.LINE_COLOR_PLAYER2, new EraseStrategyImpl01());
		} else if (level ==  StickEraserConstants.VS_COM_LEVEl_2) {
			player2 = new ComputerPlayer(getString(R.string.computer), ColorSettings.LINE_COLOR_PLAYER2, new EraseStrategyImpl02());
		} else if (level ==  StickEraserConstants.VS_COM_LEVEl_3) {
			player2 = new ComputerPlayer(getString(R.string.computer), ColorSettings.LINE_COLOR_PLAYER2, new EraseStrategyImpl03());
		} else if (level ==  StickEraserConstants.VS_HUMAN) {
			String player2Name = stageSelectIntent.getStringExtra("player2Name");
			if (player2Name == null) {
				player2Name = getString(R.string.player2Name);
			}

			player2 = new HumanPlayer(player2Name, ColorSettings.LINE_COLOR_PLAYER2);
		} else {
			throw new IllegalArgumentException("opponent not found.");
		}

		gameInfo = new GameInfoModel(level, stage);

		// 先攻・後攻
		int turn = stageSelectIntent.getIntExtra("turn", StickEraserConstants.TOP_TURN);
		if (turn == StickEraserConstants.TOP_TURN) {
			playerManger.addPlayer(player1);
			playerManger.addPlayer(player2);
		} else {
			playerManger.addPlayer(player2);
			playerManger.addPlayer(player1);
		}

		// Viewにセット
		StickView view = (StickView) findViewById(R.id.stickView);
		view.setStickManager(stickManager);

		// lineManager
		lineManager = new LineManager();
		view.setLineManager(lineManager);



		mHandler.sendEmptyMessageDelayed(NEW_GAME, COMPUTER_DELAY_MS);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume called.");
		mHandler.sendEmptyMessageDelayed(RESTART_GAME, COMPUTER_DELAY_MS);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.v(TAG, "onPause called.");

		gameState = GameState.OUT_OF_GAME;
	}

	/**
	 * ゲームがスタートした時に呼び出されるメソッドです
	 */
	public void gameStart() {

		// stickをリセット
		stickManager.reset();
		// lineをリセット
		lineManager.reset();
		// playerをリセット
		playerManger.reset();
		// Viewをリセット
		StickView view = (StickView) findViewById(R.id.stickView);
		view.reset();

		gameState = GameState.ON_GAME;

		next();

	}

	/**
	 * ゲームがリスタートしたときに呼び出されるメソッドです
	 */
	public void gameRestart() {
		StickView view = (StickView) findViewById(R.id.stickView);
		view.restart();
		gameState = GameState.ON_GAME;
	}

	/**
	 * lineの決定が行われた時に呼び出されるメソッドです
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void eraseStick(int startX, int startY, int endX, int endY) {
		StickEraseResultModel result = stickManager.eraseStick(startX, startY, endX, endY);

		if(result.getCode() != StickEraseResultCode.SUCCESS) {
			// 線の消去に失敗
			if (result.getMessageId() != null) {
				// メッセージがある場合、ダイアログに表示
				showMessage(result.getMessageId());
			}

			lineManager.clearActiveLine();

			return;
		}


		// 線を確定
		lineManager.settleActiveLine();

		next();
	}

	/**
	 * 次のプレイヤーのターンにします
	 */
	public void next() {
		// すでに呼び出し中の場合はreturn
		if (called) {
			return;
		}

		called = true;

		while (! stickManager.isAllErased()) {

			// 次のプレイヤーへ
			Player player = playerManger.getNextPlayer();

			// 次のプレイヤーが自動でない場合、return
			if (!(player instanceof ComputerPlayer)) {
				called = false;
				return;
			}

			StickView view = (StickView) findViewById(R.id.stickView);

			((ComputerPlayer)player).drawLine(stickManager, view);
		}

		gameEnd();
		called = false;
	}

	/**
	 * ゲーム終了時に呼ばれるメソッドです
	 */
	private void gameEnd() {
		Player winner = playerManger.getNextPlayer();

		winner.gameWin(this, gameInfo);

		gameState = GameState.OUT_OF_GAME;

		// ダイアログの表示
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(getString(R.string.gameEnd))
		.setMessage( MessageFormat.format(getString(R.string.youWin), new Object[]{winner.getName()}))
		.setPositiveButton(getString(R.string.newGame), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// ダイアログを閉じる
				dialog.dismiss();

				// 次のゲームを始める
				mHandler.sendEmptyMessageDelayed(0, COMPUTER_DELAY_MS);
			}
		}).setNegativeButton(getString(R.string.backToStageSelect), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// このActivityを終了する
				finish();
			}
		}).show();

	}

	/**
	 * メッセージを表示します
	 * @param messageId
	 *            R.stringのId
	 */
	public void showMessage(Integer messageId) {
		toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * メッセージを隠します
	 */
	public void hideMessage() {
		if (toast != null) {
			toast.cancel();
		}
	}

	private class StickEraserHandlerCallback implements Callback {
		public boolean handleMessage(Message msg) {
			if (msg.what == NEW_GAME) {
				gameStart();
			} else if (msg.what== RESTART_GAME) {
				gameRestart();
			}
			return true;
		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public int getLineColor() {
		return playerManger.getNowPlayer().getLineColor();
	}

}