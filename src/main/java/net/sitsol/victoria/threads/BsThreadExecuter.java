/**
 *
 */
package net.sitsol.victoria.threads;

import java.io.Closeable;

import net.sitsol.victoria.log4j.VctLogger;

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

    private Thread thread_ = null;          	// スレッドクラス
    private ThreadStatus status_ = null;		// スレッド状態
    private int threadNo_ = 0;					// スレッド番号
    private String threadTypeName_ = null;		// スレッドクラス名
    private ParamClass executeParam_ = null;	// 実行時の汎用パラメータ

    private int execCount_ = 0;				// 処理実行件数
    private int errorCount_ = 0;				// エラー件数


	// ------------------------------------------------------------------------
	//  method
	// ------------------------------------------------------------------------

    /**
     * コンストラクタ
     * @param threadNo スレッド番号 ※マルチスレッド用にログ出力に使うだけ
     */
	public BsThreadExecuter(int threadNo) {
        // メンバ変数の初期化
        this.threadNo_ = threadNo;
        this.threadTypeName_ = this.getClass().getSimpleName();

        this.status_ = ThreadStatus.Wait;
        this.executeParam_ = null;
        this.execCount_ = 0;
        this.errorCount_ = 0;

        // 自クラスをスレッドとしてインスタンス化
        this.thread_ = new Thread(this);
        // スレッド開始
        this.thread_.start();
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
        this.executeParam_ = param;
        // ステータスを実行要求にする
        this.status_ = ThreadStatus.Requested;
    }

    /**
     * 終了処理
     */
    public void close() {
        if ( this.isCompleted() ) {
            return;
        }

        // スレッドが要求待ちに戻るまで待つ
        this.waitStatusFree();

        // ステータスを終了要求にする
        this.status_ = ThreadStatus.EndRequest;
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
    	if ( ThreadStatus.Wait.equals(this.status_) ) {
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
    	if ( ThreadStatus.End.equals(this.status_) ) {
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
        // スレッド開始ログ出力
        VctLogger.getLogger().info("スレッドを開始します。"
                                + "スレッド番号：[" + this.threadNo_ + "]"
                                + ", スレッド型：[" + this.threadTypeName_ + "]"
                            );

        // ステータス変更監視ループ
        for (;;) {
            // 実行要求
            if ( ThreadStatus.Requested.equals(this.status_) ) {
                // ステータスを実行中にする
                this.status_ = ThreadStatus.Active;

                try {
                    // 実行件数をカウント
                    this.execCount_++;
                    // 本処理の実行
                    this.doWokerThreadExecute(this.executeParam_);
                }
                catch (Exception ex) {
                    // エラー件数をカウント
                    this.errorCount_++;

                    // エラーログを出力してスレッドは継続させる
                    VctLogger.getLogger().error("スレッド処理実行中にエラーが発生しました。"
                                            + "スレッド番号：[" + this.threadNo_ + "]"
                                            + ", スレッド型：[" + this.threadTypeName_ + "]"
                                        , ex
                                    );
                }
                finally {
                    // 正常終了／エラー終了 問わず、ステータスを実行待ちに戻す
                    if ( ThreadStatus.Active.equals(this.status_) ) {
                        this.executeParam_ = null;
                        this.status_ = ThreadStatus.Wait;
                    }
                }
            }
            // 終了要求
            else if ( ThreadStatus.EndRequest.equals(this.status_) ) {
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
                                + "スレッド番号：[" + this.threadNo_ + "]"
                                + ", スレッド型：[" + this.threadTypeName_ + "]"
                                + ", エラー件数／実行件数：[" + this.errorCount_ + "／" + this.execCount_ + "]"
                            );

        // ステータスを終了にする
        this.status_ = ThreadStatus.End;
    }

    /**
     * ワーカースレッド実行処理の本体
     *  ※派生クラス側で継承して実装する
     *  @param executeParam 汎用パラメータ
     */
    protected abstract void doWokerThreadExecute(ParamClass executeParam);


	/* -- getter・setter --------------------------------------------------- */

	protected ParamClass getExecuteParam_() {
		return executeParam_;
	}

	protected void setExecuteParam_(ParamClass executeParam_) {
		this.executeParam_ = executeParam_;
	}

	protected ThreadStatus getStatus_() {
		return status_;
	}

	protected void setStatus_(ThreadStatus status_) {
		this.status_ = status_;
	}

	protected Thread getThread_() {
		return thread_;
	}

	protected void setThread_(Thread thread_) {
		this.thread_ = thread_;
	}

	protected int getThreadNo_() {
		return threadNo_;
	}

	protected void setThreadNo_(int threadNo_) {
		this.threadNo_ = threadNo_;
	}

	protected String getThreadTypeName_() {
		return threadTypeName_;
	}

	protected void setThreadTypeName_(String threadTypeName_) {
		this.threadTypeName_ = threadTypeName_;
	}

	public int getErrorCount_() {
		return errorCount_;
	}

	public void setErrorCount_(int errorCount_) {
		this.errorCount_ = errorCount_;
	}

	public int getExecCount_() {
		return execCount_;
	}

	public void setExecCount_(int execCount_) {
		this.execCount_ = execCount_;
	}

}
