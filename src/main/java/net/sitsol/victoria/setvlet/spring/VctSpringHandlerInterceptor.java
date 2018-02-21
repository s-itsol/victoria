/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import net.sitsol.victoria.annotation.servlet.VctFromMapping;
import net.sitsol.victoria.annotation.servlet.VctNoLogRequestUrl;
import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctLogKeywordConst;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctHttpUtils;
import net.sitsol.victoria.utils.statics.VctServerUtils;
import net.sitsol.victoria.utils.statics.VctSpringAnnotationUtils;

/**
 * Springハンドラ・インターセプタ
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
		
		// 静的コンテンツだった場合は何もしない
		if ( handler instanceof ResourceHttpRequestHandler ) {
			// ※特に処理なし
			
		// ハンドラメソッドだった場合 ※通常はこのルートしか来ない想定
		} else if ( handler instanceof HandlerMethod ) {
			
			HandlerMethod handlerMethod = (HandlerMethod) handler;		// ハンドラメソッド
			Method mappingMethod = handlerMethod.getMethod();			// マッピングメソッド
			
			// コントローラメソッド処理前イベントを実行
			this.methodPreHandle(request, response, mappingMethod);
			
		// それ以外
		} else {
			// ※実装していない想定外なクラス型なので、警告ログを出力しておく
			VctLogger.getLogger().warn("★コントローラ処理前イベント通知にて、想定外のハンドラ・インスタンス通知あり"
											+ " - ハンドラクラス型：[" + ( handler != null ? handler.getClass().getSimpleName() : null ) + "]"
										);
		}
		
		return true;		// ※常に「処理を継続する」を返しておく
	}

	/**
	 * コントローラ処理後イベント通知
	 *  ※handlerがObject型なので、アプリ側でオーバーライド出来ないようfinalにしてある。
	 *  
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param handler ハンドラのインスタンス
	 * @param modelAndView モデル＆ビュー情報 ※コントローラ処理で当該インスタンスを返していない場合はnull
	 */
	@Override
	public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		// 静的コンテンツだった場合は何もしない
		if ( handler instanceof ResourceHttpRequestHandler ) {
			// ※特に処理なし
			
		// ハンドラメソッドだった場合 ※通常はこのルートしか来ない想定
		} else if ( handler instanceof HandlerMethod ) {
			
			HandlerMethod handlerMethod = (HandlerMethod) handler;		// ハンドラメソッド
			Method mappingMethod = handlerMethod.getMethod();			// マッピングメソッド
			
			// コントローラメソッド処理後イベントを実行
			this.methodPostHandle(request, response, mappingMethod, modelAndView);
			
		// それ以外
		} else {
			// ※実装していない想定外なクラス型なので、警告ログを出力しておく
			VctLogger.getLogger().warn("★コントローラ処理後イベント通知にて、想定外のハンドラ・インスタンス通知あり"
											+ " - ハンドラクラス型：[" + ( handler != null ? handler.getClass().getSimpleName() : null ) + "]"
										);
		}
	}

	/**
	 * リクエスト完了イベント通知
	 *  ※handlerがObject型なので、アプリ側でオーバーライド出来ないようfinalにしてある。
	 *  
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param handler ハンドラのインスタンス
	 * @param exception スローされた例外 ※スローされていない場合はnull
	 */
	@Override
	public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
		
		// 静的コンテンツだった場合は何もしない
		if ( handler instanceof ResourceHttpRequestHandler ) {
			// ※特に処理なし
			
		// ハンドラメソッドだった場合 ※通常はこのルートしか来ない想定
		} else if ( handler instanceof HandlerMethod ) {
			
			HandlerMethod handlerMethod = (HandlerMethod) handler;		// ハンドラメソッド
			Method mappingMethod = handlerMethod.getMethod();			// マッピングメソッド
			
			// メソッドリクエスト完了イベント通知
			this.methodAfterCompletion(request, response, mappingMethod, exception);
			
		// それ以外
		} else {
			// ※実装していない想定外なクラス型なので、警告ログを出力しておく
			VctLogger.getLogger().warn("★コントローラ処理後イベント通知にて、想定外のハンドラ・インスタンス通知あり"
											+ " - ハンドラクラス型：[" + ( handler != null ? handler.getClass().getSimpleName() : null ) + "]"
										);
		}
	}

	/**
	 * コントローラメソッド処理前イベント通知
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param mappingMethod マッピングメソッド
	 */
	protected void methodPreHandle(HttpServletRequest request, HttpServletResponse response, Method mappingMethod) throws Exception {
		
		// システム共通リクエスト属性値設定
		this.setCommonRequestAttributes(request, mappingMethod);
		
		// HTTPリクエストURLログ出力
		this.httpRequestUrlLog(request, mappingMethod);
		
		// 認証
		this.auth(request, mappingMethod);
	}

	/**
	 * システム共通リクエスト属性値設定
	 * @param request HTTPサーブレットリクエスト
	 * @param mappingMethod マッピングメソッド
	 */
	protected void setCommonRequestAttributes(HttpServletRequest request, Method mappingMethod) throws Exception {
		
		// システム共通リクエスト属性値設定
		request.setAttribute(VctHttpConst.ENV_NAME, VctStaticApParam.getInstance().getDispEnvName());		// 環境名
		request.setAttribute(VctHttpConst.HOST_NAME, VctServerUtils.HOST_NAME);								// ホスト名
	}

	/**
	 * HTTPリクエストURLログ出力
	 * @param request HTTPサーブレットリクエスト
	 * @param mappingMethod マッピングメソッド
	 */
	protected void httpRequestUrlLog(HttpServletRequest request, Method mappingMethod) throws Exception {
		
		if ( request == null ) { return; }
		
		// HTTPリクエストURLログ出力フラグOFF
		if ( !VctStaticApParam.getInstance().isHttpRequestUrlLogOutputFlg() ) { return; }
		
		// HTTPリクエストURLログ出力不要アノテーションあり
		if ( VctSpringAnnotationUtils.hasAnnotation(mappingMethod, VctNoLogRequestUrl.class) ) { return; }
		
		// HTTPリクエストURLログ出力
		StringBuilder message = new StringBuilder();
		message.append(VctLogKeywordConst.REQURL).append(" ").append(VctHttpUtils.createUrlQueryString(request));
		
		VctLogger.getLogger().info(message.toString());
	}

	/**
	 * 認証
	 *  ※デフォルトではログインユーザ情報を取得できない(≒ログイン状態ではない)場合にのみ「認証NG」として、
	 *    セッションタイムアウト例外をスローする。
	 *    各アプリ独自の認証判定は、本メソッドをオーバーライド実装することを想定しており、
	 *    その判定NG時、さらに任意のページへ遷移させたい場合などは、
	 *    独自の業務例外をスローしつつ、「例外ハンドラ実行」メソッドもオーバーライド実装することで可能なはず。
	 * 
	 * @param request HTTPサーブレットリクエスト
	 * @param mappingMethod マッピングメソッド
	 */
	protected void auth(HttpServletRequest request, Method mappingMethod) throws Exception {
		
		if ( request == null ) { return; }
		
//		// 認証不要アノテーションありは処理しない
//		if ( VctSpringAnnotationUtils.hasAnnotation(mappingMethod, VctNoAuth.class) ) {
//			return;
//		}
		
		// ●●●TODO：
	}

	/**
	 * コントローラメソッド処理後イベント通知
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param mappingMethod マッピングメソッド
	 * @param modelAndView モデル＆ビュー情報 ※コントローラ処理で当該インスタンスを返していない場合はnull
	 */
	protected void methodPostHandle(HttpServletRequest request, HttpServletResponse response, Method mappingMethod, ModelAndView modelAndView) throws Exception {
		
		System.out.println("★methodPostHandle-request:" + request.getAttribute("xxx"));
		
		// ●●●TODO：
		if ( modelAndView != null ) {
			
			System.out.println("★methodPostHandle-modelAndView:" + modelAndView.getModelMap().get("xxx"));
			//	→こちらはダメだった
			
			VctFromMapping targetAnno = VctSpringAnnotationUtils.findAnnotation(mappingMethod, VctFromMapping.class);
			
			String formName  = targetAnno != null ? targetAnno.name() : null;
			
			if ( formName != null ) {
				
				SessionAttributes sessAttrs = VctSpringAnnotationUtils.findAnnotation(mappingMethod.getDeclaringClass(), SessionAttributes.class);
				
				if ( sessAttrs != null ) {
					
					for ( int idx = 0; idx < sessAttrs.types().length; idx++) {
						
						String sessAttrName = sessAttrs.names()[idx];
						
						if ( !formName.equals(sessAttrName) ) {
							continue;
						}
						
						Class<?> sessAttrType = sessAttrs.types()[idx];
						
						String sessFromName = StringUtils.uncapitalize( sessAttrType.getSimpleName() );
						Object formObj = request.getSession().getAttribute(sessFromName);
						
						if ( formObj != null ) {
							modelAndView.addObject(formName, formObj);
						}
						
						break;
					}
				}
			}
		}
		
		
	}

	/**
	 * メソッドリクエスト完了イベント通知
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 * @param mappingMethod マッピングメソッド
	 * @param exception スローされた例外 ※スローされていない場合はnull
	 */
	protected void methodAfterCompletion(HttpServletRequest request, HttpServletResponse response, Method mappingMethod, Exception exception) throws Exception {
		// ※派生クラス側でオーバーライド実装させる想定なので、こちらでは特に処理なし。
		
		System.out.println("★methodAfterCompletion-request:" + request.getAttribute("xxx"));

		// ●●●TODO：
		Enumeration<String> sesNames = request.getSession().getAttributeNames();
		
		while ( sesNames.hasMoreElements() ) {
			String sesName = sesNames.nextElement();
			System.out.println(sesName + " -> " + request.getSession().getAttribute(sesName));
		}
	}

}
