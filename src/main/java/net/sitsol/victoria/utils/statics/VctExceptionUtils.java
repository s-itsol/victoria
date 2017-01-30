/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.exception.ExceptionUtils;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * 例外ユーティリティクラス
 *
 * @author shibano
 */
public class VctExceptionUtils {


	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctExceptionUtils() { }

	/**
	 * 例外包括チェック
	 * @param throwable 例外
	 * @param exceptionClass 含まれているかチェックしたい例外クラス型
	 * @return チェック結果 ※true：含まれている
	 */
	public static boolean hasThrowable(Throwable throwable, Class<?> exceptionClass) {

		if ( throwable == null || exceptionClass == null ) {
			return false;
		}

		// 含まれている例外が存在したら「含まれている」
		return ( ExceptionUtils.indexOfThrowable(throwable, exceptionClass) != -1 );
	}

	/**
	 *
	 * @param throwable
	 * @param clazz
	 * @return
	 */
	public static Throwable getThrowable(Throwable throwable, Class<?> exceptionClass) {

		if ( throwable == null || exceptionClass == null ) {
			return null;
		}

		// 含まれている例外の存在位置を取得
		int idx = ExceptionUtils.indexOfThrowable(throwable, exceptionClass);

		// 含まれていた場合
		if ( idx != -1 ) {
			// 該当した例外を返す
			return ExceptionUtils.getThrowables(throwable)[idx];
		}

		return null;
	}

	/**
	 * 例外スタックトレース文字列の生成
	 * @param targetEx 例外
	 * @return 例外スタックトレース文字列 ※TABや改行あり
	 */
	public static String createStackTraceString(Exception targetEx) {

		if ( targetEx == null ) { return null; }

		try {
			String retString = null;

			// 文字列ライター生成
			try ( StringWriter strWriter = new StringWriter();
					PrintWriter writer = new PrintWriter(strWriter);
			) {
				// スタックトレースを文字列ライターに出力
				targetEx.printStackTrace(writer);

				// 文字列生成
				retString = strWriter.getBuffer().toString();
			}

			return retString;

		} catch (Exception ex) {
			// 本来の処理に支障はないので、エラーログを出力して処理は継続させる
			VctLogger.getLogger().error("例外スタックトレース文字列の生成でエラーが発生しました。"
												+ "例外クラス型：[" + targetEx.getClass().getName() + "]"
											, ex
										);
			return null;
		}
	}

}