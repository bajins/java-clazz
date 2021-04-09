package com.bajins.clazz;

import com.bajins.clazz.delayqueue.DelayedMessage;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.Method;
import java.util.StringJoiner;

public class FxLearning {

    /**
     * 生成实例所有set调用方法并复制到剪贴板
     *
     * @param clazz
     */
    public static void createInstanceSetter(Class<?> clazz) {
        String name = clazz.getSimpleName();
        String subName = name.substring(0, 1);
        name = name.replace(subName, subName.toLowerCase());
        StringJoiner joiner = new StringJoiner(System.lineSeparator());// 获取系统换行符
        for (Method m : clazz.getMethods()) {
            if (m.getName().startsWith("set")) {
                joiner.add(name + "." + m.getName() + "();");
            }
        }
        /**
         * Swing
         */
        // 封装文本内容
        Transferable trans = new StringSelection(joiner.toString());
        // 把文本内容设置到系统剪贴板
        if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            /*JFXPanel jfxPanel = new JFXPanel();
            java.awt.Toolkit toolkit = jfxPanel.getToolkit();*/
            java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            clipboard.setContents(trans, null);
            try {
                String value = (String) trans.getTransferData(DataFlavor.stringFlavor); // 转换内容到字符串
                System.out.println("============ java.awt.Toolkit.SystemClipboard ============");
                System.out.println(value);
            } catch (Exception ex) {
                ex.printStackTrace(); // 错误处理
            }
        }

        /**
         * FX
         */
        // 配对(Pair)的实现。同org.apache.commons.lang3.tuple的ImmutablePair、MutablePair、tuple2.Vavr
        Pair<DataFormat, Object> pair = new Pair<>(DataFormat.PLAIN_TEXT, joiner.toString());
        Toolkit toolkit = Toolkit.getToolkit();
        // 方式一
        //SwingUtilities.invokeLater(() -> new JFXPanel());// 在Swing Event Dispatcher Thread中实例化JFXPanel
        //PlatformImpl.startup(()-> new JFXPanel()); // 初始化二
        PlatformImpl.startup(()-> {}); // 初始化三
        Platform.runLater(() -> { // 跨线程调用
            toolkit.getSystemClipboard().putContent(pair);
            System.out.println("============ javafx.tk.Toolkit.SystemClipboard ============");
            System.out.println(pair.getValue());
        });
        //PlatformImpl.runLater(() -> {});// 跨线程调用
        //PlatformImpl.tkExit();// 退出Toolkit
        PlatformImpl.exit();// 退出，配合初始化三
        // 方式二
        //Pair<DataFormat, Object> pair = new Pair<>(DataFormat.PLAIN_TEXT, joiner);
        TKClipboard clipboard = toolkit.createLocalClipboard();
        clipboard.putContent(pair);
        System.out.println("============ javafx.tk.Toolkit.LocalClipboard ============");
        System.out.println(pair.getValue());
    }

    public static void main(String[] args) {
        createInstanceSetter(DelayedMessage.class);
    }
}

/*public class JavaFXInitializer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // JavaFX should be initialized
        someGlobalVar.setInitialized(true);
    }
}*/
