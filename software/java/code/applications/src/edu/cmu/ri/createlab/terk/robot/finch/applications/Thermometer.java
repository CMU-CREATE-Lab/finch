package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * @author Alex Styler (astyler@gmail.com)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class Thermometer
   {
   public static void main(final String[] args) throws IOException
      {
      final Finch finch = new Finch();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("");
      System.out.println("Press ENTER to quit.");
      double max = -20;
      double min = 80;
      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         //read temperature from finch
         final double temperature = finch.getTemperature();
         System.out.println("Temp: " + temperature);

         //re-establish observed range
         //with new extremes the finch will
         //calibrate itself to the expected temperatures
         //for more dramatic effect
         if (temperature > max)
            {
            max = temperature;
            System.out.println("New max: " + max);
            }
         if (temperature < min)
            {
            min = temperature;
            System.out.println("New min:" + min);
            }

         final double scale = 255.0 / (max - min);

         //set red and blue levels for orb
         final int red = (int)((temperature - min) * scale);
         final int blue = 255 - red;
         final Color currentColor = new Color(red, 0, blue);

         // set the color
         finch.setLED(currentColor);
         }

      finch.quit();
      }

   private Thermometer()
      {
      // private to prevent instantiation
      }
   }
