/**
 * 
 */
package net.sitsol.victoria.setvlet.velocity.tools;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sitsol.victoria.messages.VctMessageResource;
import net.sitsol.victoria.setvlet.messages.VctWebMessage;
import net.sitsol.victoria.setvlet.messages.VctWebMessages;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

/**
 * Victoria共通 Spring-Velocity-Tools「エラー情報ツール」
 * 
 * @author shibano
 */
@DefaultKey("form")
@ValidScope(Scope.REQUEST)
public class VctErrorsTool extends BsVctVelocityTool {

	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	protected VctWebMessages webMessasges = null;			// メッセージ群


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * HTTPサーブレットリクエスト設定
	 *  ※Velocityフレームワークから注入される
	 *  
	 * @param request HTTPサーブレットリクエスト
	 */
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		
		if ( request == null ) {
			throw new NullPointerException("HTTPサーブレットリクエストのインスタンスにnullが設定されました。");
		}
		
		// リクエスト属性からエラーメッセージ群を取得
//		this.webMessasges = (VctWebMessages) this.getRequest().getAttribute(VctHttpConst.ATTR_GLOBALS_ERROR_KEY);
// ●●●TODO：
	}

	/**
	 * メッセージ存在判定
	 * @return 判定結果 ※true：メッセージあり
	 */
	public boolean exist() {
		
		VctWebMessages errors = this.getWebMessages();
		
		// メッセージ群が空でなければ「メッセージあり」
		return errors != null && !errors.isEmpty();
    }
	/**
	 * メッセージ取得
	 * @param property メッセージキー
	 * @return メッセージ文字列
	 */
	public String getMsgs(String property) {
		
		if ( !this.exist() ) {
			return "";
		}
		
		VctWebMessages errors = this.getWebMessages();
		
		// メッセージキーに対応するメッセージイテレータを取得 ※メッセージキーが指定なしの場合は「全メッセージ」
		Iterator<VctWebMessage> reports = ( property == null )
											? errors.get()
											: errors.get(property)
		;
		if ( !reports.hasNext() ) {
			return "";
		}
		
		// エラーメッセージ文字列生成
		StringBuilder results = new StringBuilder();
		{
			// ロケールの取得
//			Locale locale = (Locale) this.getRequest().getAttribute(VctConst.REQ_LOCALE);
//			
//			// 得られなかった場合はデフォルトロケールにしておく ※フェールセーフ
//			if ( locale == null ) {
//				locale = Locale.getDefault();
//			}
// ●●●TODO：
			Locale locale = Locale.getDefault();
			
			String header = VctMessageResource.getInstance().getMessage(locale, "errors.header");
			String footer = VctMessageResource.getInstance().getMessage(locale, "errors.footer");
			String prefix = VctMessageResource.getInstance().getMessage(locale, "errors.prefix");
			String suffix = VctMessageResource.getInstance().getMessage(locale, "errors.suffix");
			
			if ( header == null ) {
				header = "";
			}
			if ( footer == null ) {
				footer = "";
			}
			if ( prefix == null ) {
				prefix = "";
			}
			if ( suffix == null ) {
				suffix = "";
			}
			
			results.append(header);							// ヘッダー
			results.append("\r\n");
			
			// メッセージ毎にループ
			while ( reports.hasNext() ) {
				
				VctWebMessage report = reports.next();
				
				// メッセージを取得
				String message = VctMessageResource.getInstance().getMessage(locale, report.getKey(), report.getValues());
				
				results.append(prefix);						// プレフィックス
				results.append(message);					// メッセージ本体
				results.append(suffix);						// サフィックス
				results.append("\r\n");
			}
			
			results.append(footer);							// フッター
			results.append("\r\n");
		}
		
		return results.toString();
	}


	/*-- setter・getter ------------------------------------------------------*/

	public VctWebMessages getWebMessages() {
		return webMessasges;
	}


}
