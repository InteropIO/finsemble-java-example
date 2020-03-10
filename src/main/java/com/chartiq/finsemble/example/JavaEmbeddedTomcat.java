package com.chartiq.finsemble.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chartiq.finsemble.Finsemble;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jasper.servlet.JspServlet;
import org.json.JSONObject;


public class JavaEmbeddedTomcat {
    public static void main(String[] args) throws LifecycleException {
        final List<String> argList = new ArrayList<>(Arrays.asList(args));
        final Finsemble fsbl = new Finsemble(argList);

        //Create Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("tomcat");
        tomcat.setPort(8080);

        //Add war
        String contextPath = "/JavaWebapp";
        String warFilePath = new File("JavaWebappEmbedded.war").getAbsolutePath();
        tomcat.getHost().setAppBase(".");
        tomcat.addWebapp(contextPath, warFilePath);

        //Add servlet
        String servletContextPath = "/JavaWebapp/JavaServlet";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(servletContextPath, docBase);

        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Access-Control-Allow-Origin","http://localhost:3375");
                final PrintWriter out = response.getWriter();
                final String action = request.getParameter("action");
                switch (action) {
                    case "getComponentList":
                        fsbl.getClients().getLauncherClient().getComponentList((err, res) -> out.print(res));
                        break;
                    case "spawn":
                        final String componentName = request.getParameter("componentName");
                        fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject() {{
                            put("addToWorkspace", true);
                        }}, (err, res) -> {
                            if (err != null) {
                                fsbl.getClients().getLogger().error(String.format("Error spawning \"%s\"", componentName));
                                out.println(err.toString());
                            } else {
                                fsbl.getClients().getLogger().info(String.format("\"%s\" spawned by Tomcat", componentName));
                                out.print(res);
                            }
                        });
                        break;
                    case "stopEmbeddedTomcat":
                        try {
                            fsbl.getClients().getLogger().log("Tomcat closed!!!");
                            fsbl.close();
                            out.print("{'status':'closed'}");
                            out.flush();
                            tomcat.stop();
                            tomcat.destroy();
                        } catch (LifecycleException e) {
                            e.printStackTrace();
                        }finally {
                            System.exit(0);
                        }

                        break;
                    default:
                        out.println("{'status':'ok'}");
                        break;
                }
                out.flush();
            }
        };

        String servletName = "JavaServlet";
        String urlPattern = "/do";

        tomcat.addServlet(servletContextPath, servletName, servlet);
        context.addServletMappingDecoded(urlPattern, servletName);

        tomcat.start();

        //Connect FSBL
        try {
            fsbl.connect();
            fsbl.getClients().getLogger().log("Tomcat connected!!!");

            final String componentName = "example_jsp";
            fsbl.getClients().getLauncherClient().spawn(componentName, new JSONObject() {{
                put("addToWorkspace", true);
            }}, (err, res) -> {
                if (err != null) {
                    fsbl.getClients().getLogger().error(String.format("Error spawning \"%s\"", componentName));
                } else {
                    fsbl.getClients().getLogger().info(String.format("\"%s\" spawned by Tomcat", componentName));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        tomcat.getServer().await();
    }
}
