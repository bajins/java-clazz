package com.bajins.clazz;

import sun.awt.SunHints;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * https://github.com/ofpteam/ofp/blob/master/src/main/java/com/webside/ofp/common/util/PrintUtil.java
 * https://github.com/PengLiGTF/KGMS/blob/master/KindergartenManagement/src/com/kindergarten/util/print/test/KinderPrintToolBak.java
 */
public class Graphics {
    /**
     * 颜色转换
     */
    private static Color fromStrToARGB(String str) {
        String str1 = str.substring(1, 3);
        String str2 = str.substring(3, 5);
        String str3 = str.substring(5, 7);
        int red = Integer.parseInt(str1, 16);
        int green = Integer.parseInt(str2, 16);
        int blue = Integer.parseInt(str3, 16);
        if (str.length() == 9) {
            String str4 = str.substring(7, 9);
            int alpha = Integer.parseInt(str4, 16);
            return new Color(red, green, blue, alpha);
        } else {
            return new Color(red, green, blue);
        }
    }

    /**
     * 创建Graphics2D
     *
     * @param bgBufImage 底图
     * @return Graphics2D
     */
    private static Graphics2D createG2D(BufferedImage bgBufImage) {
        Graphics2D bgBufImageGraphics = bgBufImage.createGraphics();
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);
        return bgBufImageGraphics;
    }

    /**
     * 绘制海报底色（默认微软雅黑/PLAIN/32）
     *
     * @param bgBufImage 底图
     * @param color      颜色
     */
    private static void setBackGroup(BufferedImage bgBufImage, String color) {
        Graphics2D bgBufImageGraphics = createG2D(bgBufImage);
        bgBufImageGraphics.setBackground(fromStrToARGB(color));//填充整个屏幕
        bgBufImageGraphics.clearRect(0, 0, bgBufImage.getWidth(), bgBufImage.getHeight());
        bgBufImageGraphics.dispose();
    }


    /**
     * 绘制海报文字（默认微软雅黑/PLAIN/32）
     *
     * @param basebBI 底图
     * @param text    文本
     * @param x       坐标 x
     * @param y       坐标 y
     * @param color   颜色
     * @param font    字体
     */
    private static void drawText(BufferedImage basebBI, String text, int x, int y, String color, Font font) {
        // 抗锯齿
        if (font == null) {
            font = new Font("微软雅黑", Font.PLAIN, 32);
        }
        Graphics2D g2D = createG2D(basebBI);
        g2D.setFont(font);
        g2D.setPaint(new Color(0, 0, 0, 64));
        // 先绘制一遍底色
        g2D.drawString(text, x, y);
        g2D.setPaint(fromStrToARGB(color));
        // 再绘制一遍文字
        // 由于部分情况会出现文字模糊的情况，保险起见才出此对策。
        g2D.drawString(text, x, y);
        g2D.dispose();
    }

    /**
     * 绘制海报文字(换行)
     *
     * @param basebBI         底图
     * @param text            文本
     * @param x               位置：x
     * @param y               位置：y
     * @param lineHeight      单行行高
     * @param lineWidth       单行行宽
     * @param color           文本颜色
     * @param font            文本字体
     * @param limitLineNum    限制行数
     * @param backgroundWidth 底背位置（多行文字绘制时，出现为单行时居中的区域宽度。）
     */
    private static void drawTextNewLine(BufferedImage basebBI, String text, int x, int y, int lineHeight,
                                        int lineWidth, String color, Font font, int limitLineNum, int backgroundWidth) {
        Graphics2D graphics = createG2D(basebBI);
        if (font == null) {
            font = new Font("微软雅黑", Font.PLAIN, 32);
        }
        graphics.setFont(font);
        graphics.setPaint(fromStrToARGB(color));

        //计算字符串所占屏幕长度
        FontRenderContext frc = graphics.getFontRenderContext();
        graphics.getFontRenderContext();
        //记录行数
        if (backgroundWidth > 0) {
            x = (backgroundWidth - lineWidth) / 2;
        }

        List<String> lineList = new ArrayList<>();
        int stringIndex = 0;
        StringBuilder tmpLineString = new StringBuilder("");
        while (stringIndex < text.length()) {
            String tmp_char = text.substring(stringIndex, stringIndex + 1);
            Rectangle2D tempStringBounds = font.getStringBounds(tmpLineString + tmp_char, frc);
            double width = tempStringBounds.getWidth();
            if (width > lineWidth) {
                lineList.add(tmpLineString.toString());
                tmpLineString = new StringBuilder("");
            }
            tmpLineString.append(tmp_char);
            stringIndex++;
        }
        // Color.BLACK 。字体颜色
        graphics.setPaint(fromStrToARGB(color));
        if (lineHeight == 0) {
            lineHeight = 35;
        }

        for (int i = 0; i < lineList.size(); i++) {
            String lineStr = lineList.get(i);
            double width = font.getStringBounds(lineStr, frc).getWidth();
            double diffWidth = font.getStringBounds("...", frc).getWidth();
            if (i > limitLineNum - 1) {
                break;
            } else if (i == limitLineNum - 1 && lineWidth - width < diffWidth) {
                lineStr = lineStr.substring(0, lineStr.length() - 2) + "...";
            }
            graphics.drawString(lineStr, x, y + (i + 1) * lineHeight);
            graphics.drawString(lineStr, x, y + (i + 1) * lineHeight);
        }
        graphics.dispose();
    }

    /**
     * 绘制海报图片
     *
     * @param basebBI 底图
     * @param path    图片地址
     * @param x       位置：x
     * @param y       位置：y
     * @param width   图片宽度
     * @param height  图片高度
     */
    private static void drawImage(BufferedImage basebBI, String path, int x, int y, int width, int height) throws Exception {
        BufferedImage qrCodeImage = ImageIO.read(new URL(path));
        drawImage(basebBI, qrCodeImage, x, y, width, height);
    }

    /**
     * 绘制海报图片
     *
     * @param basebBI 底图
     * @param imageBI 图片 BufferedImage
     * @param x       位置：x
     * @param y       位置：y
     * @param width   图片宽度
     * @param height  图片高度
     */
    private static void drawImage(BufferedImage basebBI, BufferedImage imageBI, int x, int y, int width, int height) {
        Graphics2D g2D = createG2D(basebBI);
        if (width == -1) {
            width = imageBI.getWidth();
        }

        if (height == -1) {
            height = imageBI.getHeight();
        }
        g2D.drawImage(imageBI, x, y, width, height, null);
        g2D.dispose();
    }

    /**
     * 创建带圆角的图片
     *
     * @param path       图片地址
     * @param ratioWith  水平直径 -1 表示圆型
     * @param ratioHeith 垂直直径 -1 表示圆型
     */
    private static BufferedImage getRoundImage(String path, int ratioWith, int ratioHeith) throws Exception {
        BufferedImage qrCodeImage = ImageIO.read(new URL(path));
        BufferedImage image = new BufferedImage(qrCodeImage.getWidth(), qrCodeImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D image2D = createG2D(image);
        if (ratioWith <= -1) {
            ratioWith = qrCodeImage.getWidth();
        }
        if (ratioHeith <= -1) {
            ratioHeith = qrCodeImage.getHeight();
        }
        image2D.fillRoundRect(0, 0, qrCodeImage.getWidth(), qrCodeImage.getHeight(), ratioWith, ratioHeith);
        image2D.setComposite(AlphaComposite.SrcIn);
        image2D.drawImage(qrCodeImage, 0, 0, qrCodeImage.getWidth(), qrCodeImage.getHeight(), null);
        image2D.dispose();
        return image;
    }

    /**
     * 输出图片
     *
     * @param bgBufImage 底图
     * @param path       图片输出地址
     */
    private static void saveImage(BufferedImage bgBufImage, String path) throws Exception {
        Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter imageWriter = iterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(1);
        imageWriter.setOutput(ImageIO.createImageOutputStream(new FileOutputStream(path)));
        IIOImage iio_image = new IIOImage(bgBufImage, null, null);
        imageWriter.write(null, iio_image, imageWriteParam);
        imageWriter.dispose();
    }

    public static void drawImage() throws Exception {
        //二维码
        String qrCodeImageUrl = "";
        //头像
        String headUrl = "http://pic.51yuansu.com/pic3/cover/00/63/25/589bdedf5475d_610.jpg";

        BufferedImage bgBufImage = new BufferedImage(750, 1334,
                BufferedImage.TYPE_INT_RGB);
        setBackGroup(bgBufImage, "#FF0000");
        drawImage(bgBufImage, qrCodeImageUrl, bgBufImage.getWidth() - 200, 10, -1, -1);
        drawImage(bgBufImage, getRoundImage(headUrl, -1, -1), 100, 100, 200, 200);
        drawText(bgBufImage, "测试", 0, 100, "#000000", null);
        drawTextNewLine(bgBufImage,
                "测试圣诞快乐数据库里搭街坊卡拉手机打开拉萨奥斯陆冬季开发了喀什假大空立法解释考虑",
                0, 100, 35, 200, "#000000", null, 3, 750);
        saveImage(bgBufImage, "E:\\demo1.jpeg");
    }

    public static void main(String[] args) {
        try {
            drawImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class Graphic2DTest {

        public static void draw() throws IOException {
            //绘制宽=480，长=640的图板
            int width = 480, hight = 720;
            BufferedImage image = new BufferedImage(width, hight, BufferedImage.TYPE_INT_RGB);
            //获取图形上下文,graphics想象成一个画笔
            Graphics2D graphics = (Graphics2D) image.getGraphics();

            //消除线条锯齿
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //对指定的矩形区域填充颜色
            graphics.setColor(Color.ORANGE);    //GREEN:绿色；  红色：RED;   灰色：GRAY
            graphics.fillRect(0, 0, 240, 720);
            //对指定的矩形区域填充颜色
            graphics.setColor(Color.PINK);
            graphics.fillRect(240, 0, 240, 720);

            //生成随机数
            Random random = new Random();
            /*
             * 画线 x,y是坐标，定义线段的两个坐标点
             */
            graphics.setColor(Color.BLACK);
            int x = 100, y = 100, x1 = 100, y1 = y;
            graphics.drawLine(x, y, x + x1, y1);
            /*
             *画出一个折线
             */
            int[] xPoints = {100, 100, 250, 250};
            int[] yPoints = {180, 150, 150, 180};
            graphics.drawPolyline(xPoints, yPoints, 4);
            /*
             * 画出一个闭合多边形(三角形)
             */
            int[] xPoints1 = {100, 100, 200};
            int[] yPoints1 = {240, 320, 280};
            graphics.drawPolygon(xPoints1, yPoints1, 3);
            /*
             * 画出一个闭合多边形(菱形)
             */
            int[] xPoints2 = {240, 300, 360, 300};
            int[] yPoints2 = {280, 240, 280, 320};
            graphics.drawPolygon(xPoints2, yPoints2, 4);
            graphics.setColor(Color.ORANGE);
            graphics.fillPolygon(xPoints2, yPoints2, 4);
            /*
             *绘制一个椭圆形
             */
            graphics.setColor(Color.GREEN);
            int xOval = 100, yOval = 360;
            graphics.drawOval(xOval, yOval, 100, 100);

            /*
             *绘制一个矩形
             */
            //graphics.setColor(Color.GRAY);//--设置矩形边框颜色 。GREEN:绿色；  红色：RED;   灰色：GRAY
            int xRect = 240, yRect = 360;
            graphics.drawRect(xRect, yRect, 200, 100);

            //设置文字颜色
            graphics.setColor(new Color(20 + random.nextInt(100), 20 + random.nextInt(100), 20 + random.nextInt(100)));
            //设置文字内容、位置
            graphics.drawString("直线", 100 + 50, 100 - 5);
            graphics.drawString("折线", 200, 150 - 5);
            graphics.drawString("空心三角形", 110, 280);
            graphics.drawString("实心菱形", 300 - 20, 280);
            graphics.drawString("椭圆形", 100 + 50, 360 + 50);
            graphics.drawString("矩形", 240 + 50, 360 + 50);
            //graphics.drawString("错误的背景颜色", 100, 540);

            //graphics.setPaintMode();
            //graphics.translate(400, 600);

            graphics.dispose();//释放此图形的上下文并释放它所使用的所有系统资源
            FileOutputStream out = new FileOutputStream("d:/1.jpeg");
            ImageIO.write(image, "JPEG", out);

            out.flush();
            out.close();
            //super.doGet(request, response);

        }

        public static void main(String[] args) {
            try {
                draw();
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    public static class PrintTest1 implements Printable {
        /**
         * @param gra       指明打印的图形环境
         * @param pf        指明打印页格式(页面大小以点为计量单位，1点为1英才的1/72，1英寸为25.4毫米。A4纸大致为595×842点)
         * @param pageIndex 指明页号
         */
        @Override
        public int print(java.awt.Graphics gra, PageFormat pf, int pageIndex) throws PrinterException {
            System.out.println("pageIndex=" + pageIndex);

            Component c = null;//print string

            String str = "中华民族是勤劳、勇敢和富有智慧的伟大民族。";//转换成Graphics2D

            Graphics2D g2 = (Graphics2D) gra;//设置打印颜色为黑色

            g2.setColor(Color.black);
        /*Paper paper = pf.getPaper();//得到页面格式的纸张

        paper.setSize(500, 500);//纸张大小

        paper.setImageableArea(0, 0, 500, 500); //设置打印区域的大小

        System.out.println(paper.getWidth());

        System.out.println(paper.getHeight());

        pf.setPaper(paper);//将该纸张作为格式*/

            //打印起点坐标

            double x = pf.getImageableX();
            double y = pf.getImageableY();
            if (pageIndex == 0) {
                //设置打印字体(字体名称、样式和点大小)(字体名称可以是物理或者逻辑名称)
                // Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput

                Font font = new Font("新宋体", Font.PLAIN, 9);

                g2.setFont(font);//设置字体//BasicStroke bs_3=new BasicStroke(0.5f);

                float[] dash1 = {4.0f};

                g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4.0f, dash1, 0.0f));
                float heigth = font.getSize2D();//字体高度

                System.out.println("x=" + x);//使用抗锯齿模式完成文本呈现

                //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                //-1- 用Graphics2D直接输出//首字符的基线(右下部)位于用户空间中的 (x, y) 位置处//g2.drawLine(10,10,200,10);

                Image src = Toolkit.getDefaultToolkit().getImage("d://logo.gif");

                g2.drawImage(src, (int) x, (int) y, c);
                int img_Height = src.getHeight(c);
                int img_width = src.getWidth(c);
                //System.out.println("img_Height="+img_Height+"img_width="+img_width) ;

                g2.drawString(str, (float) x, (float) y + 1 * heigth + img_Height);

                g2.drawLine((int) x, (int) (y + 1 * heigth + img_Height + 10), (int) x + 200,
                        (int) (y + 1 * heigth + img_Height + 10));

                g2.drawImage(src, (int) x, (int) (y + 1 * heigth + img_Height + 11), c);//-2- 直接构造TextLayout打印

            /*FontRenderContext frc = g2.getFontRenderContext();

            TextLayout layout = new TextLayout(str, font, frc);

            layout.draw(g2, (float) x, (float) y + 2 * heigth);*/

                //-3- 用LineBreakMeasurer进行打印

            /*AttributedString text = new AttributedString(str);

            text.addAttribute(TextAttribute.FONT, font);

            LineBreakMeasurer lineBreaker = new LineBreakMeasurer(text.getIterator(), frc);

            //每行字符显示长度(点)

            double width = pf.getImageableWidth();

            //首字符的基线位于用户空间中的 (x, y) 位置处

            Point2D.Double pen = new Point2D.Double(100, y + 3 * heigth);

            while ((layout = lineBreaker.nextLayout((float) width)) != null) {
                layout.draw(g2, (float) x, (float) pen.y);
                pen.y += layout.getAscent();
            }*/

                return PAGE_EXISTS;
            }
            return NO_SUCH_PAGE;
        }

        public void main(String[] args) {//获取打印服务对象

            PrinterJob job = PrinterJob.getPrinterJob();

            PageFormat pageFormat = job.defaultPage();//得到默认页格式

            job.setPrintable(new PrintTest1());//设置打印类

            try {//可以用printDialog显示打印对话框，在用户确认后打印；也可以直接打印
                // boolean a=job.printDialog();
                // if(a){
                job.print();
                //}
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    public class TestPrint implements Printable {

        private BufferedImage background;
        public static final float DPI = 72;

        public void main(String[] args) {
            new TestPrint();
        }

        public TestPrint() {

            EventQueue.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                try {
                    background = ImageIO.read(new File("F:/MgkGrl_Yuki_by_fredrin.jpg"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                float width = cmToPixel(21f, DPI);
                float height = cmToPixel(29.7f, DPI);

                Paper paper = new Paper();
                float margin = cmToPixel(1, DPI);
                paper.setImageableArea(margin, margin, width - (margin * 2), height - (margin * 2));
                PageFormat pf = new PageFormat();
                pf.setPaper(paper);

                BufferedImage img = new BufferedImage(Math.round(width), Math.round(height),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fill(new Rectangle2D.Float(0, 0, width, height));
                try {
                    g2d.setClip(new Rectangle2D.Double(pf.getImageableX(), pf.getImageableY(),
                            pf.getImageableWidth(), pf.getImageableHeight()));
                    print(g2d, pf, 0);
                } catch (PrinterException ex) {
                    ex.printStackTrace();
                }
                g2d.dispose();

                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JLabel(new ImageIcon(img)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }

        public float cmToPixel(float cm, float dpi) {
            return (dpi / 2.54f) * cm;
        }

        @Override
        public int print(java.awt.Graphics graphics, PageFormat pageFormat, int page) throws PrinterException {
            if (page > 0) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g = (Graphics2D) graphics;

            g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            if (background != null) {

                int x = (int) Math.round((pageFormat.getImageableWidth() - background.getWidth()) / 2f);
                int y = (int) Math.round((pageFormat.getImageableHeight() - background.getHeight()) / 2f);

                g.drawImage(background, x, y, null);
            }
            g.setColor(Color.BLACK);
            g.draw(new Rectangle2D.Double(0, 0, pageFormat.getImageableWidth() - 1,
                    pageFormat.getImageableHeight() - 1));

            return PAGE_EXISTS;
        }
    }
}



