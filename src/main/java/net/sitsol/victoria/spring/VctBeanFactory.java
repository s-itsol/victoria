/**
 *
 */
package net.sitsol.victoria.spring;

import java.net.URL;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;

/**
 * ビーンファクトリークラス
 *  ※アプリケーション・コンテキストとなりうる
 *
 * @author shibano
 */
public class VctBeanFactory extends XmlBeanFactory {

	/* -- static ----------------------------------------------------------- */

	/** デフォルト-設定ファイルパス */
	public static final String DEFAULT_CONTEXT_CONF_PATH = "app-context.xml";
	private static VctBeanFactory instance_ = null;

	/**
	 * インスタンス取得
	 * @return ビーンファクトリーのインスタンス
	 */
	public static VctBeanFactory getInstance() {

		// 生成されていない場合
		if ( instance_ == null ) {
			// デフォルト設定ファイルパスを使って初期処理
			initialize();
		}

		return instance_;
	}

	/**
	 * アプリケーション・コンテキスト初期処理
	 *  ※コンテキスト設定ファイルパスはデフォルトを使う
	 */
	public static void initialize() {

		String confFileClassPath = DEFAULT_CONTEXT_CONF_PATH;		// デフォルト-設定ファイルパス

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(confFileClassPath);

		// クラスパス上にファイルが見つからなかった場合
		if ( url == null ) {
			throw new VctRuntimeException("アプリケーション・コンテキスト設定ファイルが見つかりませんでした。クラスパス上にファイルがあるか確認してください。"
												+ "読込ファイルパス：[" + confFileClassPath + "]"
											);
		}

		// デフォルトパスのXMLファイルから設定を読み込む
		initialize(url.getPath());
	}

	/**
	 * アプリケーション・コンテキスト初期処理
	 * @param appContextXmlFilePath アプリケーション・コンテキスト設定ファイルのファイルパス
	 */
	public static void initialize(String appContextXmlFilePath) {

		try {

			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			// XMLファイルから設定を読み込む
			Resource resource = new FileSystemResource(appContextXmlFilePath);
			instance_ = new VctBeanFactory(resource);

			stopWatch.stop();

			// 初期処理成功ログ出力
			VctLogger.getLogger().info("アプリケーション・コンテキスト初期処理終了。"
											+ "処理時間：[" + stopWatch.getTime() + "(ms)]"
											+ ", 設定ファイルパス：[" + appContextXmlFilePath + "]"
										);
		} catch (Exception ex) {
			throw new VctRuntimeException(	"アプリケーション・コンテキストの初期処理でエラーが発生しました。"
													+ "設定ファイルパス：[" + appContextXmlFilePath + "]"
												, ex
											);
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

}
