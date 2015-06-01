package edu.cmu.ri.createlab.terk.robot.finch.applications;

import edu.cmu.ri.createlab.rss.readers.EarthquakeReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * Created by: Tom Lauwers
 * Date: 2/16/2009
 * Have the Finch respond to the most recent earthquake that occured in the world.  Most 
 * eartquakes have magnitudes of 5 to 6, and usually there's a new one about once/hour.
 */

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class EarthquakeFinch
   {
   public static void main(final String[] args)
      {
      // Instantiating the Finch object
      final Finch myFinch = new Finch();

      // Instantiating the earthquake reader
      final EarthquakeReader reader = new EarthquakeReader();

      // Reading in the magnitude of the most recent earthquake
      final int magnitude = (int)reader.getMagnitude();

      // Have the Finch say what the most recent earthquake was, print it out so you can also read it
      myFinch.saySomething("An earthquake of magnitude " + magnitude + " just struck " + reader.getLocation());
      System.out.println("An earthquake of magnitude " + magnitude + " just struck " + reader.getLocation());
      // set the LED so that lower magnitudes tend towards green and higher ones towards red
      myFinch.setLED((magnitude - 4) * 50, 250 - (magnitude - 4) * 50, 0);

      // have the Finch move back and forth at a speed based on the magnitude
      for (int i = 0; i < 6; i++)
         {
         myFinch.setWheelVelocities(magnitude * 3, magnitude * 3);
         myFinch.sleep(700);

         myFinch.setWheelVelocities(-magnitude * 3, -magnitude * 3);
         myFinch.sleep(700);
         }

      // Always end your program with finch.quit()
      myFinch.quit();
      System.exit(0);
      }

   private EarthquakeFinch()
      {
      // private to prevent instantiation
      }
   }

