/**
 * 
 */
package net.sitsol.victoria.messages;

import java.util.Locale;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * メッセージソースクラス
 * 
 * @author shibano
 */
public class VctMessageSauce {

	/* -- static ----------------------------------------------------------- */

	/** デフォルト-メッセージプロパティファイルクラスパス */
	public static final String DEFAULT_MESSAGE_PROP_FILE_CLASS_PATH = "messages";
	protected static MessageSource instance_;

	/**
	 * メッセージ初期処理
	 *  ※メッセージプロパティファイルクラスパス、エンコーディングはデフォルトを使う
	 */
	public static void initialize() {
		
		String messageFileClassPath = DEFAULT_MESSAGE_PROP_FILE_CLASS_PATH;		// デフォルト-メッセージプロパティファイルクラスパス
		String encoding = VctStaticApParam.getInstance().getAppEncoding();		// アプリケーション標準エンコーディング
		
		// メッセージソース初期処理
		initialize(messageFileClassPath, encoding);
	}

	/**
	 * メッセージソース初期処理
	 * @param messageFileClassPath メッセージプロパティファイルクラスパス
	 * @param defaultEncoding デフォルトエンコーディング
	 */
	public static void initialize(String messageFileClassPath, String encoding) {

		try {
			long startMillus = System.currentTimeMillis();

			// メッセージソース生成
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasename(messageFileClassPath);	// メッセージプロパティファイルのクラスパス
			messageSource.setDefaultEncoding(encoding);			// デフォルトエンコーディング

			instance_ = messageSource;

			long execMillis = System.currentTimeMillis() - startMillus;

			// 初期処理成功ログ出力
			VctLogger.getLogger().info("メッセージソース初期処理終了。"
											+ "処理時間：[" + execMillis + "](ms)"
											+ ", メッセージプロパティファイルクラスパス：[" + messageFileClassPath + "]"
											+ ", エンコーディング：[" + encoding + "]"
										);
		} catch (Exception ex) {
			throw new VctRuntimeException(	"メッセージソース初期処理でエラーが発生しました。"
													+ "メッセージプロパティファイルクラスパス：[" + messageFileClassPath + "]"
													+ ", エンコーディング：[" + encoding + "]"
												, ex
											);
		}
	}

	/**
	 * メッセージ取得
	 *  ※ロケールはデフォルトを使う
	 * @param key メッセージキー
	 * @param values 置換文字列群
	 * @return メッセージ文字列
	 */
	public static String getMessage(String key, String ... values) {
		// デフォルト-ロケールにて、メッセージ取得
		return getMessage(Locale.getDefault(), key, values);
	}

	/**
	 * メッセージ取得
	 * @param locale ロケール
	 * @param key メッセージキー
	 * @param values 置換文字列群
	 * @return メッセージ文字列
	 */
	public static String getMessage(Locale locale, String key, String ... values) {
		// メッセージソースから取得
		return instance_.getMessage(key, values, locale);
	}


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 */
	protected VctMessageSauce() {
		super();
	}

}
