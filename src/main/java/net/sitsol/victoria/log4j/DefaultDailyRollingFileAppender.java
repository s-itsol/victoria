/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

import net.sitsol.victoria.consts.VctConst;

/**
 * org.apache.log4j.DailyRollingFileAppenderを継承してvictoria用のデフォルト設定を施したクラス
 *
 * @author shibano
 */
public class DefaultDailyRollingFileAppender extends DailyRollingFileAppender {

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultDailyRollingFileAppender() {
		super();

		// 基本的なパラメータをここでまとめて実装
		this.setEncoding("UTF-8");										// キャラセット ※victoriaデフォルト推奨エンコード
		this.setAppend(true);											// 追記フラグ
		this.setLayout(this.createLayoutInstance());					// レイアウト
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
