package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.chartiq.finsemble.model.Action;
import com.chartiq.finsemble.model.Filter;
import com.chartiq.finsemble.model.Notification;
import com.chartiq.finsemble.model.Subscription;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

public class NotificationClientFrame {
    private JPanel mainPanel;
    private JButton notifyButton;
    private JTextArea textLogs;
    private JButton clearButton;
    private JTextField titleTextField;
    private JTextField messageTextField;
    private JTextField headerTextField;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    private JTextField subscriptionIdTextField;
    private JButton getLastIssuedAtButton;
    private JButton fetchHistoryButton;
    private JButton performActionButton;
    private JCheckBox spawnCheckBox;
    private JCheckBox dismissCheckBox;
    private JCheckBox snoozeCheckBox;
    private JCheckBox transmitCheckBox;
    private JCheckBox queryCheckBox;
    private JCheckBox publishCheckBox;

    private final CallbackListener defaultCallback = ((err, res) -> {
        if (Objects.nonNull(err)) {
            writeLogs(err.toString(4));
        } else {
            writeLogs(res.toString(4));
        }
    });

    public NotificationClientFrame(Finsemble fsbl) {
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textLogs.setCaret(caret);

        clearButton.addActionListener(e -> textLogs.setText(""));

        notifyButton.addActionListener(e -> {
            Notification notification = new Notification();
            notification.setHeaderText(headerTextField.getText());
            notification.setTitle(titleTextField.getText());
            notification.setDetails(messageTextField.getText());
            notification.setHeaderLogo("http://localhost:3375/components/finsemble-notifications/components/shared/assets/email.svg");
            notification.setContentLogo("http://localhost:3375/components/finsemble-notifications/components/shared/assets/graph.png");
            notification.getActions().addAll(getActions(dismissCheckBox, snoozeCheckBox, spawnCheckBox, publishCheckBox,
                    queryCheckBox, transmitCheckBox));

            fsbl.getClients().getNotificationClient().notify(Collections.singletonList(notification), defaultCallback);
        });

        subscribeButton.addActionListener(e -> {
            Subscription subscription = new Subscription(
                    null, new Filter(emptyMap(), emptyMap()), ""
            );

            fsbl.getClients().getNotificationClient().subscribe(subscription,
                    (err, res) -> {
                        res.put("callback", "onSubscribe");
                        defaultCallback.callback(err, res);
                    },
                    (err, res) -> {
                        res.put("callback", "onNotification");
                        defaultCallback.callback(err, res);
                    });
        });

        unsubscribeButton.addActionListener(e ->
                fsbl.getClients().getNotificationClient().unsubscribe(subscriptionIdTextField.getText(), defaultCallback)
        );

        getLastIssuedAtButton.addActionListener(e ->
                fsbl.getClients().getNotificationClient().getLastIssuedAt("source", defaultCallback)
        );

        fetchHistoryButton.addActionListener(e ->
                fsbl.getClients().getNotificationClient().fetchHistory(
                        Instant.now(),
                        new Filter(emptyMap(), emptyMap()),
                        defaultCallback
                ));
        performActionButton.addActionListener(e -> {
            Notification notification = new Notification();
            notification.setHeaderText(headerTextField.getText());
            notification.setTitle(titleTextField.getText());
            notification.setDetails(messageTextField.getText());
            notification.setHeaderLogo("http://localhost:3375/components/finsemble-notifications/components/shared/assets/email.svg");
            notification.setContentLogo("http://localhost:3375/components/finsemble-notifications/components/shared/assets/graph.png");

            Action action = new Action("1", "buttonText", "typeText",
                    2000, "component", new JSONObject(), "channel", new JSONObject()
            );
            fsbl.getClients().getNotificationClient().performAction(
                    Arrays.asList(notification, notification),
                    action,
                    defaultCallback
            );
        });
    }

