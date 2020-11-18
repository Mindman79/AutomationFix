package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller {


    public static File temp = new File("C:/Temp");

    public static File x64 = new File("C:/CDKRecreation/lsinstall_2.0_x64.exe -q");
    public static File x86 = new File("C:/CDKRecreation/lsinstall_2.0_x86.exe -q");

    public static File cache = new File("C:/Program Files (x86)/LightspeedEVO Clients");
    public static File automation1ClientCache = new File(cache + "/LightspeedEVO");
    public static File automation2ClientCache = new File(cache + "/LightspeedEVO2");

    public static File automation1TempFolder = new File(temp + "/LightspeedEVO");
    public static File automation2TempFolder = new File(temp + "/LightspeedEVO2");


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RadioButton Top64bit;

    @FXML
    private ToggleGroup Grp;

    @FXML
    private RadioButton Bottom32bit;

    @FXML
    private javafx.scene.control.ProgressBar ProgressBar;

    @FXML
    void Bottom32bitButton(ActionEvent event) {

        System.out.println("32-bit selected");

        processChange(x86);

    }

    @FXML
    void Top64bitButton(ActionEvent event) {


        System.out.println("64-bit selected");


        processChange(x64);

    }


    @FXML
    void initialize() throws IOException {


        if (!temp.exists()) {

            FileUtils.forceMkdir(temp);

        }


        String classpath = System.getProperty("java.class.path");

        System.out.println(classpath);
    }

    public void processChange(File file) {


        new Thread(() -> {

            try {
                FileUtils.copyDirectoryToDirectory(automation1ClientCache, temp);
                FileUtils.copyDirectoryToDirectory(automation2ClientCache, temp);

                Platform.runLater(() -> ProgressBar.setProgress(0.25));

                Runtime.getRuntime().exec(String.valueOf(file));

                TimeUnit.SECONDS.sleep(5);

                Platform.runLater(() -> ProgressBar.setProgress(0.50));

                FileUtils.cleanDirectory(cache);

                Platform.runLater(() -> ProgressBar.setProgress(0.75));

                FileUtils.copyDirectoryToDirectory(automation1TempFolder, cache);
                FileUtils.copyDirectoryToDirectory(automation2TempFolder, cache);

                TimeUnit.SECONDS.sleep(5);

                Platform.runLater(() -> ProgressBar.setProgress(1));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }).start();


    }
}
