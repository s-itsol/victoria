/**
 *
 */
package net.sitsol.victoria.utils;

import net.sitsol.victoria.log4j.VctLogger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;

/**
 * 処理時間計測用のストップウォッチクラス
 * @author shibano
 *
 */
public class VctStopWatch {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	protected StopWatch stopWatch_ = new StopWatch();
	private String execName_ = StringUtils.EMPTY;
	private boolean isInfolog_ = false;
	private Integer count_ = 0;


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public VctStopWatch() {
		this(StringUtils.EMPTY, false);
	}

	/**
	 * コンストラクタ
	 * @param execName	実行処理名 ※ログ出力用
	 */
	public VctStopWatch(String execName) {
		this(execName, false);
	}

	/**
	 * コンストラクタ
	 * @param execName		実行処理名 ※ログ出力用
	 * @param isIntoLog 	INFOログ出力フラグ ※未指定かfalseの場合はDEBUGレベル
	 */
	public VctStopWatch(String execName, boolean isIntoLog) {
		this.execName_ = execName;
		this.isInfolog_ = isIntoLog;

		// 計測開始
		this.stopWatch_.start();
	}

	/**
	 * 処理件数カウントUP
	 *  ※１度もカウントUPしなければ、処理件数はログに出力されない
	 */
	public void countUp() {
		this.countUp(1);
	}

	/**
	 * 処理件数カウントUP
	 *  ※１度もカウントUPしなければ、処理件数はログに出力されない
	 * @param upCount カウントアップ数
	 */
	public void countUp(int upCount) {
		synchronized ( this.count_ ) {
			this.count_ = this.count_ + upCount;
		}
	}

	/**
	 * 計測終了＆実行ログ出力
	 */
	public void stopAndOutputLog() {
		// 計測終了
		this.stop();
		// 実行ログ出力
		this.outputExecLog();
	}

	/**
	 * 計測終了
	 */
	public void stop() {
		// 計測終了
		this.stopWatch_.stop();
	}

	/**
	 * 実行時間(ms)の取得
	 * @return 実行時間(ms)
	 */
	public long getTime() {
		// 実行時間(ms)
		return this.stopWatch_.getTime();
	}

	/**
	 * 実行ログ文字列生成
	 * @return 実行ログ文字列
	 */
	protected String createExecLog() {

		StringBuilder logStr = new StringBuilder();

		logStr.append(		"[");
		logStr.append(			this.execName_);
		logStr.append(		"]");
		logStr.append(		"終了！");

		logStr.append(		"実行時間：[");
		logStr.append(			this.getTime());
		logStr.append(		"(ms)]");

		if (this.count_ > 0) {
			logStr.append(	", 処理件数：[");
			logStr.append(		this.count_);
			logStr.append(	"件]");
		}

		return logStr.toString();
	}

	/**
	 * 実行結果ログ出力
	 */
	protected void outputExecLog() {

		// 実行ログ出力
		if (this.isInfolog_) {
			VctLogger.getLogger().info(this.createExecLog());

		} else {
			VctLogger.getLogger().debug(this.createExecLog());
		}
	}


	/* -- getter・setter --------------------------------------------------- */

	public Integer getCount_() {
		return count_;
	}

	public String getExecName_() {
		return execName_;
	}

	public StopWatch getStopWatch_() {
		return stopWatch_;
	}

}
