package jp.gr.java_conf.yuka.stickeraser.observer;

import jp.gr.java_conf.yuka.stickeraser.model.StickEraseResultModel;

public interface LineObserver {
	/**
	 * 引数で渡された線の情報をもとに、状態を変化させます
	 *
	 * @param aX 始点x座標
	 * @param aY 始点y座標
	 * @param bX 終点x座標
	 * @param bY 終点y座標
	 */
	public abstract void update(int aX, int aY, int bX, int bY);

	/**
	 * 引数で渡された線の情報をもとに、stickを削除します。
	 * ただし、渡された情報がルールを満たさない場合、stickは削除されません
	 *
	 * @param aX 始点x座標
	 * @param aY 終点y座標
	 * @param bX 始点x座標
	 * @param bY 終点y座標
	 * @return 削除に成功した場合はStickEraseResultCode.SUCCESS, それ以外の場合は対応するStickEraseResultCodeのクラスが返ります
	 */
	public abstract StickEraseResultModel eraseStick(int aX, int aY, int bX, int bY);
}
