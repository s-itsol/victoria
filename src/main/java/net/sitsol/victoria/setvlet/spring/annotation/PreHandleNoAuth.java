/**
 *
 */
package net.sitsol.victoria.setvlet.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 処理前イベント通知-認証不要
 *
 * @author rei_shibano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreHandleNoAuth {

}
