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
import net.sitsol.victoria.log4j.VctLogger;

/**
 * Spring-ハンドラ例外リゾルバ
 *  ※Victoriaでは今のところ特に利用用途なし
 * 
 * @author shibano
 */
public class VctSpringHandlerExceptionResolver implements HandlerExceptionResolver {

	/**
	 * 例外通知
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param handler ハンドラのインスタンス
	 * @param exception 発生例外
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
		
		//クライアント情報生成
		StringBuilder clientInfoMessage = new StringBuilder();
		clientInfoMessage.append("USER-AGENT：[").append(request.getHeader(VctHttpConst.USER_AGENT)).append("]");
		clientInfoMessage.append(", REFERER：[").append(request.getHeader(VctHttpConst.REFERER)).append("]");
		clientInfoMessage.append(", REQUEST-URL：[").append(request.getRequestURL()).append("]");
		
		// エラーログ出力
		VctLogger.getLogger().error("サーブレット-リクエストでエラーが発生しました。"
											+ clientInfoMessage.toString()
										, exception
		);
		
		// モデル＆ビュー生成
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setStatus(HttpStatus.BAD_REQUEST);		// HTTPエラーコード設定
		
		return modelAndView;
	}

}
