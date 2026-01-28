package com.bajins.clazz;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 创建一个动态列表对话框，用于实时显示数据。
 * @author bajins
 */
public class ShowDynamicListDialog {

    private JDialog dialog;
    private final DefaultListModel<String> listModel;
    private final JList<String> list;
    private int messageCounter = 0;

    /**
     * 构造一个显示动态列表对话框的实例
     *
     * @param parent 父级窗口框架，用于设置对话框的所有者关系
     */
    public ShowDynamicListDialog(JFrame parent) {
        // 1. 使用 DefaultListModel 作为 JList 的数据模型
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);

        // 创建 JScrollPane 以支持滚动
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        // 创建一个面板来容纳列表和一个关闭按钮
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // 让窗口可见
        // scrollPane.setVisible(true);
        // panel.setVisible(true);

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dialog.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);

        // 2. 创建 JDialog
        dialog = new JDialog(parent, "实时日志监控");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);

        // 3. 设置为非模态，这样它就不会阻塞主线程
        dialog.setModal(false);

        // 使用 pack() 自动调整大小，然后居中
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 在列表的最前面追加一条新数据。 这个方法是线程安全的，可以在任何线程中调用。
     * <p>
     * 使用SwingUtilities.invokeLater(() -> { ... });或javax.swing.Timer确保 UI 更新/事件监听器 在事件分发线程 (EDT) 上执行
     *
     * @param data 要追加的数据
     */
    public void prependData(final String data) {
        listModel.add(0, data);

        // 4. 自动滚动到列表顶部，让用户看到最新的数据
        list.ensureIndexIsVisible(0);
    }

    public static void main(String[] args) {
        // 创建并显示主窗口（即使不可见，也是 JDialog 的一个好父组件）
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 200);
        mainFrame.setLocationRelativeTo(null);

        // 让窗口可见
        mainFrame.setVisible(true);

        // 创建我们的动态列表对话框实例
        ShowDynamicListDialog liveDialog = new ShowDynamicListDialog(mainFrame);

        // 5. 使用 javax.swing.Timer 来模拟后台持续产生的新数据
        // Timer 是 Swing 中进行周期性任务的最佳选择，因为它确保了事件监听器在 EDT 上执行
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        ActionListener dataGenerator = e -> {
            liveDialog.messageCounter++;
            String newData = String.format("[%s] - Event #%d: A new message has arrived.",
                    LocalTime.now().format(formatter), liveDialog.messageCounter);
            liveDialog.prependData(newData);
        };

        // 创建一个每秒 (1000 毫秒) 触发一次的定时器
        Timer timer = new Timer(1000, dataGenerator);
        timer.setInitialDelay(500); // 第一次延迟半秒执行
        timer.start(); // 启动定时器
    }
}