/**
 *
 */
package net.sitsol.victoria.consts;

/**
 * victoria共通-URLパス定数クラス
 *  ※定義してあるURLやディレクトリはデフォルト推奨
 * 
 * @author shibano
 */
public class VctUrlPathConst {

	/** コンストラクタ */
	protected VctUrlPathConst() {}


	// -------------------------------------------------------------------------
	//  共通
	// -------------------------------------------------------------------------

	// サーブレット
	public static final String SYSTEMERROR_DO		= "systemerror.do";			// システムエラー
	public static final String SESSIONTIMEOUT_DO	= "sessiontimeout.do";		// セッションタイムアウト
	public static final String HEARTBEAT_DO			= "heartbeat.do";			// ハートビートリクエスト受信

	// ビュー
	public static final String SYSTEMERROR_VM		= "systemerror.vm";			// システムエラー
	public static final String SESSIONTIMEOUT_VM	= "sessiontimeout.vm";		// セッションタイムアウト


	// -------------------------------------------------------------------------
	//  ディレクトリ階層構造
	// -------------------------------------------------------------------------

	/** APコンテキストルート */
	public class Root {
		public static final String DIR = "/";
		/** エラー情報 */
		public class Errors {
			public static final String DIR = Root.DIR + "errors/";
			public static final String SYSTEMERROR_VM = DIR + VctUrlPathConst.SYSTEMERROR_VM;			// システムエラー
			public static final String SESSIONTIMEOUT_VM	= DIR + VctUrlPathConst.SESSIONTIMEOUT_VM;	// セッションタイムアウト
		}
	}

}
