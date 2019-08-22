package team02.project.visualization;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.colors.Dark;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import team02.project.App;
import team02.project.cli.CLIConfig;

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
    private ScrollPane ganttBox;

    private Tile memoryTile;
    private Tile allocationTile;
    private Tile orderTile;
    private Tile scheduleTile;



    public void init() {

        setUpMemoryTile();
        setUpAllocationTile();
        setUpOrderTile();
        setUpScheduleTile();
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
                .build();

        schedulesBox.getChildren().addAll(buildFlowGridPane(this.scheduleTile));
    }

    private FlowGridPane buildFlowGridPane(Tile tile) {
        return new FlowGridPane(1, 1, tile);
    }





}
