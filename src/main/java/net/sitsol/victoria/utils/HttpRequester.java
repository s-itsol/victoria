/**
 *
 */
package net.sitsol.victoria.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctHttpUtils;

/**
 * HTTPリクエスト支援クラス
 *
 * @author shibano
 */
public class HttpRequester {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	private HttpClient httpClient = null;		// HTTPクライアント


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * デフォルトコンストラクタ
	 */
	public HttpRequester() {
		// HTTPクライアント生成
		this.httpClient = new HttpClient();
	}

	/**
	 * GETリクエスト
	 * @param url URL文字列
	 * @return 応答結果文字列
	 */
	public String httpGet(String url) {
		return this.httpRequest(url, false, null);
	}

	/**
	 * POSTリクエスト
	 * @param url URL文字列
	 * @param parameterList リクエストパラメータ ※nullの場合はパラメータなし
	 * @return 応答結果文字列
	 */
	public String httpPost(String url, List<Pair<String, String>> parameterList) {
		return this.httpRequest(url, true, parameterList);
	}

	/**
	 * HTTPリクエスト
	 * @param url				URL文字列
	 * @param isPost			POSTメソッドフラグ ※GETの場合はfalse
	 * @param parameterList	リクエストパラメータ ※nullの場合はパラメータなし
	 * @return 応答結果文字列
	 */
	protected String httpRequest(String url, boolean isPost, List<Pair<String, String>> parameterList) {

		StringBuilder responsStr = new StringBuilder();
		BufferedReader reader = null;
		HttpMethod httpMethod = null;

		try {
			// POSTの場合
			if ( isPost ) {

				// メソッド生成
				httpMethod = new PostMethod(url);

				// リクエストパラメータあり
				if ( parameterList != null ) {
					// パラメータループ
					for ( Pair<String, String> parameter : parameterList ) {
						// 無効なPOSTパラメータは無視する
						if ( StringUtils.isEmpty(parameter.getKey())
							|| StringUtils.isEmpty(parameter.getValue())
						) {
							continue;
						}

						// POSTパラメータ追加
						((PostMethod) httpMethod).addParameter(parameter.getKey(), parameter.getValue());
					}
				}

			// GETの場合
			} else {
				// メソッド生成
				httpMethod = new GetMethod(url);
			}

			// HTTPリクエスト実行
			int sts = this.getHttpClient().executeMethod(httpMethod);

			// HTTPエラー応答あり ※200以外は全てエラー扱い
			if ( sts != 200 ) {
				throw new VctRuntimeException("HTTPエラー応答あり。HTTP応答ステータス：[" + sts + "]");
			}

			// HTTP応答結果取得
			reader = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream()));

			// 応答結果を文字列で取得
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				responsStr.append(line);
			}

		} catch ( Exception ex) {
			throw new VctRuntimeException("HTTPリクエストでエラーが発生しました。"
													+ "URL：[" + url + "]"
													+ ", cookies：[" + VctHttpUtils.cookiesToCsvString(this.getHttpClient()) + "]"
												, ex
											);
		} finally {
			try {
				if ( reader != null ) { reader.close(); }
				if ( httpMethod != null ) { httpMethod.releaseConnection(); }
			} catch ( Exception innerEx ) {
				// 根本例外ではないのでエラーログ出力のみ
				VctLogger.getLogger().error(innerEx);
			}
		}

		return responsStr.toString();
	}


	/* -- getter・setter --------------------------------------------------- */

	public HttpClient getHttpClient() {
		return httpClient;
	}

}
