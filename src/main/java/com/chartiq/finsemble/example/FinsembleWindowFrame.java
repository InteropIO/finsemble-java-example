package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.finsemblewindow.FinsembleWindow;
import com.chartiq.finsemble.interfaces.CallbackListener;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
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
}
