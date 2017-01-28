/**
 *
 */
package net.sitsol.victoria.utils.threads;

import java.io.Closeable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.NDC;

import net.sitsol.victoria.log4j.VctLogger;
import net.sitsol.victoria.models.userinfo.IUserInfo;
import net.sitsol.victoria.threadlocals.ThreadUserInfo;

/**
 * スレッド実行を支援する抽象クラス
 *
 * @param <ParamClass> スレッド実行パラメータクラス型
 * @author shibano
 */
public abstract class BsThreadExecuter<ParamClass> implements Closeable, Runnable {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	/**
	 * スレッド状態
	 */
	public enum ThreadStatus {
		/** 実行要求待ち */
		Wait
		,
		/** 実行要求直後～実行前 */
		Requested
		,
		/** 実行中 */
		Active
		,
		/** 終了要求直後～終了前 */
		EndRequest
		,
		/** 終了 */
		End
	}

	private Thread thread = null;		  			// スレッドクラス
	private ThreadStatus status = null;			// スレッド状態
	private int threadNo = 0;						// スレッド番号
	private String threadTypeName = null;			// スレッドクラス名
	private IUserInfo mainThreadUserInfo = null;	// メインスレッドのユーザー情報
	private ParamClass executeParam = null;		// 実行時の汎用パラメータ

	private int execCount = 0;						// 処理実行件数
	private int errorCount = 0;					// エラー件数


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param threadNo スレッド番号 ※マルチスレッド用にログ出力に使うだけ
	 */
	public BsThreadExecuter(int threadNo) {
		// フィールド値の初期化
		this.threadNo = threadNo;
		this.threadTypeName = this.getClass().getSimpleName();
		this.mainThreadUserInfo = ThreadUserInfo.getCurrentThreadUserInfo();

		this.status = ThreadStatus.Wait;
		this.executeParam = null;
		this.execCount = 0;
		this.errorCount = 0;

		// 自クラスをスレッドとしてインスタンス化
		this.thread = new Thread(this);
		// スレッド開始
		this.thread.start();
	}

	/**
	 * 処理実行要求
	 *  ※スレッド実行終了待ちを行いたくない場合は、本メソッドを呼ぶ前にisFree()で空き状態を確認して制御すると良い
	 *  @param param 汎用パラメータ
	 */
	public void requestExecute(ParamClass param) {
		// スレッドが要求待ちに戻るまで待つ
		this.waitStatusFree();

		// 汎用パラメータをメンバ変数に設定
		this.executeParam = param;
		// ステータスを実行要求にする
		this.status = ThreadStatus.Requested;
	}

	/**
	 * 終了処理
	 */
	@Override
	public void close() {

		if ( this.isCompleted() ) {
			return;
		}

		// スレッドが要求待ちに戻るまで待つ
		this.waitStatusFree();

		// ステータスを終了要求にする
		this.status = ThreadStatus.EndRequest;
	}

	/**
	 * スレッドが空き状態になるまで待つ
	 *  ※スリープを繰り返す
	 */
	private void waitStatusFree() {

		// スレッドが要求待ちに戻るまで待つ
		for (;;) {

			if ( this.isFree() ) {
				break;
			}

			try {
				Thread.sleep(10);

			} catch (Exception ex) {
				// エラーログを出力して処理は継続
				VctLogger.getLogger().error("スレッド空き状態待ちスリープ処理でエラーが発生しました。", ex);
			}
		}
	}

	/**
	 * スレッドが空き状態(＝待たずに実行可能な状態)かを取得
	 * @return ※true：空き状態
	 */
	public boolean isFree() {

		// ステータスが要求待ちの場合のみ「空き状態」と判定する
		if ( ThreadStatus.Wait.equals(this.getStatus()) ) {
			return true;
		}

		return false;
	}

