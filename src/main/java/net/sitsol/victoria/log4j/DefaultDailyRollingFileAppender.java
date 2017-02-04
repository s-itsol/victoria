/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

/**
 * org.apache.log4j.DailyRollingFileAppenderを継承してvictoria用のデフォルト設定を施したクラス
 *
 * @author shibano
 */
public class DefaultDailyRollingFileAppender extends DailyRollingFileAppender {

	/** デフォルト推奨-エンコーディング */
	public static String DEFAULT_ENCODING = "UTF-8";

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultDailyRollingFileAppender() {
		super();

		// 基本的なパラメータをここでまとめて実装
		this.setEncoding(this.createEncoding());						// エンコーディング
		this.setAppend(true);											// 追記フラグ
		this.setLayout(this.createLayoutInstance());					// レイアウト
	}

	/**
	 * エンコーディング生成
	 *  ※エンコーディングを差し替える場合は、派生クラス側でオーバーライド実装する事を想定している。
	 * @return エンコーディング文字列
	 */
	protected String createEncoding() {
		// デフォルト推奨-エンコーディング
		return DEFAULT_ENCODING;
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
