/**
 * 
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.net.SMTPAppender;

/**
 * org.apache.log4j.net.SMTPAppenderを継承してvictoria用のデフォルト設定を施したクラス
 * @author rei_shibano
 */
public class DefaultSMTPAppender extends SMTPAppender {

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultSMTPAppender() {
		super();
		
		// 基本的なパラメータをここでまとめて実装
		this.setLocationInfo(true);									// 位置情報の表示有無
		this.setBufferSize(1);											// ログバッファのサイズ ※原因となった1行のみ
		this.setLayout(new DefaultPatternLayout());					// 出力書式
		this.setEvaluatorClass(DefaultEvaluator.class.getName());		// イベント処理クラス名
	}

}
