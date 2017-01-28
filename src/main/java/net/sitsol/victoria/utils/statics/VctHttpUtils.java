/**
 *
 */
package net.sitsol.victoria.utils.statics;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * HTTPユーティリティ
 *
 * @author shibano
 */
public class VctHttpUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctHttpUtils() {}

	/**
	 * クエリストリング付きURL文字列を生成
	 *  ※POSTパラメータは個人情報を含むケースもあるので、あえて出力しない
	 * @param request HTTPサーブレットリクエスト
	 * @return クエリストリング付きURL文字列
	 */
	public static String createUrlQueryString(HttpServletRequest request) {

		if (request == null) {
			return StringUtils.EMPTY;
		}

		try {
			StringBuilder reqUrl = new StringBuilder();
			reqUrl.append(request.getRequestURL().toString());

			// クエリストリングあり
			if ( !StringUtils.isEmpty(request.getQueryString()) ) {
				// "?"でつなぐ
				reqUrl.append("?");
				reqUrl.append(StringUtils.stripToEmpty(request.getQueryString()));
			}

			// URL＋クエリストリング(＝GETパラメータ群)
			return reqUrl.toString();

		} catch (Exception ex) {
			// エラーログを出力して処理は継続
			VctLogger.getLogger().error("クエリストリング付きURL文字列生成でエラーが発生しました。", ex);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * クッキー値を取得する
	 * @param httpClient 	HTTPクライアント
	 * @param name			クッキー名
	 * @return クッキー値
	 */
	public static String getCookieValue(HttpClient httpClient, String name) {

		if ( httpClient == null
			|| httpClient.getState() == null
			|| httpClient.getState().getCookies() == null
			|| StringUtils.isEmpty(name)
		) {
			return null;
		}

		String value = null;

		// クッキー名が一致するクッキーを探すループ
		for ( Cookie cookie : httpClient.getState().getCookies() ) {
			if ( name.equals(cookie.getName()) ) {
				value = cookie.getValue();
				break;
			}
		}

		return value;
	}

	/**
	 * クッキー内容を「name=value」のCSV文字列化して取得する
	 * @param httpClient HTTPクライアント
	 * @return クッキーCSV文字列
	 */
	public static String cookiesToCsvString(HttpClient httpClient) {

		if ( httpClient == null
			|| httpClient.getState() == null
			|| httpClient.getState().getCookies() == null
		) {
			return null;
		}

		StringBuilder strBuff = new StringBuilder();
		{
			// クッキー群を文字列化 ※「name=value,name=value…」形式
			for ( Cookie cookie : httpClient.getState().getCookies() ) {
				// 初回以外は「,」で区切る
				if (strBuff.length() > 0) {
					strBuff.append(",");
				}
				strBuff.append(cookie.getName()).append("=").append(cookie.getValue());
			}
		}

		return strBuff.toString();
	}

}
