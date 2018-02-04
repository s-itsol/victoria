/**
 *
 */
package net.sitsol.victoria.setvlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * HTTPセッション・リスナー
 *
 * @author shibano
 */
public class VctHttpSessionListener implements HttpSessionListener {

	/**
	 * コンストラクタ
	 */
	public VctHttpSessionListener() {
	}

	/**
	 * セッション生成イベント
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {

	}

	/**
	 * セッション破棄イベント
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {

	}

}
