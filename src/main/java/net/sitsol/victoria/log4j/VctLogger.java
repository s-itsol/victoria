/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * victoria用ロガークラス
 *
 * @author shibano
 */
public class VctLogger extends Logger {

	/* -- static ----------------------------------------------------------- */

	private static VctLoggerFactory factory_ = new VctLoggerFactory();	// ログファクトリ
	public static final String DEFAULT_LOG_CONF_PATH = "src/main/config/log4j.xml";

	/**
	 * デフォルトロガー取得
	 * @return デフォルトロガーのインスタンス
	 */
	public static VctLogger getLogger(){
		return (VctLogger) Logger.getLogger(VctLogger.class.getName(), factory_);
	}

	/**
	 * log4j初期処理
	 *  ※ログ設定ファイルパスはデフォルト(＝"src/main/config/log4j.xml")を使う
	 */
	public static void initialize() {
		// デフォルトパスのXMLファイルから設定を読み込む
		initialize(DEFAULT_LOG_CONF_PATH);
	}

	/**
	 * log4j初期処理
	 * @param log4jXmlFilePath ログ設定ファイル(≒log4j.xml)のファイルパス
	 */
	public static void initialize(String log4jXmlFilePath) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// XMLファイルから設定を読み込む
		DOMConfigurator.configure(log4jXmlFilePath);

		stopWatch.stop();

		// 初期処理成功ログ出力
		VctLogger.getLogger().info("log4j初期処理終了。"
										+ "処理時間：[" + stopWatch.getTime() + "(ms)]"
										+ ", 設定ファイルパス：[" + log4jXmlFilePath + "]"
									);
	}


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param loggerName ロガー名
	 */
	public VctLogger(String loggerName) {
		super(loggerName);
	}

}
