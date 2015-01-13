/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class DynamicDataDemo extends ApplicationFrame implements ActionListener {

    private TimeSeries series;
    final JFreeChart chart;
    final JTextField port, baud;

    private double lastValue = 100.0;

    public DynamicDataDemo(final String title) {

        super(title);
        this.series = new TimeSeries("Presion Atmosferica", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);

        final JPanel content = new JPanel(new BorderLayout());
        content.add(chartPanel);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 470));
        setContentPane(content);
        
        JPanel panel = new JPanel(new FlowLayout());
        
        port = new JTextField(10);
        port.setText("COM2");
        
        baud = new JTextField(10);
        baud.setText("10417");
        
        panel.add(new JLabel("Puerto Serial: "));
        panel.add(port);
        panel.add(new JLabel("Baud Rate: "));
        panel.add(baud);
        
        final JButton button = new JButton("Start");
        button.setActionCommand("START");
        button.addActionListener(this);
        panel.add(button);

        this.add(panel, BorderLayout.NORTH);
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Dynamic Data Demo", 
            "Tiempo (seg)", 
            "Presion (kPa)",
            dataset, 
            true, 
            true, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(4000.0);  // 60 seconds
        //axis.set
        axis = plot.getRangeAxis();
        axis.setRange(50.0, 150.0); 
        return result;
    }
    
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("START")) {
            (new PuertoSerie(this, port.getText(), baud.getText())).start();
        }
    }

    public void update(double value){
            final Millisecond now = new Millisecond();
            System.out.println("Now = " + now.toString() + ", Value = "+value);
            this.series.add(new Millisecond(), value);
    }
    
    public void setTitulo(String titulo){
        chart.setTitle(titulo);
    }
    
    public static void main(final String[] args) {

        final DynamicDataDemo demo = new DynamicDataDemo("Presion Barometrica");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
