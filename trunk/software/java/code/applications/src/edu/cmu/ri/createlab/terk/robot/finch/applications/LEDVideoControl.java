package edu.cmu.ri.createlab.terk.robot.finch.applications;

/*
 * LEDVideoControl.java - A program to control the Finch beak LED with camera.  The LED color is
 * simply set to the color of the central area of the image.
 * 
 * Author:  Tom Lauwers
 */

import java.awt.Color;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

public class LEDVideoControl
   {
   public static void main(final String[] args)
      {
      // Instantiating the Finch object
      final Finch myFinch = new Finch();

      // Initializing the video stream
      myFinch.initVideo();

      // Showing the camera image
      myFinch.showVideoScreen("Look mom, I'm on TV!");

      // Continue running this program so long as the light sensor is more than 70
      while (myFinch.getLeftLightSensor() > 70)
         {
         // Update the video window and image data with the most recent camera image
         myFinch.updateVideoScreen();

         // Draw a rectangle in the area to be used to set LED color
         myFinch.drawRectangle(myFinch.getImageWidth() / 2 - 40, myFinch.getImageHeight() / 2 - 40, myFinch.getImageWidth() / 2 + 40, myFinch.getImageHeight() / 2 + 40);
         myFinch.setPolygonColor(Color.RED);

         // Get the average color in the center area
         final Color areaColor = myFinch.getAreaColor(myFinch.getImageWidth() / 2 - 40, myFinch.getImageHeight() / 2 - 40, myFinch.getImageWidth() / 2 + 40, myFinch.getImageHeight() / 2 + 40);

         // Set the LED to that color
         myFinch.setLED(areaColor);
         }
      // Close the video screen and disconnect from the Finch
      myFinch.closeVideoScreen();
      myFinch.quit();
      System.exit(0);
      }

   private LEDVideoControl()
      {
      // private to prevent instantiation
      }
   }
