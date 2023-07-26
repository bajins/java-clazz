package com.bajins.clazz;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTML和XML封装工具类
 * <p>
 * https://docs.oracle.com/javase/tutorial/jaxp/TOC.html
 * <p>
 * https://upload-images.jianshu.io/upload_images/272673-202283ad8e0cea3e.png
 * <pre>
 * 1. DOM（Document Object Model）：
 *    - DOM是一种基于树结构的XML解析技术。
 *    - DOM将整个XML文档加载到内存中，构建一个树形结构，通过操作这个树来访问和修改XML数据。
 *    - DOM适用于XML文档较小且需要频繁访问和修改的场景。
 *    - 在Java中，可以使用标准的JAXP（Java API for XML Processing）库来解析和生成DOM树。
 *
 * 2. SAX（Simple API for XML Parsing）：
 *    - SAX是一种基于事件驱动的XML解析技术。
 *    - SAX解析器在解析XML文档时，按顺序读取XML数据，并触发相应的事件，应用程序通过实现事件处理器来处理这些事件。
 *    - SAX适用于处理大型XML文档或在内存有限的情况下，因为它不需要将整个XML文档加载到内存中。
 *    - 在Java中，可以使用SAXParser来解析XML文档，也可以使用XMLWriter来生成XML文档。
 *
 * 3. JAXB（Java Architecture for XML Binding）：
 *    - JAXB是一种将Java对象与XML数据进行绑定的技术，它可以将Java对象转换为XML数据（编组）或将XML数据转换为Java对象（解组）。
 *    - JAXB使用注解或XML配置文件来定义Java对象与XML元素之间的映射关系。
 *    - JAXB适用于Java对象与XML数据之间的相互转换，特别是在Web服务和数据持久化等场景中常被使用。
 *    - 在Java中，可以使用JAXB提供的工具来生成Java类和XML配置文件之间的映射关系，并使用JAXBContext来进行编组和解组操作。
 *
 * 4. JAXP（Java API for XMLProcessing）
 *    - 使用JAXP的API创建出SAX解析器后，可以指定解析器去解析某个XML文档
 *
 * 5. TrAX（Transformation API for XML）
 *    - 用于使用 XSLT 样式表转换 XML 文档的 Java API
 *
 * 6. StAX（Streaming API for XML）
 *    - Java中处理XML的一种流式解析模式
 *
 * 7. XSLT（Extensible Stylesheet Language Transformations）
 *    - 使用XPath来选取XML文档中的元素或者节点
 *    - 可以将XML文档转换成其他格式
 * </pre>
 *
 * @author claer woytu.com
 * @program com.bajins.api.utils
 * @description HtmlFormUtil
 * @create 2019-03-18 13:57
 */
public class HtmlXml {

    /**
     * 自定义编码格式跳转HTML表单封装
     *
     * @param title   标题
     * @param url     表单提交路径
     * @param params  提交参数
     * @param charset 字符编码
     * @return String
     */
    public static String formForward(String title, String url, Map<String, String> params, String charset) {
        StringBuilder htmlForm = new StringBuilder();
        htmlForm.append("<html>");
        htmlForm.append("<head>");
        htmlForm.append("<meta http-equiv='Content-Type' content='text/html; charset='").append(charset).append("'>");
        htmlForm.append("<title>").append(title).append("</title>");
        htmlForm.append("</head>");
        htmlForm.append("<body>");
        htmlForm.append("<form name='pay' method='post' action='").append(url).append("' id='form'>");
        for (Map.Entry<String, String> entries : params.entrySet()) {
            htmlForm.append("  <input type='hidden' name='").append(entries.getKey());
            htmlForm.append("' value='").append(entries.getValue()).append("'/>");
        }
        htmlForm.append("</form>");
        htmlForm.append("</body>");
        htmlForm.append("<script type='text/javascript'>document.getElementById('form').submit();</script>");
        htmlForm.append("</html>");

        return htmlForm.toString();
    }

    /**
     * 默认以UTF-8编码格式跳转HTML表单封装
     *
     * @param title
     * @param url
     * @param params
     * @return String
     */
    public static String formForward(String title, String url, Map<String, String> params) {
        return formForward(title, url, params, StandardCharsets.UTF_8.name());
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        // 解析XML方式一
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false); // 不验证DTD
        // 通过SAX解析工厂得到解析器对象
        SAXParser sp = spf.newSAXParser();
        //DefaultHandler dh = new DefaultHandler();
        //DefaultHandler2 df2 = new DefaultHandler2();
        //sp.parse(file,dh); // 解析xml
        //XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        // 通过解析器对象得到一个XML的读取器
        XMLReader xr = sp.getXMLReader();
        // 设置读取器的事件处理器
        //xr.setContentHandler(new BookParserHandler());
        xr.setFeature("http://mybatis.org/dtd/mybatis-3-config.dtd", false); // 不验证DTD
        xr.setEntityResolver((publicId, systemId) -> {
            return new InputSource(new StringReader("")); // 不验证DTD
            //return new InputSource(new ByteArrayInputStream("".getBytes())); // 不验证DTD
        });
        //xr.parse(); // 解析xml
        InputSource inputSource = new InputSource();
        SAXSource saxSource = new SAXSource(xr, inputSource);

        // 解析XML方式二
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false); // 不验证xml文件内的dtd
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> {
                InputStream stream = HtmlXml.class.getClassLoader().getResourceAsStream("***.dtd");
                InputSource is = new InputSource(stream);
                is.setPublicId(publicId);
                is.setSystemId(systemId);
                return is;
            });
            Document document = builder.parse(new File("spring-mybatis.xml"));
            // false(no)表示XML不是独立的而是依赖于外部所定义的一个 DTD，true(yes)表示XML是自包含的(self-contained).
            document.setXmlStandalone(false);
            // document.setXmlVersion("1.0");
            // System.out.println(document.getDocumentURI());
            // DocumentType doctype = document.getDoctype();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name()); // 字符集编码
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // 设置换行
            // no表示XML不是独立的而是依赖于外部所定义的一个 DTD，yes表示XML是自包含的(self-contained).
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTDConfig 3.0//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd");
            transformer.transform(new DOMSource(document), new StreamResult(System.out)); // 把内容定位到输出
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

}