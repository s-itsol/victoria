/**
 *
 */
package net.sitsol.victoria.consts;

/**
 * victoria共通-HTTP定数クラス
 *
 * @author shibano
 */
public class VctHttpConst {

	/** コンストラクタ */
	protected VctHttpConst() {}

	// spring-mvc予約語定数
	public static final String REDIRECT_PREFIX			= "redirect:";			// リダイレクト-プレフィックス

	// HTTPヘッダ属性
	public static final String USER_AGENT				= "User-Agent";			// ユーザーエージェント
	public static final String REFERER					= "Referer";			// リファラ

	// 共通パラメータ名 ※requestの属性名、GET・POSTパラメータ名
	public static final String REQ_ST_TIME_MILLIS		= "reqStTimeMillis";	// リクエスト開始時刻(ms)
	public static final String REQ_MAPPING_METHOD		= "reqMappingMethod";	// リクエストマッピングメソッド
	public static final String ENV_NAME					= "envName";			// 環境名
	public static final String HOST_NAME				= "hostName";			// サーバーホスト名

}
