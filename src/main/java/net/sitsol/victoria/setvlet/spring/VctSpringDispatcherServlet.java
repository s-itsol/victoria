/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctLogKeywordConst;
import net.sitsol.victoria.consts.VctUrlPathConst;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.setvlet.spring.annotation.VctNoAuth;
import net.sitsol.victoria.setvlet.spring.annotation.VctNoLogRequestUrl;
import net.sitsol.victoria.threadlocals.ThreadLog4jNdc;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;
import net.sitsol.victoria.utils.statics.VctHttpUtils;
import net.sitsol.victoria.utils.statics.VctReflectionUtils;
import net.sitsol.victoria.utils.statics.VctServerUtils;

/**
 * Spring-Dispatcherサーブレット
 *
 * @author shibano
 */
public class VctSpringDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * サービス実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		long startMillis = System.currentTimeMillis();							// 開始時刻
		
		String userId = request.getSession().getId();
		IUserInfo loginUserInfo = null;
		
		try ( ThreadLog4jNdc threadLog4jNdc = new ThreadLog4jNdc(userId);				// ※log4j-NDCメッセージ
				ThreadUserInfo threadUserInfo = new ThreadUserInfo(loginUserInfo);		// ※ログインユーザー情報
		) {
			// システム共通リクエスト属性値設定
			request.setAttribute(VctHttpConst.ENV_NAME, VctStaticApParam.getInstance().getDispEnvName());		// 環境名
			request.setAttribute(VctHttpConst.HOST_NAME, VctServerUtils.HOST_NAME);							// ホスト名
			
			// 基底クラスのメソッド実行
			super.doService(request, response);
			
			
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
	 * ディスパッチ実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// リクエストマッピングメソッド取得
		Method requestMappingMethod = this.getRequestMappingMethod(request);
		
		// HTTPリクエストURLログ出力
		this.httpRequestUrlLog(request, response, requestMappingMethod);
		
		// 認証
		this.auth(request, response, requestMappingMethod);
		
		// 基底クラスのメソッド実行
		super.doDispatch(request, response);
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
	 * HTTPリクエストURLログ出力
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param requestMappingMethod リクエストマッピングメソッド
	 */
	protected void httpRequestUrlLog(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) throws Exception {
		
		if ( request == null || requestMappingMethod == null ) { return; }
		
		// HTTPリクエストURLログ出力不要アノテーション取得
		VctNoLogRequestUrl targetAnno = AnnotationUtils.findAnnotation(requestMappingMethod, VctNoLogRequestUrl.class);
		
		// ログ出力不要アノテーションあり
		if ( targetAnno != null ) { return; }
		
		// HTTPリクエストURLログ出力フラグOFF
		if ( !VctStaticApParam.getInstance().isHttpRequestUrlLogOutputFlg() ) { return; }
		
		// HTTPリクエストURLログ出力
		StringBuilder message = new StringBuilder();
		message.append(VctLogKeywordConst.REQURL).append(" ").append(VctHttpUtils.createUrlQueryString(request));
		
		VctLogger.getLogger().info(message.toString());
	}

	/**
	 * 認証
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param requestMappingMethod リクエストマッピングメソッド
	 */
	protected void auth(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) throws Exception {
		
		if ( request == null || requestMappingMethod == null ) { return; }
		
		// 認証不要アノテーション取得
		VctNoAuth targetAnno = AnnotationUtils.findAnnotation(requestMappingMethod, VctNoAuth.class);
		
		// 認証不要アノテーションあり
		if ( targetAnno != null ) { return; }
		
		// 認証判定
		boolean isAuth = this.isAuth(request, response, requestMappingMethod);
		
		// 認証OK
		if ( isAuth ) { return; }
		
		// ここまで来たら、セッションタイムアウト
		this.doSessionTimeout(request, response, requestMappingMethod);
	}

	/**
	 * 認証判定
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param handlerMethod ハンドラメソッド
	 * @return 判定結果 ※true：認証OK ／ false：認証NG
	 */
	protected boolean isAuth(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) throws Exception {
		
		VctLogger.getLogger().warn("デフォルトの認証判定が実行されたため「認証NG」を返します。"
										+ "オーバーライド実装するか、" + VctNoAuth.class.getSimpleName() + "注釈を付けて認証判定をさせないようにしてください。"
										+ "元リクエスト-メソッド名：[" + VctReflectionUtils.getSimpleClassMethodInfo(requestMappingMethod) + "]"
									);
		
		return false;
	}

	/**
	 * セッションタイムアウト実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param requestMappingMethod リクエストマッピングメソッド
	 */
	protected void doSessionTimeout(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) throws Exception {
		
		String forwardUrl = null;
		
		try {
			VctLogger.getLogger().warn("未認証のため、セッションタイムアウトURLへフォワードします。"
											+ "元リクエスト-メソッド名：[" + VctReflectionUtils.getSimpleClassMethodInfo(requestMappingMethod) + "]"
			);
			
			// セッションタイムアウト-フォワード先URL取得
			forwardUrl = this.getSessionTimeoutForwardUrl();
			
			// サーブレットフォワード実行
			RequestDispatcher dispatcher = request.getRequestDispatcher(forwardUrl);
			dispatcher.forward(request, response);
			
		} catch (Exception ex) {
			// メッセージを添えた例外をスローしておく
			throw new VctRuntimeException("セッションタイムアウト時のフォワードでエラーが発生しました。"
													+ "フォワード先URL：[" + forwardUrl + "]"
													+ ", 元リクエスト-メソッド名：[" + VctReflectionUtils.getSimpleClassMethodInfo(requestMappingMethod) + "]"
												, ex
											);
		}
	}

	/**
	 * セッションタイムアウト-フォワード先URL取得
	 * @return フォワード先URL
	 */
	protected String getSessionTimeoutForwardUrl() {
		
		VctLogger.getLogger().warn("デフォルトのセッションタイムアウト-フォワード先URL取得処理が実行されたため、"
										+ "「APコンテキストルートURL」を返します。"
										+ "任意のURLへフォワードさせたい場合は、オーバーライド実装してください。"
									);
		
		return VctUrlPathConst.Root.DIR;
	}

}
