/**
 *
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * org.apache.log4j.spi.TriggeringEventEvaluatorインターフェースを継承してvictoria用の実装を施したクラス
 *
 * @author shibano
 */
public class DefaultEvaluator implements TriggeringEventEvaluator {

    /**
     * イベント処理判定
     * @param event ロギングイベント
     * @return 判定結果 ※true：ログ出力処理をさせる ／ false：ログ出力処理をさせない
     */
	@Override
	public boolean isTriggeringEvent(LoggingEvent event) {

		// デフォルトはFATALレベル未満なら処理をしない
		if ( Level.FATAL.toInt() > event.getLevel().toInt() ) {
			return false;
		}

		return true;
	}

}
