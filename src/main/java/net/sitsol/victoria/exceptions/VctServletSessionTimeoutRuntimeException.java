/**
 * 
 */
package net.sitsol.victoria.exceptions;

/**
 * HTTPサーブレット-セッションタイムアウトをハンドリングさせるための例外クラス
 * 
 * @author shibano
 */
public class VctServletSessionTimeoutRuntimeException extends VctRuntimeException {

	/* -- static ----------------------------------------------------------- */

	private static final long serialVersionUID = 1L;


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
	public VctServletSessionTimeoutRuntimeException(String message) {
		super(message);
	}

}
