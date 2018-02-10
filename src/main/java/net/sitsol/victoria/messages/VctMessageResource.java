/**
 *
 */
package net.sitsol.victoria.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctStringUtils;

/**
 * メッセージリソースクラス
 *
 * @author shibano
 */
public class VctMessageResource {

	/* -- static ----------------------------------------------------------- */

	/** デフォルト-メッセージファイルクラスパス */
	public static final String DEFAULT_MESSAGE_FILE_CLASS_PATH		= "messages";
	/** デフォルト-メッセージファイルサフィックス */
	public static final String DEFAULT_MESSAGE_FILE_SUFFIX			= "properties";
	private static VctMessageResource instance_ = new VctMessageResource();

	/**
	 * インスタンス取得
	 * @return 本クラスのインスタンス
	 */
	public static VctMessageResource getInstance() {
		return instance_;
	}

	/**
	 * メッセージ初期処理
	 *  ※メッセージファイルクラスパス＆サフィックス、エンコーディング、対象ロケールはデフォルトを使う
	 */
	public static void initialize() {

		Locale targetLocale = Locale.getDefault();								// デフォルトロケール

		// メッセージソース初期処理
		initialize(targetLocale);
	}

	/**
	 * メッセージ初期処理
	 *  ※メッセージファイルクラスパス＆サフィックス、エンコーディングはデフォルトを使う
	 * @param targetLocale 対象ロケール
	 */
	public static void initialize(final Locale targetLocale) {

		String messageFileClassPath = DEFAULT_MESSAGE_FILE_CLASS_PATH;			// デフォルト-メッセージファイルクラスパス
		String messageFileSuffix = DEFAULT_MESSAGE_FILE_SUFFIX;					// デフォルト-メッセージファイルサフィックス
		String encoding = VctStaticApParam.getInstance().getAppEncoding();			// アプリケーション標準エンコーディング

		// メッセージソース初期処理
		initialize(messageFileClassPath, messageFileSuffix, encoding, targetLocale);
	}

	/**
	 * メッセージリソース初期処理
	 * @param messageFileClassPath メッセージファイルクラスパス
	 * @param messageFileSuffix メッセージファイルサフィックス
	 * @param encoding エンコーディング
	 * @param targetLocale 対象ロケール
	 */
	public static void initialize(final String messageFileClassPath, final String messageFileSuffix, final String encoding, final Locale targetLocale) {

		long startMillus = System.currentTimeMillis();

		final String[] readResourceFileName = { null };		// 読込みメッセージリソースファイル名 ※無名クラス内で読込み成功時に設定させるため、finalで要素1つの配列にしている

		try {
			// リソースバンドル制御クラス生成
			ResourceBundle.Control bundleControl = new ResourceBundle.Control() {

				/**
				 * 新規バンドル生成イベントハンドラ
				 */
				@Override
				public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {

					// メッセージリソースファイル名生成
					String resourceFileName = this.toResourceName(this.toBundleName(baseName, locale), messageFileSuffix);

					ResourceBundle retResourceBundle = null;

					try (
						InputStream imputStream = loader.getResourceAsStream(resourceFileName);
						InputStreamReader streamReader = new InputStreamReader(imputStream, encoding);
						BufferedReader reader = new BufferedReader(streamReader);
					) {
						// メッセージプロパティファイル読込み
						retResourceBundle = new PropertyResourceBundle(reader);
					}

					// ※ここまで来たら、ロケールに対応したプロパティファイルが見つかり、読込みが成功した

					// 読込みメッセージリソースファイル名を保持
					readResourceFileName[0] = resourceFileName;

					return retResourceBundle;
				}
			};

			// メッセージリソース生成
			ResourceBundle resourceBundle = ResourceBundle.getBundle(messageFileClassPath, targetLocale, bundleControl);

			// 生成できなかった、生成されたリソースの対象ロケールが一致しなかった(≒生成できず、デフォルトロケールで生成された)場合
			if ( resourceBundle == null || !targetLocale.equals(resourceBundle.getLocale()) ) {
				// 例外をスローさせておく
				throw new VctRuntimeException("メッセージリソースの生成に失敗しました。対象ロケールのメッセージリソースファイルが存在するか等、確認してください。"
												+ "対象ロケール：[" + targetLocale + "]"
				);
			}

			// マップへ保持
			instance_.getLocaleResourceBundleMap().put(targetLocale, resourceBundle);

		} catch (Exception ex) {
			throw new VctRuntimeException("メッセージリソース初期処理でエラーが発生しました。"
													+ "メッセージリソースファイル-クラスパス：[" + messageFileClassPath + "]"
													+ ", メッセージリソースファイル-サフィックス：[" + messageFileSuffix + "]"
													+ ", エンコーディング：[" + encoding + "]"
													+ ", 対象ロケール：[" + targetLocale + "]"
												, ex
											);
		}

		long execMillis = System.currentTimeMillis() - startMillus;

		// 初期処理成功ログ出力
		VctLogger.getLogger().info("メッセージリソース初期処理終了。"
										+ "処理時間：[" + execMillis + "](ms)"
										+ ", 読込みメッセージリソースファイル名：[" + readResourceFileName[0] + "]"
										+ ", エンコーディング：[" + encoding + "]"
									);
	}


	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	private Map<Locale, ResourceBundle> localeResourceBundleMap = new HashMap<>();		// ロケール別リソースバンドルマップ ※キー：ロケール、値：リソースバンドル


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 */
	protected VctMessageResource() {
		super();
	}

