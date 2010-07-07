package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import edu.cmu.ri.createlab.terk.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategyFinder;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class AccelerometerControlledOrb
   {
   private static final AccelerometerUnitConversionStrategy ACCELEROMETER_UNIT_CONVERTER = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.ACCELEROMETER_DEVICE_ID);

   public static void main(final String[] args) throws IOException
      {
      final Finch finch = new Finch();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("");
      System.out.println("Press ENTER to quit.");

      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         // use the native accelerometer values to set the color
         finch.setLED(ACCELEROMETER_UNIT_CONVERTER.convertToNative(finch.getXAcceleration()),
                      ACCELEROMETER_UNIT_CONVERTER.convertToNative(finch.getYAcceleration()),
                      ACCELEROMETER_UNIT_CONVERTER.convertToNative(finch.getZAcceleration()));
         }

      finch.quit();
      }

   private AccelerometerControlledOrb()
      {
      // private to prevent instantiation
      }
   }
