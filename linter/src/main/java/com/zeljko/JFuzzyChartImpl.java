package com.zeljko;

import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.defuzzifier.Defuzzifier;
import net.sourceforge.jFuzzyLogic.defuzzifier.DefuzzifierContinuous;
import net.sourceforge.jFuzzyLogic.defuzzifier.DefuzzifierDiscrete;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class JFuzzyChartImpl {

    public JFreeChart chart(Variable var, Defuzzifier defuzzifier) {
        String title = String.format("%s:%2.2f (%s)", var.getName(), var.getLatestDefuzzifiedValue(), defuzzifier.getName());
        JFreeChart chart;
        if (defuzzifier.isDiscrete()) {
            chart = this.chartDefuzzifierDiscrete((DefuzzifierDiscrete) defuzzifier, title);
        } else {
            chart = this.chartDefuzzifierContinuous((DefuzzifierContinuous) defuzzifier, title);
        }

        return chart;
    }


    private JFreeChart chartDefuzzifierContinuous(DefuzzifierContinuous def, String title) {
        if (title == null) {
            title = def.getName();
        }

        double min = def.getMin();
        double max = def.getMax();
        if (!Double.isNaN(min) && !Double.isInfinite(max)) {
            XYSeries series = new XYSeries(title);
            double[] values = def.getValues();
            int numberOfPoints = values.length;
            double xx = min;
            double step = (max - min) / (double) numberOfPoints;

            for (int i = 0; i < numberOfPoints; xx += step) {
                series.add(xx, values[i]);
                ++i;
            }

            XYDataset xyDataset = new XYSeriesCollection(series);
            return ChartFactory.createXYAreaChart(title, "complexityRisk", "Membership", xyDataset, PlotOrientation.VERTICAL, true, true, true);
        } else {
            Gpr.debug("Limits not calculated yet: [" + min + ", " + max + "]");
            return null;
        }
    }

    private JFreeChart chartDefuzzifierDiscrete(DefuzzifierDiscrete def, String title) {
        if (title == null) {
            title = def.getName();
        }

        XYSeries series = new XYSeries(title);

        for (Double x : def) {
            double xx = x;
            double yy = def.getDiscreteValue(xx);
            series.add(xx, yy);
        }

        XYDataset xyDataset = new XYSeriesCollection(series);
        return ChartFactory.createScatterPlot(title, "complexityRisk", "Membership", xyDataset, PlotOrientation.VERTICAL, true, true, true);
    }
}
