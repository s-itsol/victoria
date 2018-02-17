/**
 *
 */
package net.sitsol.victoria.annotation.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フォーム名マッピング
 *  コントローラのメソッドにマッピングするフォーム名を、注釈で予め設定するために付けることを想定。
 *
 * @author shibano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VctFromMapping {

	/**
	 * フォーム名
	 *  ※「@SessionAttributes」注釈の「names」属性に設定したフォーム名と一致すること
	 *  ※コントローラのメソッド引数のフォームクラスと一致すること
	 */
	String name();

}
