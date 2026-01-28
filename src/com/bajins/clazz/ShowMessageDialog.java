package com.bajins.clazz;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 消息提示框
 * @author bajins
 */
public class ShowMessageDialog {

    /**
     * 显示一个包含只读列表的提示对话框。
     *
     * @param parentComponent 对话框的父组件，用于定位。如果为 null，对话框将居于屏幕中央。
     * @param title           对话框的标题。
     * @param message         显示在列表上方的提示信息。
     * @param listData        要在列表中显示的字符串数组。
     */
    public static void showInfoList(Component parentComponent, String title, String message, String[] listData) {
        // 1. 创建 JList 实例来显示数据
        JList<String> list = new JList<>(listData);

        // 2. 这是关键：禁用列表，使其不可选择
        // 这会改变列表的外观（通常是灰色），明确告诉用户它仅用于显示。
        list.setEnabled(false);
        list.setVisibleRowCount(Math.min(listData.length, 10)); // 最多显示10行，超出部分需要滚动

        // 设置原型单元格以支持水平滚动
        // 找出最长的字符串，并将其设置为 JList 的“原型”，JList 将以此计算其首选宽度。
        /*if (listData != null && listData.length > 0) {
            String longest = listData[0];
            for (int i = 1; i < listData.length; i++) {
                if (listData[i].length() > longest.length()) {
                    longest = listData[i];
                }
            }
            list.setPrototypeCellValue(longest);
        }*/

        // (可选) 为了让禁用的列表在视觉上更像一个标签而不是一个坏掉的组件，
        // 我们可以手动设置其颜色以匹配对话框的背景和前景。
        list.setForeground(UIManager.getColor("Label.foreground"));
        list.setBackground(UIManager.getColor("Panel.background"));

        // 3. 将 JList 放入 JScrollPane，以便在列表项过多时可以滚动
        JScrollPane scrollPane = new JScrollPane(list);
        // 这为 pack() 方法提供了一个计算基准，避免弹窗过小或过大。
        scrollPane.setPreferredSize(new Dimension(1000, 800)); // 设置一个合适的初始大小

        // 4. 创建一个面板来组合消息标签和列表，以获得更好的布局
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // 使用 BorderLayout 并设置垂直间隙10像素
        panel.add(new JLabel(message), BorderLayout.NORTH); // 消息在上方
        panel.add(scrollPane, BorderLayout.CENTER); // 列表在中间
        // 自定义的颜色设置
        /*Color color = new Color(255, 0, 0);
        panel.setBackground(color);
        panel.setForeground(color);*/

        // 3. 创建一个 JOptionPane 实例，它将为我们提供标准按钮和图标
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);

        // 4. 从 JOptionPane 创建 JDialog，这样我们就可以控制它
        JDialog dialog = optionPane.createDialog(parentComponent, title);

        // 5. 核心优化点 2: 调用 pack() 方法！
        // 这会让对话框自动调整大小，以最佳尺寸包裹其所有内容。
        // 我们不再需要 setSize()。
        dialog.pack();

        // 防止对话框因内容过多而超出屏幕
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 设置最大尺寸为屏幕的80%
        int maxWidth = (int) (screenSize.width * 0.8);
        int maxHeight = (int) (screenSize.height * 0.8);

        Dimension dialogSize = dialog.getSize();

        // 如果当前尺寸超过了最大限制，则重设尺寸
        int newWidth = Math.min(dialogSize.width, maxWidth);
        int newHeight = Math.min(dialogSize.height, maxHeight);

        if (dialogSize.width > maxWidth || dialogSize.height > maxHeight) {
            dialog.setSize(newWidth, newHeight);
        }

        // 将对话框设置为居中显示
        dialog.setLocationRelativeTo(null);
        // 通过手动获取屏幕尺寸和窗口尺寸来计算居中位置
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // 计算居中位置
        int x = (screenWidth - width) / 2;
        int y = (screenHeight - height) / 2;
        // 设置对话框位置
        dialog.setLocation(x, y);*/

        // 3. 显示对话框（这行代码会阻塞，直到对话框被关闭）
        dialog.setVisible(true);
        // 4. 清理资源
        dialog.dispose();

