/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctLogKeywordConst;
import net.sitsol.victoria.exceptions.VctHttpRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.setvlet.spring.annotation.VctNoAuth;
import net.sitsol.victoria.setvlet.spring.annotation.VctNoLogRequestUrl;
import net.sitsol.victoria.threadlocals.ThreadLog4jNdc;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;
import net.sitsol.victoria.utils.statics.VctHttpUtils;

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
			// 基底クラスのメソッド実行
			super.doService(request, response);
			
			
			long execMillis = System.currentTimeMillis() - startMillis;			// 処理時間(ms) ※「終了時刻」－「開始時刻」
			
			// HTTP要求の警告時間(ms)
			int warnMillis = VctStaticApParam.getInstance().getWarningHttpRequestExecuteMillis();
			
			// 超過していた場合
			if ( execMillis >= warnMillis ) {
				
				// HTTP要求-処理時間警告ログ出力
				StringBuilder message = new StringBuilder();
				message.append(VctLogKeywordConst.REQURL).append(" ").append(warnMillis).append("(ms)を超過したHTTP要求がありました。");
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
		
		// システム共通リクエスト属性値設定
		request.setAttribute(VctHttpConst.ENV_NAME, VctStaticApParam.getInstance().getDispEnvName());		// 環境名
		
		// ハンドラメソッド取得
		HandlerMethod handlerMethod = this.getHandlerMethod(request);
		Method requestMappingMethod = handlerMethod != null ? handlerMethod.getMethod() : null;
		
		// HTTPリクエストURLログ出力
		this.httpRequestUrlLog(request, requestMappingMethod);
		
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
	 * ハンドラメソッド取得
	 * @param request HTTPサーブレットリクエスト
	 * @return ハンドラメソッド ※得られなかった場合はnull
	 */
	protected HandlerMethod getHandlerMethod(HttpServletRequest request) throws Exception {
		
		HandlerExecutionChain handlerChain = this.getHandler(request);
		
		if ( handlerChain == null ) { return null; }
		
		Object handler = handlerChain.getHandler();
		
		// ハンドラメソッドだった場合はキャストして返す
		return ( handler != null && handler instanceof HandlerMethod )
					? (HandlerMethod) handler
					: null
		;
	}

	/**
	 * HTTPリクエストURLログ出力
	 * @param request HTTPサーブレットリクエスト
	 * @param requestMappingMethod リクエストマッピングメソッド
	 */
	protected void httpRequestUrlLog(HttpServletRequest request, Method requestMappingMethod) {
		
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
	protected void auth(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) {
		
		if ( request == null || requestMappingMethod == null ) { return; }
		
		// 認証不要アノテーション取得
		VctNoAuth targetAnno = AnnotationUtils.findAnnotation(requestMappingMethod, VctNoAuth.class);
		
		// 認証不要アノテーションあり
		if ( targetAnno != null ) { return; }
		
		// 認証判定
		boolean isAuth = this.isAuth(request, requestMappingMethod);
		
		// 認証OK
		if ( isAuth ) { return; }
		
		// ここまで来たら、セッションタイムアウト実行
		this.sessionTimeoutExec(request, response, requestMappingMethod);
	}

	/**
	 * 認証判定
	 * @param handlerMethod ハンドラメソッド
	 * @return 判定結果 ※true：認証OK ／ false：認証NG
	 */
	protected boolean isAuth(HttpServletRequest request, Method requestMappingMethod) {
		
		VctLogger.getLogger().warn("デフォルトの認証判定が実行されたため「認証NG」を返します。"
										+ "オーバーライド実装するか、" + VctNoAuth.class.getSimpleName() + "注釈を付けて認証判定をさせないようにしてください。"
										+ "クラス#メソッド名：[" + requestMappingMethod.getDeclaringClass().getSimpleName() + "#" + requestMappingMethod.getName() + "]"
									);
		
		return false;
	}

	/**
	 * セッションタイムアウト実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param requestMappingMethod リクエストマッピングメソッド
	 */
	protected void sessionTimeoutExec(HttpServletRequest request, HttpServletResponse response, Method requestMappingMethod) {
		
		VctLogger.getLogger().warn("デフォルトのセッションタイムアウト処理が実行されたため、エラー終了します。"
										+ "オーバーライド実装してください。"
										+ "クラス#メソッド名：[" + requestMappingMethod.getDeclaringClass().getSimpleName() + "#" + requestMappingMethod.getName() + "]"
									);
		
		// 例外をスロー
		throw new VctHttpRuntimeException(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.ordinal());
	}

}
