package net.sitsol.victoria.models.userinfo;

import java.io.Serializable;

/**
 * ユーザー情報インターフェイス
 *  各サイト毎のログインユーザー情報を実装することを想定している。
 *
 * @author shibano
 */
public interface IUserInfo extends Serializable {

	/**
	 * ユーザーIDの取得
	 * @return ユーザーID
	 */
	public String getUserId();

}
