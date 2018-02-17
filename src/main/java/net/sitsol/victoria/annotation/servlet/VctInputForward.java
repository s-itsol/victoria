/**
 *
 */
package net.sitsol.victoria.annotation.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 入力ページフォワード先URL
 *  コントローラのメソッド(妥当性チェックNG時など)入力ページへのフォワード先URLを、注釈で予め設定するために付けることを想定。
 *
 * @author shibano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VctInputForward {

	/**
	 * フォワード先URL
	 */
	String url();

}
