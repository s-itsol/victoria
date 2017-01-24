/**
 * 
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * ログファクトリクラス
 * @author shibano
 *
 */
public class VctLoggerFactory implements LoggerFactory {

	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------
	
	/**
	 * コンストラクタ
	 */
	public VctLoggerFactory() {}

	
	/* -- orverride -------------------------------------------------------- */

	/**
	 * ロガーインスタンスの生成
	 */
	public Logger makeNewLoggerInstance(String loggerName) {
		// フレームワークのロガーを生成して返す
		return new VctLogger(loggerName);
	}

}
