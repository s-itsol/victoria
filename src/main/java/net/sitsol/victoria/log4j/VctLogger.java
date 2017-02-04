/**
 *
 */
package net.sitsol.victoria.log4j;

import java.net.URL;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import net.sitsol.victoria.exceptions.VctRuntimeException;

/**
 * victoria用ロガークラス
 *
 * @author shibano
 */
public class VctLogger extends Logger {

	/* -- static ----------------------------------------------------------- */

	/** デフォルト-設定ファイルパス */
	public static final String DEFAULT_LOG_CONF_FILE_PATH = "log4j.xml";
	private static VctLoggerFactory factory_ = new VctLoggerFactory();	// ログファクトリ

	/**
	 * デフォルトロガー取得
	 * @return デフォルトロガーのインスタンス
	 */
	public static VctLogger getLogger(){
		return (VctLogger) Logger.getLogger(VctLogger.class.getName(), factory_);
	}

	/**
	 * log4j初期処理
	 *  ※ログ設定ファイルパスは、クラスパス上のデフォルト-ログ設定ファイルパスを使う
	 */
	public static void initialize() {

		String confFileClassPath = DEFAULT_LOG_CONF_FILE_PATH;		// デフォルト-設定ファイルパス

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(confFileClassPath);

		// クラスパス上にファイルが見つからなかった場合
		if ( url == null ) {
			throw new VctRuntimeException("log4j設定ファイルが見つかりませんでした。クラスパス上にファイルがあるか確認してください。"
												+ "読込ファイルパス：[" + confFileClassPath + "]"
											);
		}

		// log4j初期処理
		initialize(url.getPath());
	}

	/**
	 * log4j初期処理
	 * @param log4jXmlFilePath ログ設定ファイルのファイルパス
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