	/**
	 * スレッドが完了しているかを取得
	 * @return >※true：完了している
	 */
	public boolean isCompleted() {

		// ステータスが終了の場合のみ「完全に終了した状態」と判定する
		if ( ThreadStatus.End.equals(this.getStatus()) ) {
			return true;
		}

		return false;
	}


	/*-------------------------------------------------------------------*/
	/*	WokerThread側のメソッド 										 */
	/*-------------------------------------------------------------------*/

	/**
	 * スレッド実行処理
	 */
	public void run() {

		IUserInfo mainThreadUserInfo = this.getMainThreadUserInfo();
		String userId = mainThreadUserInfo != null ? mainThreadUserInfo.getUserId() : StringUtils.EMPTY;

		// ユーザーIDをlog4jネスト化診断コンテキストに保持 TODO：こちらもAutoCloseにする
		NDC.push(userId);

		// メインスレッドのユーザー情報を引き継ぎ
		try ( ThreadUserInfo threadUserInfo = new ThreadUserInfo(mainThreadUserInfo) ) {

			// スレッド開始ログ出力
			VctLogger.getLogger().info("スレッドを開始します。"
									+ "スレッド番号：[" + this.getThreadNo() + "]"
									+ ", スレッド型：[" + this.getThreadTypeName() + "]"
								);

			// ステータス変更監視ループ
			for (;;) {

				// 実行要求
				if ( ThreadStatus.Requested.equals(this.getStatus()) ) {

					// ステータスを実行中にする
					this.status = ThreadStatus.Active;

					try {
						// 実行件数をカウント
						this.execCount++;
						// 本処理の実行
						this.doWokerThreadExecute(this.getExecuteParam());

					} catch (Exception ex) {
						// エラー件数をカウント
						this.errorCount++;

						// エラーログを出力してスレッドは継続させる
						VctLogger.getLogger().error("スレッド処理実行中にエラーが発生しました。"
															+ "スレッド番号：[" + this.threadNo + "]"
															+ ", スレッド型：[" + this.threadTypeName + "]"
														, ex
													);

					} finally {
						// 正常終了／エラー終了 問わず、ステータスを実行待ちに戻す
						if ( ThreadStatus.Active.equals(this.status) ) {
							this.executeParam = null;
							this.status = ThreadStatus.Wait;
						}
					}

				// 終了要求
				} else if ( ThreadStatus.EndRequest.equals(this.status) ) {
					break;
				}

				// 実行要求・実行終了待ち
				try {
					Thread.sleep(10);

				} catch (Exception ex) {
					// エラーログを出力して処理は継続
					VctLogger.getLogger().error("スレッド実行要求・実行終了待ちスリープ処理でエラーが発生しました。", ex);
				}
			}

			// スレッド終了ログ出力
			VctLogger.getLogger().info("スレッドを終了します。"
											+ "スレッド番号：[" + this.threadNo + "]"
											+ ", スレッド型：[" + this.threadTypeName + "]"
											+ ", エラー件数／実行件数：[" + this.errorCount + "／" + this.execCount + "]"
										);

			// ステータスを終了にする
			this.status = ThreadStatus.End;

		} finally {
			// 現在スレッドのlog4jネスト化診断コンテキストを破棄
			NDC.remove();
		}
	}

	/**
	 * ワーカースレッド実行処理の本体
	 *  ※派生クラス側で継承して実装する
	 *  @param executeParam スレッド実行パラメータ
	 */
	protected abstract void doWokerThreadExecute(ParamClass executeParam);


	/* -- getter・setter --------------------------------------------------- */

	public IUserInfo getMainThreadUserInfo() {
		return mainThreadUserInfo;
	}

	protected ParamClass getExecuteParam() {
		return executeParam;
	}

	protected ThreadStatus getStatus() {
		return status;
	}

	protected Thread getThread() {
		return thread;
	}

	protected int getThreadNo() {
		return threadNo;
	}

	protected String getThreadTypeName() {
		return threadTypeName;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public int getExecCount() {
		return execCount;
	}

}
