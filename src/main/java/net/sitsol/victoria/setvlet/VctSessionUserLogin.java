package net.sitsol.victoria.setvlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sitsol.victoria.beanfactory.spring.VctBeanFactory;
import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;
import net.sitsol.victoria.utils.statics.VctSessionAccessUtils;

/**
 * HTTPセッション・ユーザーログインクラス
 *
 * @author shibano
 */
public class VctSessionUserLogin {

	/* -- static ----------------------------------------------------------- */

	public static final String SPRING_BEAN_NAME	= "SessionUserLogin";		// Springで管理されるこのクラスのビーン名


	/**
	 * インスタンスの取得
	 * @return 本クラスのインスタンス
	 */
	public static VctSessionUserLogin getInstance() {
		return (VctSessionUserLogin) VctBeanFactory.getInstance().getBean(VctStaticApParam.SPRING_BEAN_NAME, VctSessionUserLogin.class);
	}


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * HTTPセッション・ログイン
	 * @param request HTTPサーブレットリクエスト
	 * @param userInfo	ログインするユーザー情報
	 */
	public void sessionLogin(HttpServletRequest request, IUserInfo userInfo) {

		String sessionId = null;
		String userId = null;

		try {
			if ( userInfo != null ) {
				userId = userInfo.getUserId();
			}

			if ( request != null && request.getSession() != null ) {
				sessionId = request.getSession().getId();
			}

			// ログインするユーザー情報をセッションへ保持
			VctSessionAccessUtils.setLoginUserInfo(request, userInfo);
			// スレッド毎ユーザー情報リフレッシュ
			ThreadUserInfo.reflashThreadUserInfo(userInfo);

			// ログ出力
			StringBuilder str = new StringBuilder();
			str.append("■ログイン - ユーザーID：[").append(userId).append("], セッションID：[").append(sessionId).append("]");

			VctLogger.getLogger().info(str.toString());

		} catch (Exception ex) {
			throw new VctRuntimeException("ログイン処理でエラーが発生しました。"
													+ "セッションID：[" + sessionId + "]"
													+ ", ユーザーID：[" + userId + "]"
												, ex
											);
		}
	}

	/**
	 * HTTPセッション・ログアウト
	 * @param request HTTPサーブレットリクエスト
	 */
	public void sessionLogout(HttpServletRequest request) {

		String sessionId = null;
		String loginedUserId = null;

		try {
			{
				// ログインユーザー情報を取得
				IUserInfo userProfile = (IUserInfo) this.getUserInfo(request);

				// 取得できた場合 ※セッションタイムアウト後などは取得できないことがある
				if ( userProfile != null ) {
					// ログアウトするユーザーIDを確保
					loginedUserId = userProfile.getUserId();
				}
			}

			// 有効なセッション取得 ※既にセッションが無効になっていたら取得しない
			HttpSession session = request.getSession(false);

			// 取得できた場合
			if ( session != null ) {
				// セッションIDを確保
				sessionId = session.getId();
				// セッションを全て破棄
				session.invalidate();
			}

			// ログ出力
			StringBuilder str = new StringBuilder();
			str.append("■ログアウト - ユーザーID：[").append(loginedUserId).append("], セッションID：[").append(sessionId).append("]");

			VctLogger.getLogger().info(str.toString());

		} catch (Exception ex) {
			throw new VctRuntimeException("ログアウト処理でエラーが発生しました。"
													+ "セッションID：[" + sessionId + "]"
													+ "ユーザーID：[" + loginedUserId + "]"
												, ex
											);
		}
	}

	/**
	 * ログイン中判定
	 *
	 * @param request HTTPサーブレットリクエスト
	 * @return 判定結果 ※true：ログイン中 ／false：ログイン中ではない
	 */
	public boolean isLogin(HttpServletRequest request) {
		// セッションからログイン中のユーザー情報を得られるか否かで判定
		return this.getUserInfo(request) != null;
	}

	/**
	 * ログインユーザー情報の取得
	 * @param request HTTPサーブレットリクエスト
	 * @return ログインユーザー情報 ※未ログインの場合はnull
	 */
	public IUserInfo getUserInfo(HttpServletRequest request) {

		IUserInfo userInfo = null;
		{
			if ( request != null && request.getSession() != null ) {
				userInfo = VctSessionAccessUtils.getLoginUserInfo(request);
			}
		}

		return userInfo;
	}

	/**
	 * ログインユーザーIDの取得
	 * @param request HTTPサーブレットリクエスト
	 * @return ログインユーザーID ※未ログインの場合はnull
	 */
	public String getUserId(HttpServletRequest request) {

		String userId = null;
		{
			// ログインユーザー情報の取得
			IUserInfo userProfile = this.getUserInfo(request);
			if ( userProfile != null ) {
				userId = userProfile.getUserId();
			}
		}

		return userId;
	}

}
