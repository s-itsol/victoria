/**
 * 
 */
package net.sitsol.victoria.setvlet.velocity.tools.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sitsol.victoria.annotation.servlet.VctFromMapping;
import net.sitsol.victoria.forms.VctFrom;
import net.sitsol.victoria.setvlet.velocity.tools.BsVctVelocityTool;
import net.sitsol.victoria.utils.statics.VctReflectionUtils;
import net.sitsol.victoria.utils.statics.VctSpringMvcUtils;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

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
		
		// フォーム名マッピング-アノテーション取得
		VctFromMapping targetAnno = VctSpringMvcUtils.findCurrentThreadAnnotation(VctFromMapping.class);
		
		return targetAnno != null ? targetAnno.name() : null;
	}

	/**
	 * マッピングされたフォームのインスタンス取得
	 * @return フォームのインスタンス
	 */
	public VctFrom getBean() {
		
		String formName = this.getName();
		
		if ( formName == null ) {
			return null;
		}
		
		VctFrom retObj = null;
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
			if ( fromObj != null && VctReflectionUtils.hasSuperClass(fromObj.getClass(), VctFrom.class) ) {
				retObj = (VctFrom) fromObj;
			}
		}
		
		return retObj;
	}


	/*-- setter・getter ------------------------------------------------------*/

	public HttpSession getSession() {
		return session;
	}

}
