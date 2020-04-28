package com.bajins.clazz;


import java.util.Map;

/**
 * HTML封装工具类
 *
 * @author claer woytu.com
 * @program com.bajins.api.utils
 * @description HtmlFormUtil
 * @create 2019-03-18 13:57
 */
public class Html {

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
        StringBuffer htmlForm = new StringBuffer();
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

}