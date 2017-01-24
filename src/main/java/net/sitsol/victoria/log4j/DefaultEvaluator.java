/**
 * 
 */
package net.sitsol.victoria.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * org.apache.log4j.spi.TriggeringEventEvaluatorインターフェースを継承してvictoria用の実装を施したクラス
 * @author rei_shibano
 */
public class DefaultEvaluator implements TriggeringEventEvaluator {

    /**
     * イベント処理
     */
	public boolean isTriggeringEvent(LoggingEvent event) {
		// 規定のレベル未満なら処理をしない
		//if ( VictoriaInitApParam.getInstance().getAlertMailMoreLogLevel().toInt() > event.getLevel().toInt() ) {
		if ( Level.ERROR.toInt() > event.getLevel().toInt() ) {
			return false;
		}
		
		return true;
	}

}
