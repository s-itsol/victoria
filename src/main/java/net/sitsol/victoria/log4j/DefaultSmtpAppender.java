/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.net.SMTPAppender;

/**
 * org.apache.log4j.net.SMTPAppenderを継承してvictoria用のデフォルト設定を施したクラス
 *
 * @author shibano
 */
public class DefaultSmtpAppender extends SMTPAppender {

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultSmtpAppender() {
		super();

		// 基本的なパラメータをここでまとめて実装
		this.setLocationInfo(true);									// 位置情報の表示有無
		this.setBufferSize(1);											// ログバッファのサイズ ※原因となった1行のみ
		this.setLayout(this.createLayoutInstance());					// レイアウト
		this.setEvaluatorClass(DefaultEvaluator.class.getName());		// イベント処理クラス名
	}

	/**
	 * レイアウトクラスのインスタンス生成
	 *  ※レイアウトクラスを差し替える場合は、派生クラス側でオーバーライド実装する事を想定している。
	 * @return レイアウトクラスのインスタンス
	 */
	protected Layout createLayoutInstance() {
		// victoriaデフォルト推奨レイアウトクラスのインスタンスを生成して返す
		return new DefaultPatternLayout();
	}

}
