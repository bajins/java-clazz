package com.bajins.clazz;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTML和XML封装工具类
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

    /**
     * 对象转XML
     *
     * @param obj
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    /*public static <T> String bean2Xml(Object obj) throws JAXBException, IOException {
        //import javax.xml.bind.JAXBContext;
        //import javax.xml.bind.JAXBException;
        //import javax.xml.bind.Marshaller;
        //import javax.xml.bind.Unmarshaller;
        // 实参中包含需要解析的类
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        // javaBean序列化xml文件器
        Marshaller marshaller = jaxbContext.createMarshaller();
        // 序列化后的xml是否需要格式化输出
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // 取消这个标签的显示<?xml version="1.0" encoding="utf-8" standalone="yes"?>
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        // 编码格式
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8);
        // 序列化
        try (StringWriter sw = new StringWriter();) {
            marshaller.marshal(obj, sw);
            return sw.toString();
        }
    }*/

    /**
     * XML反序列化为对象
     *
     * @param <T>
     * @param xml
     * @param clazz
     * @return
     * @throws JAXBException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    //public static <T> T xml2Bean(String xml, Class<T> clazz) throws JAXBException, UnsupportedEncodingException,
    //        IOException, FactoryConfigurationError, XMLStreamException {
    //    //import javax.xml.bind.JAXBContext;
    //    //import javax.xml.bind.JAXBException;
    //    //import javax.xml.bind.Marshaller;
    //    //import javax.xml.bind.Unmarshaller;
    //    // 实参中包含需要解析的类
    //    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
    //    // xml文件解析成JavaBean对象器
    //    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    //    try (InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"))) {
    //        XMLStreamReader xmlReader = XMLInputFactory.newFactory().createXMLStreamReader(is);
    //        // 序列化
    //        // return (T) unmarshaller.unmarshal(is);
    //        return unmarshaller.unmarshal(xmlReader, clazz).getValue();
    //    }
    //    /*try (StringReader reader = new StringReader(xml);) {
    //        // 序列化
    //        return unmarshaller.unmarshal(reader);
    //    }*/
    //}

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        /*
         * 使用 DOM解析XML文档时，需要读取整个XML文档，在内存中构架生成代表整个 DOM树的Doucment对象，才能再对XML文档进行操作。
         * 如果 XML 文档特别大，就会消耗计算机的大量内存，并且容易导致内存溢出。
         * SAX解析采用事件处理的方式解析XML文件，允许在读取文档的时候，即对文档进行处理，而不必等到整个文档装载完才会文档进行操作
         *
         * 使用JAXP的API创建出SAX解析器后，可以指定解析器去解析某个XML文档。
         * 在解析某个XML文档时，每解析到XML文档的一个组成部分，都会去调用事件处理器的一个方法，该方法会把当前解析到的XML文件内容作为方法的参数传递给事件处理器
         * 事件处理器由程序员编写，程序员通过事件处理器中方法的参数，就可以很轻松地得到sax解析器解析到的数据，从而可以决定如何对数据进行处理
         *
         * https://upload-images.jianshu.io/upload_images/272673-202283ad8e0cea3e.png
         */
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


        // 序列化为XML方式一
        /*StringWriter writer = new StringWriter();
        Object object = new Object();
        try { // 把对象数据转换成xml
            //import javax.xml.bind.JAXBContext;
            //import javax.xml.bind.JAXBException;
            //import javax.xml.bind.Marshaller;
            //import javax.xml.bind.Unmarshaller;
            //JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(writer);*/


    }

}