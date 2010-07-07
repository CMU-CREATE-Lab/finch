package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * @author Erik Pasternak (epastern@andrew.cmu.edu)
 *
 * Created using work by Chris Bartley as a starting point. This module is
 * designed to read in an RSS feed for weather conditions and changes the LED
 * output depending on the temperature. Later versions might take cloud cover
 * into consideration or shake to represent storms in the area. Also, weather
 * is boring and doesn't change very fast, so instead you can change what city
 * you're looking at.
 */
public class WeatherControlledOrb
   {
   public static void main(final String[] args) throws IOException
      {
      //First we initialize a new Finch, this finds a finch and connects to it.
      //It currently hangs indefinitely if there is no Finch to connect to.
      final Finch finch = new Finch();
      //We'll also want some input from the keyboard, so start a reader for it
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      //This will be used to store the zip code the user enters
      String zipCode;
      //This is to keep track of whether the zipCode is valid or not.
      boolean isValid;
      //Will store the temp in Fahrenheit
      int degrees;
      //These are used to threshhold the red and blue LEDs at 25 and 75.
      int redOn;
      int blueOn;
      int rThresh = 25;
      int bThresh = 75;

      while (true)
         {
         //First we want to get a zip code from the user
         do
            {
            isValid = true;
            System.out.println("\nEnter your zipcode and press Enter: ");
            zipCode = in.readLine();
            //remove any whitespace around it
            zipCode = zipCode.trim();
            //Copy it to an array so the individual elements can be checked easily
            char[] zipArray = zipCode.toCharArray();
            //Make sure it's a standard 5 length zipcode
            if (zipArray.length != 5)
               {
               isValid = false;
               System.out.println("Not a valid zip code length, please try again.");
               }
            else
               {
               //if it's 5 long make sure they're all numbers
               for (int i = 0; i < 5; i++)
                  {
                  if (zipArray[i] < '0' || zipArray[i] > '9')
                     {
                     isValid = false;
                     }
                  }
               if (!isValid)
                  {
                  System.out.println("Zipcode must contain only numbers, please try again.");
                  }
               }
            }
         while (!isValid);
         //Get the temp. This will contact the page and retrieve the temp from it.
         degrees = WCO_GetTemp(zipCode);

         System.out.println("");
         System.out.println("Press ENTER to quit.");
         //Trim the temperature. This won't work everywhere, but should be fine in Pitt.
         if (degrees < 0)
            {
            degrees = 0;
            }
         //The LEDs are indistinguishably bright over a certain amount, so I'm only using
         //up to 100 instead of 255. Also, they don't blend so nicely without the case, so
         //I reduced the overlap range between the two.
         redOn = 1;
         blueOn = 1;
         //These threshholds will turn red and blue off if it's close to the two extremes.
         if (degrees < rThresh)
            {
            redOn = 0;
            }
         if (degrees > bThresh)
            {
            blueOn = 0;
            }
         //set the leds with the scaled readings
         finch.setLED((degrees - 20) * redOn, 0, (80 - degrees) * blueOn);
         //See what the user wants to do
         System.out.println("Press 'c' to enter a new zip code, any other key to quit.");
         if (in.read() != 'c')
            {
            break;
            }
         }
      //Close the connection to the Finch, if you don't do this it takes 30 seconds to
      //timeout.
      finch.quit();
      }

   /*This function takes in a 5 digit zipCode in a string, retrieves the appropriate page
    * from weather.com, and parses it to find the current temperature, which it returns as
    * an integer. JGet from http://www.devdaily.com/blog/post/java/jget-something-like-wget/
    * referenced for parts of this function.
    */

   static int WCO_GetTemp(String zipCode)
      {
      String degreesStr;
      URL u;
      java.io.InputStream weather = null;
      BufferedReader weatherRead;
      //These two are parts of the link for the weather rss feed. The zip code goes
      //between these two pieces for the complete address
      String headURL = "http://rss.weather.com/weather/rss/local/";
      String tailURL = "?cm_ven=LWO&cm_cat=rss&par=LWO_rss";
      //This is for the completed URL
      String url;
      int degrees = -200;
      //Looks for a number with or without a minus sign followed by ' &deg;'
      //This is what the string looks like in the xml for the temperature.
      String regex = "-?\\d+ &deg;";
      // Compiles regular expression into Pattern. Java uses patterns to search
      //strings.
      Pattern p = Pattern.compile(regex);
      try
         {
         //complete the URL
         url = headURL + zipCode + tailURL;
         //Create a URL object that we can read.
         u = new URL(url);
         //Open an input stream from that URL to get access to the page
         weather = u.openStream();
         //And turn it into a buffered reader so we can use readline. There is
         //very likely a more elegant way of reading RSS feeds in java, but this
         //was nice and simple to impliment. I should go back and update it later.
         weatherRead = new BufferedReader(new InputStreamReader(weather));
         while (true)
            {
            //Get the current line from the page, this is reading in the source code
            String currLine = weatherRead.readLine();
            //If it's empty then we're done.
            if (currLine == null)
               {
               break;
               }
            //Check if the temperature is in this line
            Matcher m = p.matcher(currLine);
            //If it is...
            if (m.find())
               {
               //Get the bit with the temperature and '&deg;'
               degreesStr = m.group();
               //Print it for visual feedback
               System.out.println(degreesStr);
               //Split it and only keep the number part
               degreesStr = degreesStr.split(" ")[0];
               //Parse it into an integer
               degrees = Integer.parseInt(degreesStr);
               //We don't need to search the rest of the file
               break;
               }
            }
         }
      //Some standard catches for the types of exceptions reading the URL can throw
      catch (MalformedURLException mue)
         {
         System.err.println("Ouch - a MalformedURLException happened.");
         mue.printStackTrace();
         System.exit(2);
         }
      catch (IOException ioe)
         {
         System.err.println("Oops- an IOException happened.");
         ioe.printStackTrace();
         System.exit(3);
         }
      //try to close the weather reader. We don't really care if this fails
      //since it will dissapear when the program quits anyway
      finally
         {
         try
            {
            weather.close();
            }
         catch (IOException ioe)
            {
            }
         }
      //Return the temp that we found
      return degrees;
      }
   }