        // 步骤 3: 将要显示的消息文本和包含列表的滚动面板组合成一个对象数组
        // JOptionPane 会自动将它们垂直排列
        /*Object[] dialogContent = { message, scrollPane };

        // 步骤 4: 调用 JOptionPane.showMessageDialog 来显示弹窗
        // 这是最关键的一步，它会自动处理布局、"确定"按钮和居中显示
        JOptionPane.showMessageDialog(parentComponent, dialogContent, // 传入我们组合好的内容
             title, JOptionPane.INFORMATION_MESSAGE // 显示一个标准的信息图标
        );*/

        // 5. 使用 JOptionPane.showMessageDialog 来显示这个组合面板
        // 这是最简单的方法，它会自动添加一个 "OK" 按钮并处理关闭逻辑。
        /*JOptionPane.showMessageDialog(parentComponent, panel, // 将我们自定义的面板作为消息体
                title, JOptionPane.INFORMATION_MESSAGE // 显示一个信息图标
        );*/
    }

    /**
     * 显示一个带有信息列表和两个操作按钮（“停机”和“关闭”）的模态对话框。
     * 列表内容不可编辑，并支持垂直滚动。用户点击“停机”按钮时可触发特定逻辑，
     * 点击“关闭”按钮则仅关闭对话框。
     *
     * @param parentComponent 父级组件，用于确定对话框的显示位置
     * @param title           对话框标题
     * @param message         要在列表上方显示的消息文本
     * @param listData        要在列表中展示的数据数组
     */
    public static void showInfoListWithShutdown(Component parentComponent, String title, String message,
                                                String[] listData) {
        // 1. 创建 JList 实例来显示数据
        JList<String> list = new JList<>(listData);

        // 2. 禁用列表，使其仅用于显示
        list.setEnabled(false);
        list.setVisibleRowCount(Math.min(listData.length, 10));

        // 设置视觉样式，使其看起来更像一个标签
        list.setForeground(UIManager.getColor("Label.foreground"));
        list.setBackground(UIManager.getColor("Panel.background"));

        // 3. 将 JList 放入 JScrollPane 以支持滚动
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(500, 300)); // 设置一个合适的初始大小

        // 4. 创建一个面板来组合消息标签和列表
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel(message), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 5. 创建自定义按钮
        JButton shutdownButton = new JButton("停机");
        JButton closeButton = new JButton("关闭");

        // 将按钮放入一个数组中，这是 JOptionPane 需要的格式
        Object[] options = {shutdownButton, closeButton};

        // 6. 创建一个 JOptionPane 实例
        // 我们不再指定默认按钮 (JOptionPane.DEFAULT_OPTION)，因为我们将提供自定义按钮
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE,
                // 这个参数现在用于控制按钮区域的布局，但实际按钮由 options 数组决定
                JOptionPane.YES_NO_OPTION, null,
                // 传入我们自定义的按钮数组
                options,
                // 默认焦点在“关闭”按钮上
                options[1]);

        // 7. 从 JOptionPane 创建 JDialog
        JDialog dialog = optionPane.createDialog(parentComponent, title);

        // 8. 为“停机”按钮添加事件监听器
        shutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 在这里执行停机逻辑
                System.out.println("执行停机操作...");
                // 例如：调用一个方法来关闭服务或应用程序
                // shutdownApplication();

                // 关闭对话框
                dialog.dispose();
            }
        });

        // 9. 为“关闭”按钮添加事件监听器
        closeButton.addActionListener(e -> {
            // 只关闭对话框，不执行任何其他操作
            System.out.println("对话框已关闭。");
            dialog.dispose();
        });

        // 10. 自动调整对话框大小并进行屏幕适配
        dialog.pack();

        // 防止对话框因内容过多而超出屏幕
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = (int) (screenSize.width * 0.8);
        int maxHeight = (int) (screenSize.height * 0.8);
        Dimension dialogSize = dialog.getSize();
        int newWidth = Math.min(dialogSize.width, maxWidth);
        int newHeight = Math.min(dialogSize.height, maxHeight);
        if (dialogSize.width > maxWidth || dialogSize.height > maxHeight) {
            dialog.setSize(newWidth, newHeight);
        }

        dialog.setLocationRelativeTo(parentComponent); // 居中显示

        // 11. 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 显示一个自定义的输入对话框，支持指定消息内容、消息类型、选项类型、标题以及窗口大小。
     * 该方法内部使用 JOptionPane 和 JDialog 构建对话框，并根据用户操作返回相应的结果。
     *
     * @param message      要显示的消息内容，可以是字符串或组件（如 JPanel、JList 等）
     * @param messageType  消息类型，决定图标样式
     *                     <code>JOptionPane.ERROR_MESSAGE</code>,
     *                     <code>JOptionPane.INFORMATION_MESSAGE</code>,
     *                     <code>JOptionPane.WARNING_MESSAGE</code>,
     *                     <code>JOptionPane.QUESTION_MESSAGE</code>,
     *                     <code>JOptionPane.PLAIN_MESSAGE</code>
     * @param optionType   选项按钮类型
     *                  <code>JOptionPane.DEFAULT_OPTION</code>,
     *                  <code>JOptionPane.YES_NO_OPTION</code>,
     *                  <code>JOptionPane.YES_NO_CANCEL_OPTION</code>,
     *                  <code>JOptionPane.OK_CANCEL_OPTION</code>
     * @param title        对话框标题
     * @param width        对话框宽度
     * @param height       对话框高度
     * @return             如果用户点击“确定”并选择了有效项，则返回选中的值；否则返回 null
     */
    public static Object showInputDialog(Object message, int messageType, int optionType, String title, int width,
                                         int height) {

        // 1. 创建 JOptionPane 实例
        // 这里的 message 可以是字符串，也可以是像 JScrollPane 这样的组件
        JOptionPane pane = new JOptionPane(message, messageType, optionType);

        // 是否阻止 JOptionPane 默认添加一个文本输入框
        pane.setWantsInput(false);
        // 自定义的颜色设置
        Color color = new Color(255, 0, 0);
        pane.setBackground(color);
        pane.setForeground(color);

        // 2. 创建 JDialog
        JDialog dialog = pane.createDialog(title);

        dialog.setSize(width, height);

        // 将对话框设置为居中显示
        dialog.setLocationRelativeTo(null);
        // 通过手动获取屏幕尺寸和窗口尺寸来计算居中位置
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // 计算居中位置
        int x = (screenWidth - width) / 2;
        int y = (screenHeight - height) / 2;
        // 设置对话框位置
        dialog.setLocation(x, y);*/

        // 3. 显示对话框（这行代码会阻塞，直到对话框被关闭）
        dialog.setVisible(true);
        // 4. 清理资源
        dialog.dispose();

        /*Object value = pane.getInputValue();

        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return value;*/

        // 5. 获取用户的选择结果
        Object selectedValue = pane.getValue(); // 获取用户点击的按钮 (e.g., OK_OPTION, CANCEL_OPTION)

        // 检查用户是否点击了 "确定" 按钮
        if (selectedValue != null && selectedValue.equals(JOptionPane.OK_OPTION)) {
            // 如果用户点击了 "确定"，我们尝试从 message 组件中获取列表的选中值
            // 从 message 对象中查找 JList 实例。 因为 JList 通常被包裹在 JScrollPane 中。
            if (message instanceof JScrollPane) {
                Component view = ((JScrollPane) message).getViewport().getView();
                if (view instanceof JList) {
                    return ((JList<?>) view).getSelectedValue();// 返回 JList 中的选中项
                }
            } else if (message instanceof JList) {
                return ((JList<?>) message).getSelectedValue();// 返回 JList 中的选中项
            }
        }

        // 如果用户点击了 "取消"、关闭了窗口，或者 message 中没有 JList，则返回 null
        return null;
    }

    /**
     * 显示一个包含列表的对话框，并返回用户选择的项。
     *
     * @param parentComponent 对话框的父组件，用于定位。如果为 null，则对话框将居中显示。
     * @param title           对话框的标题。
     * @param message         显示在列表上方的信息。
     * @param listData        要在列表中显示的数据数组。
     * @return 用户选择的列表项；如果用户取消或关闭对话框，则返回 null。
     */
    public static Object showListDialog(Component parentComponent, String title, String message, Object[] listData) {
        // 1. 创建一个 JList 实例来显示数据
        JList<Object> list = new JList<>(listData);
        // 设置为单选模式
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 默认选中第一项
        if (listData.length > 0) {
            list.setSelectedIndex(0);
        }

        // 2. 将 JList 放入 JScrollPane，以便在列表项过多时可以滚动
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(250, 150)); // 设置一个合适的首选大小

        // 3. 将 JScrollPane 和消息文本组合成要显示在对话框中的内容
        Object[] dialogContent = {message, scrollPane};

        // 4. 使用 JOptionPane 的静态方法来创建和显示对话框
        // 这样做会自动处理布局、按钮和居中显示
        int option = JOptionPane.showOptionDialog(parentComponent, dialogContent, title,
                // 显示 "确定" 和 "取消" 按钮
                JOptionPane.OK_CANCEL_OPTION,
                // 不显示默认图标
                JOptionPane.PLAIN_MESSAGE, null, null, null);

        // 5. 检查用户的操作并返回结果
        if (option == JOptionPane.OK_OPTION) {
            return list.getSelectedValue(); // 用户点击了 "确定"，返回选中的值
        } else {
            return null; // 用户点击了 "取消" 或关闭了对话框
        }
    }

    /**
     * 显示一个包含列表的对话框，并返回用户的选择。
     *
     * @param parentComponent 对话框的父组件，用于定位。如果为 null，对话框将居于屏幕中央。
     * @param title           对话框的标题。
     * @param dialogMessage   显示在列表上方的主消息文本。
     * @param listData        要在列表中显示的字符串数组。
     * @return 用户选择的列表项 (String)；如果用户取消或关闭对话框，则返回 null。
     */
    public static String showListDialog(Component parentComponent, String title, String dialogMessage,
                                        String[] listData) {
        // 步骤 1 & 2: 创建一个 JList 实例来显示数据
        JList<String> list = new JList<>(listData);

        // 设置列表为单选模式
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 为了用户友好，默认选中列表的第一项
        if (listData.length > 0) {
            list.setSelectedIndex(0);
        }

        // 步骤 3: 将 JList 放入 JScrollPane，以便在列表项过多时可以滚动
        // 并为滚动面板设置一个合适的初始大小
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 150)); // 设置推荐大小

        // 步骤 4: 使用 JOptionPane 的静态方法来创建和显示对话框
        // 我们将 JScrollPane 和消息文本组合成一个 Object 数组作为弹窗的内容
        int result = JOptionPane.showOptionDialog(parentComponent, new Object[]{dialogMessage, scrollPane}, // 将消息和列表组件一同显示
                title, JOptionPane.OK_CANCEL_OPTION, // 显示 "确定" 和 "取消" 按钮
                JOptionPane.PLAIN_MESSAGE, // 不显示默认的图标 (如 Error, Info)
                null, // 不使用自定义图标
                null, // 不自定义按钮文本
                null // 没有默认焦点组件
        );

        // 步骤 5: 检查用户的操作并返回结果
        if (result == JOptionPane.OK_OPTION) {
            return list.getSelectedValue(); // 如果用户点击了 "确定"，返回列表中被选中的值
        } else {
            return null; // 如果用户点击了 "取消" 或关闭了对话框，返回 null
        }
    }

    public static void main(String... args) {

        showInputDialog("请输入:", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, "Test", 1000, 800);

        // 准备要显示的列表数据
        String[] serverLog = {"INFO: Server starting...", "INFO: Module 'Authentication' loaded.",
                "WARN: Configuration file 'settings.xml' not found, using defaults.",
                "INFO: Module 'DatabaseConnector' loaded.", "INFO: Connecting to database 'prod_db' on port 5432.",
                "ERROR: Connection failed: Timeout expired.", "INFO: Retrying connection (1/3)...",
                "ERROR: Connection failed: Timeout expired.", "INFO: Retrying connection (2/3)...",
                "INFO: Connection established successfully.", "INFO: Server listening on port 8080.",
                "INFO: Application startup sequence initiated.",
                "DEBUG: Loading configuration from 'C:\\ProgramData\\MyApp\\config\\settings.xml'.",
                "INFO: Found 3 plugins to load.",
                "WARN: Plugin 'old-plugin.jar' is deprecated and will be removed in a future version. Please update.",
                "ERROR: Failed to connect to database server at 'db.production.internal.corp:5432'. The connection attempt timed out after 3000ms.",
                "INFO: This is an extremely long log entry designed specifically to test the horizontal scrolling functionality of the JList inside the JScrollPane. Without the setPrototypeCellValue optimization, this entire line would be truncated and unreadable.",
                "DEBUG: User 'administrator' successfully authenticated from IP address 192.168.1.102.",
                "INFO: Shutting down application."};

        // 调用方法来显示提示框
        showInfoList(null, "服务器启动日志", "以下是最近的服务器启动事件：", serverLog);

        /*
         * 自定义按钮弹窗
         */

        // 创建一个 JFrame 作为父组件
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        // 模拟一些列表数据
        String[] data = new String[20];
        for (int i = 0; i < data.length; i++) {
            data[i] = "这是一个信息条目 " + (i + 1) + "，用于测试弹窗显示效果。";
        }

        // 使用 SwingUtilities.invokeLater 确保 GUI 操作在事件分发线程 (EDT) 上执行
        SwingUtilities.invokeLater(() -> {
            showInfoListWithShutdown(frame, "系统信息", "以下是详细的系统状态列表：", data);
        });

        // 这行代码只是为了演示，实际应用中可能不需要显示主窗口
        // frame.setVisible(true);

        /*
         * 可选择的弹窗
         */

        // 1. 准备要显示的列表数据
        /*String[] fruits = { "苹果 (Apple)", "香蕉 (Banana)", "橙子 (Orange)", "草莓 (Strawberry)", "蓝莓 (Blueberry)" };

        // 2. 创建 JList 实例
        JList<String> fruitList = new JList<>(fruits);
        fruitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 设置为单选
        fruitList.setSelectedIndex(0); // 默认选中第一项

        // 3. 为了支持滚动，将 JList 放入 JScrollPane
        // 这是非常重要的，当列表项很多时，可以提供滚动条
        JScrollPane scrollPane = new JScrollPane(fruitList);
        scrollPane.setPreferredSize(new Dimension(200, 100)); // 给滚动面板一个合适的尺寸

        // 4. 调用您的方法，将 JScrollPane 作为 message 参数传入
        // 注意：optionType 必须包含 OK 和 CANCEL 按钮，以便我们能捕获用户的确认操作
        Object choice = showInputDialog(scrollPane, // 传入包含列表的组件
                JOptionPane.PLAIN_MESSAGE, // 消息类型（无图标）
                JOptionPane.OK_CANCEL_OPTION, // 选项类型（确定/取消按钮）
                "请选择一个水果", // 标题
                400, // 对话框宽度
                300 // 对话框高度
        );

        // 5. 处理返回结果
        if (choice != null) {
            JOptionPane.showMessageDialog(null, "您选择了: " + choice);
        } else {
            JOptionPane.showMessageDialog(null, "您没有做出选择。");
        }*/

        /*
         * 可选择的弹窗
         */

        // 准备要显示的列表数据
        /*String[] options = { "选项一：苹果", "选项二：香蕉", "选项三：橙子", "选项四：草莓", "选项五：蓝莓", "选项六：西瓜" };

        // 调用优化后的方法来显示列表对话框
        Object selectedOption = showListDialog(null, "选择您喜欢的水果", "请从下面的列表中选择一项：", options);

        // 根据用户的选择显示不同的消息
        if (selectedOption != null) {
            JOptionPane.showMessageDialog(null, "您选择了: " + selectedOption);
        } else {
            JOptionPane.showMessageDialog(null, "您没有做出任何选择。");
        }*/

        /*
         * 可选择的弹窗
         */

        // 准备一个要在列表中显示的数据数组
        /*String[] programmingLanguages = { "Java", "Python", "JavaScript", "C++", "C#", "Go", "Rust", "TypeScript",
                "Kotlin", "Swift" };

        // 调用静态方法来显示列表对话框
        String selectedLanguage = showListDialog(null, "选择语言", "请选择您最喜欢的编程语言：", programmingLanguages);

        // 根据用户的选择结果，显示一条最终消息
        if (selectedLanguage != null) {
            // 用户做出了选择
            JOptionPane.showMessageDialog(null, "您的选择是: " + selectedLanguage, "选择结果", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 用户取消了选择
            JOptionPane.showMessageDialog(null, "您没有选择任何语言。", "操作已取消", JOptionPane.WARNING_MESSAGE);
        }*/

    }
}
