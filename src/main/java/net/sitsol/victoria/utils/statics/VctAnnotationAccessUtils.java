/**
 * 
 */
package net.sitsol.victoria.utils.statics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.SessionAttributes;

import net.sitsol.victoria.annotation.servlet.VctFromMapping;
import net.sitsol.victoria.annotation.servlet.VctInputForward;
import net.sitsol.victoria.annotation.servlet.VctNoAuth;
import net.sitsol.victoria.annotation.servlet.VctNoLogRequestUrl;
import net.sitsol.victoria.annotation.servlet.VctSuccessForward;

/**
 * アノテーションアクセスユーティリティ
 *  Victoriaで用いているカスタム・アノテーションの有無判定や属性値取得を、
 *  アプリ側で必要な形で得られるようラッピングしただけのユーティリティ。
 * 
 * @author shibano
 */
public class VctAnnotationAccessUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctAnnotationAccessUtils() {}

	/**
	 * メソッド・アノテーション取得
	 * @param targetMethod 対象メソッド
	 * @param targetAnnotationType 対象アノテーション
	 * @return アノテーション ※得られなかった場合(≒対象アノテーションが未設定)はnull
	 */
	protected static <AnnotationClass extends Annotation> AnnotationClass findMethodAnnotation(Method targetMethod, Class<AnnotationClass> targetAnnotationType) {
		
		if ( targetMethod == null || targetAnnotationType == null ) {
			return null;
		}
		
		// 対象アノテーション取得 ※spring提供ユーティリティ
		return AnnotationUtils.findAnnotation(targetMethod, targetAnnotationType);
	}

	/**
	 * クラス・アノテーション取得
	 * @param targetClazz 対象クラス
	 * @param targetAnnotationType 対象アノテーション
	 * @return アノテーション ※得られなかった場合(≒対象アノテーションが未設定)はnull
	 */
	protected static <AnnotationClass extends Annotation> AnnotationClass findClassAnnotation(Class<?> targetClazz, Class<AnnotationClass> targetAnnotationType) {
		
		if ( targetClazz == null || targetAnnotationType == null ) {
			return null;
		}
		
		// 対象アノテーション取得 ※spring提供ユーティリティ
		return AnnotationUtils.findAnnotation(targetClazz, targetAnnotationType);
	}

	/**
	 * 認証不要判定
	 * @param targetMethod 対象メソッド
	 * @return 判定結果 ※true：認証不要である
	 */
	public static boolean isNoAuth(Method targetMethod) {
		// 「認証不要」アノテーションあり
		return ( findMethodAnnotation(targetMethod, VctNoAuth.class) != null );
	}

	/**
	 * HTTPリクエストURLログ出力不要判定
	 * @param targetMethod 対象メソッド
	 * @return 判定結果 ※true：HTTPリクエストURLログ出力不要である
	 */
	public static boolean isNoLogRequestUrl(Method targetMethod) {
		// 「HTTPリクエストURLログ出力不要」アノテーションあり
		return ( findMethodAnnotation(targetMethod, VctNoLogRequestUrl.class) != null );
	}

	/**
	 * フォームマッピング属性名取得
	 * @param targetMethod 対象メソッド
	 * @return 属性名 ※得られなかった場合はnull
	 */
	public static String getFromMappingName(Method targetMethod) {
		
		// 「フォーム名マッピング」アノテーション取得
		VctFromMapping targetAnno = findMethodAnnotation(targetMethod, VctFromMapping.class);
		
		return targetAnno != null ? targetAnno.name() : null;
	}

	/**
	 * 入力フォワード先URL取得
	 * @param targetMethod 対象メソッド
	 * @return フォワード先URL ※得られなかった場合はnull
	 */
	public static String getInputForwardUrl(Method targetMethod) {
		
		// 「入力フォワード先URL」アノテーション取得
		VctInputForward targetAnno = findMethodAnnotation(targetMethod, VctInputForward.class);
		
		return targetAnno != null ? targetAnno.url() : null;
	}

	/**
	 * 正常終了フォワード先URL取得
	 * @param targetMethod 対象メソッド
	 * @return フォワード先URL ※得られなかった場合はnull
	 */
	public static String getSuccessForwardUrl(Method targetMethod) {
		
		// 「正常終了フォワード先URL」アノテーション取得
		VctSuccessForward targetAnno = findMethodAnnotation(targetMethod, VctSuccessForward.class);
		
		return targetAnno != null ? targetAnno.url() : null;
	}

	/**
	 * セッション属性情報取得
	 * @param targetClass 対象クラス
	 * @return セッション属性情報マップ(＝「キー：属性名、値：クラス」形式) ※得られなかった場合は空のマップ
	 */
	public static Map<String, Class<?>> getSessionAttributeInfoMap(Class<?> targetClass) {
		
		Map<String, Class<?>> retSessAttrMap = new LinkedHashMap<>();
		
		// 「セッション属性情報群」アノテーション取得
		SessionAttributes sessAttrs = findClassAnnotation(targetClass, SessionAttributes.class);
		
		if ( sessAttrs == null ) {
			return retSessAttrMap;
		}
		
		String[] sessAttrNames		= sessAttrs.names();
		Class<?>[] sessAttrTypes	= sessAttrs.types();
		
		if ( sessAttrNames == null || sessAttrTypes == null ) {
			return retSessAttrMap;
		}
		
		// 有効な最大インデックス値 ※いずれか少ない方の要素数
		int enableMaxIdx = sessAttrNames.length <= sessAttrTypes.length ? sessAttrNames.length : sessAttrTypes.length;
		
		// セッション属性情報ループ
		for ( int idx = 0; idx < enableMaxIdx; idx++) {
			
			String sessAttrName		= sessAttrs.names()[idx];
			Class<?> sessAttrType	= sessAttrs.types()[idx];
			
			// マップへ追加
			retSessAttrMap.put(sessAttrName, sessAttrType);
		}
		
		return retSessAttrMap;
	}

	/**
	 * マッピングしたフォームのセッション属性名取得
	 * @param targetMethod 対象メソッド
	 * @return フォームのセッション属性名 ※得られなかった場合はnull
	 */
	public static String getFromMappingSessionName(Method targetMethod) {
		
		// マッピングフォーム名
		String formName = VctAnnotationAccessUtils.getFromMappingName(targetMethod);
		
		if ( formName == null ) {
			return null;
		}
		
		// セッション属性情報マップ
		Map<String, Class<?>> sessAttrInfoMap = VctAnnotationAccessUtils.getSessionAttributeInfoMap(targetMethod.getDeclaringClass());
		
		String sessFromName = null;		// セッションフォーム属性名
		
		// セッション属性情報ループ
		for ( String sessAttrName : sessAttrInfoMap.keySet() ) {
			
			// フォーム名が一致しなければ次へ
			if ( !formName.equals(sessAttrName) ) {
				continue;
			}
			
			Class<?> sessAttrType = sessAttrInfoMap.get(sessAttrName);		// セッション属性クラス
			
			// spring標準のセッション属性名に変換 ※クラス名の先頭1文字のみ小文字
			sessFromName = StringUtils.uncapitalize( sessAttrType.getSimpleName() );
			
			break;
		}
		
		return sessFromName;
	}

}
