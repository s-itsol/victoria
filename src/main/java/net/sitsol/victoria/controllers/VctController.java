/**
 * 
 */
package net.sitsol.victoria.controllers;

/**
 * victoria共通-コントローラ 基底クラス
 * 
 * @author shibano
 */
public abstract class VctController {

	/**
	 * リダイレクト
	 * @param redirectUrl リダイレクト先URL ※APコンテキストからの相対パス
	 * @return spring-mvc用リダイレクト識別文字列
	 */
	protected String redirect(String redirectUrl) {
		// ※リダイレクト識別子＋リダイレクト先URL
		return "redirect:" + redirectUrl;
	}

}
