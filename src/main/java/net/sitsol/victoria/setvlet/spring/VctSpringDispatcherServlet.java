/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

import net.sitsol.victoria.annotation.servlet.VctNoAuth;
import net.sitsol.victoria.annotation.servlet.VctNoLogRequestUrl;
import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctLogKeywordConst;
import net.sitsol.victoria.consts.VctUrlPathConst;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.exceptions.VctServletSessionTimeoutRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.threadlocals.ThreadLog4jNdc;
import net.sitsol.victoria.threadlocals.ThreadMappingMethod;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;
import net.sitsol.victoria.utils.statics.VctExceptionUtils;
import net.sitsol.victoria.utils.statics.VctHttpUtils;
import net.sitsol.victoria.utils.statics.VctServerUtils;
import net.sitsol.victoria.utils.statics.VctSpringMvcUtils;

/**
 * Spring-Dispatcherサーブレット
 *
 * @author shibano
 */
public class VctSpringDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

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
	 * サービス実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// TODO：ログインはまだ
		String userId = request.getSession().getId();
		IUserInfo loginUserInfo = null;
		
		try (
				ThreadLog4jNdc threadLog4jNdc = new ThreadLog4jNdc(userId);				// ※log4j-NDCメッセージ
				ThreadUserInfo threadUserInfo = new ThreadUserInfo(loginUserInfo);		// ※ログインユーザー情報
		) {
			// リクエストマッピングメソッド取得
			Method requestMappingMethod = this.getRequestMappingMethod(request);
			
			try (
					ThreadMappingMethod threadMappingMethod = new ThreadMappingMethod(requestMappingMethod)		// ※リクエストマッピングメソッド
			) {
				// 基底クラスのメソッド実行
				super.doService(request, response);
			}
		}
	}

	/**
	 * ディスパッチ実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		long startMillis = System.currentTimeMillis();							// 開始時刻
		
		try {
			// システム共通リクエスト属性値設定
			this.setCommonRequestAttributes(request);
			
			// HTTPリクエストURLログ出力
			this.httpRequestUrlLog(request, response);
			
			// 認証
			this.auth(request, response);
			
			// 基底クラスのメソッド実行
			super.doDispatch(request, response);
			
		} catch (Exception ex) {
			// 例外ハンドラ実行
			this.doExceptionHanler(request, response, ex);
			
		} finally {
			
			long execMillis = System.currentTimeMillis() - startMillis;			// 処理時間(ms) ※「終了時刻」－「開始時刻」
			
			// HTTP要求の警告時間(ms)
			int warnMillis = VctStaticApParam.getInstance().getWarningHttpRequestExecuteMillis();
			
			// 超過していた場合
			if ( execMillis >= warnMillis ) {
				
				// HTTP要求-処理時間警告ログ出力
				StringBuilder message = new StringBuilder();
				message.append(VctLogKeywordConst.WARNTIME).append(" ").append(warnMillis).append("(ms)を超過したHTTP要求がありました。");
				message.append("処理時間：[").append(execMillis).append("](ms)");
				message.append(", 要求URL：[").append(VctHttpUtils.createUrlQueryString(request)).append("]");
				
				VctLogger.getLogger().warn(message.toString());
			}
		}
	}

	/**
	 * サーブレット終了処理
	 */
	@Override
	public void destroy() {
		super.destroy();
		
		VctLogger.getLogger().info("Spring-Dispatcherサーブレット終了処理が終了しました。サーブレット名：[" + this.getServletName() + "]");
	}

	/**
	 * リクエストマッピングメソッド取得
	 * @param request HTTPサーブレットリクエスト
	 * @return リクエストにマッピングされたメソッド ※得られなかった場合はnull
	 */
	protected Method getRequestMappingMethod(HttpServletRequest request) throws Exception {
		
		// ハンドラ例外チェーン取得
		HandlerExecutionChain handlerChain = this.getHandler(request);
		
		if ( handlerChain == null ) { return null; }
		
		// ハンドラオブジェクト取得
		Object handler = handlerChain.getHandler();
		
		// ハンドラメソッドだった場合はキャスト
		HandlerMethod handlerMethod =  ( handler != null && handler instanceof HandlerMethod ) ? (HandlerMethod) handler : null;
		
		if ( handlerMethod == null ) { return null; }
		
		// リクエストマッピングメソッド取得
		Method requestMappingMethod = handlerMethod != null ? handlerMethod.getMethod() : null;
		
		return requestMappingMethod;
	}

	/**
	 * システム共通リクエスト属性値設定
	 * @param request HTTPサーブレットリクエスト
	 */
	protected void setCommonRequestAttributes(HttpServletRequest request) throws Exception {
		
		// システム共通リクエスト属性値設定
		request.setAttribute(VctHttpConst.ENV_NAME, VctStaticApParam.getInstance().getDispEnvName());		// 環境名
		request.setAttribute(VctHttpConst.HOST_NAME, VctServerUtils.HOST_NAME);								// ホスト名
	}

	/**
	 * HTTPリクエストURLログ出力
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	protected void httpRequestUrlLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if ( request == null ) { return; }
		
		// HTTPリクエストURLログ出力フラグOFF
		if ( !VctStaticApParam.getInstance().isHttpRequestUrlLogOutputFlg() ) { return; }
		
		// HTTPリクエストURLログ出力不要アノテーションあり
		if ( VctSpringMvcUtils.hasCurrentThreadAnnotation(VctNoLogRequestUrl.class) ) { return; }
		
		// HTTPリクエストURLログ出力
		StringBuilder message = new StringBuilder();
		message.append(VctLogKeywordConst.REQURL).append(" ").append(VctHttpUtils.createUrlQueryString(request));
		
		VctLogger.getLogger().info(message.toString());
	}

	/**
	 * 認証
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	protected void auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if ( request == null ) { return; }
		
		// 現スレッド-マッピングメソッドが無い場合(≒静的コンテンツのリクエスト)は処理しない
		if ( !ThreadMappingMethod.hasCurrentThreadMappingMethod() ) {
			return;
		}
		
		// 認証不要アノテーションあり
		if ( VctSpringMvcUtils.hasCurrentThreadAnnotation(VctNoAuth.class) ) { return; }
		
		// 認証判定
		boolean isAuth = this.isAuth(request, response);
		
		// 認証OK
		if ( isAuth ) { return; }
		
		// ここまで来たら、セッションタイムアウト例外をスロー ※例外ハンドラで処理させる用なので、メッセージは不要
		throw new VctServletSessionTimeoutRuntimeException("");
	}

	/**
	 * 認証判定
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @return 判定結果 ※true：認証OK ／ false：認証NG
	 */
	protected boolean isAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// 警告ログを出力しておく
		VctLogger.getLogger().warn("デフォルトの認証判定が実行されたため「認証NG」を返します。"
										+ "オーバーライド実装するか、" + VctNoAuth.class.getSimpleName() + "注釈を付けて認証判定をさせないようにしてください。"
										+ "リクエストURL：[" + request.getRequestURL() + "]"
									);
		
		return false;
	}

	/**
	 * 例外ハンドラ実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param exception 発生例外
	 */
	protected void doExceptionHanler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		
		//クライアント情報生成
		StringBuilder clientInfoMessage = new StringBuilder();
		clientInfoMessage.append("USER-AGENT：[").append(request.getHeader(VctHttpConst.USER_AGENT)).append("]");
		clientInfoMessage.append(", REFERER：[").append(request.getHeader(VctHttpConst.REFERER)).append("]");
		clientInfoMessage.append(", REQUEST-URL：[").append(request.getRequestURL()).append("]");
		
		String forwardUrl = null;			// フォワード先URL
		
		// セッションタイムアウト例外
		if ( VctExceptionUtils.hasThrowable(exception, VctServletSessionTimeoutRuntimeException.class) ) {
			
			// 警告ログを出力しておく ※業務例外なので、スローしている箇所は決まっているため、スタックトレースは無し
			VctLogger.getLogger().warn("サーブレット-リクエストで未認証を検知したため、セッションタイムアウトURLへフォワードします。"
											+ clientInfoMessage.toString()
			);
			
			forwardUrl = this.getSessionTimeoutForwardUrl();		// セッションタイムアウト-フォワード先URL
			
		// その他(＝システムエラー)
		} else {
			
			// エラーログ出力 ※スタックトレース付き
			VctLogger.getLogger().error("サーブレット-リクエストでエラーが発生しました。システムエラーURLへフォワードします。"
												+ clientInfoMessage.toString()
											, exception
			);
			
			forwardUrl = this.getSystemErrorForwardUrl();			// システムエラー-フォワード先URL;
			
			// HTTPエラーコード設定 ※サーバ側原因のエラーを示す「500：INTERNAL_SERVER_ERROR」としておく
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		try {
			// サーブレットフォワード実行
			RequestDispatcher dispatcher = request.getRequestDispatcher(forwardUrl);
			dispatcher.forward(request, response);
			
		} catch (Exception ex) {
			// ※メッセージ付きの例外をスロー
			throw new VctRuntimeException("例外ハンドラ後のフォワードにてエラーが発生しました。"
												+ clientInfoMessage.toString()
												+ ", フォワード先URL：[" + forwardUrl + "]"
											, ex
										);
		}
	}

}
