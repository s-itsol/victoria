/**
 * 
 */
package net.sitsol.victoria.controllers;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;

import net.sitsol.victoria.annotation.servlet.VctSuccessForward;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.forms.VctForm;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctAnnotationAccessUtils;
import net.sitsol.victoria.utils.statics.VctReflectionUtils;

/**
 * victoria共通-コントローラ 基底クラス
 * 
 * @author shibano
 */
public abstract class VctController {

	@Autowired(required = false)
	private HttpServletRequest request;		// HTTPサーブレットリクエスト

	/**
	 * HTTPサーブレットリクエスト取得
	 * @return HTTPサーブレットリクエスト
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}
	
	/**
	 * バインド前フォーム初期化
	 * @param request HTTPサーブレットリクエスト
	 * @param binder WEBデータバインダー
	 */
	@InitBinder()
	public void resetFrom(HttpServletRequest request, WebDataBinder binder) {
		
		Object targetObj = binder.getTarget();
		
		if ( targetObj == null || !VctReflectionUtils.hasSuperClass(targetObj.getClass(), VctForm.class) ) {
			return;
		}
		
		VctForm form = (VctForm) targetObj;
		
		form.reset();
	}

	/**
	 * セッション内フォーム破棄
	 * @param request HTTPサーブレットリクエスト
	 * @param removeFormName フォーム名
	 */
	@Deprecated
	protected void removeSessionFrom(HttpServletRequest request, String removeFormName) {
		
		if ( request == null || request.getSession() == null ) {
			return;
		}
		
		// マッピングメソッド取得
		Method mappingMethod = (Method) this.getRequest().getAttribute(VctHttpConst.REQ_MAPPING_METHOD);
		
		String sessFromName = VctAnnotationAccessUtils.getFromMappingSessionName(mappingMethod);
		
		// セッションからフォームを破棄
		request.getSession().removeAttribute(sessFromName);
	}

