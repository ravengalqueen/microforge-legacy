package com.raven.microforge;

import javafx.application.*;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.slf4j.*;
import org.fxmisc.richtext.*;
import java.io.*;
import java.util.concurrent.ExecutionException;

public class main extends Application {
    private final static Logger logger = LoggerFactory.getLogger(main.class);
    public String name ="Microforge";
    public String fileMenuName="File";
    public String version = "preAlpha";
    public String terminalText ="this is the terminal, errors among other things will show here";
    public String saveButtonText="save";
    public String settingsButtonText="Settings";
    public String openButtonText="Open";

    public String textAreaText="/*Welcome to Microforge\n" +
            "version = " +version+"*/\n" +
            "void setup(){\n" +
            "//input code here to run once\n" +
            "}\n" +
            "void loop(){\n" +
            "//input code here to run over and over again forever\n" +
            "}";
    public String theme = "dark.css";
    public Boolean initialText = true;
    private TextArea terminal;
    private CodeArea mainCodeArea;
    private MenuItem openItem;
    private MenuItem saveItem;
    private MenuItem settingsItem;
    private MenuButton menuBar;


    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createTextAreaBox());
        borderPane.setBottom(createButtonBox());
        borderPane.setTop(createMenuAreaBox());

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("/themes/"+theme).toExternalForm());
        primaryStage.getIcons().add(new Image(main.class.getResourceAsStream("/textures/icon.png")));
        primaryStage.setTitle(name);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        saveItem.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Microcontroller sketch (*.ino)", "*.ino");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file != null){
                SaveFile(mainCodeArea.getText(), file);
            }
        });
        openItem.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File fileToLoad = fileChooser.showOpenDialog(null);
            if(fileToLoad != null){
                loadFileToTextArea(fileToLoad);
            }

        });
       settingsItem.setOnAction(event -> {settings.display();});
    }

    private Node createButtonBox() {


        return null;
    }

    private Node createMenuAreaBox() {
        openItem = new MenuItem(openButtonText);
        saveItem = new MenuItem(saveButtonText);
        settingsItem = new MenuItem(settingsButtonText);
        menuBar = new MenuButton();
        menuBar.setText(fileMenuName);
        menuBar.getItems().addAll(saveItem,openItem,settingsItem);
        menuBar.getStyleClass().add("menuBar");
        VBox vBox = new VBox();
        vBox.getChildren().add(menuBar);
        vBox.getStyleClass().add("topMenuBar");
        return (vBox);
    };
    private Node createTextAreaBox() {
        mainCodeArea = new CodeArea();
        mainCodeArea.setId("mainCodeArea");
        if(initialText){
            mainCodeArea.replaceText(textAreaText);
        }
        else{
            mainCodeArea.replaceText("");
        }
        terminal = new TextArea(terminalText);
        terminal.getStyleClass().add("terminalTextArea");
        terminal.setEditable(false);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainCodeArea);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("scrollPane");
        return scrollPane;
    }
    private void SaveFile(String content, File file){
        try {

            FileWriter fileWriter;
            content = mainCodeArea.getText() ;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            //another error String
            String errorDuringSave = "error occured during file save\n";
            terminal.setText(errorDuringSave);

        }
        }
        
    private Task<String> fileLoaderTask(File fileToLoad){
        //error strings
        String couldNotLoad="Could not load file from:\n " + fileToLoad.getAbsolutePath() + "\n";
        //actual code
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                StringBuilder totalFile = new StringBuilder();
                return totalFile.toString();
            }
        };
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                mainCodeArea.replaceText(loadFileTask.get());
            } catch (InterruptedException | ExecutionException e) {
                terminal.setText(couldNotLoad);
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            terminal.setText(couldNotLoad);
        });
        return loadFileTask;
    }
    private void loadFileToTextArea(File fileToLoad) {
        Task<String> loadFileTask = fileLoaderTask(fileToLoad);
        loadFileTask.run();
    }
    public void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File fileToLoad = fileChooser.showOpenDialog(null);
        if(fileToLoad != null){
            loadFileToTextArea(fileToLoad);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
//code in java they said, write once run everywhere they said. I hate this