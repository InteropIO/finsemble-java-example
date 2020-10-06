package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.finsemblewindow.FinsembleWindow;
import com.chartiq.finsemble.interfaces.CallbackListener;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

public class FinsembleWindowFrame {
    private static final Logger LOGGER = Logger.getLogger(FinsembleWindowFrame.class.getName());

    private JPanel mainPanel;
    private JPanel logPanel;
    private JPanel getInsatancePanel;

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

    private FinsembleWindow finsembleWindow;
    private Finsemble finsemble;

    private final CallbackListener defaultCallback = ((err, res) -> {
        if (Objects.nonNull(err)) {
            writeLogs(err.toString(4));
        } else {
            writeLogs(res.toString(4));
        }
    });

    public FinsembleWindowFrame(Finsemble finsemble) {
        Arrays.stream(mainPanel.getComponents()).forEach(n -> n.setEnabled(false));

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
        showAtButton.addActionListener(e -> finsembleWindow.showAt(new JSONObject(), defaultCallback));
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
        getComponentStateButton.addActionListener(e -> finsembleWindow.getComponentState(new JSONObject(), defaultCallback));

        setAlwaysOnTopCheckBox.addActionListener(e -> finsembleWindow.setAlwaysOnTop(
                new JSONObject(Collections.singletonMap("alwaysOnTop", setAlwaysOnTopCheckBox.isSelected())),
                defaultCallback)
        );

        setOpacityButton.addActionListener(e -> finsembleWindow.setOpacity(
                new JSONObject(Collections.singletonMap("opacity", textSetOpacity.getText())),
                defaultCallback)
        );

        setBoundsButton.addActionListener(e -> {
                    JSONObject params = new JSONObject();
                    params.put("left", left.getText());
                    params.put("right", right.getText());
                    params.put("top", top.getText());
                    params.put("bottom", bottom.getText());
                    params.put("width", width.getText());
                    params.put("height", height.getText());
                    finsembleWindow.setBounds(params, defaultCallback);
                }
        );

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
}
