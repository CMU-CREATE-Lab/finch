package edu.cmu.ri.createlab.terk.robot.finch.applications;

/*
 * VideoTester.java - a simple example using video.  The program starts up the video
 * window, and then in a loop it updates the image in the display, and gets the average
 * color of the center rectangle of the image.  It draws a rectangle in the center of the
 * image and sets it to the average color of that area.
 * 
 * Author:  Tom Lauwers 
 */

import java.awt.Color;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

public class VideoTester
   {

   public static void main(String[] args)
      {

      // Instantiating the Finch object
      Finch myFinch = new Finch();
      System.out.println("Finch connecting");

      // Initializing the video
      myFinch.initVideo();
      System.out.println("Init video");

      // Display the video screen
      myFinch.showVideoScreen("I'm on TV");
      System.out.println("Video drawing");

      // Start by drawing a circle in the center of the image
      myFinch.setPolygonColor(Color.MAGENTA);
      myFinch.drawCircle(30, 160, 120);

      // Holds the average color of the center of the screen
      Color areaColor;

      // Continue doing this as long as the left light sensor is above 80
      while (myFinch.getLeftLightSensor() > 80)
         {

         // Update the video screen with the most recent image
         myFinch.updateVideoScreen();

         // Get the average color value of the rectangle bounded by (100,70) and (220, 170)
         areaColor = myFinch.getAreaColor(myFinch.getImageWidth() / 2 - 20, myFinch.getImageHeight() / 2 - 20, myFinch.getImageWidth() / 2 + 20, myFinch.getImageHeight() / 2 + 20);

         // Print out the color
         System.out.println("Color is " + areaColor);

         // If the right light sensor is greater than 180, draw a circle
         if (myFinch.getRightLightSensor() > 180)
            {
            myFinch.drawCircle(50, 160, 120);
            // Set the circle color to Magenta
            myFinch.setPolygonColor(Color.MAGENTA);
            // Set the circle to be an outline
            myFinch.setFillPolygon(false);
            }

         // Else draw a filled in rectangle and set its color to the average color of the
         // image in the rectangle.
         else
            {
            myFinch.drawRectangle(myFinch.getImageWidth() - 50, myFinch.getImageHeight() - 80, myFinch.getImageWidth() - 1, myFinch.getImageHeight() - 1);
            myFinch.setPolygonColor(areaColor);
            myFinch.setFillPolygon(true);
            }
         }
      // Close the video screen and disconnect from the Finch
      myFinch.closeVideoScreen();
      myFinch.quit();
      System.exit(0);
      }
   }
