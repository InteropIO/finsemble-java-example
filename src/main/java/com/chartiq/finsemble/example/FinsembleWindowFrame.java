package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.finsemblewindow.FinsembleWindow;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.*;

public class FinsembleWindowFrame {

    private JPanel mainPanel;
    private JPanel logPanel;
    private JPanel getInstancePanel;

    private JButton showButton;
    private JButton blurButton;
    private JButton hideButton;
    private JButton clearButton;
    private JButton focusButton;
    private JButton closeButton;
    private JButton showAtButton;
    private JButton restoreButton;
    private JButton maximizeButton;
    private JButton minimizeButton;
    private JButton isShowingButton;
    private JButton getBoundsButton;
    private JButton setBoundsButton;
    private JButton getMonitorButton;
    private JButton setOpacityButton;
    private JButton getOptionsButton;
    private JButton getInstanceButton;
    private JButton bringToFrontButton;
    private JButton isAlwaysOnTopButton;
    private JButton getWindowStateButton;
    private JButton getComponentStateButton;
    private JCheckBox setAlwaysOnTopCheckBox;

    private JTextArea textLogs;
    private JTextField textWindowName;
    private JTextField left;
    private JTextField right;
    private JTextField top;
    private JTextField bottom;
    private JTextField width;
    private JTextField height;
    private JTextField textSetOpacity;
    private JTextField textShowAtLeft;
    private JTextField textShowAtTop;
    private JTextField textGetComponentState;
    private JTextField textRemoveComponentState;
    private JTextField textNameSetComponentState;
    private JTextField textValueSetComponentState;
    private JButton setComponentStateButton;
    private JButton removeComponentStateButton;

    private FinsembleWindow finsembleWindow;

    private final CallbackListener defaultCallback = ((err, res) -> {
        if (Objects.nonNull(err)) {
            writeLogs(err.toString(4));
        } else {
            writeLogs(res.toString(4));
        }
    });

    public FinsembleWindowFrame(Finsemble finsemble) {
        Arrays.stream(getMainPanel().getComponents()).forEach(n -> n.setEnabled(false));

        getInstanceButton.addActionListener(e -> {
            if (textWindowName.getText().isEmpty()) {
                writeLogs("Enter window name");
            } else {
                try {
                    finsembleWindow = finsemble.getFinsembleWindow(textWindowName.getText(), defaultCallback);
                    Arrays.stream(mainPanel.getComponents()).forEach(n -> n.setEnabled(true));
                } catch (IllegalStateException ignore) {
                }
            }
        });

        hideButton.addActionListener(e -> finsembleWindow.hide(new JSONObject(), defaultCallback));
        blurButton.addActionListener(e -> finsembleWindow.blur(new JSONObject(), defaultCallback));
        showButton.addActionListener(e -> finsembleWindow.show(new JSONObject(), defaultCallback));
        focusButton.addActionListener(e -> finsembleWindow.focus(new JSONObject(), defaultCallback));
        closeButton.addActionListener(e -> finsembleWindow.close(new JSONObject(), defaultCallback));

        restoreButton.addActionListener(e -> finsembleWindow.restore(new JSONObject(), defaultCallback));
        minimizeButton.addActionListener(e -> finsembleWindow.minimize(new JSONObject(), defaultCallback));
        maximizeButton.addActionListener(e -> finsembleWindow.maximize(new JSONObject(), defaultCallback));
        isShowingButton.addActionListener(e -> finsembleWindow.isShowing(new JSONObject(), defaultCallback));
        getBoundsButton.addActionListener(e -> finsembleWindow.getBounds(new JSONObject(), defaultCallback));
        getOptionsButton.addActionListener(e -> finsembleWindow.getOptions(new JSONObject(), defaultCallback));
        getMonitorButton.addActionListener(e -> finsembleWindow.getMonitor(new JSONObject(), defaultCallback));
        bringToFrontButton.addActionListener(e -> finsembleWindow.bringToFront(new JSONObject(), defaultCallback));
        isAlwaysOnTopButton.addActionListener(e -> finsembleWindow.isAlwaysOnTop(new JSONObject(), defaultCallback));
        getWindowStateButton.addActionListener(e -> finsembleWindow.getWindowState(new JSONObject(), defaultCallback));

        getComponentStateButton.addActionListener(e -> {
            finsembleWindow.getComponentState(new JSONObject(Collections.singletonMap("field", textGetComponentState.getText())), defaultCallback);
        });

        setComponentStateButton.addActionListener(e -> {
            JSONObject params = new JSONObject();
            params.put("field", textNameSetComponentState.getText());
            params.put("value", Double.parseDouble(textValueSetComponentState.getText()));
            finsembleWindow.setComponentState(params, defaultCallback);
        });

        removeComponentStateButton.addActionListener(e -> {
            finsembleWindow.removeComponentState(new JSONObject(Collections.singletonMap("field", textRemoveComponentState.getText())), defaultCallback);
        });

        setAlwaysOnTopCheckBox.addActionListener(e -> finsembleWindow.setAlwaysOnTop(
                new JSONObject(Collections.singletonMap("alwaysOnTop", setAlwaysOnTopCheckBox.isSelected())),
                defaultCallback)
        );

        setOpacityButton.addActionListener(e -> finsembleWindow.setOpacity(
                new JSONObject(Collections.singletonMap("opacity", Double.parseDouble(textSetOpacity.getText()))),
                defaultCallback)
        );

        setBoundsButton.addActionListener(e -> {
                    Map<String, String> bounds = new HashMap<>();
                    bounds.put("left", left.getText());
                    bounds.put("right", right.getText());
                    bounds.put("top", top.getText());
                    bounds.put("bottom", bottom.getText());
                    bounds.put("width", width.getText());
                    bounds.put("height", height.getText());

                    JSONObject params = new JSONObject();
                    params.put("bounds", bounds);
                    finsembleWindow.setBounds(params, defaultCallback);
                }
        );

        showAtButton.addActionListener(e -> {
            JSONObject params = new JSONObject();
            params.put("left", Integer.parseInt(textShowAtLeft.getText()));
            params.put("top", Integer.parseInt(textShowAtTop.getText()));
            finsembleWindow.showAt(params, defaultCallback);
        });

        clearButton.addActionListener(e -> textLogs.setText(""));

        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textLogs.setCaret(caret);
    }

