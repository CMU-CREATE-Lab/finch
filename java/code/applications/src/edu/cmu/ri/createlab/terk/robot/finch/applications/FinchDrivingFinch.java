package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * Connects to two Finches, and allows you to drive one around by holding the other and tilting it forward, back, left,
 * and right.  The temperature sensed by the controlling Finch is displayed with the full-color LED in the controlled
 * Finch.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class FinchDrivingFinch
   {
   private static final int VELOCITY = 255;
   private static final int HALF_VELOCITY = VELOCITY / 2;

   public static void main(final String[] args) throws IOException
      {
      new FinchDrivingFinch();
      }

   private double max = -20;
   private double min = 80;

   private FinchDrivingFinch() throws IOException
      {
      final Finch finch1 = new Finch();
      final Finch finch2 = new Finch();

      finch1.setLED(Color.GREEN);
      finch2.setLED(Color.BLUE);

      System.out.println("");
      System.out.println("Press ENTER to quit.");
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         if (finch1.isFinchLevel())
            {
            finch2.setWheelVelocities(VELOCITY, VELOCITY);
            }
         else if (finch1.isBeakUp())
            {
            finch2.stopWheels();
            }
         else if (finch1.isFinchUpsideDown())
            {
            finch2.setWheelVelocities(-VELOCITY, -VELOCITY);
            }
         else if (finch1.isRightWingDown())
            {
            finch2.setWheelVelocities(VELOCITY, HALF_VELOCITY);
            }
         else if (finch1.isLeftWingDown())
            {
            finch2.setWheelVelocities(HALF_VELOCITY, VELOCITY);
            }

         updateTemperature(finch1, finch2);
         }

      finch1.quit();
      finch2.quit();
      }

   private void updateTemperature(final Finch finch1, final Finch finch2)
      {
      //read temperature from finch
      final double temperature = finch1.getTemperature();

      //re-establish observed range
      //with new extremes the finch will
      //calibrate itself to the expected temperatures
      //for more dramatic effect
      if (temperature > max)
         {
         max = temperature;
         }
      if (temperature < min)
         {
         min = temperature;
         }

      final double scale = 255.0 / (max - min);

      //set red and blue levels for orb
      final int red = (int)((temperature - min) * scale);
      final int blue = 255 - red;
      final Color currentColor = new Color(red, 0, blue);

      // set the color
      finch2.setLED(currentColor);
      }
   }
