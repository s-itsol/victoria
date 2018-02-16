/**
 * 
 */
package net.sitsol.victoria.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.log4j.VctLogger;

/**
 * victoria共通-コントローラ 基底クラス
 * 
 * @author shibano
 */
public abstract class VctController {

	/**
	 * セッション内フォーム破棄
	 * @param request HTTPサーブレットリクエスト
	 * @param removeFormName フォーム名
	 */
	protected void removeSessionFrom(HttpServletRequest request, String removeFormName) {
		
		if ( request == null || request.getSession() == null ) {
			return;
		}
		
		// セッションからフォームを破棄
		request.getSession().removeAttribute(removeFormName);
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
