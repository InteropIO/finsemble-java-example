package com.chartiq.finsemble.poc;


import com.chartiq.finsemble.Finsemble;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommerzPocServlet extends HttpServlet {
    private Finsemble fsbl;

    public void init() throws ServletException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "/config/config.properties";
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String args[] = new String[]{"componentType=" + prop.getProperty("componentType"), "finsembleWindowName=" + prop.getProperty("finsembleWindowName"), "iac=" + prop.getProperty("iac"), "serverAddress=" + prop.getProperty("serverAddress")};
        final List<String> argList = new ArrayList<>(Arrays.asList(args));
        fsbl = new Finsemble(argList);
        try {
            fsbl.connect();
            fsbl.getClients().getLogger().log("Tomcat connected!!!");

            String componentName = "commerz_poc_jsp";
            fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject(){{put("addToWorkspace", true);}}, (err, res) -> {
                if (err != null) {
                    fsbl.getClients().getLogger().error(String.format("Error spawning \"%s\"", componentName));
                } else {
                    fsbl.getClients().getLogger().info(String.format("\"%s\" spawned by Tomcat", componentName));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        switch (action) {
            case "getComponentList":
                fsbl.getClients().getLauncherClient().getComponentList((err, res) -> {
                    out.print(res.toString());
                });
                break;
            case "spawn":
                String componentName = request.getParameter("componentName");
                fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject(){{put("addToWorkspace", true);}}, (err, res) -> {
                    if (err != null) {
                        fsbl.getClients().getLogger().error(String.format("Error spawning \"%s\"", componentName));
                        out.println(err.toString());
                    } else {
                        fsbl.getClients().getLogger().info(String.format("\"%s\" spawned by Tomcat", componentName));
                    }
                });
            case "stopTomcat":
//                Socket socket = new Socket("localhost", 8005);
//                if (socket.isConnected()) {
//                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
//                    pw.println("SHUTDOWN");//send shut down command
//                    pw.close();
//                    socket.close();
//                }

                break;
            default:
                break;
        }
        out.flush();
    }

    public void destroy() {
        try {
            fsbl.getClients().getLogger().log("Tomcat disconnected!!!");
            fsbl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}