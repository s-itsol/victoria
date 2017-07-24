/**
 *
 */
package net.sitsol.victoria.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.exceptions.VctHttpRuntimeException;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;

import org.apache.commons.lang.StringUtils;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;

/**
 * HTTPリクエスト支援クラス
 *  ※HTTP-APIの接続先サービスごとに派生クラスを作成し、
 *    リクエストヘッダ等をオーバーライド実装で差し替えることを想定している。
 *
 * @author shibano
 */
public class VctHttpRequester {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	private String proxyHost = null;					// プロキシ-ホスト
	private Integer proxyPort = null;					// プロキシ-ポート


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * デフォルトコンストラクタ
	 */
	public VctHttpRequester() {
		this(null, null);
	}

	/**
	 * コンストラクタ
	 * @param proxyHost プロキシ-ホスト
	 * @param proxyPort プロキシ-ポート
	 */
	public VctHttpRequester(String proxyHost, Integer proxyPort) {
		super();

		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	/**
	 * GETリクエスト
	 * @param requestUrl リクエスト先URL
	 * @return 応答結果文字列
	 */
	public String httpGet(String requestUrl) {
		return this.httpRequest(requestUrl, false, null);
	}

	/**
	 * POSTリクエスト
	 * @param requestUrl リクエスト先URL
	 * @param parameterList リクエストパラメータ ※nullの場合はパラメータなし
	 * @return 応答結果文字列
	 */
	public String httpPost(String requestUrl, List<Parameter> parameterList) {
		return this.httpRequest(requestUrl, true, parameterList);
	}

	/**
	 * HTTPリクエスト
	 * @param requestUrl リクエスト先URL
	 * @param isPost POSTメソッドフラグ ※GETの場合はfalse
	 * @param parameterList リクエストパラメータ ※nullの場合はパラメータなし
	 * @return 応答結果文字列
	 */
	protected String httpRequest(String requestUrl, boolean isPost, List<Parameter> parameterList) {

		HttpURLConnection con = null;
		StringBuilder responseStr = new StringBuilder();
		BufferedReader reader = null;

		try {

			URL url = new URL(requestUrl);

			// プロキシ-ホスト指定あり
			if ( !StringUtils.isBlank(this.getProxyHost()) ) {

				int proxyPort = this.getProxyPort() != null ? this.getProxyPort().intValue() : 80;	// ※デフォルト：80番

				// プロキシ経由でHTTPコネクション生成
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.getProxyHost(), proxyPort));
				con = (HttpURLConnection) url.openConnection(proxy);

			// プロキシ-ホスト指定なし
			} else {
				// HTTPコネクション生成
				con = (HttpURLConnection) url.openConnection();
			}

			// 基本-接続設定
			this.settingConnectionBase(con);

			// POSTの場合
			if ( isPost ) {
				con.setRequestMethod("POST");
				con.setDoOutput(true);

				// POST専用-接続設定
				this.settingConnectionPost(con);

				// リクエストパラメータあり
				if ( parameterList != null && parameterList.size() > 0 ) {

					// リクエストパラメータ-クエリストリング文字列生成
					String parameterQueryString = this.createParameterQueryString(parameterList);

					// POSTパラメータ一括出力
					OutputStreamWriter writer = null;
					try {
						writer = new OutputStreamWriter(con.getOutputStream(), Charset.forName(this.getPostRequestEncoding()));
						writer.write(parameterQueryString);
						writer.flush();
					} finally {
						try {
							if ( writer != null ) { writer.close(); }
						} catch ( Exception innerEx ) {
							// 根本例外ではないのでエラーログ出力のみ
							VctLogger.getLogger().error("HTTPリクエスト-POSTパラメータ出力ストリーム終了でエラーが発生しました。", innerEx);
						}
					}
				}

			// GETの場合
			} else {
				con.setRequestMethod("GET");

				// GET専用-接続設定
				this.settingConnectionGet(con);
			}

			// HTTPステータス取得 ※ここでTCP接続確立～リクエスト実行
			int sts = con.getResponseCode();

			// HTTPエラー応答あり ※HTTP_OK以外は全てエラー扱い
			if ( sts != HttpURLConnection.HTTP_OK ) {
				throw new VctHttpRuntimeException("HTTPエラー応答あり。HTTP応答ステータス：[" + sts + "]", sts);
			}

			// HTTP応答結果取得
			reader = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName(this.getResponseEncoding())));

			// 応答結果を文字列で取得
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				responseStr.append(line);
			}

		// HTTPエラー応答はそのままスロー
		} catch ( VctHttpRuntimeException ex ) {
			throw ex;

		} catch ( Exception ex) {
			throw new VctRuntimeException("HTTPリクエストでエラーが発生しました。"
													+ "URL：[" + requestUrl + "]"
												, ex
											);
		} finally {
			try {
				if ( reader != null ) { reader.close(); }
			} catch ( Exception innerEx ) {
				// 根本例外ではないのでエラーログ出力のみ
				VctLogger.getLogger().error("HTTPリクエスト-レスポンス入力ストリーム終了でエラーが発生しました。", innerEx);
			}
			try {
				if ( con != null ) { con.disconnect(); }
			} catch ( Exception innerEx ) {
				// 根本例外ではないのでエラーログ出力のみ
				VctLogger.getLogger().error("HTTPリクエスト-接続切断でエラーが発生しました。", innerEx);
			}
		}

		return responseStr.toString();
	}

	/**
	 * リクエストパラメータ-クエリストリング文字列生成
	 * @param parameterList リクエストパラメータ
	 * @return リクエストパラメータ-クエリストリング文字列
	 */
	private String createParameterQueryString(List<Parameter> parameterList) {

		StringBuilder parameterQueryString = new StringBuilder();

		// パラメータループ
		for ( Parameter parameter : parameterList ) {

			// 無効なPOSTパラメータは無視する
			if ( StringUtils.isEmpty(parameter._name)
				|| parameter._value == null
				|| !( parameter._value instanceof String )
			) {
				continue;
			}

			// 2つ目以降のパラメータは接続文字列で繋ぐ
			if ( parameterQueryString.length() > 0 ) {
				parameterQueryString.append("&");
			}

			// POSTパラメータ追加
			parameterQueryString.append(parameter._name).append("=").append((String) parameter._value);
		}

		return parameterQueryString.toString();
	}

	/**
	 * 基本-接続設定
	 * @param con IN/OUT：HTTPコネクション
	 */
	protected void settingConnectionBase(HttpURLConnection con) {
		con.setConnectTimeout(0);							// ※接続タイムアウト無し
		con.setReadTimeout(0);								// ※読込みタイムアウト無し
		con.setUseCaches(false);							// ※WEBキャッシュ未使用
		con.setRequestProperty("Connection", "close");		// ※keep-Alive接続しない(＝1回のリクエストでtcp接続を切断する)
	}

	/**
	 * POST専用-接続設定
	 * @param con IN/OUT：HTTPコネクション
	 */
	protected void settingConnectionPost(HttpURLConnection con) {
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
	}

	/**
	 * GET専用-接続設定
	 * @param con IN/OUT：HTTPコネクション
	 */
	protected void settingConnectionGet(HttpURLConnection con) {
		// ※デフォルトは特になし
	}

	/**
	 * POSTリクエスト-エンコーディング
	 * @return エンコーディング文字列
	 */
	protected String getPostRequestEncoding() {
		// ※アプリケーション標準エンコーディング
		return VctStaticApParam.getInstance().getAppEncoding();
	}

	/**
	 * レスポンス-エンコーディング
	 * @return エンコーディング文字列
	 */
	protected String getResponseEncoding() {
		// ※アプリケーション標準エンコーディング
		return VctStaticApParam.getInstance().getAppEncoding();
	}


	/* -- getter・setter --------------------------------------------------- */

	public String getProxyHost() {
		return proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

}
