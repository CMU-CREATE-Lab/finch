package edu.cmu.ri.createlab.video;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 */
public final class VideoHelper
   {
   private VideoPlayer video;

   private boolean videoOn = false;
   private boolean videoScreenOn = false;

   /** Initializes and starts a new video stream, which can be used to track objects, react
    * to colors placed in the field of view of the camera, and to bring up a window of what
    * the camera is seeing.  Note that this does NOT automatically bring up the window showing
    * the camera image - call showVideoScreen() to show that.
    */
   public void initVideo()
   {
   video = new VideoPlayer();
   video.startVideoStream();
   videoOn = true;
   }

   /** Closes the video stream. */
   public void closeVideo()
   {
   if (videoOn)
      {
      video.stopVideoStream();
      video.closeVideoStream();
      videoOn = false;
      }
   }

   /**
    * Returns as a BufferedImage object the most recent image retrieved from the camera
    * @return The image data
    */
   public BufferedImage getImage()
   {
   return video.getImage();
   }

   /**
    * Get the image height
    * @return image height as an int
    */
   public int getImageHeight()
   {
   return video.getImageHeight();
   }

   /**
    * Get the image width
    * @return image width as an int
    */
   public int getImageWidth()
   {
   return video.getImageWidth();
   }

   /**
    * Gets the Red, Green, and Blue values of the pixel at the coordinate specified by x,y
    * @param x The row of the pixel
    * @param y The column of the pixel
    * @return An 3-int array of the red, green, and blue values of the pixel.  Values are 0 to 255 and
    * represent the intensity of color.
    */
   public int[] getPixelRGBValues(final int x, final int y)
   {
   return video.getPixelRGBValues(x, y);
   }

   /**
    * Gets the color of a given pixel as a Java Color object at the coordinate specified by x,y
    * @param x The row of the pixel
    * @param y The column of the pixel
    * @return A Color object representing the color of the pixel
    */
   public Color getPixelColor(final int x, final int y)
   {
   return video.getPixelColor(x, y);
   }

   /**
    * Gets the AVERAGE RGB values of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average values in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a 3 element array holding the red, green, and blue intensities of the area
    */
   public int[] getAreaRGBValues(final int minX, final int minY, final int maxX, final int maxY)
   {
   return video.getAreaRGBValues(minX, minY, maxX, maxY);
   }

   /**
    * Gets the AVERAGE Color value of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average color in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a Color object holding the average color of the area
    */

   public Color getAreaColor(final int minX, final int minY, final int maxX, final int maxY)
   {
   return video.getAreaColor(minX, minY, maxX, maxY);
   }

   /**
    * Method for getting back calibration values for the blob detector method.
    * Draws a rectangle on the screen and holds it there for five seconds.  To calibrate on an
    * object, make sure that it is entirely within the rectangle.  Calibration occurs at
    * the end of the method, so it is only necessary to have the object positioned properly
    * at the end of the five seconds.
    *
    * @return a 3 element array of red, green, and blue color values of the blob to be tracked
    */
   public int[] blobCalibration()
   {
   return video.blobCalibration();
   }

   /**
    * The blob detector detects all of the pixels that are within a certain range of the CalibrationVals,
    * where the width of the range is determined by the value sensitivity.  What the algorithm does is:
    * 1.  For every pixel, it compares the RGB values of that pixel to the calibration values; if the pixel's
    * R, G, AND B values are within the calibration values +/- the sensitivity, then the pixel is counted.
    * 2.  Take the average of all the counted pixels' coordinates to get the center of the blob.
    * 3.  Finds the edges of the blob by traversing the rows and columns of the image and setting an edge
    * when 1/10 of the total counted pixels have been seen.  Traversal is from top to bottom and left to right
    * to find the top and left edges respectively, and from bottom to top and right to left to find the bottom
    * and right edges.
    * The detector returns an array of six ints - elements 0 and 1 are the x,y coordinates of the center of the
    * blob, elements 2 and 3 are the minimum and maximum x coordinates, while elements 4 and 5 are the min and
    * max y coordinates.
    *
    * @param calibrationVals  An array containing the RGB values of the pixel to look for
    * @param sensitivity  The sensitivity of the detector - higher values lead to more noise, while low values
    * might not pick up very much of the object being tracked.  A suggested value for a brightly colored object is 10.
    * @return An array containing the center, top left, and bottom right x,y coordinates of the blob.
    */
   public int[] blobDetector(final int[] calibrationVals, final int sensitivity)
   {
   return video.blobDetector(calibrationVals, sensitivity);
   }

   /**
    * Displays a window that shows the camera image.  Note that the image must be updated
    * through program calls to the method updateVideoScreen().
    * @param name the name to give the window
    */
   public void showVideoScreen(final String name)
   {
   video.drawVideo(name);
   videoScreenOn = true;
   }

   /**
    * Updates the image in the video window.  Note that this method also updates the image data
    * in the same way that getImage() does, so it is not necessary to call both getImage and updateVideoScreen.
    * Rather, call getImage() if your program does not display a video window, and use updateVideoScreen() if
    * it does display a window.
    */
   public void updateVideoScreen()
   {
   video.updateVideo();
   }

   /**
    * Closes the video window
    */
   public void closeVideoScreen()
   {
   if (videoScreenOn)
      {
      video.closeVideo();
      videoScreenOn = false;
      }
   }

   /**
    * Draws a rectangle in the video window.  Useful for displaying where the
    * blob tracker thinks a blob is located.
    * Once called, the rectangle will be persistent across all calls
    * of updateVideoScreen.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param minX minimum X coordinate of rectangle
    * @param minY maximum X coordinate of rectangle
    * @param maxX minimum Y coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    */
   public void drawRectangle(final int minX, final int minY, final int maxX, final int maxY)
   {
   video.drawRectangle(minX, minY, maxX, maxY);
   }

   /**
    * Draws a circle on the camera image. Useful for displaying where the
    * blob tracker thinks a blob is located.
    * Once called, the circle will be persistent across all calls
    * of updateVideoScreen.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param radius The radius of the circle in pixels
    * @param centerX The X coordinate of the center of the circle
    * @param centerY The Y coordinate of the center of the circle
    */
   public void drawCircle(final int radius, final int centerX, final int centerY)
   {
   video.drawCircle(radius, centerX, centerY);
   }

   /**
    * Call this if you want to no longer display a polygon on the
    * camera image.
    */
   public void drawNothing()
   {
   video.drawNothing();
   }

   /**
    * Sets the color of any polygon, rectangle, or circle drawn into
    * the image.
    * @param polyColor The color to set the polygon to.
    */
   public void setPolygonColor(final Color polyColor)
   {
   video.setPolygonColor(polyColor);
   }

   /**
    * Sets whether the polygon is filled in or an outline.
    * @param setting true sets the polygon to be filled in, false sets it to outline
    */
   public void setFillPolygon(final boolean setting)
   {
   video.setFillPolygon(setting);
   }

   /**
    * Draws a generic polygon into the image.  Note
    * that once called, the polygon will be persistent across all calls
    * of updateVideo.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param poly The polygon object to draw into the image
    */
   public void drawPolygon(final Polygon poly)
   {
   video.drawPolygon(poly);
   }
   }
