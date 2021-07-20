/***********************************************************************************************************************
 Copyright 2018-2020 by ChartIQ, Inc.
 Licensed under the ChartIQ, Inc. Developer License Agreement https://www.chartiq.com/developer-license-agreement
 **********************************************************************************************************************/
package com.chartiq.finsemble.example.fdc3;

import com.chartiq.finsemble.example.JavaExampleApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InteropServiceExampleApplication extends Application {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(InteropServiceExampleApplication.class.getName());

    /**
     * Arguments passed via the command line
     */
    private static final InteropServiceMessageHandler messageHandler = new InteropServiceMessageHandler();

    /**
     * Initializes a new instance of the JavaExample class.
     */
    public InteropServiceExampleApplication() {
    }

    private InteropServiceExample controller;

    /**
     * The main function of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        final List<String> argList = new ArrayList<>(Arrays.asList(args));

        initLogging(argList);

        // the following statement is used to log any messages
        LOGGER.info(String.format("Starting InteropServiceExampleApplication: %s", Arrays.toString(args)));

        launch(args);

        // the following statement is used to log any messages
        LOGGER.info("Started InteropServiceExampleApplication");
    }

    //region JavaFX Application Implementation

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) {
        LOGGER.info("Start method called");

        // Get arguments from Application
        final List<String> args = getParameters().getRaw();

        LOGGER.info(String.format(
                "Finsemble Java FDC3 Desktop Agent Example starting with arguments:\n\t%s", String.join("\n\t", args)));
        final URL resource = JavaExampleApplication.class.getResource("InteropService.fxml");
        final FXMLLoader loader = new FXMLLoader(resource);
        try {
            final VBox anchorPane = loader.load();
            LOGGER.info("Parent loaded from resource");
            primaryStage.setTitle("InteropServiceExampleApplication");
            primaryStage.setResizable(false);
            final Scene scene = new Scene(anchorPane, 265, 400);

            LOGGER.info("Scene created");
            primaryStage.setScene(scene);

            LOGGER.info("Showing window");
            primaryStage.show();

            // Updated controller
            controller = loader.getController();
            controller.setArguments(args);
            final Window window = scene.getWindow();
            controller.setWindow(window);
            messageHandler.setJavaExample(controller);

            final Thread connectThread = new Thread(controller::connect);
            connectThread.setDaemon(true);
            connectThread.start();

            primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

            LOGGER.info("Started successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in start", e);
        }
    }
    //endregion

    private static void initLogging(List<String> args) {
        LOGGER.addHandler(messageHandler);

        if (System.getProperty("java.util.logging.config.file") != null) {
            // Config file property has been set, no further initialization needed.
            return;
        }

        // Check whether logging file has been specified
        final List<String> properties = args
                .stream()
                .filter(arg -> arg.startsWith("-Djava.util.logging.config.file"))
                .collect(Collectors.toList());

        if ((properties.size() == 0) || !properties.get(0).contains("=")) {
            // No logging properties specified
            return;
        }

        // Get filename from parameter
        final String loggingPropertiesPath = properties.get(0).split("=")[1];
        try {
            final InputStream inputStream = new FileInputStream(loggingPropertiesPath);
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load default logging.properties file", e);
        }
    }


    private void closeWindowEvent(WindowEvent event) {
        try {
            controller.getFinsembleBridge().getClients().getLoggerClient().close();
        } catch (IOException | NullPointerException e) {
            LOGGER.warning(String.format("Error sending the unregister message: %s", e));
        }
    }
}
