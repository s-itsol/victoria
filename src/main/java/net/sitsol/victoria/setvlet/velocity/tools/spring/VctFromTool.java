/**
 * 
 */
package net.sitsol.victoria.setvlet.velocity.tools.spring;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.forms.VctForm;
import net.sitsol.victoria.setvlet.velocity.tools.BsVctVelocityTool;
import net.sitsol.victoria.utils.statics.VctAnnotationAccessUtils;
import net.sitsol.victoria.utils.statics.VctReflectionUtils;

/**
 * Victoria共通 Spring-Velocity-Tools「フォームツール」
 * 
 * @author shibano
 */
@DefaultKey("form")
@ValidScope(Scope.REQUEST)
public class VctFromTool extends BsVctVelocityTool {

	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	protected HttpSession session;						// HTTPセッション


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
		
		this.session = request.getSession(false);
	}

	/**
	 * マッピングされたフォーム名取得
	 * @return フォーム名取得
	 */
	public String getName() {
		
		// マッピングメソッド取得
		Method mappingMethod = (Method) this.getRequest().getAttribute(VctHttpConst.REQ_MAPPING_METHOD);
		
		// フォームマッピング名取得
		return VctAnnotationAccessUtils.getFromMappingName(mappingMethod);
	}

	/**
	 * マッピングされたフォームのインスタンス取得
	 * @return フォームのインスタンス
	 */
	public VctForm getBean() {
		
		String formName = this.getName();
		
		if ( formName == null ) {
			return null;
		}
		
		VctForm retObj = null;
		{
			Object fromObj = null;
			{
				// セッションから優先して取得
				if ( this.getSession() != null ) {
					fromObj = this.getSession().getAttribute(formName);
				}
				
				// 得られなかった場合はリクエストから取得
				if ( fromObj == null ) {
					fromObj = this.getRequest().getAttribute(formName);
				}
			}
			
			// フォーム基底クラスのインスタンスであればキャストして戻り値にする
			if ( fromObj != null && VctReflectionUtils.hasSuperClass(fromObj.getClass(), VctForm.class) ) {
				retObj = (VctForm) fromObj;
			}
		}
		
		return retObj;
	}


	/*-- setter・getter ------------------------------------------------------*/

	public HttpSession getSession() {
		return session;
	}

}
