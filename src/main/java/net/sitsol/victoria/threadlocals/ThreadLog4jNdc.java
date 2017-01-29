package net.sitsol.victoria.threadlocals;

import org.apache.log4j.NDC;

/**
 * スレッド毎log4j-NDC情報保持クラス
 *  try-with-resourceにて、スレッド開始～終了を包括して使用することを想定している。
 *
 * @author shibano
 */
public class ThreadLog4jNdc implements AutoCloseable {

	/* -- static ----------------------------------------------------------- */

	/**
	 * スレッド毎log4j-NDCメッセージのプッシュ(＝スタックへの追加)
	 * @param pushMessage プッシュするlog4j-NDCメッセージ
	 */
	private static void pushThreadNdcMessage(String pushMessage) {
		// スレッド毎追加メッセージをスタックへプッシュ
		NDC.push(pushMessage);
	}

	/**
	 * スレッド毎log4j-NDCメッセージのポップ(＝スタックから取り出して破棄)
	 */
	private static void popThreadNdcMessage() {
		// スレッド毎追加メッセージをスタックからポップ
		NDC.pop();
	}

	/**
	 * 現スレッド-最終プッシュlog4j-NDCメッセージの取得
	 * @return 最後にプッシュされたlog4j-NDCメッセージ
	 */
	public static String getCurrentThreadLastPushNdcMessage() {
		return NDC.peek();
	}


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ
	 * @param pushMessage プッシュするlog4j-NDCメッセージ
	 */
	public ThreadLog4jNdc(String pushMessage) {
		// スレッド毎ユーザー情報の保持開始
		pushThreadNdcMessage(pushMessage);
	}

	/**
	 * リソースクローズ
	 */
	@Override
	public void close() {
		// スレッド毎ユーザー情報の保持終了
		popThreadNdcMessage();
	}

}
