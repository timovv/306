package team02.project.visualization;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.colors.Dark;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.AlgorithmMonitor;
import team02.project.cli.CLIConfig;
import team02.project.visualization.GanttChart.ExtraData;

import java.util.*;

import static javafx.scene.paint.Color.rgb;

public class MainController {

    private static final int CHART_MAX_ELEMENTS = 600;

    @FXML
    private VBox statsBox;

    @FXML
    private VBox rootVbox;

    @FXML
    private VBox memBox;

    @FXML
    private VBox schedulesBox;

    @FXML
    private HBox allocBox;

    @FXML
    private HBox orderBox;

    @FXML
    private VBox ganttBox;

    @FXML
    private TextFlow numProFlow, inGraphFlow, outGraphFlow;

    @FXML
    private Text schedCreatedText, currentBestText, timeElapsedText, runningText;

    private Tile memoryTile;
    private Tile allocationTile;
    private Tile orderTile;
    private Tile scheduleTile;
    private GanttChart<Number,String> chart;
    private Timeline timerHandler;

    private int numSchedules;
    private double startTime;
    private double currentTime;
    private double finishTime;

    private CLIConfig config;
    private AlgorithmMonitor monitor;
    
    public void injectConfig(CLIConfig config){
        this.config = config;
    }

    public void injectMonitor(AlgorithmMonitor monitor) {
        this.monitor = monitor;
    }
    
    public void init() {
        Objects.requireNonNull(config);

        // set up display elements
        setUpMemoryTile();
        setUpAllocationTile();
        setUpOrderTile();
        setUpScheduleTile();
        setUpGanttBox();
        setUpStatsBox();

        // start polling
        startPolling();

        // initialize the value in order for setValue to work properly
        memoryTile.setValue(0);

        // begin timer
        startTimer();
    }

    private void startPolling() {
        Timeline poller = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if(monitor.isFinished()){
                runningText.setStyle("-fx-fill: rgb(15,157,88);");
                runningText.setText("Done!");
                stopTimer();
                return;
            }

            double memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1000000d);
            memoryTile.setValue(memoryUsage);

            if(monitor.getCurrentBest() != null){
                updateGannt(monitor.getCurrentBest());
            }
            currentBestText.setText("" + monitor.getCurrentBest().getFinishTime());
            updateNumSchedules(monitor.getCompleteSchedules());

            ObservableList<ChartData> allocData = allocationTile.getChartData();
            ObservableList<ChartData> orderData = orderTile.getChartData();

            // Randomly remove data points to not overload GUI
            if (allocData.size() >= CHART_MAX_ELEMENTS) {
                allocData.remove((int) (Math.random() * allocData.size()));
            }

            if (orderData.size() >= CHART_MAX_ELEMENTS) {
                orderData.remove((int) (Math.random() * orderData.size()));
            }

