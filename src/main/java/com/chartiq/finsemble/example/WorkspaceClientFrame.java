package com.chartiq.finsemble.example;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.interfaces.CallbackListener;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class WorkspaceClientFrame {
    private JPanel mainPanel;
    private JButton autoArrangeButton;
    private JTextArea textLogs;
    private JButton clearButton;
    private JButton minimizeAllButton;
    private JButton bringToFrontButton;
    private JButton getActiveWorkspaceButton;
    private JButton getWorkspacesButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton isDirtyButton;
    private JButton renameButton;
    private JTextField oldNameTextField;
    private JTextField newNameTextField;
    private JCheckBox overwriteCheckBox;
    private JButton createButton;
    private JTextField nameTextField;
    private JCheckBox switchAfterCheckBox;
    private JButton saveAsButton;
    private JTextField saveAsName;
    private JTextField switchToName;
    private JButton switchToButton;
    private JButton removeButton;
    private JTextField removeName;
    private JButton saveButton;
    private JTextField exportName;
    private JButton addEventListener;
    private JButton removeEventListener;
    private JTextField eventNameAdd;
    private JTextField eventNameRemove;

    private final CallbackListener defaultCallback = ((err, res) -> {
        if (Objects.nonNull(err)) {
            writeLogs(err.toString(4));
        } else {
            writeLogs(res.toString(4));
        }
    });

    public WorkspaceClientFrame(Finsemble fsbl) {
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textLogs.setCaret(caret);
        JSONObject windowIdentifier = fsbl.getClients().getWindowClient().getWindowIdentifier();

        clearButton.addActionListener(e -> textLogs.setText(""));
        autoArrangeButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().autoArrange("all", "", defaultCallback));
        minimizeAllButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().minimizeAll(new JSONObject(), defaultCallback));
        bringToFrontButton.addActionListener(e -> {
            fsbl.getClients().getWorkspaceClient().bringWindowsToFront(new JSONObject() {{
                put("monitor", "all");
                put("windowIdentifier", windowIdentifier);
            }}, defaultCallback);
        });
        getActiveWorkspaceButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().getActiveWorkspace(defaultCallback));
        getWorkspacesButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().getWorkspaces(defaultCallback));
        isDirtyButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().getActiveWorkspaceDirty(defaultCallback));
        saveButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().save(defaultCallback));
        renameButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().rename(
                oldNameTextField.getText(), newNameTextField.getText(), overwriteCheckBox.isSelected(), overwriteCheckBox.isSelected(), defaultCallback));
        createButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().createWorkspace(
                nameTextField.getText(), switchAfterCheckBox.isSelected(), defaultCallback));
        saveAsButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().saveAs(
                new JSONObject() {{
                    put("name", saveAsName.getText());
                    put("force", true);
                }}, defaultCallback));
        switchToButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().switchTo(switchToName.getText(), defaultCallback));
        removeButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().remove(removeName.getText(), defaultCallback));
        exportButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().exportWorkspace(
                new JSONObject() {{
                    put("workspaceName", exportName.getText());
                }}, defaultCallback));
        addEventListener.addActionListener(e -> fsbl.getClients().getWorkspaceClient().addEventListener(eventNameAdd.getText(), defaultCallback));
        removeEventListener.addActionListener(e -> fsbl.getClients().getWorkspaceClient().removeEventListener(eventNameRemove.getText(), defaultCallback));
        importButton.addActionListener(e -> fsbl.getClients().getWorkspaceClient().importWorkspace(
                new JSONObject() {{
                    put("workspaceJSONDefinition", new JSONObject(textLogs.getText()));
                    put("force", true);
                }}, defaultCallback));
    }

    private void writeLogs(String text) {
        textLogs.append("\n" + text);
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
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        mainPanel = new JPanel();
        mainPanel.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:108px:grow,left:5dlu:noGrow,left:64dlu:noGrow,left:4dlu:noGrow,fill:124px:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:d:grow,left:5dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        mainPanel.setPreferredSize(new Dimension(640, 550));
        CellConstraints cc = new CellConstraints();
        panel1.add(mainPanel, cc.xy(1, 1));
        autoArrangeButton = new JButton();
        autoArrangeButton.setText("autoArrange");
        mainPanel.add(autoArrangeButton, cc.xy(3, 3));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, cc.xyw(3, 17, 10, CellConstraints.FILL, CellConstraints.FILL));
        textLogs = new JTextArea();
        textLogs.setText("");
        scrollPane1.setViewportView(textLogs);
        clearButton = new JButton();
        clearButton.setText("Clear");
        mainPanel.add(clearButton, cc.xy(12, 19, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        minimizeAllButton = new JButton();
        minimizeAllButton.setText("minimizeAll");
        mainPanel.add(minimizeAllButton, cc.xy(5, 3));
        bringToFrontButton = new JButton();
        bringToFrontButton.setText("bringToFront");
        mainPanel.add(bringToFrontButton, cc.xy(7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        getActiveWorkspaceButton = new JButton();
        getActiveWorkspaceButton.setText("getActiveWorkspace");
        mainPanel.add(getActiveWorkspaceButton, cc.xy(9, 3));
        getWorkspacesButton = new JButton();
        getWorkspacesButton.setText("getWorkspaces");
        mainPanel.add(getWorkspacesButton, cc.xy(3, 5));
        importButton = new JButton();
        importButton.setText("import");
        mainPanel.add(importButton, cc.xy(7, 5, CellConstraints.FILL, CellConstraints.FILL));
        isDirtyButton = new JButton();
        isDirtyButton.setText("isDirty");
        mainPanel.add(isDirtyButton, cc.xy(12, 5));
        renameButton = new JButton();
        renameButton.setText("rename");
        mainPanel.add(renameButton, cc.xy(3, 7));
        oldNameTextField = new JTextField();
        oldNameTextField.setText("oldName");
        mainPanel.add(oldNameTextField, cc.xy(5, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        newNameTextField = new JTextField();
        newNameTextField.setText("newName");
        mainPanel.add(newNameTextField, cc.xy(7, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        overwriteCheckBox = new JCheckBox();
        overwriteCheckBox.setText("overwrite");
        mainPanel.add(overwriteCheckBox, cc.xy(9, 7));
        createButton = new JButton();
        createButton.setText("create");
        mainPanel.add(createButton, cc.xy(3, 9));
        nameTextField = new JTextField();
        nameTextField.setText("name");
        mainPanel.add(nameTextField, cc.xy(5, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        switchAfterCheckBox = new JCheckBox();
        switchAfterCheckBox.setText("switch after");
        mainPanel.add(switchAfterCheckBox, cc.xy(7, 9));
        saveAsButton = new JButton();
        saveAsButton.setText("saveAs");
        mainPanel.add(saveAsButton, cc.xy(3, 11));
        saveAsName = new JTextField();
        saveAsName.setText("name");
        mainPanel.add(saveAsName, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        switchToButton = new JButton();
        switchToButton.setText("switchTo");
        mainPanel.add(switchToButton, cc.xy(7, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        switchToName = new JTextField();
        switchToName.setText("name");
        mainPanel.add(switchToName, cc.xy(9, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        removeButton = new JButton();
        removeButton.setText("remove");
        mainPanel.add(removeButton, cc.xy(3, 13));
        removeName = new JTextField();
        removeName.setText("name");
        mainPanel.add(removeName, cc.xy(5, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        saveButton = new JButton();
        saveButton.setText("save");
        mainPanel.add(saveButton, cc.xy(12, 3));
        exportButton = new JButton();
        exportButton.setText("export");
        mainPanel.add(exportButton, cc.xy(7, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        exportName = new JTextField();
        exportName.setText("name");
        mainPanel.add(exportName, cc.xy(9, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        addEventListener = new JButton();
        addEventListener.setText("addEventListener");
        mainPanel.add(addEventListener, cc.xy(3, 15));
        removeEventListener = new JButton();
        removeEventListener.setText("removeListener");
        mainPanel.add(removeEventListener, cc.xy(7, 15));
        eventNameAdd = new JTextField();
        eventNameAdd.setText("name");
        mainPanel.add(eventNameAdd, cc.xy(5, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        eventNameRemove = new JTextField();
        eventNameRemove.setText("name");
        mainPanel.add(eventNameRemove, cc.xy(9, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
    }
}
