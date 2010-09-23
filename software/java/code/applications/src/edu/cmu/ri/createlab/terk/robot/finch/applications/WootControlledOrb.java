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
 * Written by Erik Pasternak. 2.18.09
 *
 * This program is designed as a woot-off tracker to let you know how much of an
 * item is left and to alert you to new items on woot.com during a woot-off. The
 * nose of the Finch will transition from green to red as the current item sells
 * out and when it is sold out the nose will turn blue. When a new item is
 * posted the Finch will beep and your computer will read the name of the new
 * item to you.
 */

//We start with a class. Everything contained in here describes an object in
//java.
public class WootControlledOrb
   {
   //This object has a String and a boolean item.
   String item;
   boolean newItem;
   //This is the initialization function. Whenever you create a new object of
   //this type you call this function.

   public WootControlledOrb()
      {
      //This initialization function sets the String to an empty string and
      //whether a new item is up to false. The 'this' keyword tells it the
      //variables are part of this object. It can be left off if no other
      //variables in the method have the same name (it could just be 'item').
      this.item = "";
      this.newItem = false;
      }

   /**
    * This function contacts bagsofcrap.com, a woot tracking website, and gets
    * the current item and amount left. If the current item is different than
    * the previous current item it will also set a flag in the object that a new
    * item has been posted.
    */
   int WCO_GetPercent()
   {
   //Here we store the string that contains the percent
   String percentStr;
   //And the url object that we'll contact for the information
   URL u;
   //This sets up an input stream and reader so we can read through the web
   //page once we've downloaded it
   java.io.InputStream woot = null;
   BufferedReader wootRead;
   //The webpage we want to contact
   String url = "http://www.bagsofcrap.com/woot_simple.htm";
   //Again, we're storing how much of the item is left.
   int percent = -1;
   //Looks for a number that may be followed by a decimal point and another
   //number. The regular expression (regex) we want to search for is
   // '\d+\.?\d?% Left', which means:
   // '\d+' 1 or more numbers
   // '\.?' one or no dot characters, since . is a special char we have to use \.
   // '\d?' one or no numbers
   // '% Left' the actual string, none of those are special characters
   //
   // However, since \ is a special character in java strings we have to use
   // '\\' to tell it that it's just the \ character and not something special
   String regex = "\\d+\\.?\\d?% Left";
   //This time we're looking for '<h1>' followed by any number of anything
   //and ending with '</h1>'
   String itemRegex = "<h1>.*</h1>";
   // Compiles regular expression into Pattern. Java uses patterns to search
   //strings.
   Pattern p = Pattern.compile(regex);
   Pattern i = Pattern.compile(itemRegex);
   try
      {
      //Flags to see if we've found the item name and it's percent left yet
      boolean itemFound = false;
      boolean percentFound = false;

      //Create a URL object that we can read.
      u = new URL(url);

      //Open an input stream from that URL to get access to the page
      woot = u.openStream();

      //And turn it into a buffered reader so we can use readline. There may
      //be more elegant ways of reading web pages in java, but this
      //is nice and simple to implement.
      wootRead = new BufferedReader(new InputStreamReader(woot));
      //While we haven't found both the item and the percent
      while (!(itemFound && percentFound))
         {
         //Get the current line from the page, this is reading in the source code
         String currLine = wootRead.readLine();
         //If it's null then we're out of lines to read and should give up
         if (currLine == null)
            {
            break;
            }
         //run the regular expression matchers we made on this line
         //These create matcher objects
         Matcher m = p.matcher(currLine);
         Matcher itemMatcher = i.matcher(currLine);
         //If we haven't already found the item and the item was listed on
         //this line
         if (!itemFound && itemMatcher.find())
            {
            //Grab the string that matched, which still has <h1> and </h1>
            String tempItem = itemMatcher.group();
            //Strip off the <h1> and </h1> tags
            tempItem = tempItem.substring(4, tempItem.length() - 5);

            //If this is a different item than the one we already have listed
            if (!(tempItem.equals(this.item)))
               {
               //Set the flag for a new item and set this as the current item
               this.newItem = true;
               this.item = tempItem;
               }
            //Print it out as feedback
            System.out.println(this.item);
            //Set the flag so we stop looking for it
            itemFound = true;
            }

         //If we haven't found the percent yet and it's in this line
         if (!percentFound && m.find())
            {
            //Get the correct bit. This shows up like "65.8% Left"
            percentStr = m.group();
            //Print it for visual feedback
            System.out.println(percentStr);
            //Split it at the % so we can get just the number part
            //The [0] says to only grab the first element returned, which
            //will be the number
            percentStr = percentStr.split("%")[0];
            //Parse it into a double and then convert into an integer, which
            //automatically rounds it.
            percent = (int)Double.parseDouble(percentStr);
            //Set the flag so we don't keep looking for this
            percentFound = true;
            }
         }
      }
   //Some standard catches for the types of exceptions reading the URL can throw
   catch (MalformedURLException mue)
      {
      //This happens if it doesn't know how to read the url we gave it
      System.err.println("Ouch - a MalformedURLException happened.");
      mue.printStackTrace();
      System.exit(2);
      }
   catch (IOException ioe)
      {
      //this happens if it couldn't read from the input stream we created
      System.err.println("Oops- an IOException happened.");
      ioe.printStackTrace();
      System.exit(3);
      }
   //Now that we're done try to close it.
   finally
      {
      try
         {
         woot.close();
         }
      catch (IOException ioe)
         {
         System.err.println("Error on closing the reader");
         }
      }
   //Return the temp that we found
   return percent;
   }

   //This is our main function. It runs the majority of the code and uses the
   //WootControlledOrb object the rest of this file defines.

   public static void main(final String[] args) throws IOException
      {
      //Let's start by declaring variables and initializing some things

      //First we initialize a new Finch, this finds a finch and connects to it.
      //It currently hangs indefinitely if there is no Finch to connect to.
      final Finch finch = new Finch();

      //We'll also want some input from the keyboard, so start a reader for it
      //System.in is defined by java as the standard input
      //Because of the way the code has developed over the years we wrap the
      //standard input in an InputStreamReader, which then gets passed to a
      //BufferedReader that we can use.
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      //This stores how much of the current item is left. -1 is a flag we're
      //using to say the item is sold out.
      int percentLeft = -1;

      //These are used to threshhold the red and green LEDs at 10% and 90%.
      int redOn;
      int greenOn;
      //Here is where we set those threshholds
      int rThresh = 10;
      int gThresh = 90;
      //Finally, we create a WootControlledOrb object. This object has access
      //to all the variables and functions defined as part of the class. This
      //also runs the initialization function earlier in the code.
      WootControlledOrb wco = new WootControlledOrb();

      while (true)
         {
         //Get the current percent left. This calls the WCO_GetPercent()
         //method which is part of the WeatherControlledOrb object.
         //This will contact http://www.bagsofcrap.com/woot_simple.htm
         //This also updates the name of the current item on woot and sets a
         //flag if it's a new item.
         percentLeft = wco.WCO_GetPercent();
         //If the new item flag is set do some things
         if (wco.newItem)
            {
            //make a beep on the Finch
            finch.buzz(1800, 800);
            //Print out the name of the item
            System.out.println(wco.item);
            //Say the name of the item
            finch.saySomething(wco.item);
            //and disable the flag so we don't repeat this
            wco.newItem = false;
            }

         //Print out some instructions to the user
         System.out.println("");
         System.out.println("Press ENTER to quit.");

         //Check to make sure we got a percent left returned and not our sold
         //out flag.
         if (percentLeft < 0)
            {
            System.out.println("Item sold out or no current woot off!");
            }
         //These are used to enable or disable the red and green LEDs in the
         //nose of the Finch. Start with them both enabled.
         redOn = 1;
         greenOn = 1;
         //These threshholds will turn red and green off if it's close to the
         //two extremes.
         if (percentLeft < rThresh)
            {
            //Having these as 1 or 0 lets us use them as a multiplier instead
            //of having an if/else statement later
            greenOn = 0;
            }
         //We don't use if else here because we want to check each independently
         if (percentLeft > gThresh)
            {
            redOn = 0;
            }
         //Here we are setting the color of the nose to show how much is left
         if (percentLeft >= 0)
            {
            //I precomputed the values to make it easy to check, but you could
            //use these expressions directly in the function calls
            int redVal = (255 - (percentLeft * 2)) * redOn;
            int greenVal = (55 + (percentLeft * 2)) * greenOn;
            //Print out the computed values
            System.out.println("R = " + redVal + " G = " + greenVal);
            //set the color of the finch's nose. Note again that we reference
            //the object then the function in that object.
            finch.setLED(redVal, greenVal, 0);
            }
         //If we got the sold out flag turn the nose blue
         else
            {
            finch.setLED(0, 0, 255);
            }
         //Wait for 5 seconds before checking again
         try
            {
            Thread.sleep(5000);
            }
         catch (InterruptedException e)
            {
            System.out.println("Oh noes! Error " + e);
            }
         //Check if the user has pressed return, if so we can quit.
         if (in.ready())
            {
            break;
            }
         }
      //Close the connection to the Finch, if you don't do this it takes 30 seconds to
      //timeout.
      finch.quit();
      }
   }
