/**
 * 
 */
package net.sitsol.victoria.enums;

import java.io.Serializable;

/**
 * victoria共通-Enum定数アクセッサ・インタフェース
 * 
 * @author shibano
 */
public interface VctEnumAccessible extends Serializable {

	/**
	 * コード値
	 */
	String getCode();

	/**
	 * デコード文字列
	 */
	String getDecode();

}
