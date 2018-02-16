/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import net.sitsol.victoria.log4j.VctLogger;

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
		// ※現状は特に処理なし
		return true;
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
