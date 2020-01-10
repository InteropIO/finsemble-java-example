package com.chartiq.finsemble.poc;

import com.chartiq.finsemble.Finsemble;
import com.chartiq.finsemble.example.JavaSwingExample;
import com.chartiq.finsemble.interfaces.ConnectionEventGenerator;
import com.chartiq.finsemble.interfaces.ConnectionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CommerzPocSwing extends JFrame implements WindowListener {
    private static final Logger LOGGER = Logger.getLogger(JavaSwingExample.class.getName());

    private final List<String> launchArgs;

    private Container contentPane;
    private JButton changeColorButton;
    private JButton publishColorButton;
    private JPanel colorPanel;

    private Finsemble fsbl;

    public static void main(String[] args) throws IOException {
        if(args[0].contains("loggingConfigFile")){
            FileInputStream fis =  new FileInputStream(args[0].substring(args[0].indexOf("=")+1));
            LogManager.getLogManager().readConfiguration(fis);
            fis.close();
        }

        final List<String> argList = new ArrayList<>(Arrays.asList(args));
        launchForm(argList);

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting JavaSwingExample: %s", Arrays.toString(args)));
    }

    private static void launchForm(List<String> args) {
        final CommerzPocSwing commerzPocSwing = new CommerzPocSwing(args);
        commerzPocSwing.addWindowListener(commerzPocSwing);
        commerzPocSwing.setVisible(true);
    }

    private CommerzPocSwing(List<String> args) {
        LOGGER.info(String.format(
                "Finsemble Java Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        LOGGER.info("Initiating Finsemble connection");

        launchArgs = args;

        createForm();

        setFormEnable(false);


        // Add button handler
        changeColorButton.addActionListener((e) -> this.changeColor());
        publishColorButton.addActionListener((e) -> this.publishColor());

    }

    private void createForm() {
        setTitle("Commerz POC Swing");
        setBounds(20, 20, 300, 375);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 0, 10);

        // region functions
        changeColorButton = new JButton("Change Color");
        changeColorButton.setEnabled(false);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 3;
        contentPane.add(changeColorButton, constraints);

        publishColorButton = new JButton("Publish Color");
        publishColorButton.setEnabled(false);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 4;
        constraints.gridy = 5;
        constraints.gridwidth = 3;
        contentPane.add(publishColorButton, constraints);

        colorPanel = new JPanel();
        colorPanel.setLayout(new CardLayout(100, 100));
        colorPanel.setBackground(new Color(0, 0, 255));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 7;

        contentPane.add(colorPanel, constraints);

        // endregion
    }

    private void setFormEnable(boolean enabled) {
        changeColorButton.setEnabled(enabled);
        publishColorButton.setEnabled(enabled);
    }

    private void changeColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color rgb = new Color(r, g, b);
        colorPanel.setBackground(rgb);
        fsbl.getClients().getLogger().log("Generated color: " + rgb.toString());
        saveState(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    private void changeColor(int r, int g, int b) {
        colorPanel.setBackground(new Color(r, g, b));
        saveState(r, g, b);
    }

    private void publishColor() {
        JSONObject colorObj = new JSONObject();
        colorObj.put("r", colorPanel.getBackground().getRed());
        colorObj.put("g", colorPanel.getBackground().getGreen());
        colorObj.put("b", colorPanel.getBackground().getBlue());
        fsbl.getClients().getRouterClient().transmit("color", colorObj);
        fsbl.getClients().getLogger().log("Transmitted color: " + colorObj.toString());
    }

    private void saveState(int r, int g, int b) {
        JSONObject temp = new JSONObject();
        JSONArray fields = new JSONArray();
        JSONObject rObj = new JSONObject(){{
            put("field", "r");
            put("value", r);
        }};
        JSONObject gObj = new JSONObject(){{
            put("field", "g");
            put("value", g);
        }};
        JSONObject bObj = new JSONObject(){{
            put("field", "b");
            put("value", b);
        }};

        fields.put(rObj);
        fields.put(gObj);
        fields.put(bObj);
        temp.put("fields", fields);

        fsbl.getClients().getWindowClient().setComponentState(temp, this::handleSetComponentStateCb);
        fsbl.getClients().getLogger().log("Saved state: " + temp.toString());
    }

    private void handleSetComponentStateCb(JSONObject err, JSONObject res) {
        fsbl.getClients().getLogger().log("Saved state: " + res.toString());
    }

    private void restoreState() {
        JSONObject param = new JSONObject();
        JSONArray fields = new JSONArray();
        fields.put("r");
        fields.put("g");
        fields.put("b");
        param.put("fields", fields);
        fsbl.getClients().getWindowClient().getComponentState(param, this::handleGetComponentStateCb);
        
    }

    private void handleGetComponentStateCb(JSONObject err, JSONObject state) {
        if (err == null) {
            fsbl.getClients().getLogger().log("Restoreing state with: " + state.toString());
            if(!state.isNull("r")){
                changeColor(Integer.valueOf(state.get("r").toString()), Integer.valueOf(state.get("g").toString()), Integer.valueOf(state.get("b").toString()));
                fsbl.getClients().getLogger().log("Restored state: " + state.toString());
            }
        } else {
            fsbl.getClients().getLogger().error(err.toString());
        }
    }

    private void initFinsemble() {
        fsbl = new Finsemble(launchArgs, this);
        try {
            fsbl.connect();
            fsbl.addListener(new ConnectionListener() {
                @Override
                public void disconnected(ConnectionEventGenerator from) {
                    LOGGER.info("Finsemble connection closed");
                }

                @Override
                public void error(ConnectionEventGenerator from, Exception e) {
                    LOGGER.log(Level.SEVERE, "Error from Finsemble", e);
                }
            });

            initForm();
            setFormEnable(true);
            restoreState();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initializing Finsemble connection", ex);
            try {
                fsbl.close();
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, "Error closing Finsemble connection", e1);
            }
        }
    }

    private void initForm() {
        fsbl.getClients().getRouterClient().addListener("color", this::handleRouterColorChange);
    }

    private void handleRouterColorChange(JSONObject err, JSONObject res) {
        if (err != null) {
            LOGGER.severe(err.toString());
        } else {
            JSONObject temp = res.getJSONObject("data");
            fsbl.getClients().getLogger().log("Received color: " + temp.toString());
            int r = temp.getInt("r");
            int g = temp.getInt("g");
            int b = temp.getInt("b");
            changeColor(r, g, b);
        }
    }


    @Override
    public void windowOpened(WindowEvent e) {
        initFinsemble();
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (fsbl != null) {
            try {
                fsbl.close();
            } catch (Exception ex) {
                // Do nothing
                fsbl = null;
            }
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
