package analyzerdisk;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Starter extends Application {
    private Stage stage;
    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> observableData = FXCollections.observableArrayList();
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Анализатор диска");

        Button button = new Button("Проверить деректорию");
        button.setOnAction(event -> {
            File file = new DirectoryChooser().showDialog(stage);
            String path = file.getAbsolutePath();
            sizes = new Analyzer().CalculateDirectorySize(Path.of(path));
            BildChart(path);
        });

        StackPane pane = new StackPane();
        pane.getChildren().addAll(button);
        stage.setScene(new Scene(pane, 300, 300));
        stage.show();
    }

    private void BildChart(String path){
        pieChart = new PieChart(observableData);

        RefillChart(path);

        Button button = new Button(path);
        button.setOnAction(event -> RefillChart(path));

        BorderPane pane = new BorderPane();
        pane.setTop(button);
        pane.setCenter(pieChart);
        stage.setScene(new Scene(pane, 900, 600));
        stage.show();
    }

    private void RefillChart(String path) {
        observableData.clear();
        observableData.addAll(
                sizes
                        .entrySet()
                        .parallelStream()
                        .filter(entry -> {
                    Path perent = Path.of(entry.getKey()).getParent();
                    return perent != null && perent.toString().equals(path);
                })
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
        );
        pieChart.getData().forEach(data -> {
            data.getNode().addEventHandler(
                    MouseEvent.MOUSE_PRESSED,
                    event -> RefillChart(data.getName())
            );
        });
    }
}
