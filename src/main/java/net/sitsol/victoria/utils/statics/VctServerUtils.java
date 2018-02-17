/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.net.InetAddress;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * サーバーサイドの共通ユーティリティ
 *
 * @author shibano
 */
public class VctServerUtils {

	/** 実行マシンのホスト名 */
	public static final String HOST_NAME;
	static {
		HOST_NAME = VctServerUtils.getHostName();
	}

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctServerUtils() {}

	/**
	 * 実行しているマシンのホスト名の取得
	 *  ※処理は軽微なコストだが、1万回実行すると3秒ほど消費するので、
	 *    考えなしに何万回もループ実行させないよう要注意。
	 * @return ホスト名 ※予期せぬ例外が発生時は、文字列 "UnknownHost" を返します。
	 */
	protected static String getHostName() {

		String retHostName = null;
		try {
	    	// 実行マシンのホスト名の取得
			retHostName = InetAddress.getLocalHost().getHostName();

		} catch (Exception ex) {
			// エラーログを出力して処理は継続
			VctLogger.getLogger().error("実行マシンのホスト名取得でエラーが発生しました。", ex);
			retHostName = "UnknownHost";
		}

		return retHostName;
	}

}
