/**
 *
 */
package net.sitsol.victoria.utils.statics;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

/**
 * 文字列ユーティリティ
 * @author shibano
 *
 */
public class VctStringUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctStringUtils() {}

	/**
	 * 渡された文字列のCSV用ダブルクオーテーション囲い(「="XXX"」)を除去します。
	 * 囲いなしはそのまま返し、nullなら空文字を返します。
	 * @param str	変換元文字列
	 * @return 変換後文字列
	 */
	public static String trimCsvDoubleQuote(String str) {

		if ( StringUtils.isEmpty(str) ) {
			return StringUtils.EMPTY;
		}

		final String EQUAL 		= "=";
		final String DB_QUOTE 	= "\"";

		// 「=」はじまり
		if ( str.startsWith(EQUAL) ) {
			// 開始文字列の「=」を空文字に変換
			str = str.replaceFirst(EQUAL, StringUtils.EMPTY);
		}

		// 「"」で囲われている
		if ( str.startsWith(DB_QUOTE) && str.endsWith(DB_QUOTE) ) {
			// 囲われている中の文字列を切り出す
			str = str.substring(DB_QUOTE.length(), str.length() - DB_QUOTE.length());
		}

		return str;
	}

	/**
	 * 文字列のマスキング(文字列指定)  ※XMLログ出力時の個人情報保護等で使う
	 *  ※マスキングする対象は１個所のみ。ネストした構成にも対応していない
	 * @param source		元の文字列
	 * @param maskFromStr	マスク開始文字列
	 * @param maskToStr	マスク終了文字列
	 * @param maskingStr	マスクして上書く文字列 ※nullの場合は空文字列で上書く
	 * @return マスキング後の文字列
	 */
	public static String maskString(String source, String maskFromStr, String maskToStr, String maskingStr) {

		// 元文字列、マスク開始文字列、マスク終了文字列 いずれかひとつでも空の場合は何もしない
		if ( StringUtils.isEmpty(source) || StringUtils.isEmpty(maskFromStr) || StringUtils.isEmpty(maskToStr) ) {
			return source;
		}

		// マスク開始文字位置の取得 ※マスク開始文字列の文字数も加える
		int maskFromIdx = source.indexOf(maskFromStr) + maskFromStr.length();
		// マスク終了文字位置の取得
		int maskToIdx = source.indexOf(maskToStr);

		return maskString(source, maskFromIdx, maskToIdx, maskingStr);
	}

	/**
	 * 文字列のマスキング(文字位置指定)  ※XMLログ出力時の個人情報保護等で使う
	 *  ※マスキングする対象は１個所のみ。ネストした構成にも対応していない
	 * @param source		元の文字列
	 * @param maskFromIdx	マスク開始文字位置 ※0オリジン
	 * @param maskToIdx	マスク終了文字位置 ※0オリジン
	 * @param maskingStr	マスクして上書く文字列 ※nullの場合は空文字列で上書く
	 * @return マスキング後の文字列
	 */
	public static String maskString(String source, int maskFromIdx, int maskToIdx, String maskingStr) {

		// 元文字列が空の場合は何もしない
		if ( StringUtils.isEmpty(source) ) {
			return source;
		}

		// マスク開始文字位置、マスク終了文字位置 いずれかが0未満(＝位置不明)場合は何もしない
		if ( maskFromIdx < 0 || maskToIdx < 0 ) {
			return source;
		}

		// 開始～終了をマスク文字列に置き換えた文字列を返す
		return source.substring(0, maskFromIdx) + maskingStr + source.substring(maskToIdx);
	}

	/**
	 * Base64エンコード
	 * @param str		変換前文字列
	 * @param charset	文字コード
	 * @return エンコード後の文字列
	 */
	public static String encodeBase64(String str, String charset) {

		try {
			// Base64エンコード
			return new String( Base64.encodeBase64(str.getBytes(charset)) );

		} catch (Exception ex) {
			throw new RuntimeException("Base64エンコードに失敗しました。"
												+ "文字コード：[" + charset + "]"
												+ ", 変換前文字列：[" + str + "]"
											, ex
										);
		}
	}

	/**
	 * Base64デコード
	 * @param str		変換前文字列
	 * @param charset	文字コード
	 * @return エンコード後の文字列
	 */
	public static String decodeBase64(String str, String charset) {

		try {
			// Base64デコード
			return new String( Base64.decodeBase64(str.getBytes()), charset );

		} catch (Exception ex) {
			throw new RuntimeException("Base64デコードに失敗しました。"
												+ "文字コード：[" + charset + "]"
												+ ", 変換前文字列：[" + str + "]"
											, ex
										);
		}
	}

	/**
	 * HMAC-SHA1ハッシュ化
	 * @param str			変換前文字列
	 * @param commonKeyStr	共有キー文字列
	 * @param charset		文字コード
	 * @return 暗号化後の文字列 ※16進数表記
	 */
	public static String hashHmacSha1(String str, String commonKeyStr, String charset) {

		try {
			// 共有キーでMACオブジェクト生成
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init( new SecretKeySpec(commonKeyStr.getBytes(), "HmacSHA1") );

			// HMACSHA1ハッシュ化＆16進数文字列化
			return new String( Hex.encodeHex( mac.doFinal( str.getBytes(charset) ) ) );

		} catch (Exception ex) {
			throw new RuntimeException("HMACSHA1暗号化に失敗しました。"
												+ "文字コード：[" + charset + "]"
												+ ", 共有キー文字列：[" + commonKeyStr + "]"
												+ ", 暗号化前文字列：[" + str + "]"
											, ex
										);
		}
	}

}
