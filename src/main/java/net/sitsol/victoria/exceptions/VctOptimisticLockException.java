/**
 *
 */
package net.sitsol.victoria.exceptions;

/**
 * 楽観排他エラー例外クラス
 *
 * @author shibano
 */
public class VctOptimisticLockException extends Exception {

	/* -- static ----------------------------------------------------------- */

	private static final long serialVersionUID = 2614770840529768758L;


	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	// ※現状は特に実装なし。アプリ側で楽観排他の発生をハンドリングするために設けたもの。

	/**
	 * コンストラクタ
	 * @param message エラーメッセージ
	 */
	public VctOptimisticLockException(String message) {
		super(message);
	}

}
