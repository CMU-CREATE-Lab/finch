package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategyFinder;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class AccelerometerControlledOrb
   {
   public static void main(final String[] args) throws IOException
      {
      final Finch finch = new Finch();
      final AccelerometerUnitConversionStrategy accelerometerUnitConverter = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(finch.getFinchProperties().getAccelerometerDeviceId());

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
         finch.setLED(accelerometerUnitConverter.convertToNative(finch.getXAcceleration()),
                      accelerometerUnitConverter.convertToNative(finch.getYAcceleration()),
                      accelerometerUnitConverter.convertToNative(finch.getZAcceleration()));
         }

      finch.quit();
      }

   private AccelerometerControlledOrb()
      {
      // private to prevent instantiation
      }
   }
