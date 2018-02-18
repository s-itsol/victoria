/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Springハンドラ・インターセプタ
 *  ※Victoriaでは今のところ特に利用用途なし
 *
 * @author shibano
 */
public class VctSpringHandlerInterceptor extends HandlerInterceptorAdapter {

	/**
	 * デフォルトコンストラクタ
	 */
	public VctSpringHandlerInterceptor() { }

	/**
	 * コントローラ処理前イベント通知
	 * @param handler ハンドラのインスタンス
	 * @return 処理継続フラグ ※true：処理を継続する ／ false：処理を中断する(＝以降の処理を実施しない)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
