package edu.cmu.ri.createlab.terk.robot.finch;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.device.connectivity.BackpackedFinchConnectivityManager;
import edu.cmu.ri.createlab.device.connectivity.FinchConnectivityManager;
import edu.cmu.ri.createlab.device.connectivity.HIDFinchConnectivityManager;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.userinterface.component.DatasetPlotter;
import edu.cmu.ri.createlab.util.FileUtils;
import org.apache.log4j.Logger;

/**
 * Contains all methods necessary to program for the Finch robot
 *
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class Finch implements FinchInterface
   {
   private static final Logger LOG = Logger.getLogger(Finch.class);

   // set new plotters to graph sensor values
   private final DatasetPlotter<Double> accelerometerPlotter = new DatasetPlotter<Double>(-1.7, 1.7, 340, 340, 10, TimeUnit.MILLISECONDS);
   private final DatasetPlotter<Integer> lightPlotter = new DatasetPlotter<Integer>(-10, 270, 340, 340, 10, TimeUnit.MILLISECONDS);
   private final DatasetPlotter<Double> temperaturePlotter = new DatasetPlotter<Double>(0.0, 40.0, 340, 340, 10, TimeUnit.MILLISECONDS);

   // create accelerometer, temperature, and light sensor jFrames
   private JFrame jFrameAccel;
   private JFrame jFrameTemp;
   private JFrame jFrameLight;

   private FinchController finchController;
   private final FinchConnectivityManager connectivityManager;

   public Finch()
      {
      this(new HIDFinchConnectivityManager());
      }

   public Finch(final String serialPortName)
      {
      this(new BackpackedFinchConnectivityManager(serialPortName));
      }

   public Finch(final FinchConnectivityManager connectivityManager)
      {
      this.connectivityManager = connectivityManager;

      System.out.println("Connecting to Finch...this may take a few seconds...");

      // Set system properties to point to the freeTTS directory for saySomething support
      System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

      try
         {
         finchController = connectivityManager.connect();
         }
      catch (final Exception e)
         {
         LOG.error("Exception caught while trying to create the Finch!  Aborting.", e);
         System.exit(1);
         }
      }

   /**
    * Returns the {@link FinchProperties} for this finch.
    */
   public FinchProperties getFinchProperties()
      {
      return finchController.getFinchProperties();
      }

   /**
    * Sets the color of the LED in the Finch's beak using a Color object.
    *
    * @param     color is a Color object that determines the beaks color
    */
   @Override
   public void setLED(final Color color)
      {
      if (color != null)
         {
         if (!finchController.setFullColorLED(color))
            {
            System.out.println("LED not responding, check Finch connection");
            }
         }
      else
         {
         System.out.println("Color object was null, LED could not be set");
         }
      }

   /**
    * Sets the color of the LED in the Finch's beak.  The LED can be any color that can be
    * created by mixing red, green, and blue; turning on all three colors in equal amounts results
    * in white light.  Valid ranges for the red, green, and blue elements are 0 to 255.
    *
    * @param     red sets the intensity of the red element of the LED
    * @param     green sets the intensity of the green element of the LED
    * @param     blue sets the intensity of the blue element of the LED
    */

   @Override
   public void setLED(final int red, final int green, final int blue)
      {
      boolean inRange = true;
      if (red > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Red value exceeds appropriate values (0-255), LED will not be set");
         }
      if (red < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Red value is negative, LED will not be set");
         }

      if (green > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Green value exceeds appropriate values (0-255), LED will not be set");
         }
      if (green < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Green value is negative, LED will not be set");
         }

      if (blue > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Blue value exceeds appropriate values (0-255), LED will not be set");
         }
      if (blue < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Blue value is negative, LED will not be set");
         }

      if (inRange)
         {
         setLED(new Color(red, green, blue));
         }
      }

   /**
    * Sets the color of the LED in the Finch's beak using a Color object for the length of time specified by duration.
    *
    * @param     color is a Color object that determines the beaks color
    * @param     duration is the length of time the color will display on the beak
    */
   @Override
   public void setLED(final Color color, final int duration)
      {
      if (color != null)
         {
         if (finchController.setFullColorLED(color))
            {
            sleep(duration);
            if (!finchController.setFullColorLED(new Color(0, 0, 0)))
               {
               System.out.println("LED not responding, check Finch connection");
               }
            }
         else
            {
            System.out.println("LED not responding, check Finch connection");
            }
         }
      else
         {
         System.out.println("Color object was null, LED could not be set");
         }
      }

   /**
    * Sets the color of the LED in the Finch's beak for the length of time specified by duration.  
    * The LED can be any color that can be created by mixing red, green, and blue; turning on all three colors in equal amounts results
    * in white light.  Valid ranges for the red, green, and blue elements are 0 to 255.
    *
    * @param     red sets the intensity of the red element of the LED
    * @param     green sets the intensity of the green element of the LED
    * @param     blue sets the intensity of the blue element of the LED
    * @param     duration is the length of time the color will display on the beak
    */

   @Override
   public void setLED(final int red, final int green, final int blue, final int duration)
      {
      boolean inRange = true;
      if (red > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Red value exceeds appropriate values (0-255), LED will not be set");
         }
      if (red < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Red value is negative, LED will not be set");
         }

      if (green > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Green value exceeds appropriate values (0-255), LED will not be set");
         }
      if (green < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Green value is negative, LED will not be set");
         }

      if (blue > finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity())
         {
         inRange = false;
         System.out.println("Blue value exceeds appropriate values (0-255), LED will not be set");
         }
      if (blue < finchController.getFinchProperties().getFullColorLedDeviceMinIntensity())
         {
         inRange = false;
         System.out.println("Blue value is negative, LED will not be set");
         }

      if (inRange)
         {
         setLED(new Color(red, green, blue));
         sleep(duration);
         setLED(new Color(0, 0, 0));
         }
      }

   /**
    * Stops both wheels.
    */
   @Override
   public void stopWheels()
      {
      setWheelVelocities(0, 0);
      }

   /**
    * This method simultaneously sets the velocities of both wheels. Current valid values range from
    * -255 to 255; negative values cause a wheel to move backwards.
    *
    * @param leftVelocity The velocity at which to move the left wheel
    * @param rightVelocity The velocity at which to move the right wheel
    */
   @Override
   public void setWheelVelocities(final int leftVelocity, final int rightVelocity)
      {
      setWheelVelocities(leftVelocity, rightVelocity, -1);
      }

   /**
    * This method simultaneously sets the velocities of both wheels. Current valid values range from
    * -255 to 255.  If <code>timeToHold</code> is positive, this method blocks further program execution for the amount
    * of time specified by timeToHold, and then stops the wheels once time has elapsed.
    *
    * @param leftVelocity The velocity in native units at which to move the left wheel
    * @param rightVelocity The velocity in native units at which to move the right wheel
    * @param timeToHold The amount of time in milliseconds to hold the velocity for; if 0 or negative, program
    *                   execution is not blocked and the wheels are not stopped.
    */
   @Override
   public void setWheelVelocities(final int leftVelocity, final int rightVelocity, final int timeToHold)
      {
      if (leftVelocity <= finchController.getFinchProperties().getMotorDeviceMaxVelocity() &&
          leftVelocity >= finchController.getFinchProperties().getMotorDeviceMinVelocity() &&
          rightVelocity <= finchController.getFinchProperties().getMotorDeviceMaxVelocity() &&
          rightVelocity >= finchController.getFinchProperties().getMotorDeviceMinVelocity())
         {
         if (finchController.setMotorVelocities(leftVelocity, rightVelocity))
            {
            if (timeToHold > 0)
               {
               sleep(timeToHold);
               stopWheels();
               }
            }
         else
            {
            System.out.println("Couldn't set motors, check Finch connection");
            }
         }
      else
         {
         System.out.println("Velocity values out of range");
         }
      }

   /**
    * This method uses Thread.sleep to cause the currently running program to sleep for the
    * specified number of seconds.
    *
    * @param ms - the number of milliseconds to sleep for.  Valid values are all positive integers.
    */
   @Override
   public void sleep(final int ms)
      {
      if (ms < 0)
         {
         System.out.println("Program sent a negative time to sleep for");
         }
      else
         {
         try
            {
            Thread.sleep(ms);
            }
         catch (InterruptedException ignored)
            {
            System.out.println("Error:  sleep was interrupted for some reason");
            }
         }
      }

   /**
    * This method returns the current X-axis acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.  The X-axis is the beak-tail axis.
    *
    * @return The X-axis acceleration value
    */
   @Override
   public double getXAcceleration()
      {
      final AccelerometerGs accelerometerGs = finchController.getAccelerometerGs();
      if (accelerometerGs != null)
         {
         return accelerometerGs.getX();
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return 0.0;
      }

   /**
    * This method returns the current Y-axis acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.  The Y-axis is the wheel-to-wheel axis.
    *
    * @return The Y-axis acceleration value
    */
   @Override
   public double getYAcceleration()
      {
      final AccelerometerGs accelerometerGs = finchController.getAccelerometerGs();
      if (accelerometerGs != null)
         {
         return accelerometerGs.getY();
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return 0.0;
      }

   /**
    * This method returns the current Z-axis acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.  The Z-axis runs perpendicular to the Finch's circuit board.
    *
    * @return The Z-axis acceleration value
    */
   @Override
   public double getZAcceleration()
      {
      final AccelerometerGs accelerometerGs = finchController.getAccelerometerGs();
      if (accelerometerGs != null)
         {
         return accelerometerGs.getZ();
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return 0.0;
      }

   /**
    * Use this method to simultaneously return the current X, Y, and Z accelerations experienced by the robot.
    * Values for acceleration can be in the range of -1.5g to +1.5g.  When the robot is on a flat surface,
    * X and Y should be close to 0g, and Z should be near +1.0g.
    *
    * @return a an array of 3 doubles containing the X, Y, and Z acceleration values
    */
   @Override
   public double[] getAccelerations()
      {
      final AccelerometerGs accelerometerGs = finchController.getAccelerometerGs();
      if (accelerometerGs != null)
         {
         final double[] accelerations = new double[3];
         accelerations[0] = accelerometerGs.getX();
         accelerations[1] = accelerometerGs.getY();
         accelerations[2] = accelerometerGs.getZ();
         return accelerations;
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return null;
      }

   /**
    * This method returns true if the beak is up (Finch sitting on its tail), false otherwise
    *
    * @return true if beak is pointed at ceiling
    */
   @Override
   public boolean isBeakUp()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] < -0.8 && accels[0] > -1.5 && accels[1] > -0.3 && accels[1] < 0.3 && accels[2] > -0.3 && accels[2] < 0.3)
            {
            return true;
            }
         }
      return false;
      }

   /**
    * This method returns true if the beak is pointed at the floor, false otherwise
    *
    * @return true if beak is pointed at the floor
    */
   @Override
   public boolean isBeakDown()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] < 1.5 && accels[0] > 0.8 && accels[1] > -0.3 && accels[1] < 0.3 && accels[2] > -0.3 && accels[2] < 0.3)
            {
            return true;
            }
         }
      return false;
      }

   /**
    * This method returns true if the Finch is on a flat surface
    *
    * @return true if the Finch is level
    */
   @Override
   public boolean isFinchLevel()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] > -0.5 && accels[0] < 0.5 && accels[1] > -0.5 && accels[1] < 0.5 && accels[2] > 0.65 && accels[2] < 1.5)
            {
            return true;
            }
         }
      return false;
      }

   /**
    * This method returns true if the Finch is upside down, false otherwise
    *
    * @return true if Finch is upside down
    */
   @Override
   public boolean isFinchUpsideDown()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] > -0.5 && accels[0] < 0.5 && accels[1] > -0.5 && accels[1] < 0.5 && accels[2] > -1.5 && accels[2] < -0.65)
            {
            return true;
            }
         }
      return false;
      }

   /**
    * This method returns true if the Finch's left wing is pointed at the ground
    *
    * @return true if Finch's left wing is down
    */
   @Override
   public boolean isLeftWingDown()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] > -0.5 && accels[0] < 0.5 && accels[1] > 0.7 && accels[1] < 1.5 && accels[2] > -0.5 && accels[2] < 0.5)
            {
            return true;
            }
         }
      return false;
      }

   /**
    * This method returns true if the Finch's right wing is pointed at the ground
    *
    * @return true if Finch's right wing is down
    */
   @Override
   public boolean isRightWingDown()
      {
      final double[] accels = getAccelerations();
      if (accels != null)
         {
         if (accels[0] > -0.5 && accels[0] < 0.5 && accels[1] > -1.5 && accels[1] < -0.7 && accels[2] > -0.5 && accels[2] < 0.5)
            {
            return true;
            }
         }
      return false;
      }

   /**
    *  Returns true if the Finch has been shaken since the last accelerometer read
    *
    *  @return true if the Finch was recently shaken
    */
   @Override
   public boolean isShaken()
      {
      final AccelerometerState accelerometerState = finchController.getAccelerometerState();
      if (accelerometerState != null)
         {
         return accelerometerState.wasShaken();
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return false;
      }

   /**
    *  Returns true if the Finch has been tapped since the last accelerometer read
    *
    *  @return true if the Finch was recently tapped
    */
   @Override
   public boolean isTapped()
      {
      final AccelerometerState accelerometerState = finchController.getAccelerometerState();
      if (accelerometerState != null)
         {
         return accelerometerState.wasTapped();
         }
      System.out.println("Accelerometer not responding, check Finch connection");
      return false;
      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds.  Middle C is about 262Hz.  Visit http://www.phy.mtu.edu/~suits/notefreqs.html for
    * frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param duration The time to play the tone in milliseconds
    */
   @Override
   public void playTone(final int frequency, final int duration)
      {
      playTone(frequency, finchController.getFinchProperties().getAudioDeviceMaxAmplitude(), duration);
      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds at a specified volume.  Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param volume The volume of the tone on a 1 to 10 scale
    * @param duration The time to play the tone in milliseconds
    */
   @Override
   public void playTone(final int frequency, final int volume, final int duration)
      {
      finchController.playTone(frequency, volume, duration);
      }

   /**
    * Plays a wav file over computer speakers at the specificied fileLocation path.  If you place the audio
    * file in the same path as your source, you can just specify the name of the file.
    *
    * @param     fileLocation Absolute path of the file or name of the file if located in some directory as source code
    */
   @Override
   public void playClip(final String fileLocation)
      {
      try
         {
         final File file = new File(fileLocation);
         final byte[] rawSound = FileUtils.getFileAsBytes(file);

         finchController.playClip(rawSound);
         }
      catch (IOException e)
         {
         LOG.error("IOException while trying to play sound at [" + fileLocation + "]", e);
         System.out.println("Failed to play sound.");
         }
      }

   /**
    * Takes the text of 'sayThis' and synthesizes it into a sound file and plays the sound file over
    * computer speakers.  sayThis can be arbitrarily long and can include variable arguments.
    *
    * Example:
    *   myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
    *
    * @param     sayThis The string of text that will be spoken by the computer
    */
   @Override
   public void saySomething(final String sayThis)
      {
      if (sayThis != null && sayThis.length() > 0)
         {
         finchController.speak(sayThis);
         }
      else
         {
         System.out.println("Given text to speak was null or empty");
         }
      }

   /**
    * Takes the text of 'sayThis' and synthesizes it into a sound file and plays the sound file over
    * computer speakers. sayThis can be arbitrarily long and can include variable arguments. The duration
    * argument allows you to delay program execution for a number of milliseconds. 
    *
    * Example:
    *   myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
    *
    * @param     sayThis The string of text that will be spoken by the computer
    * @param     duration The time in milliseconds to halt further program execution
    */
   @Override
   public void saySomething(final String sayThis, final int duration)
      {
      if (sayThis != null && sayThis.length() > 0)
         {
         finchController.speak(sayThis);
         sleep(duration);
         }
      else
         {
         System.out.println("Given text to speak was null or empty");
         }
      }

   /**
    * Plays a tone at the specified frequency for the specified duration on the Finch's internal buzzer.
    * Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    * Note that this is different from playTone, which plays a tone on the computer's speakers.
    * Also note that buzz is non-blocking - so if you call two buzz methods in a row without
    * an intervening sleep, you will only hear the second buzz (it will over-write the first buzz).
    *
    * @param     frequency Frequency in Hertz of the tone to be played
    * @param     duration  Duration in milliseconds of the tone
    */
   @Override
   public void buzz(final int frequency, final int duration)
      {
      if (!finchController.playBuzzerTone(frequency, duration))
         {
         System.out.println("Buzzer not responding, check Finch connection");
         }
      }

   /**
    * Plays a tone at the specified frequency for the specified duration on the Finch's internal buzzer.
    * Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    * Note that this is different from playTone, which plays a tone on the computer's speakers.
    * Unlike the buzz method, this method will block program execution for the time specified by duration.
    *
    * @param     frequency Frequency in Hertz of the tone to be played
    * @param     duration  Duration in milliseconds of the tone
    */
   @Override
   public void buzzBlocking(final int frequency, final int duration)
      {
      buzz(frequency, duration);
      sleep(duration);
      }

   /**
    * Returns the value of the left light sensor.  Valid values range from 0 to 255, with higher
    * values indicating more light is being detected by the sensor.
    *
    *
    * @return The current light level at the left light sensor
    */
   @Override
   public int getLeftLightSensor()
      {
      return getLightSensor(0);
      }

   /**
    * Returns the value of the right light sensor.  Valid values range from 0 to 255, with higher
    * values indicating more light is being detected by the sensor.
    *
    *
    * @return The current light level at the right light sensor
    */
   @Override
   public int getRightLightSensor()
      {
      return getLightSensor(1);
      }

   private int getLightSensor(final int id)
      {
      final int[] values = finchController.getPhotoresistors();
      if (values != null)
         {
         return values[id];
         }

      System.out.println("Light sensor not responding, check Finch connection");
      return 0;
      }

   /**
    * Returns a 2 integer array containing the current values of both light sensors.
    * The left sensor is the 0th array element, and the right sensor is the 1st element.
    *
    *
    * @return A 2 int array containing both light sensor readings.
    */
   @Override
   public int[] getLightSensors()
      {
      final int[] values = finchController.getPhotoresistors();

      if (values == null)
         {
         System.out.println("Light sensor not responding, check Finch connection");
         }

      return values;
      }

   /**
    * Returns true if the left light sensor is greater than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the light sensor needs to exceed
    * @return whether the light sensor exceeds the value specified by limit
    */
   @Override
   public boolean isLeftLightSensor(final int limit)
      {
      return (limit < getLeftLightSensor());
      }

   /**
    * Returns true if the right light sensor is greater than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the light sensor needs to exceed
    * @return true if the light sensor exceeds the value specified by limit
    */
   @Override
   public boolean isRightLightSensor(final int limit)
      {
      return (limit < getRightLightSensor());
      }

   /**
    * Returns true if there is an obstruction in front of the left side of the robot.
    *
    *
    * @return Whether an obstacle exists in front of the left side of the robot.
    */
   @Override
   public boolean isObstacleLeftSide()
      {
      return isObstactleDetected(0);
      }

   /**
    * Returns true if there is an obstruction in front of the right side of the robot.
    *
    *
    * @return Whether an obstacle exists in front of the right side of the robot.
    */
   @Override
   public boolean isObstacleRightSide()
      {
      return isObstactleDetected(1);
      }

   private boolean isObstactleDetected(final int id)
      {
      final Boolean isDetected = finchController.isObstacleDetected(id);
      if (isDetected == null)
         {
         System.out.println("Obstacle sensor not responding, check Finch connection");
         return false;
         }
      return isDetected;
      }

   /**
    * Returns true if either left or right obstacle sensor detect an obstacle.
    *
    *
    * @return Whether either obstacle sensor sees an obstacle.
    */
   @Override
   public boolean isObstacle()
      {
      return isObstacleLeftSide() || isObstacleRightSide();
      }

   /**
    * Returns the value of both obstacle sensors as 2 element boolean array.
    * The left sensor is the 0th element, and the right sensor is the 1st element.
    *
    *
    * @return The values of left and right obstacle sensors in a 2 element array
    */
   @Override
   public boolean[] getObstacleSensors()
      {
      final boolean[] areDetected = finchController.areObstaclesDetected();
      if (areDetected == null)
         {
         System.out.println("Obstacle sensors not responding, check Finch connection");
         }
      return areDetected;
      }

   /**
    * The current temperature reading at the temperature probe.  The value
    * returned is in Celsius.  To get Fahrenheit from Celsius, multiply the number
    * by 1.8 and then add 32.
    *
    * @return The current temperature in degrees Celsius
    */
   @Override
   public double getTemperature()
      {
      final Double temperature = finchController.getThermistorCelsiusTemperature();
      if (temperature == null)
         {
         System.out.println("Temperature sensor not responding, check Finch connection");
         return 0;
         }
      return temperature;
      }

   /**
    * Returns true if the temperature is greater than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the temperature needs to exceed
    * @return true if the temperature exceeds the value specified by limit
    */
   @Override
   public boolean isTemperature(final double limit)
      {
      return (limit < getTemperature());
      }

   /**
    * Returns the current value of the analog input specified by the given <code>id</code>.  Invalid analog input ids
    * cause this method to return <code>null</code>.    Note that, for finches without analog inputs, this method will
    * always return <code>null</code>.  This method also returns <code>null</code> if an error occurred while trying to
    * read the value.
    *
    * @see FinchProperties#getAnalogInputDeviceCount()
    */
   @Override
   public Integer getAnalogInput(final int id)
      {
      return finchController.getAnalogInput(id);
      }

   /**
    * Returns the voltage.  This doesn't have much meaning for finches connected via USB HID, and so the value returned
    * may be <code>null</code> or completely bogus.  For backpacked finches, it returns the voltage of the battery pack
    * in millivolts.
    */
   @Override
   public Integer getVoltage()
      {
      return finchController.getVoltage();
      }

   /**
    * Displays a graph of the X, Y, and Z accelerometer values.  Note that this graph
    * does not update on its own - you need to call updateAccelerometerGraph to
    * do so.
    *
    */
   @Override
   public void showAccelerometerGraph()
      {
      accelerometerPlotter.addDataset(Color.RED);
      accelerometerPlotter.addDataset(Color.GREEN);
      accelerometerPlotter.addDataset(Color.BLUE);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = accelerometerPlotter.getComponent();

               // create the main frame
               jFrameAccel = new JFrame("Accelerometer Values");

               // add the root panel to the JFrame
               jFrameAccel.add(plotComponent);

               // set various properties for the JFrame
               jFrameAccel.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameAccel.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameAccel.setVisible(false);
                        jFrameAccel.dispose();
                        }
                     });
               jFrameAccel.setBackground(Color.WHITE);
               jFrameAccel.setResizable(false);
               jFrameAccel.pack();
               jFrameAccel.setLocation(400, 200);// center the window on the screen
               jFrameAccel.setVisible(true);
               }
            });
      }

   /**
    * updates the accelerometer graph with accelerometer data specified by xVal,
    * yVal, and zVal.
    *
    * @param xVal  The X axis acceleration value
    * @param yVal  The Y axis acceleration value
    * @param zVal  The Z axis acceleration value
    */
   @Override
   public void updateAccelerometerGraph(final double xVal, final double yVal, final double zVal)
      {
      accelerometerPlotter.setCurrentValues(xVal, yVal, zVal);
      }

   /**
    * Closes the opened Accelerometer Graph
    */
   @Override
   public void closeAccelerometerGraph()
      {
      jFrameAccel.setVisible(false);
      jFrameAccel.dispose();
      }

   /**
    * Displays a graph of the left and right light sensor values.  Note that this graph
    * does not update on its own - you need to call updateLightSensorGraph to
    * do so.
    *
    */

   @Override
   public void showLightSensorGraph()
      {
      lightPlotter.addDataset(Color.RED);
      lightPlotter.addDataset(Color.BLUE);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = lightPlotter.getComponent();

               // create the main frame
               jFrameLight = new JFrame("Light Sensor Values");

               // add the root panel to the JFrame
               jFrameLight.add(plotComponent);

               // set various properties for the JFrame
               jFrameLight.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameLight.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameLight.setVisible(false);
                        jFrameLight.dispose();
                        }
                     });
               jFrameLight.setBackground(Color.WHITE);
               jFrameLight.setResizable(false);
               jFrameLight.pack();
               jFrameLight.setLocation(20, 200);// center the window on the screen
               jFrameLight.setVisible(true);
               }
            });
      }

   /**
    * Updates the light sensor graph with the left and right light sensor data.
    *
    * @param leftSensor  Variable containing left light sensor value
    * @param rightSensor  Variable containing right light sensor value
    */
   @Override
   public void updateLightSensorGraph(final int leftSensor, final int rightSensor)
      {
      lightPlotter.setCurrentValues(leftSensor, rightSensor);
      }

   /**
    * Closes the opened Light sensor Graph
    */
   @Override
   public void closeLightSensorGraph()
      {
      jFrameLight.setVisible(false);
      jFrameLight.dispose();
      }

   /**
    * Displays a graph of the temperature value.  Note that this graph
    * does not update on its own - you need to call updateTemperatureGraph to
    * do so.
    *
    */

   @Override
   public void showTemperatureGraph()
      {
      temperaturePlotter.addDataset(Color.GREEN);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = temperaturePlotter.getComponent();

               // create the main frame
               jFrameTemp = new JFrame("Temperature Values");

               // add the root panel to the JFrame
               jFrameTemp.add(plotComponent);

               // set various properties for the JFrame
               jFrameTemp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameTemp.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameTemp.setVisible(false);
                        jFrameTemp.dispose();
                        }
                     });
               jFrameTemp.setBackground(Color.WHITE);
               jFrameTemp.setResizable(false);
               jFrameTemp.pack();
               jFrameTemp.setLocation(780, 200);// center the window on the screen
               jFrameTemp.setVisible(true);
               }
            });
      }

   /**
    * Updates the temperature graph with the most recent temperature data.
    *
    * @param temp   variable containing a temperature value
    */

   @Override
   public void updateTemperatureGraph(final double temp)
      {
      temperaturePlotter.setCurrentValues(temp);
      }

   /**
    * Closes the opened temperature Graph
    */
   @Override
   public void closeTemperatureGraph()
      {
      jFrameTemp.setVisible(false);
      jFrameTemp.dispose();
      }

   /**
    * This method properly closes the connection with the Finch and resets the Finch so that
    * it is immediately ready to be controlled by subsequent programs.  Note that if this
    * method is not called at the end of the program, the Finch will continue to act on its
    * most recent command (such as drive forward) for 5 seconds before automatically timing
    * out and resetting.  This is why we recommend you always call the quit method at the end
    * of your program.
    */
   @Override
   public void quit()
      {
      if (jFrameAccel != null)
         {
         closeAccelerometerGraph();
         }
      if (jFrameLight != null)
         {
         closeLightSensorGraph();
         }
      if (jFrameTemp != null)
         {
         closeTemperatureGraph();
         }

      connectivityManager.disconnect();
      }
   }


