package jp.gr.java_conf.yuka.stickeraser.strategy;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.manager.StickManager.Block;

public interface EraseStrategy {

	Block getEraseSticks(StickManager stickManager);

}
