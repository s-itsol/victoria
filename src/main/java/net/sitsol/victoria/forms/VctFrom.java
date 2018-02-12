/**
 * 
 */
package net.sitsol.victoria.forms;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * victoria共通-フォーム 基底クラス
 * 
 * @param <FromClass> 派生クラスのクラス型
 * @author shibano
 */
public abstract class VctFrom implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * デフォルト名取得
	 *  
	 * @return デフォルト名 ※spring-mvcの「@SessionAttributes」アノテーションにて、「names」パラメータを省略した場合のフォーム名と一致する
	 */
	public String getDefaultName() {
		// ※クラス名の先頭1文字だけを小文字にした文字列
		return StringUtils.uncapitalize(this.getClass().getSimpleName());
	}

}
