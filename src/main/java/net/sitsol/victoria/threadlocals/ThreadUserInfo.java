package net.sitsol.victoria.threadlocals;

import org.apache.commons.lang.StringUtils;

import net.sitsol.victoria.consts.VctConst;
import net.sitsol.victoria.models.userinfo.IUserInfo;

/**
 * スレッド毎ユーザー情報保持クラス
 *  try-with-resourceにて、スレッド開始～終了を包括して使用することを想定している。
 *
 * @author shibano
 */
public class ThreadUserInfo implements AutoCloseable {

	/* -- static ----------------------------------------------------------- */

	private static ThreadLocal<IUserInfo> userInfoMap = new ThreadLocal<IUserInfo>();		// スレッド毎ユーザー情報

	/**
	 * スレッド毎ユーザー情報リフレッシュ
	 *  ※セッションログイン時以外からは呼ばれない想定
	 * @param userInfo ユーザー情報
	 */
	public static void reflashThreadUserInfo(IUserInfo userInfo) {
		// スレッド毎ユーザー情報の保持開始 ※既に保持していたら差し替え
		beginThreadUserInfo(userInfo);
	}

	/**
	 * スレッド毎ユーザー情報の保持開始
	 * @param userInfo ユーザー情報 ※ログイン前などユーザー情報が無い場合はnull
	 */
	private static void beginThreadUserInfo(IUserInfo userInfo) {
		// スレッド毎ユーザー情報の設定
		userInfoMap.set(userInfo);
	}

	/**
	 * スレッド毎ユーザー情報の保持終了
	 */
	private static void completeThreadUserInfo() {
		// スレッド毎ユーザー情報の破棄
		userInfoMap.remove();
	}

	/**
	 * 現スレッド-ユーザー情報の取得
	 * @return ユーザー情報
	 */
	public static IUserInfo getCurrentThreadUserInfo() {
		return userInfoMap.get();
	}

	/**
	 * 現スレッド-ユーザーIDの取得
	 * @return ユーザーID
	 */
	public static String getCurrentThreadUserId() {

		String userId = VctConst.UNNOWN_USER_ID;		// デフォルト：特定不能
		{
			// 現スレッド-ユーザー情報の取得
			IUserInfo userProfile = ThreadUserInfo.getCurrentThreadUserInfo();
			// ユーザーIDが得られた場合
			if ( userProfile != null && !StringUtils.isEmpty(userProfile.getUserId()) ) {
				// ユーザーIDを確保
				userId = userProfile.getUserId();
			}
		}

		return userId;
	}


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param userInfo ユーザー情報 ※ログイン前などユーザー情報が無い場合はnull
	 */
	public ThreadUserInfo(IUserInfo userInfo) {
		// スレッド毎ユーザー情報の保持開始
		beginThreadUserInfo(userInfo);
	}

	/**
	 * リソースクローズ
	 */
	@Override
	public void close() {
		// スレッド毎ユーザー情報の保持終了
		completeThreadUserInfo();
	}

}
