/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.threadlocals.ThreadLog4jNdc;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;

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
		
		// TODO：ログインはまだ
		String userId = request.getSession().getId();
		IUserInfo loginUserInfo = null;
		
		try (
				ThreadLog4jNdc threadLog4jNdc = new ThreadLog4jNdc(userId);				// ※log4j-NDCメッセージ
				ThreadUserInfo threadUserInfo = new ThreadUserInfo(loginUserInfo);		// ※ログインユーザー情報
		) {
			// 基底クラスのメソッド実行
			super.doService(request, response);
		}
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
