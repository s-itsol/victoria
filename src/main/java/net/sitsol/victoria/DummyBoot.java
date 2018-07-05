/**
 * 
 */
package net.sitsol.victoria;

/**
 * ダミー起動クラス
 * 
 *  ※gradleの「dependency-management」プラグインにて、spring-bootのバージョンに合わせたライブラリ依存管理を行わせた際、
 *    bootJarタスクにて、mainメソッドを保持するクラスが無いため、ビルドエラーになってしまう。
 *    2018年7月時点で、gradle設定での回避方法が見つからなかったので、致し方なく設けた。
 * 
 * @author shibano
 */
public class DummyBoot {

	/**
	 * アプリケーション起動メソッド
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("このライブラリは、jar単体で実行することはできません。");
	}

}
