package net.sitsol.victoria.threadlocals;

import java.lang.reflect.Method;

/**
 * スレッド毎マッピングメソッド保持クラス
 *  Spring-mvcのコントローラ・メソッドに設定した注釈を、コントローラクラス以外で参照する用途で設けた。
 *  try-with-resourceにて、スレッド開始～終了を包括して使用することを想定している。
 *
 * @author shibano
 */
public class ThreadMappingMethod implements AutoCloseable {

	/* -- static ----------------------------------------------------------- */

	private static ThreadLocal<Method> mappingMethodMap = new ThreadLocal<>();		// スレッド毎マッピングメソッド

	/**
	 * スレッド毎マッピングメソッドの保持開始
	 * @param mappingMethod マッピングメソッド
	 */
	private static void beginThreadMappingMethod(Method mappingMethod) {
		// スレッド毎マッピングメソッドの設定
		mappingMethodMap.set(mappingMethod);
	}

	/**
	 * スレッド毎マッピングメソッドの保持終了
	 */
	private static void completeThreadMappingMethod() {
		// スレッド毎マッピングメソッドの破棄
		mappingMethodMap.remove();
	}

	/**
	 * 現スレッド-マッピングメソッドの保持判定
	 * @return 判定結果 ※true：保持している
	 */
	public static boolean hasCurrentThreadMappingMethod() {
		// ※得られたら「保持している」
		return ( getCurrentThreadMappingMethod() != null );
	}

	/**
	 * 現スレッド-マッピングメソッドの取得
	 * @return ユーザー情報
	 */
	public static Method getCurrentThreadMappingMethod() {
		return mappingMethodMap.get();
	}


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param mappingMethod マッピングメソッド
	 */
	public ThreadMappingMethod(Method mappingMethod) {
		// スレッド毎マッピングメソッドの保持開始
		beginThreadMappingMethod(mappingMethod);
	}

	/**
	 * リソースクローズ
	 */
	@Override
	public void close() {
		// スレッド毎マッピングメソッドの保持終了
		completeThreadMappingMethod();
	}

}
