/**
 *
 */
package net.sitsol.victoria.setvlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sitsol.victoria.beanfactory.spring.VctBeanFactory;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.messages.VctMessage;

/**
 * サーブレット・コンテキスト・リスナー
 *  ※サーブレットコンテキスト（Webアプリケーション）が起動するときや
 *    破棄されるときに呼び出されます。
 *
 * @author shibano
 *
 */
public class VctServletContextListener implements ServletContextListener {

	/**
	 * コンストラクタ
	 */
	public VctServletContextListener() {}

	/**
	 * コンテキスト初期イベント
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {

		// ログ初期設定
		VctLogger.initialize();
		// アプリケーション・コンテキスト(＝APPパラメータ含む)初期設定
		VctBeanFactory.initialize();
		// メッセージ初期設定
		VctMessage.initialize();

		VctLogger.getLogger().info("アプリケーション起動処理が正常終了しました。");
	}

	/**
	 * コンテキスト解放イベント
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		VctLogger.getLogger().info("アプリケーション終了処理が正常終了しました。");
	}

}
