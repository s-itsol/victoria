/**
 * 
 */
package net.sitsol.victoria.setvlet.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctUrlPathConst;
import net.sitsol.victoria.exceptions.VctServletSessionTimeoutRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctExceptionUtils;

/**
 * Spring-ハンドラ例外リゾルバ
 * 
 * @author shibano
 */
public class VctSpringHandlerExceptionResolver implements HandlerExceptionResolver {

	/**
	 * システムエラー-フォワード先URL取得
	 * @return フォワード先URL
	 */
	protected String getSystemErrorForwardUrl() {
		// ※デフォルト推奨-システムエラーURLパス
		return VctUrlPathConst.Root.Errors.SYSTEMERROR_VM;
	}

	/**
	 * セッションタイムアウト-フォワード先URL取得
	 * @return フォワード先URL
	 */
	protected String getSessionTimeoutForwardUrl() {
		// ※デフォルト推奨-セッションタイムアウトURLパス
		return VctUrlPathConst.Root.Errors.SESSIONTIMEOUT_VM;
	}

	/**
	 * 例外通知
	 *  各アプリ独自の例外は、オーバーライド実装して先にハンドリンクする想定
	 *  
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param handler ハンドラのインスタンス
	 * @param exception 発生例外
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
		
		// クライアント情報メッセージ生成
		String clientInfoMessage = this.createClientInfoMessage(request);
		
		String forwardUrl = null;									// フォワード先URL
		HttpStatus httpStatus = null;								// HTTP応答ステータス
		
		// セッションタイムアウト例外
		if ( VctExceptionUtils.hasThrowable(exception, VctServletSessionTimeoutRuntimeException.class) ) {
			
			// 警告ログを出力しておく ※業務例外なので、スローしている箇所は決まっているため、スタックトレースは無し
			VctLogger.getLogger().warn("サーブレット-リクエストで未認証を検知したため、セッションタイムアウトURLへフォワードします。"
											+ clientInfoMessage.toString()
			);
			
			forwardUrl = this.getSessionTimeoutForwardUrl();		// セッションタイムアウト-フォワード先URL
			httpStatus = HttpStatus.UNAUTHORIZED			;		// 認証失敗
			
		// その他(＝システムエラー)
		} else {
			
			// エラーログ出力 ※スタックトレース付き
			VctLogger.getLogger().error("サーブレット-リクエストでエラーが発生しました。システムエラーURLへフォワードします。"
											+ clientInfoMessage.toString()
										, exception
			);
			
			forwardUrl = this.getSystemErrorForwardUrl();			// システムエラー-フォワード先URL;
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;		// サーバ要因エラー
		}
		
		// モデル＆ビュー生成
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(forwardUrl);						// フォワード先URL
		modelAndView.setStatus(httpStatus);							// HTTPエラーコード設定
		
		return modelAndView;
	}

	/**
	 * クライアント情報メッセージ生成
	 * @param request HTTPサーブレットリクエスト
	 * @return クライアント情報メッセージ文字列
	 */
	protected final String createClientInfoMessage(HttpServletRequest request) {
		
		// クライアント情報生成
		StringBuilder clientInfoMessage = new StringBuilder();
		clientInfoMessage.append("USER-AGENT：[").append(request.getHeader(VctHttpConst.USER_AGENT)).append("]");
		clientInfoMessage.append(", REFERER：[").append(request.getHeader(VctHttpConst.REFERER)).append("]");
		clientInfoMessage.append(", REQUEST-URL：[").append(request.getRequestURL()).append("]");
		
		return clientInfoMessage.toString();
	}

}
