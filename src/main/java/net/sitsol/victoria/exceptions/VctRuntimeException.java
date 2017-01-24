/**
 * 
 */
package net.sitsol.victoria.exceptions;


/**
 * ランタイム例外クラス
 * @author shibano
 *
 */
public class VctRuntimeException extends RuntimeException {
	
	/* -- static ----------------------------------------------------------- */
	
	private static final long serialVersionUID = 6487660868566987247L;
	public static final int DEFAULT_ERROR_CODE = 100;						// デフォルトエラーコード
	
	
	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------
	
	private int errorCode_;	// エラーコード
	
	
	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------
	
	/**
	 * コンストラクタ
	 * @param message エラーメッセージ
	 */
	public VctRuntimeException(String message) {
		this(message, DEFAULT_ERROR_CODE, null);
	}
	
	/**
	 * コンストラクタ
	 * @param message 		エラーメッセージ
	 * @param th			例外情報
	 */
	public VctRuntimeException(String message, Throwable th) {
		this(message, DEFAULT_ERROR_CODE, th);
	}

	/**
	 * コンストラクタ
	 * @param message 		エラーメッセージ
	 * @param errorCode		エラーコード
	 */
	public VctRuntimeException(String message, int errorCode) {
		this(message, errorCode, null);
	}

	/**
	 * コンストラクタ
	 * @param message 		エラーメッセージ
	 * @param errorCode		エラーコード
	 * @param th			例外情報
	 */
	public VctRuntimeException(String message, int errorCode, Throwable th) {
		super(message, th);
		this.errorCode_ = errorCode;
	}

	/**
	 * エラーコード取得
	 * @return エラーコード
	 */
	public int getErrorCode() {
		return errorCode_;
	}

}
