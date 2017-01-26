/**
 *
 */
package net.sitsol.victoria.spring;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.VctStopWatch;

/**
 * ビーンファクトリークラス
 *  ※アプリケーション・コンテキストとなりうる
 *
 * @author shibano
 */
public class VctBeanFactory extends XmlBeanFactory {

	/* -- static ----------------------------------------------------------- */

	public static final String DEFAULT_CONTEXT_CONF_PATH = "src/main/config/app-context.xml";
	private static VctBeanFactory instance_ = null;

	/**
	 * インスタンス取得
	 * @return ビーンファクトリーのインスタンス
	 */
	public static VctBeanFactory getInstance() {

		// 生成されていない場合
		if (instance_ == null) {
			// デフォルト設定ファイルパスを使って初期処理
			initialize();
		}

		return instance_;
	}

	/**
	 * アプリケーション・コンテキスト初期処理
	 *  ※コンテキスト設定ファイルパスはデフォルト(＝"src/main/config/app-context.xml")を使う
	 */
	public static void initialize() {
		// デフォルトパスのXMLファイルから設定を読み込む
		initialize(DEFAULT_CONTEXT_CONF_PATH);
	}

	/**
	 * アプリケーション・コンテキスト初期処理
	 * @param appContextXmlFilePath コンテキスト設定ファイル(≒app-context.xml)のファイルパス
	 */
	public static void initialize(String appContextXmlFilePath) {

		try {

			VctStopWatch stopWatch = new VctStopWatch();

			// XMLファイルから設定を読み込む
			Resource resource = new FileSystemResource(appContextXmlFilePath);
			instance_ = new VctBeanFactory(resource);

			stopWatch.stop();

			// 初期処理成功ログ出力
			VctLogger.getLogger().info("アプリケーション・コンテキスト初期処理終了。"
										+ "処理時間：[" + stopWatch.getTime() + "(ms)]"
										+ ", 設定ファイルパス：[" + appContextXmlFilePath + "]");
		} catch (Exception ex) {
			throw new VctRuntimeException(
						"アプリケーション・コンテキストの初期処理でエラーが発生しました。"
							+ "設定ファイルパス：[" + appContextXmlFilePath + "]"
						, ex);
		}
	}


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param resource Springリソース
	 */
	protected VctBeanFactory(Resource resource) {
		super(resource);
	}


	/* -- orverride -------------------------------------------------------- */

}
