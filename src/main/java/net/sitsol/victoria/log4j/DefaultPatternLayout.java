/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.PatternLayout;

/**
 * org.apache.log4j.PatternLayoutを継承してvictoria用のデフォルト設定を施したクラス
 *
 * ＜書式メモ＞
 *  %c：ログイベントのカテゴリ名
 *  %d：出力日時ミリ秒(yyyy-mm-dd hh:mm:ss,sss)
 *  %p：ログレベル文字列
 *  %t：スレッド名称
 *  %x：ネスト化診断コンテキスト(≒追加情報)
 *  %l：出力処理位置 ※「パッケージ(クラス:行番号)」形式
 *  %m：ログメッセージ本文
 *  %n：改行(CRが付くか否かはOS依存？)
 *  参考URL：http://www.techscore.com/tech/ApacheJakarta/Log4J/7.html
 *
 * @author shibano
 */
public class DefaultPatternLayout extends PatternLayout {

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultPatternLayout() {
		super();

		// 基本的なパラメータをここでまとめて実装
		this.setConversionPattern("[%d],[%p],[%t],[%x],[%l], %m%n");
	}

}
