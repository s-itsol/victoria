/**
 *
 */
package net.sitsol.victoria.utils.threads;

/**
 * マルチスレッドによる同期型分散実行を支援する抽象クラス-簡易版
 *  アプリ側ロジックの可読性が上がるよう、無名クラスでインスタンス化して各スレッド実行処理だけを
 *  実装するだけで使えるようにしています。
 *
 * @param <ParamClass> スレッド実行パラメータクラス型
 * @author shibano
 */
public abstract class SimpleMultiThreadExecuter<ParamClass> extends BsMultiThreadExecuter<ParamClass> {

	/**
	 * コンストラクタ
	 * @param maxThreadCount 最大同時実行スレッド数
	 * @param threadName スレッド名 ※マルチスレッド用ログ出力に使うだけ
	 */
	public SimpleMultiThreadExecuter(int maxThreadCount, String threadName) {
		super(maxThreadCount, threadName);
	}

	/**
	 * スレッド実行クラスのインスタンス化
	 * @param threadNo スレッド番号
	 * @param threadName スレッド名
	 */
	@SuppressWarnings("resource")
	@Override
	protected BsThreadExecuter<ParamClass> createThreadExecuter(int threadNo, String threadName) {

		final SimpleMultiThreadExecuter<ParamClass> multiThreadExecuter = this;	// 本クラスのインスタンス

		/**
		 * スレッド実行支援クラス生成
		 */
		BsThreadExecuter<ParamClass> threadExecuter = new BsThreadExecuter<ParamClass>(threadNo, threadName) {

			/**
			 * スレッド実行処理コールバック
			 * @param executeParam スレッド実行パラメータ
			 */
			@Override
			protected void doWokerThreadExecute(ParamClass executeParam) {
				// スレッド実行処理
				multiThreadExecuter.doWokerThreadExecute(this.getThreadNo(), executeParam);
			}
		};

		// スレッド実行支援クラスのインスタンスを返す
		return threadExecuter;
	}

	/**
	 * 全スレッド実行完了イベント通知
	 *  ※エラーあり終了を検知したい場合など、必要応じて派生クラス側で継承して実装する
	 * @param totalExecCount 処理実行件数
	 * @param totalErrorCount エラー件数
	 */
	@Override
	protected void noticeAllThreadCompleted(int totalExecCount, int totalErrorCount) {
		super.noticeAllThreadCompleted(totalExecCount, totalErrorCount);

		// ※無名クラスでインスタンス化したアプリロジック側から追いやすいようにオーバーライドしただけ
	}

	/**
	 * スレッド実行処理コールバック
	 *  ※派生クラス側で継承して実装する
	 *  @param threadNo スレッド番号
	 *  @param executeParam スレッド実行パラメータ
	 */
	protected abstract void doWokerThreadExecute(int threadNo, ParamClass executeParam);

}
