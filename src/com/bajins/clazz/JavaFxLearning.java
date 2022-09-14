package com.bajins.clazz;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaFxLearning {

    /**
     * 获取所有可访问字段
     *
     * @param clazz
     * @param func
     * @date: 2022-8-20 10:56:57
     */
    public static void getFields(Class<?> clazz, Consumer<String> func) {
        Method[] methods = clazz.getMethods();
        // 过滤出可使用的字段
        Set<String> fieldSet = Arrays.stream(methods)
                .filter(t -> Pattern.matches("^set.*", t.getName()))//
                .map(t -> {
                    String mName = t.getName().replace("set", "");
                    return mName.substring(0, 1).toLowerCase() + mName.substring(1);
                }).collect(Collectors.toSet());

        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())); // 获取字段
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) { // 获取父级Class的字段
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
            if (superclass != null) {
                fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            }
        }
        for (Field field : fields) {
            String fName = field.getName();
            if (fieldSet.contains(fName)) {
                func.accept(fName);
            }
        }
    }

    /**
     * 生成实例所有set调用方法并复制到剪贴板
     *
     * @param clazz
     */
    public static void createInstanceSetter(Class<?> clazz) {
        String name = clazz.getSimpleName();
        String subName = name.substring(0, 1);
        name = name.replace(subName, subName.toLowerCase());
        String name_ = name;

        StringJoiner joiner = new StringJoiner(System.lineSeparator());// 获取系统换行符
        getFields(clazz, t -> {
            joiner.add(name_ + ".set" + t.substring(0, 1).toUpperCase() + t.substring(1) + "();");
        });
        /*
         * Swing
         */
        // 封装文本内容
        Transferable trans = new StringSelection(joiner.toString());
        // 把文本内容设置到系统剪贴板
        if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            //JFXPanel jfxPanel = new JFXPanel();
            //java.awt.Toolkit toolkit = jfxPanel.getToolkit();
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
        /*
         * FX
         */
        // 配对(Pair)的实现。同org.apache.commons.lang3.tuple的ImmutablePair、MutablePair、tuple2.Vavr
        Pair<DataFormat, Object> pair = new Pair<>(DataFormat.PLAIN_TEXT, joiner.toString());
        Toolkit toolkit = Toolkit.getToolkit();
        TKClipboard tkClipboard = toolkit.getSystemClipboard();
        // 在系统剪贴板中操作数据
        //SwingUtilities.invokeLater(() -> new JFXPanel());// 在Swing Event Dispatcher Thread中实例化JFXPanel
        /** 方式一 */
        PlatformImpl.startup(() -> { // 初始化即跨线程调用
            tkClipboard.putContent(pair);
            System.out.println("============ javafx.tk.Toolkit.SystemClipboard ============");
            System.out.println(pair.getValue());
        });
        PlatformImpl.tkExit(); // 退出Toolkit
        //Platform.exit();
        PlatformImpl.exit();

        /** 方式二 */
        PlatformImpl.startup(JFXPanel::new); // 初始化二
        PlatformImpl.startup(() -> {
        }); // 初始化三
        PlatformImpl.runLater(() -> {// 跨线程调用
            tkClipboard.putContent(pair);
            System.out.println("============ javafx.tk.Toolkit.SystemClipboard ============");
            System.out.println(pair.getValue());
        });
        PlatformImpl.tkExit(); // 退出Toolkit
        // 退出，配合初始化二会报错
        //Platform.exit();
        PlatformImpl.exit();

        /** 方式三 */
        //PlatformImpl.startup(()-> new JFXPanel()); // 初始化二
        PlatformImpl.startup(() -> {
        }); // 初始化三
        Platform.runLater(() -> { // 跨线程调用
            tkClipboard.putContent(pair);
            System.out.println("============ javafx.tk.Toolkit.SystemClipboard ============");
            System.out.println(pair.getValue());
        });
        PlatformImpl.tkExit(); // 退出Toolkit
        // 退出，配合初始化二会报错
        //Platform.exit();
        PlatformImpl.exit();

        // 以下方式不是在系统剪贴板中操作数据，所以在不能直接使用Ctrl+v粘贴
        //Pair<DataFormat, Object> pair = new Pair<>(DataFormat.PLAIN_TEXT, joiner);
        /*TKClipboard clipboard = toolkit.createLocalClipboard();
        clipboard.putContent(pair);*/
    }

    public static void main(String[] args) {
        createInstanceSetter(DelayedManager.DelayedMessage.class);
    }
}

/*public class JavaFXInitializer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // JavaFX should be initialized
        someGlobalVar.setInitialized(true);
    }
}*/
