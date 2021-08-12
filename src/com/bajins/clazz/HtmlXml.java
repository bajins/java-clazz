package com.bajins.clazz;


import com.sun.xml.internal.ws.util.xml.XmlUtil;
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
import java.util.Map;

/**
 * HTML封装工具类
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
        return formForward(title, url, params, "UTF-8");
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false); // 不验证DTD
        SAXParser sp = spf.newSAXParser();
        //DefaultHandler dh = new DefaultHandler();
        //DefaultHandler2 df2 = new DefaultHandler2();
        //sp.parse(file,dh); // 解析xml
        //XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        XMLReader xr = sp.getXMLReader();
        xr.setFeature("http://mybatis.org/dtd/mybatis-3-config.dtd", false); // 不验证DTD
        xr.setEntityResolver((publicId, systemId) -> {
            return new InputSource(new StringReader("")); // 不验证DTD
            //return new InputSource(new ByteArrayInputStream("".getBytes())); // 不验证DTD
        });
        //xr.parse(); // 解析xml
        InputSource inputSource = new InputSource();
        SAXSource saxSource = new SAXSource(xr, inputSource);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false); // 不验证xml文件内的dtd
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> {
                InputStream stream = XmlUtil.class.getClassLoader().getResourceAsStream("***.dtd");
                InputSource is = new InputSource(stream);
                is.setPublicId(publicId);
                is.setSystemId(systemId);
                return is;
            });
            Document document = builder.parse(new File("spring-mybatis.xml"));
            // DocumentType doctype = document.getDoctype();
            // document.setXmlStandalone(true);
            // document.setXmlVersion("1.0");
            // System.out.println(document.getDocumentURI());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTDConfig 3.0//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd");
            transformer.transform(new DOMSource(document), new StreamResult(System.out)); // 把内容定位到输出
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

}