package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller {

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
    private TextArea textArea;

    @FXML
    private javafx.scene.control.ProgressBar ProgressBar;

    public static File temp = new File("C:/Temp");

    public static File x64 = new File("C:/CDKRecreation/lsinstall_2.0_x64.exe -q");
    public static File x86 = new File("C:/CDKRecreation/lsinstall_2.0_x86.exe -q");

    public static File cache = new File("C:/Program Files (x86)/LightspeedEVO Clients");
    public static File automation1ClientCache = new File(cache + "/LightspeedEVO");
    public static File automation2ClientCache = new File(cache + "/LightspeedEVO2");

    public static File automation1TempFolder = new File(temp + "/LightspeedEVO");
    public static File automation2TempFolder = new File(temp + "/LightspeedEVO2");

    public static final String evoRunning = "Oops! Evo is currently running. Try again after closing it.";
    public static final String evoNotRunning = "Evo is not currently running, great job! Proceeding.";
    public static final String processAborted = "Process failed. Please try again.";


    @FXML
    void Bottom32bitButton(ActionEvent event) throws IOException {

        textArea.clear();

        try {

            textArea.appendText("32-bit selected." + "\n\n");

            processChange(x86);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            textArea.appendText(e + "\n\n");
            textArea.appendText(processAborted + "\n\n");
            Top64bit.setSelected(false);
            Bottom32bit.setSelected(false);
        }

    }

    @FXML
    void Top64bitButton(ActionEvent event) throws IOException {

        textArea.clear();

        try {

            textArea.appendText("64-bit selected." + "\n\n");

            processChange(x64);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            textArea.appendText(e + "\n\n");
            textArea.appendText(processAborted);
            Top64bit.setSelected(false);
            Bottom32bit.setSelected(false);

        }

    }


    @FXML
    void initialize() throws IOException {


        try {
            if (!temp.exists()) {

                FileUtils.forceMkdir(temp);

            }
        } catch (IOException e) {
            e.printStackTrace();
            textArea.appendText(e + "\n\n");
            textArea.appendText(processAborted);
            Top64bit.setSelected(false);
            Bottom32bit.setSelected(false);
        }

    }

    public Boolean checkForRunning() throws IOException {

        boolean running = false;

        try {

            String line;
            String pidInfo = "";

            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfo += line;
            }

            input.close();

            if (pidInfo.contains("Lightspeed.exe")) {

                running = true;

            } else if (pidInfo.contains("Lightspeed_x64.exe")) {

                running = true;

            } else {

                running = false;
            }


        } catch (IOException e) {
            e.printStackTrace();
            textArea.appendText(e + "\n\n");
            textArea.appendText(processAborted);
            Top64bit.setSelected(false);
            Bottom32bit.setSelected(false);

        }

        return running;
    }


    public void processChange(File file) throws IOException, InterruptedException {


        if (checkForRunning()) {

            textArea.appendText(evoRunning + "\n\n");
            Top64bit.setSelected(false);
            Bottom32bit.setSelected(false);

        } else {

            textArea.appendText(evoNotRunning + "\n\n");

            new Thread(() -> {

                try {


                    FileUtils.copyDirectoryToDirectory(automation1ClientCache, temp);
                    FileUtils.copyDirectoryToDirectory(automation2ClientCache, temp);

                    TimeUnit.SECONDS.sleep(3);

                    Platform.runLater(() -> ProgressBar.setProgress(0.25));

                    Runtime.getRuntime().exec(String.valueOf(file));

                    TimeUnit.SECONDS.sleep(3);

                    Platform.runLater(() -> ProgressBar.setProgress(0.50));

                    FileUtils.cleanDirectory(cache);

                    Platform.runLater(() -> ProgressBar.setProgress(0.75));

                    FileUtils.copyDirectoryToDirectory(automation1TempFolder, cache);
                    FileUtils.copyDirectoryToDirectory(automation2TempFolder, cache);

                    TimeUnit.SECONDS.sleep(1);

                    Platform.runLater(() -> ProgressBar.setProgress(1));

                    textArea.appendText("Process complete!" + "\n\n");

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    textArea.appendText(e + "\n\n");
                    textArea.appendText(processAborted);
                    Top64bit.setSelected(false);
                    Bottom32bit.setSelected(false);
                    Thread.currentThread().interrupt();
                    return;
                }


            }).start();


        }

    }
}
