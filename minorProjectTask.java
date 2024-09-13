package eecs1021;
import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
public class minorProjectTask extends TimerTask{
    private final Pin sensorDevice;
    private final Pin buttonDevice;
    private final Pin led;
    private final Pin motorDevice;
    private final SSD1306 display;
    private final ArrayList<Long> moisture;

    minorProjectTask(Pin sensorDevice, Pin buttonDevice, Pin led, Pin motorDevice, SSD1306 display, ArrayList<Long> moisture) {
        this.sensorDevice = sensorDevice;
        this.buttonDevice = buttonDevice;
        this.led = led;
        this.motorDevice = motorDevice;
        this.display = display;
        this.moisture = moisture;
    }
    @Override
    public void run() {
        try{
            //creating Graph
            XYSeriesCollection chartValue = new XYSeriesCollection();
            XYSeries newGraph = new XYSeries("Moisture");
            chartValue.addSeries(newGraph);

            JFreeChart finalChart = ChartFactory.createXYLineChart("Moisture vs Time (s)", "Time(s)","Moisture", chartValue);
            ChartFrame Frame = new ChartFrame("Graph", finalChart);

            Frame.pack();
            Frame.setVisible(true);

            while ((int)buttonDevice.getValue() != 1){
                int reallyDry = 700;
                int halfDry = 650;
                int wet = 600;
                int count = 0;
                int counter = count++;
                int voltage = (int) sensorDevice.getValue();
                this.display.getCanvas().clear(); // clears device values
                this.display.getCanvas().setTextsize(1);
                int buttonValue = (int) buttonDevice.getValue();
                System.out.println("The current Voltage is: " + voltage);
                long currentTime = System.currentTimeMillis();
                long moistness = sensorDevice.getValue();

                // fill empty graph with new values added to ArrayList
                newGraph.clear();
                for (int i = 0; i < moisture.size(); i++) {
                    newGraph.add(i, moisture.get(i));
                }

                if (voltage > reallyDry){ // if soil is too dry condition
                    System.out.println("The soil is very dry!");
                    System.out.println("Watering will begin");
                    display.getCanvas().setCursor(0,0);
                    display.getCanvas().drawString(0,0,"Soil is very dry"); //draws on Arduino display
                    display.getCanvas().drawString(0,20, "Recorded Moisture: \n" + voltage);
                    display.display();
                    motorDevice.setValue(1); //pump is turned on
                    led.setValue(1); //LED is turned on
                    moisture.add(moistness);
                }
                else if (voltage <= reallyDry && voltage > halfDry)  { //if soil is somewhat dry condition
                    System.out.println("The soil is half dry!");
                    System.out.println("Watering will begin");
                    display.getCanvas().setCursor(0,0);
                    display.getCanvas().drawString(0,25, "Recorded Moisture: \n" + voltage);
                    display.getCanvas().drawString(0,0,"Soil is half dry"); //draws on Arduino display
                    display.display();
                    motorDevice.setValue(1); //pump is turned on
                    led.setValue(1); //LED is turned on
                    moisture.add(moistness);

                }
                else{ // else
                    System.out.println("The soil is wet");
                    System.out.println("No water is needed");
                    display.getCanvas().setCursor(0,0);
                    display.getCanvas().drawString(0,25, "Recorded Moisture: \n"  + voltage);
                    display.getCanvas().drawString(0,0,"Soil is wet!"); //draws on Arduino display
                    display.display();
                    motorDevice.setValue(0); //pump is turned off
                    led.setValue(0); //LED is turned off
                    newGraph.clear();
                    moisture.add(moistness);
                }
            }

            //Button acts as emergency stop button
            System.out.println("Button has been pressed, all operations will stop");
            display.getCanvas().clear();
            display.getCanvas().drawString(0,0,"Button has been \npressed, all \noperations will \nstop."); //draws on Arduino display
            display.display();
            led.setValue(0);
            motorDevice.setValue(0);
            cancel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
