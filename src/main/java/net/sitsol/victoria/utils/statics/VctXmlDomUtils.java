/**
 * 
 */
package net.sitsol.victoria.utils.statics;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sitsol.victoria.exceptions.VctRuntimeException;

/**
 * DOM-XML支援ユーティリティ
 * 
 * @author rei_shibano
 */
public class VctXmlDomUtils {
	
	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctXmlDomUtils() {}

	/**
	 * DOMドキュメントから指定タグ名に一致する最初のノード取得
	 * @param document DOMドキュメント
	 * @param targetTagName 指定タグ名
	 * @return ノード ※該当なしはnull
	 */
	public static Node getFirstNode(Document document, String targetTagName) {
		
		if ( document == null ) { return null; }
		
		// DOMドキュメント内のタグ名に一致するノード群を取得
		NodeList nodes = document.getElementsByTagName(targetTagName);
		
		if ( nodes == null ) { return null; }
		
		// 候補となるノードループ
		for ( int nodeIdx = 0; nodeIdx < nodes.getLength(); nodeIdx++ ) {
			
			Node node = nodes.item(nodeIdx);
			
			// ノード配下から指定タグ名に一致する最初のノード取得
			node = VctXmlDomUtils.getFirstNode(node, targetTagName);
			
			if ( node != null ) {
				return node;
			}
		}
		
		return null;
	}
	
	/**
	 * ノード配下から指定タグ名に一致する最初のノード取得
	 * @param parentNode 親ノード ※親ノードのタグ名と一致したら親ノード自身が返る
	 * @param targetTagName 指定タグ名
	 * @return ノード ※該当なしはnull
	 */
	public static Node getFirstNode(Node parentNode, String targetTagName) {
		
		if ( parentNode == null ) { return null; }
		
		// ノード名が対象タグ名と一致
		if ( StringUtils.equals(targetTagName, parentNode.getNodeName()) ) {
			return parentNode;
		}
		
		String parentNodeName = null;
		
		try {
			parentNodeName = parentNode.getNodeName();
			
			NodeList childNodes = parentNode.getChildNodes();
			
			// 候補となるノードループ
			for ( int childIdx = 0; childIdx < childNodes.getLength(); childIdx++ ) {
				
				Node childNode = childNodes.item(childIdx);
				
				// ノード配下から指定タグ名に一致する最初のノード取得
				childNode = VctXmlDomUtils.getFirstNode(childNode, targetTagName);
				
				if ( childNode != null ) {
					return childNode;
				}
			}
			
			return null;
			
		} catch (Exception ex) {
			throw new VctRuntimeException("DOM-XMLノード取得でエラーが発生しました。"
												+ "親ノード名：[" + parentNodeName + "]"
												+ ", 対象タグ名：[" + targetTagName + "]"
											, ex
			);
		}
	}
	
	/**
	 * ノード直下の値を取得
	 *  ※「<node1>値１</node1>」といった様に、「node1」配下にタブや改行など、余計なテキストノードなどが存在せず、
	 *    実値のテキストノードのみであるケースでの利用を想定している
	 * @param node ノード
	 * @return 値
	 */
	public static String getFirstTextValue(Node node) {
		
		if ( node == null ) { return null; }
		
		Node chiledNode = node.getFirstChild();
		
		return chiledNode != null ? chiledNode.getNodeValue() : null;
	}

}
