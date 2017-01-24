/**
 * 
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.DailyRollingFileAppender;

/**
 * org.apache.log4j.DailyRollingFileAppenderを継承してvictoria用のデフォルト設定を施したクラス
 * @author rei_shibano
 */
public class DefaultDailyRollingFileAppender extends DailyRollingFileAppender {

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultDailyRollingFileAppender() {
		super();
		
		// 基本的なパラメータをここでまとめて実装
		this.setEncoding("UTF-8");										// キャラセット
		this.setAppend(true);											// 追記フラグ？
		this.setLayout(new DefaultPatternLayout());					// 出力書式
	}


}
