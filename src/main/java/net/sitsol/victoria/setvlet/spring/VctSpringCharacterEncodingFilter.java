/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import org.springframework.web.filter.CharacterEncodingFilter;

import net.sitsol.victoria.configs.VctStaticApParam;

/**
 * Spring文字エンコーディングフィルター
 *
 * @author shibano
 */
public class VctSpringCharacterEncodingFilter extends CharacterEncodingFilter {

	/**
	 * デフォルトコンストラクタ
	 */
	public VctSpringCharacterEncodingFilter() {
		super();

		this.setEncoding(VctStaticApParam.getInstance().getAppEncoding());		// エンコーディング ※デフォルト：アプリ標準エンコーディング
		this.setForceEncoding(true);
	}

}
