/**
 *
 */
package net.sitsol.victoria.setvlet.velocity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityLayoutServlet;

import net.sitsol.victoria.exceptions.VctRuntimeException;
import net.sitsol.victoria.log4j.VctLogger;

/**
 * Velocityサーブレット
 *
 * @author shibano
 */
public class VctVelocityViewServlet extends VelocityLayoutServlet {

	private static final long serialVersionUID = -779465106469555856L;

	/**
	 * サーブレット初期処理
	 */
	@Override
	public void init() throws ServletException {

		long startMillus = System.currentTimeMillis();

		// 基底クラスの処理実行
		super.init();

		long execMillis = System.currentTimeMillis() - startMillus;

		VctLogger.getLogger().info("Velocityサーブレット初期処理が終了しました。"
										+ "処理時間：[" + execMillis + "](ms)"
										+ ", サーブレット名：[" + this.getServletName() + "]"
									);
	}

	/**
	 * サーブレット終了処理
	 */
	@Override
	public void destroy() {

		// 基底クラスの処理実行
		super.destroy();

		VctLogger.getLogger().info("Velocityサーブレット終了処理が終了しました。サーブレット名：[" + this.getServletName() + "]");
	}

	/**
	 * リクエストハンドラ
	 */
	@Override
	protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {

		// ※ほぼ動作検証用。普段は量が多いのでデバッグレベル
		if ( VctLogger.getLogger().isDebugEnabled() ) {
			VctLogger.getLogger().debug("Velocity-handleRequest開始 - リクエストURI：[" + ( request != null ? request.getRequestURI() : null ) + "]");
		}

		// 基底クラスの処理実行
		return super.handleRequest(request, response, ctx);
	}

	/**
	 * リソース・テンプレートファイル不在例外ハンドラ
	 *  原因特定を素早く行えるようにするため、明示的なエラーログ出力をする
	 * @param ex リソース不在例外
	 */
	@Override
	protected void manageResourceNotFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException ex) throws IOException {

		// 基底クラスの処理実行
		super.manageResourceNotFound(request, response, ex);

		// テンプレート基底ディレクトリ
		//  ※複数指定の場合はコレクション型で得られるケースもあるのでObject型としておく
		//    デフォルトは「当該プロパティ設定が無い」旨を明示した文字列(＝そのままログ出力に使う)
		Object basePath = "Not Found Property!!(" + VelocityEngine.FILE_RESOURCE_LOADER_PATH + ")";
		{
			try {
				// テンプレート基底ディレクトリ設定の取得
				basePath = this.getVelocityView().getVelocityEngine().getProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH);

			} catch ( Exception innerEx ) {
				// ※警告ログを出力して処理は継続する
				VctLogger.getLogger().warn("Velocityテンプレート読込み基底ディレクトリ設定の取得でエラーが発生しました。"
													+ "対象プロパティ：[" + VelocityEngine.FILE_RESOURCE_LOADER_PATH + "]"
												, innerEx
											);
			}
		}

		// アプリ側にもエラーログ出力
		VctLogger.getLogger().error("Velocityテンプレートファイルが見つかりませんでした。"
										+ "テンプレート読込み基底ディレクトリ設定：[" + basePath + "]"
										, ex
									);
	}

	/**
	 * 応答結果とテンプレートファイルのマージ
	 * @param template テンプレート情報
	 */
	@Override
	protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws IOException {

		String templateName = template != null ? template.getName() : null;

		// ※ほぼ動作検証用。普段は量が多いのでデバッグレベル
		if ( VctLogger.getLogger().isDebugEnabled() ) {
			VctLogger.getLogger().debug("Velocity-mergeTemplate開始 - テンプレートファイル名：[" + templateName + "]");
		}

		try {
			// 基底クラスの処理実行
			super.mergeTemplate(template, context, response);

		} catch ( Exception ex ) {
			throw new VctRuntimeException("Velocityテンプレート・マージでエラーが発生しました。"
													+ "テンプレート名：[" + templateName + "]"
												, ex
											);
		}
	}

}
