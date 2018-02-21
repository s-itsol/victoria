/**
 * 
 */
package net.sitsol.victoria.setvlet.velocity.tools;

import javax.servlet.http.HttpServletRequest;

/**
 * Victoria共通 Velocity-Tools 基底クラス
 *  
 * @author shibano
 */
public class BsVctVelocityTool {

	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	protected HttpServletRequest request = null;			// HTTPサーブレットリクエスト


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
		
		if ( request == null ) {
			throw new NullPointerException("HTTPサーブレットリクエストのインスタンスにnullが設定されました。");
		}
		
		this.request = request;
	}


	/*-- setter・getter ------------------------------------------------------*/

	public HttpServletRequest getRequest() {
		return request;
	}


}
