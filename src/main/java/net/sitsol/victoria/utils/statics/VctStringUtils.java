/**
 *
 */
package net.sitsol.victoria.utils.statics;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * 文字列ユーティリティ
 *
 * @author shibano
 */
public class VctStringUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctStringUtils() {}

	/**
	 * 囲い有りCSV文字列(＝「="xxx"」形式)の生成
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return 囲い有りCSV形式(＝「="xxx"」形式)の文字列
	 */
	public static String createBoxedCsvString(Object... stringObjs) {
		// 囲いありCSV文字列生成
		return createCsvString(true, stringObjs);
	}

	/**
	 * 囲いなしCSV文字列の生成
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return 囲いなしCSV形式の文字列
	 */
	public static String createCsvString(Object... stringObjs) {
		// 囲いなしCSV文字列生成
		return createCsvString(false, stringObjs);
	}

	/**
	 * CSV文字列の生成
	 * @param isBox 囲い有りフラグ
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return CSV形式の文字列
	 */
	private static String createCsvString(boolean isBox, Object... stringObjs) {
		// カンマ区切り文字列生成
		return createSplitString(",", isBox, stringObjs);
    }

	/**
	 * 囲いなし半角スペース区切り文字列の生成
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return 半角スペース区切り文字列
	 */
	public static String createSpaceSplitString(Object... stringObjs) {
		// 半角スペース区切り、囲いなし文字列生成
		return createSplitString(" ", false, stringObjs);
	}

	/**
	 * 囲いなしTSV文字列の生成
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return 囲いなしTSV形式の文字列
	 */
	public static String createTsvString(Object... stringObjs) {
		// タブ区切り、囲いなし文字列生成
		return createSplitString("\t", false, stringObjs);
	}

	/**
	 * 区切り文字列の生成
	 * @param splitStr 区切り文字列 ※カンマやタブ等
	 * @param isBox 囲い有りフラグ
	 * @param stringObjs 元となる文字列の可変長配列
	 * @return 区切り形式の文字列
	 */
	private static String createSplitString(String splitStr, boolean isBox, Object... stringObjs) {

		if (stringObjs == null) {
			return null;
		}

		StringBuilder strBuff = new StringBuilder();
		int columnNo = 1;

		for (Object stringObj : stringObjs) {
			try {
				// １文字でも生成済み(＝最初の列でない)場合
				if ( strBuff.length() > 0 ) {
					// 区切り文字
					strBuff.append(splitStr);
				}

				if ( isBox ) {
					// 改行あり文字列でない場合 ※改行あり文字列に「=」をつけると、excelで１セルとして表示してくれない
					if ( !(stringObj instanceof String) || StringUtils.indexOf((String)stringObj, "\n") < 0) {
						strBuff.append("=");	// 囲い記号(=)
					}

					strBuff.append("\"");		// 囲い記号
				}

				// nullの場合は空文字に変換
				if ( stringObj == null ) {
					stringObj = StringUtils.EMPTY;
				}

				// 列文字列の追加 ※null参照対策、あえてToStringしない
				strBuff.append(stringObj);

				if ( isBox ) {
					strBuff.append("\"");		// 囲い記号
				}
			}
			catch (Exception ex) {
				// どの列が失敗したかを特定できるようなエラーログを出力
				VctLogger.getLogger().error("区切り文字列の生成でエラーが発生しました。"
											+ "列連番：[" + columnNo + "]列目"
										, ex);

				// 変換エラーを明示する文字列として追加
				//  ※予期せぬエラーでも原因特定がしやすいよう、業務アプリ向けの配慮
				strBuff.append("Error!!");
			}
			finally {
				// 列連番カウントアップ
				columnNo++;
			}
		}

		return strBuff.toString();
	}

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
