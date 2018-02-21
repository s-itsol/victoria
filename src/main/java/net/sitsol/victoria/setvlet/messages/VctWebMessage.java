/**
 * 
 */
package net.sitsol.victoria.setvlet.messages;

import java.io.Serializable;

/**
 * Victoria共通 WEBメッセージクラス
 * 
 * @author shibano
 */
public class VctWebMessage implements Serializable {

	private static final long serialVersionUID = 1L;


	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	protected String key = null;					// メッセージキー
	protected Object[] values = null;				// 置換文字列群


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param key メッセージキー
	 * @param values 置換文字列群
	 */
	public VctWebMessage(String key, Object ... values) {
		this.key = key;
		this.values = values;
	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		
		StringBuilder buff = new StringBuilder();
		
		buff.append(this.getKey());
		buff.append("[");
		
		// 置換文字列群あり
		if ( this.getValues() != null ) {
			
			// 置換文字列ループ
			for ( int valueIdx = 0; valueIdx < this.getValues().length; valueIdx++ ) {
				
				// 最初の1件目以外は「, 」で繋ぐ
				if ( valueIdx > 0 ) {
					buff.append(", ");
				}
				
				buff.append( this.getValues()[valueIdx] );
			}
		}
		
		buff.append("]");
		
		return buff.toString();
	}


	/*-- setter・getter ------------------------------------------------------*/

	public String getKey() {
		return key;
	}

	public Object[] getValues() {
		return values;
	}

}
