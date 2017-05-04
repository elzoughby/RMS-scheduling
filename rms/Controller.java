package rms;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Controller {

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private String[] style = {"status-one", "status-two", "status-three",
            "status-four", "status-five", "status-six", "status-seven", "status-eight", "status-nine", "status-ten",
            "status-eleven", "status-twelve"};
    private List<String> styleList;

    @FXML
    private TextField txtET;
    @FXML
    private TextField txtPeriod;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn columnId;
    @FXML
    private TableColumn columnET;
    @FXML
    private TableColumn columnPeriod;
    @FXML
    private Label lblLCM;




    public Controller() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtET.setText(Integer.toString(newSelection.getET()));
                        txtPeriod.setText(Integer.toString(newSelection.getPeriod()));
                    }
                });
            }
        });

    }

    @FXML
    private void addTask() {

        taskList.add(new Task(Integer.parseInt(txtET.getText()),Integer.parseInt(txtPeriod.getText())));

        columnET.setCellValueFactory(new PropertyValueFactory<>("eT"));
        columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        columnId.setCellValueFactory(new PropertyValueFactory<>("name"));

        taskTable.setItems(taskList);
        txtET.setText("");
        txtPeriod.setText("");
        lblLCM.setText(Integer.toString(Scheduler.calcLCM(taskList)));
    }

    @FXML
    private void schedule() {

        styleList = new ArrayList<>(Arrays.asList(style));
        TimelineChart chart = drawChart();
        int width = 800; //Integer.parseInt(lblLCM.getText()) * 55;
        int height = 400; //taskList.size() * 20 * 2 + 100;
        Scene scene  = new Scene(chart, width, height);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private TimelineChart drawChart() {

        List<Task> resultList = new ArrayList<>(Scheduler.schedule(taskList));
        List<String> nameList = new ArrayList<>();
        for(Task t : taskList)
            nameList.add(t.getName());

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Execution Time");
        xAxis.setAutoRanging(false);
        xAxis.setMinorTickCount(5);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Double.parseDouble(lblLCM.getText()));
        xAxis.setTickUnit(1);
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Task ID");
        yAxis.setTickLabelGap(10);
        yAxis.setAutoRanging(false);
        yAxis.setCategories(FXCollections.observableArrayList(nameList));

        final TimelineChart chart = new TimelineChart(xAxis, yAxis);
        chart.setTitle("Rate Monotonic Schedule");
        chart.setLegendVisible(false);

        ObservableList<XYChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        for(Task t : taskList) {
            ObservableList<XYChart.Data<Number, String>> seriesData = FXCollections.observableArrayList();
            String styleClass = getRandomStyle();
            for(int i = 0; i < resultList.size(); i++) {
                if(t.equals(resultList.get(i)))
                    seriesData.add(new XYChart.Data<>(i, t.getName(), new TimelineChart.ExtraData(styleClass)));
            }
            chartData.add(new XYChart.Series<>(seriesData));
        }
        chart.setData(chartData);
        chart.getStylesheets().add(getClass().getResource("timeline.css").toExternalForm());

        return chart;
    }

    private String getRandomStyle() {
        Random random = new Random();
        int randomIndex = random.nextInt(styleList.size());
        String randomStyle = styleList.get(randomIndex);
        styleList.remove(randomIndex);
        return randomStyle;
    }

}
