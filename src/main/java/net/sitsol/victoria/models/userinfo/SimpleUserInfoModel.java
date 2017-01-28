package net.sitsol.victoria.models.userinfo;

/**
 * 簡易ユーザー情報モデル
 *  コンソールアプリのバッチや、バックグラウンドの別スレッドなど、擬似的なログインユーザー情報を使いたいケースを想定している。
 *
 * @author shibano
 */
public class SimpleUserInfoModel implements IUserInfo {

	/* -- static ----------------------------------------------------------- */

	private static final long serialVersionUID = -6176740865778575401L;


	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	private String userId			= null;		// ユーザーID


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param userId ユーザーID
	 */
	public SimpleUserInfoModel(String userId) {
		this.userId = userId;
	}

	/**
	 * ユーザーIDの取得
	 * @return ユーザーID
	 */
	@Override
	public String getUserId() {
		return this.userId;
	}

}
