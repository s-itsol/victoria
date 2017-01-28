/**
 *
 */
package net.sitsol.victoria.utils.threads;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * マルチスレッドによる同期型分散実行を支援する抽象クラス
 *  AutoCloseableインタフェースを継承し、colseメソッドで各スレッドの終了を待つので、
 *  「
 * 		try( ExMultiThreadExecuter<String> executer = ExMultiThreadExecuter(3) ) {
 * 			// 分散実行させる処理ループ
 * 			for (;;) {
 * 				// スレッド実行要求
 * 				executer.requestExecute("xxx");
 * 			}
 * 		}
 *  」
 *  とすることで、業務ロジック側は同期型処理になります。
 *
 * @param <ParamClass> スレッド実行パラメータクラス型
 * @author shibano
 */
public abstract class BsMultiThreadExecuter<ParamClass> implements AutoCloseable {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

	private BsThreadExecuter<ParamClass>[] threadExecuters = null;		// スレッド実行支援クラス群
	private int totalExecCount = 0;									// 処理実行件数 ※終了処理で集計されます
	private int totalErrorCount = 0;									// エラー件数 ※終了処理で集計されます


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param maxThreadCount 最大同時実行スレッド数
	 */
	@SuppressWarnings("unchecked")
	public BsMultiThreadExecuter(int maxThreadCount) {

		// 同期型マルチスレッド分散処理の開始ログ
		VctLogger.getLogger().info("同期型分散処理を開始します。最大スレッド数：[" + maxThreadCount + "]");

		this.totalExecCount = 0;
		this.totalErrorCount = 0;

		// 最大スレッド数の配列を生成
		BsThreadExecuter<ParamClass>[] executers = new BsThreadExecuter[maxThreadCount];
		{
			// スレッドを生成・開始して配列に格納
			for ( int idx = 0; idx < maxThreadCount; idx++ ) {
				executers[idx] = this.createThreadExecuter(idx + 1);
			}
		}

		this.threadExecuters = executers;
	}

	/**
	 * 実行要求
	 *  ※スレッドの空きが無ければ待機してから実行する
	 * @param param 汎用パラメータ
	 */
	public void requestExecute(ParamClass param) {

		BsThreadExecuter<ParamClass> thread = null;

		// スレッド実行待ちループ
		for (;;) {

			// 空きスレッド取得
			thread = this.getFreeThread();

			// 空きスレッドがあったらループ終了
			if ( thread != null ) {
				break;
			}

			// スレッドが空くのを待つ
			try {
				Thread.sleep(10);

			} catch (Exception ex) {
				// エラーログを出力して処理は継続
				VctLogger.getLogger().error("マルチスレッド空き状態待ちスリープ処理でエラーが発生しました。", ex);
			}
		}

		// スレッド実行要求
		thread.requestExecute(param);
	}

	/**
	 * 空きスレッドの取得
	 */
	private BsThreadExecuter<ParamClass> getFreeThread() {

		BsThreadExecuter<ParamClass> retThread = null;

		// スレッド検査ループ
		for ( BsThreadExecuter<ParamClass> thread : this.getThreadExecuters() ) {
			// 空きスレッドがある場合
			if ( thread.isFree() ) {
				retThread = thread;
				break;
			}
		}

		return retThread;
	}

	/**
	 * 終了処理
	 *  各スレッドの終了待ちと件数集計を行ったのち、全スレッド実行完了イベント通知をします。
	 */
	@Override
	public void close() {

		// 未集計(＝終了要求が未実施)の場合
		if ( this.getTotalExecCount() == 0 && this.getTotalErrorCount() == 0 ) {
			// 全スレッド終了要求
			this.requestAllThreadClose();
		}

		// 同期型マルチスレッド分散処理の終了ログ
		VctLogger.getLogger().info("同期型分散処理を終了しました。エラー件数／実行件数：[" + this.getTotalErrorCount() + "／" + this.getTotalExecCount() + "]");

		// 全スレッド実行完了イベント通知
		this.noticeAllThreadCompleted(this.getTotalExecCount(), this.getTotalErrorCount());
	}

	/**
	 * 全スレッド終了要求
	 */
	protected void requestAllThreadClose() {

		// スレッドループ
		for ( BsThreadExecuter<ParamClass> thread : this.getThreadExecuters() ) {
			// 終了処理
			thread.close();
		}

		// 全スレッドが完了するまで待つ
		for (;;) {

			if ( this.isAllCompleted() ) {
				break;
			}

			try {
				Thread.sleep(10);

			} catch (Exception ex) {
				// エラーログを出力して処理は継続
				VctLogger.getLogger().error("マルチスレッド実行要求・実行終了待ちスリープ処理でエラーが発生しました。", ex);
			}
		}

		// 件数集計
		int totalExecCount = 0;
		int totalErrorCount = 0;

		// スレッドループ
		for ( BsThreadExecuter<ParamClass> thread : this.getThreadExecuters() ) {
			totalExecCount += thread.getExecCount();
			totalErrorCount += thread.getErrorCount();
		}

		this.totalExecCount = totalExecCount;
		this.totalErrorCount = totalErrorCount;
	}

	/**
	 * 全スレッド実行完了イベント通知
	 *  ※エラーあり終了を検知したい場合など、必要応じて派生クラス側で継承して実装する
	 * @param totalExecCount 処理実行件数
	 * @param totalErrorCount エラー件数
	 */
	protected void noticeAllThreadCompleted(int totalExecCount, int totalErrorCount) {
		// ※デフォルトは何もしない
	}

	/**
	 * 全スレッドの完了判定
	 */
	private boolean isAllCompleted() {

		boolean isAllCompleted = true;

		// スレッド検査ループ
		for ( BsThreadExecuter<ParamClass> thread : this.getThreadExecuters() ) {
			// 未完了のスレッドがある場合
			if ( !thread.isCompleted() ) {
				isAllCompleted = false;			// 未完了あり
				break;
			}
		}

		return isAllCompleted;
	}

	/**
	 * スレッド実行クラスの生成
	 *  ※派生クラス側で継承して実装する
	 *  ※BsThreadExecuterの派生クラスをインスタンス化して返すようにすること
	 *  @param threadNo スレッド番号
	 *  @return インスタンス化したスレッド実行クラス
	 */
	protected abstract BsThreadExecuter<ParamClass> createThreadExecuter(int threadNo);


	/* -- getter・setter --------------------------------------------------- */

	protected BsThreadExecuter<ParamClass>[] getThreadExecuters() {
		return threadExecuters;
	}

	public int getTotalErrorCount() {
		return totalErrorCount;
	}

	public int getTotalExecCount() {
		return totalExecCount;
	}

}
