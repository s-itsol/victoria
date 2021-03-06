/**
 *
 */
package net.sitsol.victoria.annotation.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 認証不要
 *  ログイン認証を必要としないコントローラのメソッドに付けることを想定。
 *
 * @author shibano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VctNoAuth {

}
