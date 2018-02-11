/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.setvlet.spring.annotation.PreHandleNoAuth;

/**
 * Springハンドラ・インターセプタ
 *
 * @author shibano
 */
public class VctHandlerInterceptor extends HandlerInterceptorAdapter {

	/**
	 * デフォルトコンストラクタ
	 */
	public VctHandlerInterceptor() { }

	/**
	 * コントローラ処理前イベント通知
	 * @param handler ハンドラのインスタンス
	 * @return 処理継続フラグ ※true：処理を継続する ／ false：処理を中断する(＝以降の処理を実施しない)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// 静的コンテンツだった場合は何もしない
		if ( handler instanceof ResourceHttpRequestHandler ) {
			return true;
		}
		
		// ハンドラメソッドだった場合
		if ( handler instanceof HandlerMethod ) {
			// コントローラメソッド処理前イベントを実行
			return this.methodPreHandle( request, response, (HandlerMethod) handler );
		}
		
		// ※実装していない想定外なクラス型なので、警告ログを出力しておく
		VctLogger.getLogger().warn("★コントローラ処理前イベント通知にて、想定外のハンドラ・インスタンス通知あり - ハンドラクラス型：[" + handler.getClass().getSimpleName() + "]");
		
		return true;
	}

	/**
	 * コントローラメソッド処理前イベント通知
	 * @param handlerMethod ハンドラメソッド
	 * @return 処理継続フラグ ※true：処理を継続する ／ false：処理を中断する(＝以降の処理を実施しない)
	 */
	protected boolean methodPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
		
		Method requestMappingMethod = handlerMethod.getMethod();		// 要求マッピングされた対象メソッド
		
		// 認証不要アノテーション取得
		PreHandleNoAuth preHandleNoAuthAnno = AnnotationUtils.findAnnotation(requestMappingMethod, PreHandleNoAuth.class);
		
		// 認証不要アノテーション無し(＝認証要)だった場合
		if ( preHandleNoAuthAnno == null ) {
			
			// 認証判定
			boolean isAuth = this.isAuth(request, response, handlerMethod);
			
			// 認証NGだった場合
			if ( !isAuth ) {
				
				if ( VctLogger.getLogger().isDebugEnabled() ) {
					VctLogger.getLogger().debug("×認証NG - " + handlerMethod.getShortLogMessage());
				}
				
				return false;				// 処理を中断する
			}
		}
		
		// ここまで来たら「処理を継続する」
		return true;
	}

	/**
	 * 認証判定
	 * @param handlerMethod ハンドラメソッド
	 * @return 判定結果 ※true：認証OK ／ false：認証NG
	 */
	protected boolean isAuth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
		
		VctLogger.getLogger().warn("コントローラ処理前イベント通知にて、デフォルトの認証判定が実行されたため「認証NG」を返します。"
										+ "認証判定をオーバーライド実装するか、PreHandleNoAuth注釈を付けて認証判定をさせないようにしてください。"
										+ "ハンドラメソッド情報：[" + handlerMethod.getShortLogMessage() + "]"
									);
		
		return false;
	}

	/**
	 * コントローラ処理後イベント通知
	 * @param handler ハンドラのインスタンス
	 * @param modelAndView モデル＆ビュー情報 ※コントローラ処理で当該インスタンスを返していない場合はnull
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// ※現状は特に処理なし
	}

	/**
	 * リクエスト完了イベント通知
	 * @param handler ハンドラのインスタンス
	 * @param ex スローされた例外 ※スローされていない場合はnull
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// ※現状は特に処理なし
	}

}