            allocationTile.addChartData(new ChartData(monitor.getAllocationsExpanded()));
            orderTile.addChartData(new ChartData(monitor.getOrderingsExpanded()));
        }));
        poller.setCycleCount(Animation.INDEFINITE);
        poller.play();
    }

    private void setUpMemoryTile() {
        this.memoryTile = TileBuilder.create().skinType(Tile.SkinType.BAR_GAUGE)
                .unit("MB")
                .maxValue(Runtime.getRuntime().maxMemory() / (1024 * 1024))
                .threshold(Runtime.getRuntime().maxMemory() * 0.8 / (1024 * 1024))
                .gradientStops(new Stop(0, rgb(244,160,0)),
                        new Stop(0.8, Bright.RED),
                        new Stop(1.0, Dark.RED))
                .animated(true)
                .decimals(0)
                .strokeWithGradient(true)
                .thresholdVisible(true)
                .backgroundColor(Color.WHITE)
                .valueColor(rgb(244,160,0))
                .unitColor(rgb(244,160,0))
                .barBackgroundColor(rgb(242, 242, 242))
                .thresholdColor(rgb(128, 84, 1))
                .needleColor(rgb(244,160,0))
                .build();

        memBox.getChildren().addAll(buildFlowGridPane(this.memoryTile));

    }

    private void setUpAllocationTile() {
        this.allocationTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .chartData(new ChartData(0), new ChartData(0))
                .animated(false)
                .smoothing(true)
                .title("Allocations Checked")
                .titleColor(rgb(219,68,55))
                .textSize(Tile.TextSize.BIGGER)
                .decimals(0)
                .minWidth(387)
                .backgroundColor(Color.WHITE)
                .valueColor(rgb(219,68,55))
                .build();

        allocBox.getChildren().addAll(buildFlowGridPane(this.allocationTile));
    }

    private void setUpOrderTile() {
        this.orderTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .chartData(new ChartData(0), new ChartData(0))
                .animated(false)
                .smoothing(true)
                .minWidth(387)
                .decimals(0)
                .title("Orderings Checked")
                .textSize(Tile.TextSize.BIGGER)
                .titleColor(rgb(15,157,88))
                .backgroundColor(Color.WHITE)
                .valueColor(rgb(15,157,88))
                .build();

        orderBox.getChildren().addAll(buildFlowGridPane(this.orderTile));
    }

    private void setUpScheduleTile() {
        this.scheduleTile =  TileBuilder.create().skinType(Tile.SkinType.CHARACTER)
                .description("" + monitor.getCompleteSchedules())
                .textColor(rgb(66,133,244))
                .animated(true)
                .decimals(0)
                .backgroundColor(Color.WHITE)
                .textSize(Tile.TextSize.BIGGER)
                .title("Complete Schedules Checked")
                .titleAlignment(TextAlignment.CENTER)
                .titleColor(rgb(66,133,244))
                .build();

        schedulesBox.getChildren().addAll(buildFlowGridPane(this.scheduleTile));
    }

    private void updateNumSchedules(long i){
        scheduleTile.setDescription(""+i);
    }

    private FlowGridPane buildFlowGridPane(Tile tile) {
        return new FlowGridPane(1, 1, tile);
    }

    private void setUpStatsBox(){
//        numProFlow.getChildren().add(new Text(String.valueOf(config.numberOfScheduleProcessors())));
//
//        String inputString = config.inputDOTFile();
//        String outputString = config.outputDOTFile();
//
//        inputString = inputString.substring(inputString.lastIndexOf('/')+1);
//        outputString = outputString.substring(outputString.lastIndexOf('/')+1);
//
//        inGraphFlow.getChildren().add(new Text(inputString));
//        outGraphFlow.getChildren().add(new Text(outputString));

        String inputString = config.inputDOTFile();
        String outputString = config.outputDOTFile();

        inputString = inputString.substring(inputString.lastIndexOf('/')+1);
        outputString = outputString.substring(outputString.lastIndexOf('/')+1);

        Text inputText = new Text(inputString);
        Text outputText = new Text(outputString);
        Text processorText = new Text(String.valueOf(config.numberOfScheduleProcessors()));

        inputText.setStyle("-fx-font-family:\'Roboto Mono\'");
        outputText.setStyle("-fx-font-family:\'Roboto Mono\'");
        processorText.setStyle("-fx-font-family:\'Roboto Mono\'");


        inGraphFlow.getChildren().add(inputText);
        outGraphFlow.getChildren().add(outputText);
        numProFlow.getChildren().add(processorText);
    }

    private void startTimer(){

        startTime=System.currentTimeMillis();
        timerHandler = new Timeline(new KeyFrame(Duration.seconds(0.05), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                currentTime=System.currentTimeMillis();
                timeElapsedText.setText(""+((currentTime-startTime)/1000));
            }
        }));
        timerHandler.setCycleCount(Timeline.INDEFINITE);
        timerHandler.play();
    }

    private void stopTimer(){
        timerHandler.stop();
    }


    private void setUpGanttBox(){

        // Setting up number of processors and array of their names
        int numberPro = config.numberOfScheduleProcessors();
        String[] processors = new String[numberPro];
        for (int i = 0;i<numberPro;i++){
            processors[i]="Processor "+i;
        }

        // Setting up time (x) axis
        final NumberAxis timeAxis = new NumberAxis();
        timeAxis.setLabel("");
        timeAxis.setTickLabelFill(Color.rgb(254,89,21));
        timeAxis.setMinorTickCount(4);

        // Setting up processor (y) axis
        final CategoryAxis processorAxis = new CategoryAxis();
        processorAxis.setLabel("");
        timeAxis.setTickLabelFill(Color.rgb(254,89,21));
        processorAxis.setTickLabelGap(1);
        processorAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));

        // Setting up chart
        chart = new GanttChart<Number,String>(timeAxis,processorAxis);
        chart.setLegendVisible(false);
        chart.setBlockHeight(280/numberPro);

        chart.getStylesheets().add(getClass().getResource("/GanttChart.css").toExternalForm());
        chart.setMaxHeight(ganttBox.getPrefHeight());
        ganttBox.getChildren().add(chart);
        ganttBox.setStyle("-fx-background-color: WHITE");

    }

    private void updateGannt(Schedule bestSchedule){

        int numProcessers = config.numberOfScheduleProcessors();

        // new array of series to write onto
        Series[] seriesArray = new Series[numProcessers];

        // initializing series obj
        for (int i=0;i<numProcessers;i++){
            seriesArray[i]=new Series();
        }

        // for every task in schedule, write its data onto the specific series
        for (ScheduledTask scTask:bestSchedule.getTasks()){
            int idOfTask = scTask.getProcessorId();

            XYChart.Data newData = new XYChart.Data(scTask.getStartTime(), "Processor "+ String.valueOf(idOfTask),
                    new ExtraData(scTask, "task-style"));

            seriesArray[idOfTask].getData().add(newData);

        }

        //clear and rewrite series onto the chart
        chart.getData().clear();
        for (Series series: seriesArray){
            chart.getData().add(series);
        }

        // update the best text
        currentBestText.setText(""+bestSchedule.getFinishTime());
    }

//    @Override
//    public void update(AlgorithmStats stats) {
//        // take new timestamp
//        System.out.println("Allocations: " + stats.getAllocationsExpanded());
//        System.out.println("Orderings: " + stats.getOrderingsExpanded());
//
//        System.out.println("Complete schedules: " + stats.getCompleteSchedules());
//
//        // use old timestamp and cached values to update stuff
//
//        // cache new values
//
//        updateGannt(stats.getCurrentBest());
//    }
}