	/**
	 * メッセージ取得
	 *  ※ロケールはデフォルトを使う
	 * @param key メッセージキー
	 * @param values 置換文字列群
	 * @return メッセージ文字列
	 */
	public String getMessage(String key, Object ... values) {
		// デフォルトロケールにて、メッセージ取得
		return this.getMessage(Locale.getDefault(), key, values);
	}

	/**
	 * メッセージ取得
	 * @param locale ロケール
	 * @param key メッセージキー
	 * @param values 置換文字列群
	 * @return メッセージ文字列 ※得られなかった場合は明示的なエラーを示す文字列
	 */
	public String getMessage(Locale locale, String key, Object ... values) {

		String retMessage = "get message error!! [" + locale + "][" + key + "]";	// ※デフォルト：明示的なエラーを示す文字列

		try {
			// ロケール別のメッセージリソースバンドル取得
			ResourceBundle resourceBundle = this.getLocaleResourceBundleMap().get(locale);

			// ロケール別のメッセージリソースバンドル無し
			if ( resourceBundle == null ) {
				// エラーログを出力し、代替えメッセージを返して継続させる ※原因は明確なので、スタックトレースは付けない
				VctLogger.getLogger().error("対象ロケールのメッセージリソース取得に失敗しました。代替えメッセージを返します。"
												+ "ロケール：[" + locale + "]"
												+ ", メッセージキー：[" + key + "]"
												+ ", 置換文字列群：[" + VctStringUtils.createCsvString(values) + "]"
											);
				return retMessage;
			}

			// メッセージキーに該当するメッセージ無し
			if ( key == null || !resourceBundle.containsKey(key) ) {
				// エラーログを出力し、代替えメッセージを返して継続させる ※原因は明確なので、スタックトレースは付けない
				VctLogger.getLogger().error("対象メッセージキーのメッセージ取得に失敗しました。代替えメッセージを返します。"
												+ "ロケール：[" + locale + "]"
												+ ", メッセージキー：[" + key + "]"
												+ ", 置換文字列群：[" + VctStringUtils.createCsvString(values) + "]"
											);
				return retMessage;
			}

			// メッセージリソースから基底メッセージを取得 ※これ以降にエラーとなってもデフォルトメッセージにならないよう、この時点で基底メッセージ(＝置換文字列の変換前)を戻り値としておく
			retMessage = resourceBundle.getString(key);

			// 置換文字列群でメッセージを変換
			retMessage = MessageFormat.format(retMessage, (Object[]) values);

		} catch (Exception ex) {
			// エラーログを出力し、代替えメッセージを返して継続させる
			VctLogger.getLogger().error("メッセージリソースから対象メッセージが得られませんでした。代替えメッセージを返します。"
												+ "ロケール：[" + locale + "]"
												+ ", メッセージキー：[" + key + "]"
												+ ", 置換文字列群：[" + VctStringUtils.createCsvString(values) + "]"
											, ex
										);
		}

		return retMessage;
	}


	/*-- setter・getter ------------------------------------------------------*/

	/**
	 * ロケール別リソースバンドルマップ取得
	 * @return ロケール別リソースバンドルマップ
	 */
	protected Map<Locale, ResourceBundle> getLocaleResourceBundleMap() {
		return localeResourceBundleMap;
	}

}
