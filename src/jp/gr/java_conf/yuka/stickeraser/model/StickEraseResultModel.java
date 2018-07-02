package jp.gr.java_conf.yuka.stickeraser.model;

import java.io.Serializable;

import jp.gr.java_conf.yuka.stickeraser.code.StickEraseResultCode;

public class StickEraseResultModel implements Serializable {
	private static final long serialVersionUID = 5044063026313053736L;
	private StickEraseResultCode code;
	private Integer messageId;

	public StickEraseResultModel(StickEraseResultCode code) {
		this(code, null);
	}

	public StickEraseResultModel(StickEraseResultCode code, Integer messageId) {
		super();
		this.code = code;
		this.messageId = messageId;
	}

	public StickEraseResultCode getCode() {
		return code;
	}

	public void setCode(StickEraseResultCode code) {
		this.code = code;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

}
