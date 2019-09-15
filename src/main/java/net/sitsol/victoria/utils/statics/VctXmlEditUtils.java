/**
 *
 */
package net.sitsol.victoria.utils.statics;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.sitsol.victoria.exceptions.VctRuntimeException;

/**
 * XML編集支援ユーティリティ
 *
 * @author rei_shibano
 */
public class VctXmlEditUtils {

	// ドキュメントビルダー ※生成に時間がかかるため、シングルトン
	private static DocumentBuilder builder_ = null;
	// １度取得したJAXBコンテンツはマップ ※生成に時間がかかるため、シングルトン
	private static Map<String, JAXBContext> jaxbContentMap_ = new HashMap<String, JAXBContext>();
	// トランスフォーマー ※生成に時間がかかるため、シングルトン
    private static Transformer transformer_ = null;

	/**
	 * コンストラクタ ※外部からインスタンス化させない
	 */
	protected VctXmlEditUtils() {}

	/**
	 * ドキュメントビルダーの取得
	 * @return ドキュメントビルダーのインスタンス
	 */
	protected static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {

		if ( builder_ == null ) {
			// デフォルトのドキュメントビルダー生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder_ = factory.newDocumentBuilder();
		}

		return builder_;
	}

	/**
	 * JAXBコンテンツの取得
	 *  ※パッケージ名ごとにインスタンス化して使いまわす
	 * @param packageName パッケージ名
	 * @return JAXBコンテンツのインスタンス
	 * @throws JAXBException JAXB例外
	 */
	protected static JAXBContext getJaxbContent(String packageName) throws JAXBException {

		// 同名のパッケージがマップ上に無い場合
		if ( !jaxbContentMap_.containsKey(packageName) ) {
			// 新規に生成してマップに保持
			jaxbContentMap_.put(packageName, JAXBContext.newInstance(packageName));
		}

		return jaxbContentMap_.get(packageName);
	}

	/**
	 * トランスフォーマーの取得
	 * @return トランスフォーマーのインスタンス
	 */
	protected static Transformer getTransformer() throws TransformerConfigurationException {
		
		if ( transformer_ == null ) {
			// デフォルトのトランスフォーマー生成
			TransformerFactory factory = TransformerFactory.newInstance();
			transformer_ = factory.newTransformer();
		}

		return transformer_;
	}

	/**
	 * XML文字列→DOMへ変換
	 * @param xmlString XML文字列
	 * @return XMLドキュメント
	 */
	public static Document stringToDom(String xmlString) {

		try {
			// ドキュメントビルダー取得
			DocumentBuilder builder = getDocumentBuilder();
			// 入力ソース生成
			InputSource inputSource	= new InputSource(new StringReader(xmlString) );

			// XML解析
			return builder.parse(inputSource);

		} catch ( Exception ex ) {
			throw new VctRuntimeException("XML文字列→DOMへの変換でエラーが発生しました。"
												+ "XML文字列：[" + xmlString + "]"
											, ex
			);
		}
	}

	/**
	 * DOM→XML文字列へ変換
	 * @param document XMLドキュメント
	 * @return 変換後文字列
	 */
	public static String domToString(Document document) {

		try {
			// 文字列出力クラス生成
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			// トランスフォーマー取得
			Transformer transformer = getTransformer();
			// XML文字列へ変換
			transformer.transform(new DOMSource(document), result);

			// xmlタグの後ろの改行を除外して戻す
			return StringUtils.replaceOnce(writer.toString(), "\n", StringUtils.EMPTY);

		} catch ( Exception ex ) {
			throw new VctRuntimeException("DOM→XML文字列への変換でエラーが発生しました。", ex);
		}
	}

	/**
	 * XML文字列→JAXBエレメントへ変換
	 * @param xmlString XML文字列
	 * @param JAXBエレメントのクラス型
	 * @return JAXBエレメントのインスタンス
	 * @param <JaxbClass> JAXBエレメントのクラス型
	 */
	public static <JaxbClass> JAXBElement<JaxbClass> stringToJaxb(String xmlString, Class<JaxbClass> declaredType) {

		String className = null;

		try {
			if ( declaredType != null ) {
				className = declaredType.getSimpleName();
			}

			// 一旦、DOMへ変換
			Document document = stringToDom(xmlString);

			// JAXBコンテンツ取得
			JAXBContext context = getJaxbContent(declaredType.getPackage().getName());
			// アン・マーシャラー生成
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// JAXB生成
			return unmarshaller.unmarshal(document, declaredType);

		} catch ( Exception ex ) {
			throw new VctRuntimeException("XML文字列→JAXBエレメントへの変換でエラーが発生しました。"
												+ "XML文字列：[" + xmlString + "]"
												+ ", JAXBエレメント型：[" + className + "]"
											, ex
			);
		}
	}

	/**
	 * JAXBエレメント→XML文字列へ変換
	 * @param jaxbElement JAXBエレメント
	 * @return 変換後文字列
	 */
	public static String jaxbToString(Object jaxbElement) {

		String className = null;

		try {
			if ( jaxbElement != null ) {
				className = jaxbElement.getClass().getSimpleName();
			}

			// JAXBコンテンツ取得
			JAXBContext context = getJaxbContent(jaxbElement.getClass().getPackage().getName());
			// マーシャラー生成
			Marshaller marshaller = context.createMarshaller();

			// 新規DOMオブジェクト生成
		    Document document = getDocumentBuilder().newDocument();
			document.setXmlStandalone(false);			// デフォルトでstandalone属性はなし

			// DOM出力
			marshaller.marshal(jaxbElement, document);

			return domToString(document);

		} catch ( Exception ex ) {
			throw new VctRuntimeException("JAXBエレメント→XML文字列への変換でエラーが発生しました。"
												+ "JAXBエレメント型：[" + className + "]"
											, ex
			);
		}
	}

	/**
	 * DOM→XMLファイル出力
	 *  ※XMLファイルは改行やインデントなし
	 * @param document XMLドキュメント
	 * @param outFilePath 出力先ファイルパス
	 */
	public static void domToXmlFile(Document document, String outFilePath) {
		
		try {
			File outfile = new File(outFilePath);
			
			// ディレクトリ情報取得
			File dirInfo = new File(outfile.getParent());
			
			// ディレクトリが存在しない場合、作成しておく
			if ( !dirInfo.exists() ) {
				dirInfo.mkdirs();
			}
			
			// トランスフォーマー生成
			Transformer transformer = VctXmlEditUtils.getTransformer();
			
			transformer.transform(new DOMSource(document), new StreamResult(outfile));
			
		} catch (Exception ex) {
			throw new VctRuntimeException(	"DOM→XMLファイル出力でエラーが発生しました。"
												+ "出力先ファイルパス：[" + outFilePath + "]"
											, ex
			);
		}
	}

	/**
	 * XMLファイル読込み→DOM
	 * @param readFilePath 読込みファイルパス
	 */
	public static Document xmlFileToDom(String readFilePath) {
		
		try {
			// ドキュメントビルダー生成
			DocumentBuilder builder = VctXmlEditUtils.getDocumentBuilder();
			
			// XML解析
			return builder.parse(new File(readFilePath));
			
		} catch (Exception ex) {
			throw new VctRuntimeException(	"XMLファイル読込み→DOMへの変換でエラーが発生しました。"
												+ "読込みファイルパス：[" + readFilePath + "]"
											, ex
			);
		}
	}

}