    private void writeLogs(String text) {
        textLogs.append("\n" + text);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private List<Action> getActions(JCheckBox... checkboxes) {
        return Arrays.stream(checkboxes).filter(AbstractButton::isSelected).map(n -> {
            switch (n.getText()) {
                case "snooze":
                    return new Action(null, "Snooze", "SNOOZE",
                            2000, "", new JSONObject(), "", new JSONObject());
                case "spawn":
                    return new Action(null, "Welcome", "SPAWN",
                            0, "Welcome Component", new JSONObject(), "", new JSONObject());
                case "dismiss":
                    return new Action(null, "Dismiss", "DISMISS",
                            0, "", new JSONObject(), "", new JSONObject());
                case "transmit":
                    return new Action(null, "Send Transmit", "TRANSMIT",
                            0, "", new JSONObject(), "transmit-channel",
                            new JSONObject() {{
                                put("foo", "bar");
                            }});
                case "query":
                    return new Action(null, "Send Query", "QUERY",
                            0, "", new JSONObject(), "query-channel",
                            new JSONObject() {{
                                put("hello", "world");
                            }});
                case "publish":
                    return new Action(null, "Send Publish", "PUBLISH",
                            0, "", new JSONObject(), "publish-channel",
                            new JSONObject() {{
                                put("my", "name");
                            }});
                default:
                    return new Action(null, "Unknown", "UNKNOWN",
                            0, "", new JSONObject(), "", new JSONObject());
            }
        }).collect(Collectors.toList());
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        mainPanel = new JPanel();
        mainPanel.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:108px:grow,left:5dlu:noGrow,left:64dlu:noGrow,left:4dlu:noGrow,fill:125px:grow,fill:d:grow,left:5dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        mainPanel.setPreferredSize(new Dimension(640, 550));
        CellConstraints cc = new CellConstraints();
        panel1.add(mainPanel, cc.xy(1, 1));
        notifyButton = new JButton();
        notifyButton.setText("notify");
        mainPanel.add(notifyButton, cc.xy(3, 3));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, cc.xyw(3, 19, 8, CellConstraints.FILL, CellConstraints.FILL));
        textLogs = new JTextArea();
        textLogs.setText("");
        scrollPane1.setViewportView(textLogs);
        clearButton = new JButton();
        clearButton.setText("Clear");
        mainPanel.add(clearButton, cc.xy(10, 21, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        messageTextField = new JTextField();
        messageTextField.setText("message");
        mainPanel.add(messageTextField, cc.xyw(9, 3, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        headerTextField = new JTextField();
        headerTextField.setText("header");
        mainPanel.add(headerTextField, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        titleTextField = new JTextField();
        titleTextField.setText("title");
        mainPanel.add(titleTextField, cc.xy(7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        subscribeButton = new JButton();
        subscribeButton.setText("subscribe");
        mainPanel.add(subscribeButton, cc.xy(3, 9));
        unsubscribeButton = new JButton();
        unsubscribeButton.setText("unsubscribe");
        mainPanel.add(unsubscribeButton, cc.xy(3, 11));
        subscriptionIdTextField = new JTextField();
        subscriptionIdTextField.setText("subscribtionId");
        mainPanel.add(subscriptionIdTextField, cc.xyw(5, 11, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        getLastIssuedAtButton = new JButton();
        getLastIssuedAtButton.setText("getlastIssuedAt");
        mainPanel.add(getLastIssuedAtButton, cc.xyw(3, 13, 3));
        fetchHistoryButton = new JButton();
        fetchHistoryButton.setText("fetchHistoryFromNow");
        mainPanel.add(fetchHistoryButton, cc.xyw(3, 15, 3));
        performActionButton = new JButton();
        performActionButton.setText("performAction");
        mainPanel.add(performActionButton, cc.xyw(3, 17, 3));
        spawnCheckBox = new JCheckBox();
        spawnCheckBox.setText("spawn");
        mainPanel.add(spawnCheckBox, cc.xy(3, 5));
        dismissCheckBox = new JCheckBox();
        dismissCheckBox.setText("dismiss");
        mainPanel.add(dismissCheckBox, cc.xy(5, 5));
        snoozeCheckBox = new JCheckBox();
        snoozeCheckBox.setText("snooze");
        mainPanel.add(snoozeCheckBox, cc.xy(7, 5));
        queryCheckBox = new JCheckBox();
        queryCheckBox.setText("query");
        mainPanel.add(queryCheckBox, cc.xy(3, 7));
        transmitCheckBox = new JCheckBox();
        transmitCheckBox.setText("transmit");
        mainPanel.add(transmitCheckBox, cc.xy(7, 7));
        publishCheckBox = new JCheckBox();
        publishCheckBox.setText("publish");
        mainPanel.add(publishCheckBox, cc.xy(5, 7));
    }
}
