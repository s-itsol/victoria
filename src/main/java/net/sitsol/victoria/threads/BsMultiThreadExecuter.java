/**
 *
 */
package net.sitsol.victoria.threads;

import java.io.Closeable;

import net.sitsol.victoria.log4j.VctLogger;

/**
 * マルチスレッドによる同期型分散実行を支援する抽象クラス
 *  Closeableインタフェースを継承しているので
 *  「
 *    ExMultiThreadExecuter<String> executer = null;
 *    try {
 *        executer = new ExMultiThreadExecuter
          …
 *    } finally {
 *        if ( executer != null ) {
 *            executer.close();
 *        }
 *    }
 *  」
 *  とすることで同期型処理になります。
 *
 * @param <ParamClass> スレッド実行パラメータクラス型
 * @author shibano
 */
public abstract class BsMultiThreadExecuter<ParamClass> implements Closeable {

	// ------------------------------------------------------------------------
	//  field
	// ------------------------------------------------------------------------

    private BsThreadExecuter<ParamClass>[] threadExecuters_ = null;	// スレッド実行支援クラス群
    private int totalExecCount_ = 0;									// 処理実行件数 ※終了処理で集計されます
    private int totalErrorCount_ = 0;									// エラー件数 ※終了処理で集計されます


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

        this.totalExecCount_ = 0;
        this.totalErrorCount_ = 0;

        // 最大スレッド数の配列を確保
        this.threadExecuters_ = new BsThreadExecuter[maxThreadCount];
        // スレッドを生成・開始して配列に格納
        for ( int idx = 0; idx < maxThreadCount; idx++ ) {
            this.threadExecuters_[idx] = this.createThreadExecuter(idx + 1);
        }
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
        for (BsThreadExecuter<ParamClass> thread : this.threadExecuters_) {
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
     */
    public void close() {
        // 未集計(＝終了要求が未実施)の場合
        if (this.totalExecCount_ == 0 && this.totalErrorCount_ == 0) {
            // 終了要求
            this.requestClose();
        }

        // 同期型マルチスレッド分散処理の終了ログ
        VctLogger.getLogger().info(
            "同期型分散処理を終了しました。エラー件数／実行件数：[" + this.totalErrorCount_ + "／" + this.totalExecCount_ + "]");
    }

    /**
     * 終了要求
     *  各スレッドの終了待ちと件数集計を行います。
     *  インスタンスを破棄する前に件数を取得したい場合に明示的に呼び出してください。
     */
    public void requestClose() {
        // 全スレッド終了要求
        for ( BsThreadExecuter<ParamClass> thread : this.threadExecuters_ ) {
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

        for ( BsThreadExecuter<ParamClass> thread : this.threadExecuters_ ) {
        	totalExecCount += thread.getExecCount_();
        	totalErrorCount += thread.getErrorCount_();
        }

        this.totalExecCount_ = totalExecCount;
        this.totalErrorCount_ = totalErrorCount;
    }

    /**
     * 全スレッドの完了判定
     */
    private boolean isAllCompleted() {
        boolean isAllCompleted = true;

        // スレッド検査ループ
        for (BsThreadExecuter<ParamClass> thread : this.threadExecuters_) {
            // 未完了のスレッドがある場合
            if ( !thread.isCompleted() ) {
                isAllCompleted = false;
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

	protected BsThreadExecuter<ParamClass>[] getThreadExecuters_() {
		return threadExecuters_;
	}

	protected void setThreadExecuters_(BsThreadExecuter<ParamClass>[] threadExecuters_) {
		this.threadExecuters_ = threadExecuters_;
	}

	public int getTotalErrorCount_() {
		return totalErrorCount_;
	}

	public void setTotalErrorCount_(int totalErrorCount_) {
		this.totalErrorCount_ = totalErrorCount_;
	}

	public int getTotalExecCount_() {
		return totalExecCount_;
	}

	public void setTotalExecCount_(int totalExecCount_) {
		this.totalExecCount_ = totalExecCount_;
	}

}
