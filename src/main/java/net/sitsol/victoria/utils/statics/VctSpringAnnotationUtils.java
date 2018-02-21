/**
 * 
 */
package net.sitsol.victoria.utils.statics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

/**
 * Springのアノテーション支援ユーティリティ・ラッパー
 * 
 * @author shibano
 */
public class VctSpringAnnotationUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctSpringAnnotationUtils() {}

	/**
	 * アノテーションあり判定
	 * @param targetMethod 対象メソッド
	 * @param targetAnnotationType 対象アノテーション
	 * @return 判定結果 ※true：アノテーションあり
	 */
	public static <AnnotationClass extends Annotation> boolean hasAnnotation(Method targetMethod, Class<AnnotationClass> targetAnnotationType) {
		// ※得られたら「アノテーションあり」
		return ( findAnnotation(targetMethod, targetAnnotationType) != null );
	}

	/**
	 * アノテーション取得
	 * @param targetMethod 対象メソッド
	 * @param targetAnnotationType 対象アノテーション
	 * @return アノテーション ※得られなかった場合(≒対象アノテーションが未設定)はnull
	 */
	public static <AnnotationClass extends Annotation> AnnotationClass findAnnotation(Method targetMethod, Class<AnnotationClass> targetAnnotationType) {
		
		if ( targetMethod == null || targetAnnotationType == null ) {
			return null;
		}
		
		// 対象アノテーション取得 ※spring提供ユーティリティ
		return AnnotationUtils.findAnnotation(targetMethod, targetAnnotationType);
	}

	/**
	 * アノテーション取得
	 * @param targetClazz 対象クラス
	 * @param targetAnnotationType 対象アノテーション
	 * @return アノテーション ※得られなかった場合(≒対象アノテーションが未設定)はnull
	 */
	public static <AnnotationClass extends Annotation> AnnotationClass findAnnotation(Class<?> targetClazz, Class<AnnotationClass> targetAnnotationType) {
		
		if ( targetClazz == null || targetAnnotationType == null ) {
			return null;
		}
		
		// 対象アノテーション取得 ※spring提供ユーティリティ
		return AnnotationUtils.findAnnotation(targetClazz, targetAnnotationType);
	}

}
