package edu.cmu.ri.createlab.terk.robot.finch.applications;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/*
 * VideoTracker.java - Tracks an object using the camera.  The program exits when the
 * object is moved completely off-screen.
 */

public class VideoTracker
   {

   public static void main(String[] args)
      {
      // Instantiating the Finch object
      Finch myFinch = new Finch();
      System.out.println("Finch connecting");

      // Initializing the video
      myFinch.initVideo();
      System.out.println("Init video");

      // Showing the video screen
      myFinch.showVideoScreen("Look mom, I'm on TV!");
      System.out.println("Video drawing");

      // Calibrating to an object to track later
      System.out.println("Put something in the center of the Finch video for Finch to track");
      int[] calibrationVals = myFinch.blobCalibration();

      // Printing out the RGB calibration values
      System.out.println("Calibration R" + calibrationVals[0] + " G" + calibrationVals[1] + " B" + calibrationVals[2]);

      // Array to store the center and edges of the detected blob
      int[] blobCornersAndCenter;

      // Loop through this unless your left light sensor is less than 50.
      while (myFinch.getLeftLightSensor() > 50)
         {
         // Update the video window and image data
         myFinch.updateVideoScreen();

         // Get the center and edges of the blob
         blobCornersAndCenter = myFinch.blobDetector(calibrationVals, 15);

         // If a blob was detected, draw a rectangle where it was detected and print out its coordinates
         if (blobCornersAndCenter != null)
            {
            System.out.println("Center: " + blobCornersAndCenter[0] + "," + blobCornersAndCenter[1] + " Min x,y: " + blobCornersAndCenter[2] + "," + blobCornersAndCenter[4] + " Max x,y " + blobCornersAndCenter[3] + "," + blobCornersAndCenter[5]);
            myFinch.drawRectangle(blobCornersAndCenter[2], blobCornersAndCenter[4], blobCornersAndCenter[3], blobCornersAndCenter[5]);
            // myFinch.setPolygonColor(myFinch.getAreaColor(blobCornersAndCenter[2], blobCornersAndCenter[4], blobCornersAndCenter[3], blobCornersAndCenter[5]));
            }

         // If no blob was detected break out of the loop
         else
            {
            myFinch.drawNothing();
            break;
            }
         }
      // Close the video window and disconnect from the Finch
      myFinch.closeVideoScreen();
      myFinch.quit();
      System.exit(0);
      }
   }

