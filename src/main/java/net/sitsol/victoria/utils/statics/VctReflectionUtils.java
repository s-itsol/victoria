/**
 *
 */
package net.sitsol.victoria.utils.statics;

import static java.util.Locale.ENGLISH;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * リフレクション支援ユーティリティクラス
 *
 * @author shibano
 */
public class VctReflectionUtils {

	/**
	 * setter／getterを表す種別
	 */
	public enum PrefixType {
		/** setter */
		Set
		/** getter */
		, Get
	}

	/** デフォルトコンストラクタ ※外部からインスタンス化させない **/
	protected VctReflectionUtils() { }

	/**
	 * 簡易クラスメソッド名取得
	 * @param method メソッド
	 * @return クラスメソッド名の文字列 ※「簡易クラス名」#「メソッド名」形式
	 */
	public static String getSimpleClassMethodInfo(Method method) {
		
		if ( method == null ) {
			return null;
		}
		
		// 簡易クラス名取得
		String className =  method.getDeclaringClass() != null ? method.getDeclaringClass().getSimpleName() : null;
		
		return className + "#" + method.getName();
	}

	/**
	 * フィールド名から該当するsetter・getterメソッドを取得する
	 * @param beanClass ビーンクラス型
	 * @param prefixType setter／getterを表す種別
	 * @param fieldName フィールド名
	 * @return 該当するメソッドオブジェクト ※存在しなかった場合はnull
	 */
	public static Method getFieldSetGetMethod(Class<?> beanClass, PrefixType prefixType, String fieldName) {

		if ( beanClass == null || prefixType == null || StringUtils.isEmpty(fieldName) ) {
			return null;
		}

		String prefix = null;
		{
			if ( PrefixType.Set.equals(prefixType) ) {
				prefix = "set";

			} else if ( PrefixType.Get.equals(prefixType) ) {
				prefix = "get";
			}
		}

		// 全て小文字のフィールド名から、対応するsetterメソッド名を生成
		String setterName
				= prefix
				+ StringUtils.substring(fieldName, 0, 1).toUpperCase(ENGLISH)
				+ StringUtils.substring(fieldName, 1);

		Method regMethod = null;
		{
			// クラスのメソッドループ
			for ( Method method : beanClass.getMethods() ) {
				// 該当するsetterメソッドの場合
				if ( method != null && setterName.equals(method.getName()) ) {
					regMethod = method;
					break;
				}
			}
		}

		return regMethod;
	}

	/**
	 * ビーンのgetterメソッドを使ったプロパティ値の取得
	 *  ※プロパティ形式(＝単純なget／setメソッドを持つフィールド)にのみ対応
	 * @param bean ビーンオブジェクト
	 * @param fieldName フィールド名 ※「get／set」の接頭辞なし、先頭は小文字
	 * @param classType 取得する型
	 * @return 値 ※エラー発生時はnull
	 * @param <FieldClass> フィールドクラス型
	 */
	@SuppressWarnings("unchecked")
	public static <FieldClass> FieldClass getPropertyValue(Object bean, String fieldName, Class<FieldClass> classType) {

		if ( bean == null || StringUtils.isEmpty(fieldName) || classType == null ) {
			return null;
		}

		try {
			// getterメソッド取得
			Method method = VctReflectionUtils.getFieldSetGetMethod(bean.getClass(), VctReflectionUtils.PrefixType.Get, fieldName);

			// メソッドを実行し、VOから得た結果を返す
			return (FieldClass) method.invoke(bean);

		} catch ( Exception ex ) {
			// 業務処理に支障を出さないため、エラーログを出力してnullを返すにとどめる
			VctLogger.getLogger().error("ビーン・プロパティ値の取得でエラーが発生しました。"
												+ "ビーンクラス：[" + bean.getClass().getSimpleName() + "]"
												+ ", フィールド名：[" + fieldName + "]"
												+ ", フィールドクラス型：[" + classType.getName() + "]"
											, ex
										);
			return null;
		}
	}

	/**
	 * ビーンのsetterメソッドを使ったプロパティ値の設定
	 *  ※プロパティ形式(＝単純なget／setメソッドを持つフィールド)にのみ対応
	 * @param bean ビーンオブジェクト
	 * @param fieldName フィールド名 ※「get／set」の接頭辞なし、先頭は小文字
	 * @param setValue	設定する値
	 */
	public static void setPropertyValue(Object bean, String fieldName, Object setValue) {

		if ( bean == null || StringUtils.isEmpty(fieldName) || setValue == null ) {
			return;
		}

		try {
			// setterメソッド取得
			Method method = VctReflectionUtils.getFieldSetGetMethod(bean.getClass(), VctReflectionUtils.PrefixType.Set, fieldName);

			// メソッドを実行し、VOに値を設定する
			method.invoke(bean, setValue);

		} catch (Exception ex) {
			// 業務処理に支障を出さないため、エラーログを出力しするにとどめる
			VctLogger.getLogger().error("ビーン・プロパティ値の設定でエラーが発生しました。"
												+ "ビーンクラス：[" + bean.getClass().getSimpleName() + "]"
												+ ", フィールド名：[" + fieldName + "]"
												+ ", 設定値：[" + setValue + "]"
												+ ", 設定値のクラス型：[" + setValue.getClass().getName() + "]"
											, ex
										);
		}
	}

	/**
	 * ターゲットの親クラスの存在判定
	 * @param classType 対象のクラス型
	 * @param targetSuperClassType	ターゲットの親クラス型
	 * @return 判定結果 ※true：親クラスあり
	 */
	public static boolean hasSuperClass(Class<?> classType, Class<?> targetSuperClassType) {

		if ( classType == null || targetSuperClassType == null ) {
			return false;
		}

		// ターゲットの親クラスと対象のクラスが一致した場合「親クラスあり」の判定結果を返す
		if ( targetSuperClassType.getName().equals(classType.getName()) ) {
			return true;
		}

		// １段階上の基底クラスを比較対象に代えて再起呼び出し
		return hasSuperClass(classType.getSuperclass(), targetSuperClassType);
	}

}