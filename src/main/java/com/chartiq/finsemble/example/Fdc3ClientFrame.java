package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.fdc3.impl.DesktopAgentClient;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.chartiq.finsemble.interfaces.CallbackListenerContextObject;
import com.sun.deploy.util.StringUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.util.Objects;

public class Fdc3ClientFrame {

    private JPanel mainPanel;

    private JTextArea textLogs;
    private JButton getSystemChannelsButton;
    private JButton getOrCreateDesktopAgentButton;
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
    private JTextField intentNameTextField;
    private JTextField targetTextField;
    private JButton raiseIntentButton;
    private JTextField channelNameTextField2;
    private JButton getOrCreateChannelButton;
    private JButton clearButton1;
    private JTextField channelNameTextField1;
    private JButton joinChannelButton;
    private JButton leaveChannelButton;
    private JTextField typeFdc3InstrumentIdTextField;
    private JTextField findIntentTextField;
    private JButton findIntentButton;
    private JCheckBox byContextCheckBox;

    public Fdc3ClientFrame(Finsemble fsbl) {
        getSystemChannelsButton.addActionListener(e -> {
            fsbl.getClients().getFdc3Client().getSystemChannels(defaultCallback);
        });

        broadcastButton.addActionListener(e -> {
            fsbl.getClients().getFdc3Client().broadcast(new JSONObject(typeFdc3InstrumentIdTextField.getText()));
        });

        getOrCreateDesktopAgentButton.addActionListener(e -> {
            DesktopAgentClient desktopAgentClient
                    = fsbl.getClients().getFdc3Client().getOrCreateDesktopAgent(channelNameTextField.getText());
            //TODO print desktopAgentClient
            writeLogs("getChannelChanging = " + desktopAgentClient.getChannelChanging());
        });

        openButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().open(openComponentTextField.getText(), null, defaultContextCallback);
        });

        raiseIntentButton.addActionListener(e -> {
            if ("".equals(targetTextField.getText()) || "Target".equalsIgnoreCase(targetTextField.getText().trim())) {
                fsbl.getClients().getDesktopAgent()
                        .raiseIntent(intentNameTextField.getText(), null, defaultCallback);
            } else {
                fsbl.getClients().getDesktopAgent()
                        .raiseIntent(intentNameTextField.getText(), null, targetTextField.getText(),  defaultCallback);
            }
        });

        getOrCreateChannelButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().getOrCreateChannel(channelNameTextField2.getText(), defaultCallback);
        });

        findIntentButton.addActionListener(e -> {
            if (byContextCheckBox.isSelected()) {
                //TODO set the context object properly
                fsbl.getClients().getDesktopAgent().findIntentsByContext(null, defaultCallback);
            } else {
                fsbl.getClients().getDesktopAgent().findIntent(findIntentTextField.getText(), defaultCallback);
            }
        });

        addIntentListenerButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().addIntentListener(intentTextField.getText(), defaultCallback);
        });

        removeIntentListenerButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().removeListener(intentTextField.getText());
        });

        getCurrentChannelButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().getCurrentChannel(defaultCallback);
        });

        addContextListenerButton.addActionListener(e -> {
            if (contextTypeTextField.getText() != null && contextTypeTextField.getText().length() > 0) {
                fsbl.getClients().getChannelClient().addContextListener(contextTypeTextField.getText(), defaultCallback);
            } else {
                fsbl.getClients().getChannelClient().addContextListener(defaultCallback);
            }
        });

        removeContextListenerButton.addActionListener(e -> {
            fsbl.getClients().getDesktopAgent().removeListener(contextTypeTextField.getText());
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

    private final CallbackListenerContextObject defaultContextCallback = ((err, context, res) -> {
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
