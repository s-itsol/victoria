/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

import net.sitsol.victoria.threadlocals.ThreadMappingMethod;

/**
 * Spring-MVCの支援ユーティリティ
 *
 * @author shibano
 */
public class VctSpringMvcUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctSpringMvcUtils() {}

	/**
	 * 現スレッド-アノテーションあり判定
	 * @param targetAnnotationType 対象アノテーション
	 * @return 判定結果 ※true：アノテーションあり
	 */
	public static <AnnotationClass extends Annotation> boolean hasCurrentThreadAnnotation(Class<AnnotationClass> targetAnnotationType) {
		// ※得られたら「アノテーションあり」
		return ( findCurrentThreadAnnotation(targetAnnotationType) != null );
	}

	/**
	 * 現スレッド-アノテーション取得
	 * @param targetAnnotationType 対象アノテーション
	 * @return アノテーション ※得られなかった場合(≒対象アノテーションが未設定)はnull
	 */
	public static <AnnotationClass extends Annotation> AnnotationClass findCurrentThreadAnnotation(Class<AnnotationClass> targetAnnotationType) {
		
		if ( targetAnnotationType == null ) {
			return null;
		}
		
		// 現スレッド-マッピングメソッドの取得
		Method targetMethod = ThreadMappingMethod.getCurrentThreadMappingMethod();
		
		if ( targetMethod == null ) {
			return null;
		}
		
		// 対象アノテーション取得
		return AnnotationUtils.findAnnotation(targetMethod, targetAnnotationType);
	}

}
