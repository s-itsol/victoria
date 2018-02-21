/**
 * 
 */
package net.sitsol.victoria.setvlet.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Victoria共通 WEBメッセージ群クラス
 * 
 * @author shibano
 */
public class VctWebMessages implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 特定のプロパティと関連付けられたものとは対照的に、グローバルメッセージを使用するための"プロパティ名"の文字列。
	 */
	public static final String GLOBAL_MESSAGE = VctWebMessages.class.getPackage().getName() + ".GLOBAL_MESSAGE";

	/**
	 * WEBメッセージアイテムインスタンス比較クラス
	 */
	private static final Comparator<VctWebMessageItem> ACTION_ITEM_COMPARATOR
		= new Comparator<VctWebMessageItem>() {
			public int compare(VctWebMessageItem o1, VctWebMessageItem o2) {
				return o1.getOrder() - o2.getOrder();
			}
		};

	/**
	 * WEBメッセージアイテムクラス
	 */
	protected class VctWebMessageItem implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		
		// -------------------------------------------------------------------------
		//  field
		// -------------------------------------------------------------------------
		
		protected List<VctWebMessage> list = null;		// メッセージリスト
		protected int iOrder = 0;						// メッセージアイテムの追加位置 ※0オリジン
		protected String property = null;				// メッセージに関連付けられたプロパティ
		
		/**
		 * コンストラクタ
		 * @param list メッセージリスト
		 * @param iOrder メッセージアイテムの追加位置 ※インスタンス化する側で一意な数値(＝0オリジン)が採番される想定
		 * @param property メッセージに関連付けられたプロパティ
		 */
		public VctWebMessageItem(List<VctWebMessage> list, int iOrder, String property) {
			this.list = list;
			this.iOrder = iOrder;
			this.property = property;
		}
		
		/**
		 * 文字列化
		 */
		@Override
		public String toString() {
			return this.getList().toString();
		}
		
		
		/*-- setter・getter ------------------------------------------------------*/
		
		public List<VctWebMessage> getList() {
			return list;
		}
		
		public void setList(List<VctWebMessage> list) {
			this.list = list;
		}
		
		public int getOrder() {
			return iOrder;
		}
		
		public void setOrder(int iOrder) {
			this.iOrder = iOrder;
		}
		
		public String getProperty() {
			return property;
		}
		
		public void setProperty(String property) {
			this.property = property;
		}
		
	}


	// -------------------------------------------------------------------------
	//  field
	// -------------------------------------------------------------------------

	protected boolean accessed = false;												// アクセス済みフラグ
	protected HashMap<String, VctWebMessageItem> messages = new HashMap<>();		// プロパティ-メッセージアイテムマップ ※キー：プロパティ名、値：メッセージアイテム
	protected int iCount = 0;														// 追加したプロパティ別メッセージアイテム件数


	// -------------------------------------------------------------------------
	//  method
	// -------------------------------------------------------------------------

	/**
	 * デフォルトコンストラクタ
	 */
	public VctWebMessages() {
		super();
	}

	/**
	 * コンストラクタ
	 * @param messages メッセージ群クラスのインスタンス
	 */
	public VctWebMessages(VctWebMessages messages) {
		super();
		this.add(messages);
	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		return this.messages.toString();
	}

	/**
	 * メッセージ群追加
	 * @param messages 追加するメッセージ群クラスのインスタンス
	 */
	public void add(VctWebMessages actionMessages) {
		
		if ( actionMessages == null ) {
			return;
		}
		
		Iterator<String> props = actionMessages.properties();		// 追加メッセージ群のプロパティ群
		
		// プロパティループ
		while ( props.hasNext() ) {
			
			String property = props.next();
			
			Iterator<VctWebMessage> msgs = actionMessages.get(property);		// 追加メッセージ群から、対象プロパティのメッセージ群
			
			// メッセージループ
			while ( msgs.hasNext() ) {
				
				VctWebMessage msg = msgs.next();
				
				// メッセージ追加
				this.add(property, msg);
			}
		}
	}

	/**
	 * メッセージ追加
	 * @param property 追加するメッセージ-プロパティ名
	 * @param message 追加するメッセージクラスのインスタンス
	 */
	public void add(String property, VctWebMessage message) {
		
		VctWebMessageItem item = this.messages.get(property);		// 対象プロパティのメッセージアイテム
		
		List<VctWebMessage> list;				// 追加先メッセージリスト
		{
			// プロパティに該当するメッセージアイテム無し
			if ( item == null ) {
				
				// 新規メッセージリスト生成して追加先とする
				list = new ArrayList<>();
				
				// メッセージアイテム生成
				item = new VctWebMessageItem(list, this.iCount, property);
				this.iCount++;				// アイテム件数カウントアップ
				
				// プロパティ-メッセージアイテムマップへ追加
				this.messages.put(property, item);
				
			// プロパティに該当するメッセージアイテムあり
			} else {
				// メッセージアイテムのメッセージリストを追加先とする
				list = item.getList();
			}
		}
		
		// 追加先へ、メッセージを追加
		list.add(message);
	}

	/**
	 * メッセージ群クリア
	 */
	public void clear() {
		this.messages.clear();
	}

	/**
	 * メッセージ無し判定
	 * @return 判定結果 ※true：メッセージ無し
	 */
	public boolean isEmpty() {
		return this.messages.isEmpty();
	}

	/**
	 * メッセージアクセス済み判定
	 *  ※get()またはget(String)メソッドが呼ばれている場合に「アクセス済み」を返す
	 * @return 判定結果 ※true：アクセス済み
	 */
	public boolean isAccessed() {
		return this.accessed;
	}

	/**
	 * 全プロパティのイテレータ取得
	 * @return 全プロパティのイテレータ
	 */
	public Iterator<String> properties() {
		
		if ( this.messages.isEmpty() ) {
			// 空リストを生成してイテレータを返す ※strutsソースでは「Collections.EMPTY_LIST」を使っていたが、警告が出るので普通に実装しておいた
			return new ArrayList<String>().iterator();
		}
		
		// 全メッセージアイテムリスト編集
		ArrayList<VctWebMessageItem> actionItems = new ArrayList<>();
		{
			// 全メッセージアイテムループ
			for ( Iterator<VctWebMessageItem> itemIter = this.messages.values().iterator(); itemIter.hasNext(); ) {
				actionItems.add( itemIter.next() );
			}
			
			// ソート
			Collections.sort(actionItems, ACTION_ITEM_COMPARATOR);
		}
		
		// 全プロパティリスト編集
		ArrayList<String> results = new ArrayList<>();
		{
			// メッセージアイテムループ
			for ( Iterator<VctWebMessageItem> itemIter = actionItems.iterator(); itemIter.hasNext(); ) {
				VctWebMessageItem ami = itemIter.next();
				results.add( ami.getProperty() );
			}
		}
		
		return results.iterator();
	}

	/**
	 * 全メッセージのイテレータ取得
	 * @return 全メッセージのイテレータ
	 */
	public Iterator<VctWebMessage> get() {
		
		this.accessed = true;
		
		if ( this.messages.isEmpty() ) {
			// 空リストを生成してイテレータを返す ※strutsソースでは「Collections.EMPTY_LIST」を使っていたが、警告が出るので普通に実装しておいた
			return new ArrayList<VctWebMessage>().iterator();
		}
		
		// 全メッセージアイテムリスト編集
		ArrayList<VctWebMessageItem> actionItems = new ArrayList<>();
		{
			// 全メッセージアイテムループ
			for ( Iterator<VctWebMessageItem> itemIter = this.messages.values().iterator(); itemIter.hasNext(); ) {
				actionItems.add( itemIter.next() );
			}
			
			// ソート
			Collections.sort(actionItems, ACTION_ITEM_COMPARATOR);
		}
		
		// 全メッセージリスト編集
		ArrayList<VctWebMessage> results = new ArrayList<>();
		{
			// メッセージアイテムループ
			for ( Iterator<VctWebMessageItem> itemIter = actionItems.iterator(); itemIter.hasNext(); ) {
				
				VctWebMessageItem ami = itemIter.next();
				
				// メッセージループ
				for ( Iterator<VctWebMessage> msgsIter = ami.getList().iterator(); msgsIter.hasNext(); ) {
					results.add( msgsIter.next() );
				}
			}
		}
		
		return results.iterator();
	}

	/**
	 * メッセージのイテレータ取得
	 * @param property 対象メッセージ-プロパティ名
	 * @return メッセージのイテレータ
	 */
	public Iterator<VctWebMessage> get(String property) {
		
		this.accessed = true;
		
		VctWebMessageItem item = this.messages.get(property);		// 対象プロパティのメッセージアイテム
		
		// メッセージアイテム無し
		if ( item == null ) {
			// 空リストを生成してイテレータを返す ※strutsソースでは「Collections.EMPTY_LIST」を使っていたが、警告が出るので普通に実装しておいた
			return new ArrayList<VctWebMessage>().iterator();
			
		// メッセージアイテムあり
		} else {
			return item.getList().iterator();
		}
	}

	/**
	 * 全メッセージ件数取得
	 * @return 全メッセージ件数
	 */
	public int size() {
		
		int total = 0;
		
		for ( Iterator<VctWebMessageItem> itemIter = this.messages.values().iterator(); itemIter.hasNext(); ) {
			VctWebMessageItem ami = itemIter.next();
			total += ami.getList().size();
		}
		
		return total;
	}

	/**
	 * メッセージ件数取得
	 * @param property 対象メッセージ-プロパティ名
	 * @return メッセージ件数
	 */
	public int size(String property) {
		
		VctWebMessageItem item = this.messages.get(property);
		
		return ( item == null ) ? 0 : item.getList().size();
	}

}