    private void writeLogs(String text) {
        textLogs.append(text);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FormLayout("fill:d:noGrow,left:4px:noGrow,fill:105px:noGrow,left:4px:noGrow,fill:99px:noGrow,fill:max(d;4px):noGrow,fill:102px:noGrow,fill:max(d;4px):noGrow,left:102px:noGrow,left:4px:noGrow,fill:102px:noGrow,fill:max(d;4px):noGrow,fill:100px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:30px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:56dlu:noGrow,top:31dlu:noGrow,center:132px:noGrow,top:4dlu:noGrow,center:32px:noGrow"));
        mainPanel.setMinimumSize(new Dimension(739, 653));
        mainPanel.setPreferredSize(new Dimension(739, 653));
        mainPanel.setVisible(true);
        mainPanel.putClientProperty("html.disable", Boolean.TRUE);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, null, null));
        blurButton = new JButton();
        blurButton.setText("blur");
        CellConstraints cc = new CellConstraints();
        mainPanel.add(blurButton, cc.xy(1, 3));
        getInstancePanel = new JPanel();
        getInstancePanel.setLayout(new BorderLayout(4, 0));
        mainPanel.add(getInstancePanel, cc.xyw(1, 1, 11));
        textWindowName = new JTextField();
        textWindowName.setPreferredSize(new Dimension(500, 30));
        textWindowName.setText("");
        getInstancePanel.add(textWindowName, BorderLayout.EAST);
        getInstanceButton = new JButton();
        getInstanceButton.setBackground(new Color(-12266205));
        getInstanceButton.setHorizontalTextPosition(4);
        getInstanceButton.setPreferredSize(new Dimension(150, 30));
        getInstanceButton.setText("getInstance");
        getInstancePanel.add(getInstanceButton, BorderLayout.CENTER);
        setBoundsButton = new JButton();
        setBoundsButton.setText("setBounds");
        mainPanel.add(setBoundsButton, cc.xy(1, 9));
        setOpacityButton = new JButton();
        setOpacityButton.setText("setOpacity");
        mainPanel.add(setOpacityButton, cc.xy(1, 11));
        logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(logPanel, cc.xywh(1, 17, 13, 3));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setAutoscrolls(true);
        scrollPane1.setEnabled(true);
        logPanel.add(scrollPane1, BorderLayout.CENTER);
        textLogs = new JTextArea();
        textLogs.setEnabled(true);
        textLogs.setText("");
        scrollPane1.setViewportView(textLogs);
        clearButton = new JButton();
        clearButton.setText("Clear");
        mainPanel.add(clearButton, cc.xy(13, 21));
        left = new JTextField();
        left.setText("left");
        mainPanel.add(left, cc.xy(3, 9));
        top = new JTextField();
        top.setText("top");
        mainPanel.add(top, cc.xy(7, 9));
        right = new JTextField();
        right.setText("right");
        mainPanel.add(right, cc.xy(5, 9));
        maximizeButton = new JButton();
        maximizeButton.setText("maximize");
        mainPanel.add(maximizeButton, cc.xy(3, 3));
        hideButton = new JButton();
        hideButton.setText("hide");
        mainPanel.add(hideButton, cc.xy(1, 5));
        isAlwaysOnTopButton = new JButton();
        isAlwaysOnTopButton.setText("isAlwaysOnTop");
        mainPanel.add(isAlwaysOnTopButton, cc.xyw(3, 5, 3));
        minimizeButton = new JButton();
        minimizeButton.setText("minimize");
        mainPanel.add(minimizeButton, cc.xy(5, 3));
        showButton = new JButton();
        showButton.setText("show");
        mainPanel.add(showButton, cc.xy(7, 3));
        bringToFrontButton = new JButton();
        bringToFrontButton.setText("bringToFront");
        mainPanel.add(bringToFrontButton, cc.xyw(3, 7, 3));
        bottom = new JTextField();
        bottom.setText("bottom");
        mainPanel.add(bottom, cc.xy(9, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        width = new JTextField();
        width.setText("width");
        mainPanel.add(width, cc.xy(11, 9));
        height = new JTextField();
        height.setText("height");
        mainPanel.add(height, cc.xy(13, 9));
        getOptionsButton = new JButton();
        getOptionsButton.setText("getOptions");
        mainPanel.add(getOptionsButton, cc.xy(9, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        textSetOpacity = new JTextField();
        mainPanel.add(textSetOpacity, cc.xy(3, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        restoreButton = new JButton();
        restoreButton.setText("restore");
        mainPanel.add(restoreButton, cc.xy(7, 5));
        getBoundsButton = new JButton();
        getBoundsButton.setText("getBounds");
        mainPanel.add(getBoundsButton, cc.xy(9, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        focusButton = new JButton();
        focusButton.setText("focus");
        mainPanel.add(focusButton, cc.xy(11, 5));
        getWindowStateButton = new JButton();
        getWindowStateButton.setText("getWindowState");
        mainPanel.add(getWindowStateButton, cc.xyw(7, 7, 3));
        setAlwaysOnTopCheckBox = new JCheckBox();
        setAlwaysOnTopCheckBox.setText("setAlwaysOnTop");
        mainPanel.add(setAlwaysOnTopCheckBox, cc.xyw(11, 7, 3));
        closeButton = new JButton();
        closeButton.setText("close");
        mainPanel.add(closeButton, cc.xy(1, 7));
        isShowingButton = new JButton();
        isShowingButton.setText("isShowing");
        mainPanel.add(isShowingButton, cc.xyw(11, 3, 3));
        getComponentStateButton = new JButton();
        getComponentStateButton.setText("getComponentState");
        mainPanel.add(getComponentStateButton, cc.xyw(1, 13, 3));
        getMonitorButton = new JButton();
        getMonitorButton.setText("getMonitor");
        mainPanel.add(getMonitorButton, cc.xy(13, 5));
        showAtButton = new JButton();
        showAtButton.setText("showAt");
        mainPanel.add(showAtButton, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        textShowAtLeft = new JTextField();
        textShowAtLeft.setText("left");
        mainPanel.add(textShowAtLeft, cc.xy(7, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        textShowAtTop = new JTextField();
        textShowAtTop.setText("top");
        mainPanel.add(textShowAtTop, cc.xy(9, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        setComponentStateButton = new JButton();
        setComponentStateButton.setText("setComponentState");
        mainPanel.add(setComponentStateButton, cc.xyw(1, 15, 3));
        textGetComponentState = new JTextField();
        textGetComponentState.setText("name");
        mainPanel.add(textGetComponentState, cc.xy(5, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        textNameSetComponentState = new JTextField();
        textNameSetComponentState.setText("name");
        mainPanel.add(textNameSetComponentState, cc.xy(5, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        textValueSetComponentState = new JTextField();
        textValueSetComponentState.setText("value");
        mainPanel.add(textValueSetComponentState, cc.xy(7, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        removeComponentStateButton = new JButton();
        removeComponentStateButton.setText("removeComponentState");
        mainPanel.add(removeComponentStateButton, cc.xyw(9, 15, 3));
        textRemoveComponentState = new JTextField();
        textRemoveComponentState.setText("name");
        mainPanel.add(textRemoveComponentState, cc.xy(13, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
