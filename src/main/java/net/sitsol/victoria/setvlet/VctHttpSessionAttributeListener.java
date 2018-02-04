/**
 *
 */
package net.sitsol.victoria.setvlet;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * HTTPセッション属性・リスナー
 *
 * @author shibano
 */
public class VctHttpSessionAttributeListener implements HttpSessionAttributeListener {

	/**
	 * コンストラクタ
	 */
	public VctHttpSessionAttributeListener() {
	}

	/**
	 * セッション属性追加イベント
	 */
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {

	}

	/**
	 * セッション属性破棄イベント
	 */
	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {

	}

	/**
	 * セッション属性置換イベント
	 */
	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {

	}

}
