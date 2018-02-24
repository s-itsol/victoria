/**
 *
 */
package net.sitsol.victoria.utils.statics;

import javax.servlet.http.HttpServletRequest;

import net.sitsol.victoria.models.userinfo.IUserInfo;

/**
 * HTTPセッション属性値アクセスユーティリティ
 *  セッション属性値への設定・取得をラッピングしただけのユーティリティ。
 *  セッション名をリテラル記述にした属性値の直接格納は、どこのWEBシステムでも
 *  「影響範囲が特定し切れず、拡張・保守が劇的に困難になる」という問題を抱えているので、
 *  本クラスでラッピングして使うようなコーディングを推奨する。
 *
 * @author shibano
 */
public class VctSessionAccessUtils {

	// セッション名定数
	public static final String ATTR_LOGIN_USER_INFO			= "loginUserInfo";			// ログインユーザー情報


	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctSessionAccessUtils() {}

	/**
	 * セッション属性設定
	 * @param request HTTPサーブレットリクエスト
	 * @param sessionName セッション名
	 * @param sessionValue セッション格納値
	 */
	protected static void setSessionAttribute(HttpServletRequest request, String sessionName, Object sessionValue) {
		// セッション属性設定
		request.getSession().setAttribute(sessionName, sessionValue);
	}

	/**
	 * セッション属性破棄
	 * @param request HTTPサーブレットリクエスト
	 * @param sessionName セッション名
	 */
	protected static void removeSessionAttribute(HttpServletRequest request, String sessionName) {
		// セッション属性破棄
		request.getSession().removeAttribute(sessionName);
	}

	/**
	 * セッション属性取得
	 * @param request HTTPサーブレットリクエスト
	 * @param sessionName セッション名
	 * @return セッション格納値
	 */
	protected static Object getSessionAttribute(HttpServletRequest request, String sessionName) {
		// セッション属性取得
		return request.getSession().getAttribute(sessionName);
	}

	/**
	 * ログインユーザー情報
	 */
	public static void setLoginUserInfo(HttpServletRequest request, IUserInfo userInfo) {
		setSessionAttribute(request, ATTR_LOGIN_USER_INFO, userInfo);
	}

	/**
	 * ログインユーザー情報
	 */
	public static IUserInfo getLoginUserInfo(HttpServletRequest request) {
		return (IUserInfo) getSessionAttribute(request, ATTR_LOGIN_USER_INFO);
	}

}
