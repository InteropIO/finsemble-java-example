package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Objects;

public class StoreClientFrame {
    private JPanel mainPanel;
    private JButton saveButton;
    private JButton setStoreButton;
    private JButton setUserButton;
    private JTextField textKey;
    private JButton clearButton;
    private JTextArea textLogs;
    private JButton clearCacheButton;
    private JButton keysButton;
    private JTextField textTopic;
    private JTextField valueTextField;
    private JButton getButton;
    private JTextField keyGetText;
    private JTextField topicGetText;
    private JTextField textKeyTopic;
    private JTextField textKeyPrefix;
    private JTextField textKeyRemove;
    private JTextField textTopicRemove;
    private JButton removeButton;
    private JTextField textTopicSetStore;
    private JTextField textDataStore;
    private JTextField textUser;

    private final CallbackListener defaultCallback = ((err, res) -> {
        if (Objects.nonNull(err)) {
            writeLogs(err.toString(4));
        } else {
            writeLogs(res.toString(4));
        }
    });

    private void writeLogs(String text) {
        textLogs.append("\n" + text);
    }

    public StoreClientFrame(Finsemble fsbl) {
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textLogs.setCaret(caret);

        clearButton.addActionListener(e -> textLogs.setText(""));
        clearCacheButton.addActionListener(e -> fsbl.getClients().getStorageClient().clearCache(defaultCallback));

        saveButton.addActionListener(e -> fsbl.getClients().getStorageClient().save(new JSONObject() {{
            put("key", textKey.getText());
            put("topic", textTopic.getText());
            put("value", valueTextField.getText());
        }}, defaultCallback));

        getButton.addActionListener(e -> fsbl.getClients().getStorageClient().get(new JSONObject() {{
            put("key", keyGetText.getText());
            put("topic", topicGetText.getText());
        }}, defaultCallback));

        keysButton.addActionListener(e -> fsbl.getClients().getStorageClient().keys(new JSONObject() {{
            put("topic", textKeyTopic.getText());
            put("keyPrefix", textKeyPrefix.getText());
        }}, defaultCallback));

        removeButton.addActionListener(e -> fsbl.getClients().getStorageClient().remove(new JSONObject() {{
            put("key", textKeyRemove.getText());
            put("topic", textTopicRemove.getText());
        }}, defaultCallback));

        setStoreButton.addActionListener(e -> fsbl.getClients().getStorageClient().setStore(new JSONObject() {{
            put("topic", textTopicSetStore.getText());
            put("dataStore", textDataStore.getText());
        }}, defaultCallback));

        setUserButton.addActionListener(e -> fsbl.getClients().getStorageClient().setUser(new JSONObject() {{
            put("user", textUser.getText());
        }}, defaultCallback));
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        mainPanel = new JPanel();
        mainPanel.setLayout(new FormLayout("left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:171px:noGrow,fill:max(d;4px):noGrow,left:72dlu:noGrow,fill:max(d;4px):noGrow,left:77dlu:noGrow,fill:180px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:32px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:146dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        mainPanel.setMinimumSize(new Dimension(640, 550));
        mainPanel.setPreferredSize(new Dimension(640, 550));
        panel1.add(mainPanel, BorderLayout.CENTER);
        saveButton = new JButton();
        saveButton.setText("save");
        CellConstraints cc = new CellConstraints();
        mainPanel.add(saveButton, cc.xy(3, 3));
        setStoreButton = new JButton();
        setStoreButton.setText("setStore");
        mainPanel.add(setStoreButton, cc.xy(3, 5));
        setUserButton = new JButton();
        setUserButton.setText("setUser");
        mainPanel.add(setUserButton, cc.xy(3, 7));
        textKey = new JTextField();
        textKey.setText("key");
        mainPanel.add(textKey, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        clearButton = new JButton();
        clearButton.setText("Clear");
        mainPanel.add(clearButton, cc.xy(8, 19, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, cc.xyw(3, 17, 6, CellConstraints.FILL, CellConstraints.FILL));
        textLogs = new JTextArea();
        scrollPane1.setViewportView(textLogs);
        clearCacheButton = new JButton();
        clearCacheButton.setText("clearCache");
        mainPanel.add(clearCacheButton, cc.xy(3, 9));
        keysButton = new JButton();
        keysButton.setText("keys");
        mainPanel.add(keysButton, cc.xy(3, 11));
        textKeyTopic = new JTextField();
        textKeyTopic.setText("topic");
        mainPanel.add(textKeyTopic, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        removeButton = new JButton();
        removeButton.setText("remove");
        mainPanel.add(removeButton, cc.xy(3, 13));
        textTopic = new JTextField();
        textTopic.setText("topic");
        mainPanel.add(textTopic, cc.xy(7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        valueTextField = new JTextField();
        valueTextField.setText("value");
        mainPanel.add(valueTextField, cc.xy(8, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        getButton = new JButton();
        getButton.setText("get");
        mainPanel.add(getButton, cc.xy(3, 15));
        keyGetText = new JTextField();
        keyGetText.setText("key");
        mainPanel.add(keyGetText, cc.xy(5, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        topicGetText = new JTextField();
        topicGetText.setText("topic");
        mainPanel.add(topicGetText, cc.xy(7, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        textKeyPrefix = new JTextField();
        textKeyPrefix.setText("prefix");
        mainPanel.add(textKeyPrefix, cc.xy(7, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        textKeyRemove = new JTextField();
        textKeyRemove.setText("key");
        mainPanel.add(textKeyRemove, cc.xy(5, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        textTopicRemove = new JTextField();
        textTopicRemove.setText("topic");
        mainPanel.add(textTopicRemove, cc.xy(7, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        textTopicSetStore = new JTextField();
        textTopicSetStore.setText("topic");
        mainPanel.add(textTopicSetStore, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        textDataStore = new JTextField();
        textDataStore.setText("dataStore");
        mainPanel.add(textDataStore, cc.xy(7, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        textUser = new JTextField();
        textUser.setText("user");
        mainPanel.add(textUser, cc.xy(5, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
    }
}