/**
 *
 */
package net.sitsol.victoria.annotation.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTPリクエストURLログ出力不要
 *  HTTPリクエストURLログの出力を必要としないコントローラのメソッドに付けることを想定。
 *  ハートビート受信など、メンテナンス用リクエストが主な用途。
 *
 * @author shibano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VctNoLogRequestUrl {

}
