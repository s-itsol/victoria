/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.VctStopWatch;

/**
 * Bean編集支援ユーティリティ
 *
 * @author shibano
 */
public class VctBeanUtils {

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctBeanUtils() {}

	/**
	 * プロパティ群のINFOログ出力
	 * @param targetBean 対象のビーン ※nullの場合は何も処理をしない
	 */
	public static void writePropertiesInfoLog(Object targetBean) {

		if (targetBean == null) { return; }

		String beanName = StringUtils.EMPTY;

		try {
			VctStopWatch stopWatch = new VctStopWatch();

			// Beanクラス名取得
			beanName = targetBean.getClass().getSimpleName();
			// 出力開始行
			VctLogger.getLogger().info("▼▼▼" + beanName + "▼▼▼");

			// プロパティループ
			for ( PropertyDescriptor prop : PropertyUtils.getPropertyDescriptors(targetBean) ) {

				// プロパティ文字列生成
				StringBuilder propStr = new StringBuilder();
				propStr.append(prop.getName());
				propStr.append(":[");
				propStr.append(BeanUtils.getProperty(targetBean, prop.getName()));
				propStr.append("]");

				// プロパティ１件のログ出力
				VctLogger.getLogger().info(propStr.toString());
			}

			stopWatch.stop();

			// 出力終了行
			VctLogger.getLogger().info("▲▲▲" + beanName + ":[" + stopWatch.getTime() + "(ms)]▲▲▲");

		} catch (Exception ex) {
			VctLogger.getLogger().error("Beanのログ出力でエラーが発生しました。Bean：[" + beanName + "]", ex);
		}
	}

}
