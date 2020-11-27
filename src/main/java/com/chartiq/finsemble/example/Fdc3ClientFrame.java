package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.CallbackListener;
import org.json.JSONObject;

import javax.swing.*;
import java.util.Objects;

public class Fdc3ClientFrame {

    private JPanel mainPanel;

    private JTextArea textLogs;
    private JButton getSystemChannelsButton;
    private JButton getOrCreateDesctopAgentButton;
    private JButton broadcastButton;
    private JTextArea keyValueTextArea;
    private JTextField channelNameTextField;
    private JButton getCurrentChannelButton;
    private JTextField openComponentTextField;
    private JButton openButton;
    private JCheckBox withContextCheckBox;
    private JTextField contextTypeTextField;
    private JButton addContextListenerButton;
    private JButton removeContextListenerButton;
    private JTextField contextTypeTextField1;
    private JButton getChannelCurrentContextButton;
    private JTextField intentTextField;
    private JButton addIntentListenerButton;
    private JButton removeIntentListenerButton;
    private JTextField raseIntentTextField;
    private JTextField targetTextField;
    private JButton raseIntentButton;
    private JTextField getOrCreateChannelTextField;
    private JButton getOrCreateChannelButton;
    private JButton clearButton1;
    private JTextField joinChannelTextField;
    private JButton joinChannelButton;
    private JButton leaveChannelButton;
    private JButton broadcastButton2;

    public Fdc3ClientFrame(Finsemble fsbl) {

        getSystemChannelsButton.addActionListener(e -> {
            fsbl.getClients().getFdc3Client().getSystemChannels(defaultCallback);
        });

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
