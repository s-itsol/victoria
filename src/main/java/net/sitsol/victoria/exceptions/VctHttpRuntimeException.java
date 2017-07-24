/**
 *
 */
package net.sitsol.victoria.exceptions;


/**
 * HTTPエラーをハンドリングさせるための例外クラス
 *  ※HTTPステータスコードの参考：http://sy2920.s151.xrea.com/web/http-status.html
 *
 * @author shibano
 */
public class VctHttpRuntimeException extends VctRuntimeException {

	/* -- static ----------------------------------------------------------- */

	private static final long serialVersionUID = 7705264402559636511L;
	public static final int DEFAULT_ERROR_CODE = 100;						// デフォルトエラーコード


	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	private int responceStatus_ = 0;	// HTTP応答ステータス


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param message			メッセージ
	 * @param responceStatus	HTTP応答ステータス
	 */
	public VctHttpRuntimeException(String message, int responceStatus) {
		super(message);
		
		this.responceStatus_ = responceStatus;
	}

	/**
	 * HTTP応答ステータスの取得
	 * @return HTTP応答ステータス ※例外発生時に未設定の場合は0
	 */
	public int getResponceStatus() {
		return this.responceStatus_;
	}

	/**
	 * HTTP応答がSuccessful(＝クライアントのリクエストは正常終了している)であるかの判定
	 * @return 判定結果 true：応答ステータス200番台のSuccessfulである
	 */
	public boolean isSuccessful() {
		// 応答ステータスが200番台
		if (200 <= this.responceStatus_ && this.responceStatus_ < 300) {
			return true;
		}
		
		return false;
	}

	/**
	 * HTTP応答がRedirection(＝クライアント側でさらにアクションが必要)であるかの判定
	 * @return 判定結果 true：応答ステータス300番台のRedirectionである
	 */
	public boolean isRedirection() {
		// 応答ステータスが300番台
		if (300 <= this.responceStatus_ && this.responceStatus_ < 400) {
			return true;
		}
		
		return false;
	}

}