	/**
	 * 入力ページフォワード
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView inputForward() {
		// ※モデル＆ビュー情報は新規インスタンス生成
		return this.inputForward(new ModelAndView());
	}

	/**
	 * 入力ページフォワード
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView inputForward(ModelAndView modelAndView) {
		
		// マッピングメソッド取得
		Method mappingMethod = (Method) this.getRequest().getAttribute(VctHttpConst.REQ_MAPPING_METHOD);
		
		// フォワード先URL取得
		String forwardUrl = VctAnnotationAccessUtils.getInputForwardUrl(mappingMethod);
		
		// アノテーション指定フォワード実行
		return this.doAnnotationForward(VctSuccessForward.class, forwardUrl, modelAndView);
	}

	/**
	 * 正常終了フォワード
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView succsessForward() {
		// ※モデル＆ビュー情報は新規インスタンス生成
		return this.succsessForward(new ModelAndView());
	}

	/**
	 * 正常終了フォワード
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView succsessForward(ModelAndView modelAndView) {
		
		// マッピングメソッド取得
		Method mappingMethod = (Method) this.getRequest().getAttribute(VctHttpConst.REQ_MAPPING_METHOD);
		
		// フォワード先URL取得
		String forwardUrl = VctAnnotationAccessUtils.getSuccessForwardUrl(mappingMethod);
		
		// アノテーション指定フォワード実行
		return this.doAnnotationForward(VctSuccessForward.class, forwardUrl, modelAndView);
	}

	/**
	 * アノテーション指定フォワード実行
	 * @param targetAnnotaion 対象アノテーション
	 * @param forwardUrl フォワード先URL
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	private <AnnotationClass> ModelAndView doAnnotationForward(Class<AnnotationClass> targetAnnotaion, String forwardUrl, ModelAndView modelAndView) {
		
		if ( forwardUrl == null ) {
			// フォワード出来ないので明示的なメッセージ付きで例外を発生させてしまう
			throw new VctRuntimeException("フォワード先URLが得られませんでした。"
												+ "コントローラのメソッドに" + targetAnnotaion.getSimpleName() + "注釈を付け忘れていないか、確認してください。"
											);
		}
		
		// フォワード-AP内URL
		return this.forwardForApp(forwardUrl, modelAndView);
	}

	/**
	 * フォワード-AP内URL
	 * @param forwardAppUrl	IN    ：フォワード先-AP内URL(＝GETパラメータ付きも可) ※APコンテキストルート「/」から始まる絶対パス ／ 現在パスからの相対パス 例：「/xxxmanage/xxxupdate.do?xxxId=xxx」「xxxmanagetop.vm」
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView forwardForApp(String forwardAppUrl) {
		// ※モデル＆ビュー情報は新規インスタンス生成
		return this.forwardForApp(forwardAppUrl, new ModelAndView());
	}

	/**
	 * フォワード-AP内URL
	 * @param forwardAppUrl	IN    ：フォワード先-AP内URL(＝GETパラメータ付きも可) ※APコンテキストルート「/」から始まる絶対パス ／ 現在パスからの相対パス 例：「/xxxmanage/xxxupdate.do?xxxId=xxx」「xxxmanagetop.vm」
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView forwardForApp(String forwardAppUrl, ModelAndView modelAndView) {
		
		this.getRequest().setAttribute("xxx", "渡せるのか？テスト");
		
		modelAndView.setViewName(forwardAppUrl);		// ※ビュー名：「フォワード先-AP内URL」
		
		return modelAndView;
	}

	/**
	 * リダイレクト-外部サイトURL
	 * @param redirectOuterUrl	IN    ：リダイレクト先-外部サイトURL(＝GETパラメータ付きも可) ※プロトコルから始まるフルURL 例：「http://www.yahooo.co.jp/」「https://www.google.co.jp/search?q=s-itsol」
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView sendRedirectForOuter(String redirectOuterUrl) {
		// ※モデル＆ビュー情報は新規インスタンス生成
		return this.sendRedirectForOuter(redirectOuterUrl, new ModelAndView());
	}

	/**
	 * リダイレクト-外部サイトURL
	 * @param redirectOuterUrl	IN    ：リダイレクト先-外部サイトURL(＝GETパラメータ付きも可) ※プロトコルから始まるフルURL 例：「http://www.yahooo.co.jp/」「https://www.google.co.jp/search?q=s-itsol」
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView sendRedirectForOuter(String redirectOuterUrl, ModelAndView modelAndView) {
		
		// 外部サイトへのリダイレクトは画面遷移を追えなくなるため、最後に必ずログを出力する
		VctLogger.getLogger().info(" --> 外部サイトへのリダイレクト - URL：[" + redirectOuterUrl + "]");
		
		// リダイレクト
		return this.sendRedirect(redirectOuterUrl, modelAndView);
	}

	/**
	 * リダイレクト-AP内URL
	 * @param redirectAppUrl	IN    ：リダイレクト先-AP内URL(＝GETパラメータ付きも可) ※APコンテキストルート「/」から始まる絶対パス ／ 現在パスからの相対パス 例：「/xxxmanage/xxxupdate.do?xxxId=xxx」「xxxmanagetop.do」
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView sendRedirectForApp(String redirectAppUrl) {
		// ※モデル＆ビュー情報は新規インスタンス生成
		return this.sendRedirectForApp(redirectAppUrl, new ModelAndView());
	}

	/**
	 * リダイレクト-AP内URL
	 * @param redirectAppUrl	IN    ：リダイレクト先-AP内URL(＝GETパラメータ付きも可) ※APコンテキストルート「/」から始まる絶対パス ／ 現在パスからの相対パス 例：「/xxxmanage/xxxupdate.do?xxxId=xxx」「xxxmanagetop.do」
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	protected ModelAndView sendRedirectForApp(String redirectAppUrl, ModelAndView modelAndView) {
		// リダイレクト
		return this.sendRedirect(redirectAppUrl, modelAndView);
	}

	/**
	 * リダイレクト
	 * @param redirectUrl		IN    ：リダイレクト先URL
	 * @param modelAndView		IN/OUT：モデル＆ビュー情報
	 * @return モデル＆ビュー情報
	 */
	private ModelAndView sendRedirect(String redirectUrl, ModelAndView modelAndView) {
		
		modelAndView.setViewName( VctHttpConst.REDIRECT_PREFIX + redirectUrl );		// ※ビュー名：「リダイレクト-プレフィックス」＋「リダイレクト先URL」
		
		return modelAndView;
	}

}
