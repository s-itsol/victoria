/**
 *
 */
package net.sitsol.victoria.setvlet.spring;

import org.springframework.web.servlet.DispatcherServlet;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * Spring-Dispatcherサーブレット
 *
 * @author shibano
 */
public class VctSpringDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * サーブレット終了処理
	 */
	@Override
	public void destroy() {
		super.destroy();

		VctLogger.getLogger().info("Spring-Dispatcherサーブレット終了処理が終了しました。サーブレット名：[" + this.getServletName() + "]");
	}

}
