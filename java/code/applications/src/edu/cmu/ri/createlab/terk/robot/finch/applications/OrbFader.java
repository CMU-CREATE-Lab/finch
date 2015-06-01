package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class OrbFader
   {
   private static final int COLOR_CHANGE_STEP_SIZE = 5;

   public static void main(final String[] args) throws IOException
      {
      final Finch finch = new Finch();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      final Color[] colors = new Color[]{
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(0, 0, 255),
            new Color(255, 0, 255)
      };
      Color currentColor = colors[0];
      finch.setLED(currentColor);  // this isn't really necessary, but causes the service to be preloaded

      System.out.println("");
      System.out.println("Press ENTER to quit.");
      int targetColorIndex = 1;
      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         // see if we've reached the target color and need to update our starting and ending colors
         if (currentColor.equals(colors[targetColorIndex]))
            {
            targetColorIndex += 1;
            if (targetColorIndex >= colors.length)
               {
               targetColorIndex = 0;
               }
            }

         // compute the deltas for the color components
         final int rDelta = computeDelta(currentColor.getRed(), colors[targetColorIndex].getRed());
         final int gDelta = computeDelta(currentColor.getGreen(), colors[targetColorIndex].getGreen());
         final int bDelta = computeDelta(currentColor.getBlue(), colors[targetColorIndex].getBlue());

         // create the new color
         currentColor = new Color(currentColor.getRed() + rDelta,
                                  currentColor.getGreen() + gDelta,
                                  currentColor.getBlue() + bDelta);

         // set the color
         finch.setLED(currentColor);
         }

      finch.quit();
      }

   private static int computeDelta(final int currentValue, final int targetValue)
      {
      if (currentValue > targetValue)
         {
         return -COLOR_CHANGE_STEP_SIZE;
         }
      else if (currentValue < targetValue)
         {
         return COLOR_CHANGE_STEP_SIZE;
         }
      return 0;
      }

   private OrbFader()
      {
      // private to prevent instantiation
      }
   }
