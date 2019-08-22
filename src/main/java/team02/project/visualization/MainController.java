package team02.project.visualization;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.colors.Dark;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import team02.project.App;
import team02.project.algorithm.Schedule;
import team02.project.algorithm.ScheduledTask;
import team02.project.algorithm.SchedulingContext;
import team02.project.cli.CLIConfig;
import team02.project.graph.Graph;
import team02.project.visualization.GanttChart.ExtraData;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static team02.project.App.*;

public class MainController {

    @FXML
    private VBox statsBox;

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

    private Tile memoryTile;
    private Tile allocationTile;
    private Tile orderTile;
    private Tile scheduleTile;
    private GanttChart<Number,String> chart;



    public void init() {

        setUpMemoryTile();
        setUpAllocationTile();
        setUpOrderTile();
        setUpScheduleTile();
        setUpGanttBox();
        this.memoryTile.setValue(2000);
        this.scheduleTile.setValue(200);


    }

    private void setUpMemoryTile() {
        this.memoryTile = TileBuilder.create().skinType(Tile.SkinType.BAR_GAUGE)
                .title("Memory usage")
                .unit("MB")
                .maxValue(Runtime.getRuntime().maxMemory() / (1000 * 1000))
                .threshold(Runtime.getRuntime().maxMemory() * 0.8 / (1024 * 1024))
                .gradientStops(new Stop(0, Bright.GREEN),
                        new Stop(0.8, Bright.RED),
                        new Stop(1.0, Dark.RED))
                .animated(true)
                .decimals(0)
                .strokeWithGradient(true)
                .thresholdVisible(true)
                .build();

        memBox.getChildren().addAll(buildFlowGridPane(this.memoryTile));
    }

    private void setUpAllocationTile() {
        this.allocationTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .title("Feasible Allocations")
                .chartData(new ChartData(0), new ChartData(0))
                .animated(false)
                .smoothing(true)
                .minWidth(387)
                .build();

        allocBox.getChildren().addAll(buildFlowGridPane(this.allocationTile));
    }

    private void setUpOrderTile() {
        this.orderTile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .title("Feasible Orderings")
                .chartData(new ChartData(0), new ChartData(0))
                .animated(false)
                .smoothing(true)
                .minWidth(387)
                .build();

        orderBox.getChildren().addAll(buildFlowGridPane(this.orderTile));
    }

    private void setUpScheduleTile() {
        this.scheduleTile =  TileBuilder.create().skinType(Tile.SkinType.NUMBER)
                .title("Schedules Per Second")
                .titleAlignment(TextAlignment.CENTER)
                .animated(true)
                .decimals(0)
                .build();

        schedulesBox.getChildren().addAll(buildFlowGridPane(this.scheduleTile));
    }

    private FlowGridPane buildFlowGridPane(Tile tile) {
        return new FlowGridPane(1, 1, tile);
    }

    private void setUpGanttBox(){

        // Setting up number of processors and array of their names
        int numberPro = App.config.numberOfScheduleProcessors();
        String[] processors = new String[numberPro];
        for (int i = 0;i<numberPro;i++){
            processors[i]="Processor "+i;
        }

        // Setting up time (x) axis
        final NumberAxis timeAxis = new NumberAxis();
        timeAxis.setLabel("Time");
        timeAxis.setTickLabelFill(Color.CHOCOLATE);
        timeAxis.setMinorTickCount(1);

        // Setting up processor (y) axis
        final CategoryAxis processorAxis = new CategoryAxis();
        processorAxis.setLabel("");
        processorAxis.setTickLabelFill(Color.CHOCOLATE);
        processorAxis.setTickLabelGap(50);
        processorAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));

        // Setting up chart
        chart = new GanttChart<Number,String>(timeAxis,processorAxis);
        chart.setLegendVisible(false);
        chart.setBlockHeight(280/numberPro);

        chart.getStylesheets().add(getClass().getResource("/GanttChart.css").toExternalForm());
        chart.setMaxHeight(ganttBox.getPrefHeight());
        ganttBox.getChildren().add(chart);

        //TODO Remove me uwu
        testGannt();
    }

    public void updateGannt(Schedule bestSchedule){

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
                    new ExtraData(scTask.getFinishTime()-scTask.getStartTime(), "task-style"));
            Tooltip.install(newData.getNode(), new Tooltip("Symbol-0"));

            seriesArray[idOfTask].getData().add(newData);

        }

        //clear and rewrite series onto the chart
        chart.getData().clear();
        for (Series series: seriesArray){
            chart.getData().add(series);
        }
    }

    //TODO ############# REMOVE ME
    public void testGannt(){
        System.out.println("Remove the test Gantt method ");

        Graph graph = createGraph(Paths.get(App.config.inputDOTFile()));
        SchedulingContext ctx = new SchedulingContext(graph, App.config.numberOfScheduleProcessors());
        Schedule maybeOptimal = calculateSchedule(App.config, graph, ctx);
        System.out.print(maybeOptimal.getFinishTime());
        writeOutput(Paths.get(config.outputDOTFile()),ctx,maybeOptimal);

        updateGannt(maybeOptimal);
    }


}
