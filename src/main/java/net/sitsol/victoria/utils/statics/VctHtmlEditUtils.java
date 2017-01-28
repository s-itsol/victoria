/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * HTML編集支援ユーティリティ
 *
 * @author shibano
 */
public class VctHtmlEditUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctHtmlEditUtils() {}

	/**
	 * HTMLテキストから静的リンクURLを抜き出して文字列リストとして取得
	 * @param baseHtmlStr HTMLテキスト
	 * @return 静的リンクURLリスト ※静的リンクURLが見つからなかった場合は空のリスト
	 */
	public static List<String> substringAhrefUrlList(String baseHtmlStr) {

		if ( StringUtils.isBlank(baseHtmlStr) ) {
			return new ArrayList<String>();
		}

		List<String> ahrefUrlList = new ArrayList<String>();

		// 静的リンクURL群の取得
		//  ※現状は、大文字・小文字の「<a href="」と「"」で囲まれている文字列のみ
		String[] ahrefUrlArray1 = StringUtils.substringsBetween(baseHtmlStr, "<a href=\"", "\"");
		String[] ahrefUrlArray2 = StringUtils.substringsBetween(baseHtmlStr, "<A HREF=\"", "\"");

		String[][] ahrefUrlArrays = { ahrefUrlArray1, ahrefUrlArray2 };

		for ( String[] ahrefUrlArray : ahrefUrlArrays ) {

			if ( ahrefUrlArray == null ) { continue; };

			for ( String ahrefUrl : ahrefUrlArray ) {

				if ( StringUtils.isBlank(ahrefUrl) ) { continue; };

				// URL文字列として有効であれば、書式を整えるための変換
				ahrefUrl = StringUtils.replace(ahrefUrl, "\r\n", "");
				ahrefUrl = StringUtils.replace(ahrefUrl, "\n", "");
				ahrefUrl = StringUtils.replace(ahrefUrl, " ", "");
				ahrefUrl = StringUtils.replace(ahrefUrl, "\t", "");
				ahrefUrl = StringUtils.replace(ahrefUrl, "&amp;", "&");

				ahrefUrlList.add(ahrefUrl);
			}
		}

		return ahrefUrlList;
	}

}
