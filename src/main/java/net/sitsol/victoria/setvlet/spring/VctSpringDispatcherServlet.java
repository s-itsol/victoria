/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import net.sitsol.victoria.configs.VctStaticApParam;
import net.sitsol.victoria.consts.VctHttpConst;
import net.sitsol.victoria.consts.VctLogKeywordConst;
import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.utils.statics.VctHttpUtils;

/**
 * Spring-Dispatcherサーブレット
 *
 * @author shibano
 */
public class VctSpringDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * サービス実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// システム共通リクエスト属性値設定
		request.setAttribute(VctHttpConst.ENV_NAME, VctStaticApParam.getInstance().getDispEnvName());		// 環境名

		// HTTPリクエストURLログ出力フラグONの場合
		if ( VctStaticApParam.getInstance().isHttpRequestUrlLogOutputFlg() ) {

			// HTTPリクエストURLログ出力
			StringBuilder message = new StringBuilder();
			message.append(VctLogKeywordConst.REQURL).append(" ").append(VctHttpUtils.createUrlQueryString(request));

			VctLogger.getLogger().info(message.toString());
		}

		// 基底クラスのメソッド実行
		super.doService(request, response);
	}

	/**
	 * ディスパッチ実行
	 * @param request HTTPサーブレットリクエスト
	 * @param response HTTPサーブレットレスポンス
	 */
	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// ※特に処理なし

		// 基底クラスのメソッド実行
		super.doDispatch(request, response);
	}

	/**
	 * サーブレット終了処理
	 */
	@Override
	public void destroy() {
		super.destroy();

		VctLogger.getLogger().info("Spring-Dispatcherサーブレット終了処理が終了しました。サーブレット名：[" + this.getServletName() + "]");
	}

}
