/**
 *
 */
package net.sitsol.victoria.configs;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

import net.sitsol.victoria.beanfactory.spring.VctBeanFactory;

/**
 * 静的アプリケーション・パラメータクラス
 *  victoriaで共通なアプリケーション・パラメータを定義する。
 *  独自なパラメータは各プロジェクトで派生させて定義すること。
 *  また、各プロジェクトの独自仕様でvictoria用のデフォルト値以外の値を使いたい場合は、
 *  getterメソッドをオーバーライドして実装することを想定している。
 *
 * @author shibano
 */
public class VctStaticApParam implements Serializable {

	/* -- static ----------------------------------------------------------- */

	private static final long serialVersionUID = 759495219902390118L;
	public static String SPRING_BEAN_NAME = "StaticApParam";			// Springで管理されるこのクラスのビーン名

	/**
	 * インスタンスの取得
	 *  ※派生クラスでは、本メソッドをオーバーロードしてキャストするだけでインスタンスを取得することができるように想定
	 * @return 本クラスのインスタンス
	 */
	public static VctStaticApParam getInstance() {
		return (VctStaticApParam) VctBeanFactory.getInstance().getBean(SPRING_BEAN_NAME, VctStaticApParam.class);
	}


	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	// ※パラメータ値として保持したいものを、ここでフィールドとして定義する
	private String dispEnvName						= StringUtils.EMPTY;	// 表示用環境名称
	private String appEncoding						= "UTF-8";				// アプリケーション標準エンコーディング
	private String clientEncoding					= "MS932";				// クライアントPCエンコーディング
	private Level alertMailMoreLogLevel			= Level.FATAL;			// アラート・メールを送るログレベル ※設定レベル以上ならアラートを発する
	private boolean httpRequestUrlLogOutputFlg	= true;					// HTTPリクエストURLログ出力フラグ ※true：HTTPリクエストURLログを出力する
	private boolean sqlTraceLogOutputFlg			= false;				// SQLトレースログ出力フラグ ※true：SQLトレースログを出力する
	private boolean sqlTraceLogLiteralFlg			= true;					// SQLトレースログリテラル化フラグ ※true：SQLトレースログ出力時にリテラルSQL化する
	private int warningSqlExecuteMillis			= 1000;					// 警告を出す超過SQL実行時間(ミリ秒)
	private int warningFacadeExecuteMillis			= 2000;					// 警告を出す超過ファサード実行時間(ミリ秒)
	private int warningHttpRequestExecuteMillis	= 5000;					// 警告を出す超過HTTP要求実行時間(ミリ秒)


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * デフォルトコンストラクタ
	 *  ※外部からはインスタンス化させない
	 */
	protected VctStaticApParam() {}


	/*-- setter・getter ------------------------------------------------------*/

	public String getDispEnvName() {
		return this.dispEnvName;
	}

	public void setDispEnvName(String dispEnvName) {
		this.dispEnvName = dispEnvName;
	}

	public String getAppEncoding() {
		return appEncoding;
	}

	public void setAppEncoding(String appEncoding) {
		this.appEncoding = appEncoding;
	}

	public String getClientEncoding() {
		return clientEncoding;
	}

	public void setClientEncoding(String clientEncoding) {
		this.clientEncoding = clientEncoding;
	}

	public Level getAlertMailMoreLogLevel() {
		return this.alertMailMoreLogLevel;
	}

	public void setAlertMailMoreLogLevel(Level alertMailMoreLogLevel) {
		this.alertMailMoreLogLevel = alertMailMoreLogLevel;
	}

	public boolean isHttpRequestUrlLogOutputFlg() {
		return httpRequestUrlLogOutputFlg;
	}

	public void setHttpRequestUrlLogOutputFlg(boolean httpRequestUrlLogOutputFlg) {
		this.httpRequestUrlLogOutputFlg = httpRequestUrlLogOutputFlg;
	}

	public boolean isSqlTraceLogOutputFlg() {
		return this.sqlTraceLogOutputFlg;
	}

	public void setSqlTraceLogOutputFlg(boolean sqlTraceLogOutputFlg) {
		this.sqlTraceLogOutputFlg = sqlTraceLogOutputFlg;
	}

	public boolean isSqlTraceLogLiteralFlg() {
		return this.sqlTraceLogLiteralFlg;
	}

	public void setSqlTraceLogLiteralFlg(boolean sqlTraceLogLiteralFlg) {
		this.sqlTraceLogLiteralFlg = sqlTraceLogLiteralFlg;
	}

	public int getWarningSqlExecuteMillis() {
		return this.warningSqlExecuteMillis;
	}

	public void setWarningSqlExecuteMillis(int warningSqlExecuteMillis) {
		this.warningSqlExecuteMillis = warningSqlExecuteMillis;
	}

	public int getWarningFacadeExecuteMillis() {
		return this.warningFacadeExecuteMillis;
	}

	public void setWarningFacadeExecuteMillis(int warningFacadeExecuteMillis) {
		this.warningFacadeExecuteMillis = warningFacadeExecuteMillis;
	}

	public int getWarningHttpRequestExecuteMillis() {
		return this.warningHttpRequestExecuteMillis;
	}

	public void setWarningHttpRequestExecuteMillis(
			int warningHttpRequestExecuteMillis) {
		this.warningHttpRequestExecuteMillis = warningHttpRequestExecuteMillis;
	}

}
