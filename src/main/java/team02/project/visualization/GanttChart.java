package team02.project.visualization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import team02.project.algorithm.ScheduledTask;

/*
 * All credit to "Roland" from Stackoverflow for this code.
 * Source: https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch/27978436
 */
public class GanttChart<X,Y> extends XYChart<X,Y> {

    public static class ExtraData {

        public long length;
        public String styleClass;
        private ScheduledTask scTask;

        public ScheduledTask getScTask() {
            return scTask;
        }

        public ExtraData(ScheduledTask scTask, String styleClass) {
            super();
            this.length = scTask.getFinishTime()-scTask.getStartTime();
            this.scTask = scTask;
            this.styleClass = styleClass;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public void setStyleClass(String styleClass) {
            this.styleClass = styleClass;
        }


    }

    private double blockHeight = 10;

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getStyleClass(Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    private static double getLength(Object obj) {
        return ((ExtraData) obj).getLength();
    }

    private static ScheduledTask getScheduledTask(Object obj){
        return ((ExtraData) obj).getScTask();
    }

    @Override
    protected void layoutPlotChildren() {

        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {

            Series<X, Y> series = getData().get(seriesIndex);

            Iterator<Data<X, Y>> iter = getDisplayedDataIterator(series);
            while (iter.hasNext()) {
                Data<X, Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();

                // **** Liam's super cool custom code to add tooltips to all datapoints
                ScheduledTask scTask = getScheduledTask(item.getExtraValue());
                team02.project.graph.Node task=scTask.getTask();
                Tooltip tooltipToAdd = new Tooltip("Task: "+task.getId()+"\nStart Time: "+scTask.getStartTime()
                        + "\nEnd Time: "+scTask.getFinishTime());

                //Custom tooltip style
                tooltipToAdd.setStyle("-fx-font-size: 12px;\n-fx-background-color: RED;");


                Tooltip.install(block, tooltipToAdd);
                // **** End of Liam's super cool code.

                Rectangle ellipse;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane) item.getNode();
                        if (region.getShape() == null) {
                            ellipse = new Rectangle(getLength(item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            ellipse = (Rectangle) region.getShape();
                        } else {
                            return;
                        }
                        ellipse.setWidth(getLength(item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
                        ellipse.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getYAxis()).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;

                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(ellipse);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override
    protected void dataItemAdded(Series<X, Y> series, int itemIndex, Data<X, Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override
    protected void dataItemRemoved(final Data<X, Y> item, final Series<X, Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override
    protected void dataItemChanged(Data<X, Y> item) {
    }

    @Override
    protected void seriesAdded(Series<X, Y> series, int seriesIndex) {
        for (int j = 0; j < series.getData().size(); j++) {
            Data<X, Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override
    protected void seriesRemoved(final Series<X, Y> series) {
        for (XYChart.Data<X, Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }


    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X, Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add(getStyleClass(item.getExtraValue()));

        return container;
    }

    @Override
    protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if (xa.isAutoRanging()) xData = new ArrayList<X>();
        if (ya.isAutoRanging()) yData = new ArrayList<Y>();
        if (xData != null || yData != null) {
            for (Series<X, Y> series : getData()) {
                for (Data<X, Y> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if (yData != null) {
                        yData.add(data.getYValue());
                    }
                }
            }
            if (xData != null) xa.invalidateRange(xData);
            if (yData != null) ya.invalidateRange(yData);
        }
    }
}
