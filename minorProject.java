package eecs1021;
import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
public class minorProject {
    static final int D6 = 6; //Button
    static final int D4 = 4; //LED
    static final byte I2C0 = 0x3C; //OLEDDisplay
    static final int D2 = 2; //Motor
    static final int A0 = 15; //Moisture

    public static void main(String[]args) throws InterruptedException, IOException{
        // Define your USB Connection
        String myUSB = "COM3";
        // Create a FirmataDevice object with a USB connection.
        IODevice theArduinoObject = new FirmataDevice(myUSB);
        ArrayList<Long> moisture = new ArrayList<>();
        try{
            // Start up the FirmataDevice object.
            theArduinoObject.start();
            theArduinoObject.ensureInitializationIsDone();
            // Set up OLED Display
            I2CDevice I2cObject = theArduinoObject.getI2CDevice((byte) 0x3C);
            SSD1306 display = new SSD1306(I2cObject, SSD1306.Size.SSD1306_128_64);
            display.init();
            var buttonDevice = theArduinoObject.getPin(D6);
            buttonDevice.setMode(Pin.Mode.INPUT);
            var led = theArduinoObject.getPin(D4);
            led.setMode(Pin.Mode.OUTPUT);
            var motorDevice = theArduinoObject.getPin(D2);
            motorDevice.setMode(Pin.Mode.OUTPUT);
            var sensorDevice = theArduinoObject.getPin(A0);
            sensorDevice.setMode(Pin.Mode.ANALOG);
            var sidetask = new minorProjectTask(sensorDevice, buttonDevice, led, motorDevice, display, moisture);
            //Set up Timer
            Timer time = new Timer();
            time.schedule(sidetask, 0, 1000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }



    }
}
