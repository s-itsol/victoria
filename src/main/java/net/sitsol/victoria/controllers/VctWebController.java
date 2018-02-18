/**
 * 
 */
package net.sitsol.victoria.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.sitsol.victoria.annotation.servlet.VctNoAuth;
import net.sitsol.victoria.annotation.servlet.VctNoLogRequestUrl;
import net.sitsol.victoria.annotation.servlet.VctSuccessForward;
import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctUrlPathConst;
import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.exceptions.VctServletSessionTimeoutRuntimeException;
import net.sitsol.victoria.utils.statics.VctServerUtils;

/**
 * victoria共通-WEBコントローラ
 * 
 * @author shibano
 */
@Controller															// springのコントローラであることを示す
@RequestMapping(VctUrlPathConst.Root.DIR)							// リクエストURLとのマッピング ※APコンテキストからのディレクトリ
public class VctWebController extends VctController {

	/**
	 * 必ずシステムエラーが発生するページ ※検証用
	 */
	@RequestMapping(value = "error.do", method = RequestMethod.GET)
	@VctNoAuth
	public void error() {
		// ※常に例外をスロー
		throw new VctRuntimeException("★明示的に発生させたシステムエラーです。");
	}

	/**
	 * 必ずセッションタイムアウト例外が発生するページ ※検証用
	 */
	@RequestMapping(value = "timeouterror.do", method = RequestMethod.GET)
	@VctNoAuth
	public void timeouterror() {
		// ※常にセッションタイムアウト例外をスロー
		throw new VctServletSessionTimeoutRuntimeException("★明示的に発生させたシステムエラーです。");
	}

	/**
	 * システムエラー
	 * @return モデル＆ビュー情報
	 */
	@RequestMapping(value = VctUrlPathConst.SYSTEMERROR_DO, method = RequestMethod.GET)
	@VctSuccessForward(url = VctUrlPathConst.Root.Errors.SYSTEMERROR_VM)
	@VctNoAuth
	public ModelAndView systemerror() {
		// ビューへフォワードするだけ
		return this.succsessForward();
	}

	/**
	 * セッションタイムアウト
	 * @return モデル＆ビュー情報
	 */
	@RequestMapping(value = VctUrlPathConst.SESSIONTIMEOUT_DO, method = RequestMethod.GET)
	@VctSuccessForward(url = VctUrlPathConst.Root.Errors.SESSIONTIMEOUT_VM)
	@VctNoAuth
	public ModelAndView sessiontimeout() {
		// ビューへフォワードするだけ
		return this.succsessForward();
	}

	/**
	 * ハートビートリクエスト受信
	 */
	@RequestMapping(value = VctUrlPathConst.HEARTBEAT_DO, method = RequestMethod.GET)
	@VctNoAuth
	@VctNoLogRequestUrl
	public void heartbeat(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String hostName = VctServerUtils.HOST_NAME;
		String envName = VctStaticApParam.getInstance().getDispEnvName();
		
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">").append("\r\n");
		html.append("<!-- hostname:[").append(hostName).append("] -->").append("\r\n");
		html.append("<HTML xmlns=\"http://www.w3.org/1999/xhtml\">").append("\r\n");
		html.append("<HEAD>").append("\r\n");
		html.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />").append("\r\n");
		html.append("    <meta http-equiv=\"Pragma\" content=\"no-cache\" />").append("\r\n");
		html.append("    <meta http-equiv=\"Cache-Control\" content=\"no-cache\" />").append("\r\n");
		html.append("    <meta http-equiv=\"Expires\" content=\"-1\" />").append("\r\n");
		html.append("    <title>").append(envName).append("ハートビートチェックページ</title>").append("\r\n");
		html.append("</HEAD>").append("\r\n");
		html.append("<body>").append("\r\n");
		html.append("    ハートビートチェックOK！").append("\r\n");
		html.append("</body>").append("\r\n");
		html.append("</HTML>").append("\r\n");
		
		PrintWriter writer = response.getWriter();
		writer.write(html.toString());
		
		writer.flush();
	}

}
