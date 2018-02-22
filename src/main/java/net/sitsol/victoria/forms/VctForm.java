/**
 * 
 */
package net.sitsol.victoria.forms;

import java.io.Serializable;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * victoria共通-フォーム 基底クラス
 * 
 * @author shibano
 */
public abstract class VctForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void reset() {
		
		if ( VctLogger.getLogger().isDebugEnabled() ) {
			VctLogger.getLogger().debug(" ☆ " + this.getClass().getSimpleName() + "バインド前初期化");
		}
	}

}
