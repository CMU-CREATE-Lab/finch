package edu.cmu.ri.createlab.terk.robot.finch.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import edu.cmu.ri.createlab.terk.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.util.ArrayUtils;
import edu.cmu.ri.createlab.util.FileUtils;
import edu.cmu.ri.createlab.util.commandline.BaseCommandLineApplication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public abstract class BaseCommandLineFinch extends BaseCommandLineApplication
   {
   private static final Log LOG = LogFactory.getLog(BaseCommandLineFinch.class);
   private static final int THIRTY_SECONDS_IN_MILLIS = 30000;

   private final Runnable connectToFinchAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println("You are already connected to a finch.");
               }
            else
               {
               connect();
               }
            }
         };

   private final Runnable disconnectFromFinchAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            }
         };

   private final Runnable fullColorLEDAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer r = readInteger("Red Intensity   [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (r == null || r < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || r > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid red intensity");
                  return;
                  }
               final Integer g = readInteger("Green Intensity [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (g == null || g < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || g > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid green intensity");
                  return;
                  }
               final Integer b = readInteger("Blue Intensity  [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (b == null || b < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || b > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid blue intensity");
                  return;
                  }

               setFullColorLED(r, g, b);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertAccelerometerStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertAccelerometerStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getObstacleDetectorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertObstacleDetectorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetObstacleDetectorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertObstacleDetectorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertPhotoresistorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertPhotoresistorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getThermistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertThermistorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetThermistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertThermistorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorVelocitiesAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer leftVelocity = readInteger("Left Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
               if (leftVelocity == null || leftVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY || leftVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)
                  {
                  println("Invalid velocity");
                  return;
                  }
               final Integer rightVelocity = readInteger("Right Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
               if (rightVelocity == null || rightVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY || rightVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)
                  {
                  println("Invalid velocity");
                  return;
                  }

               if (!setMotorVelocities(leftVelocity, rightVelocity))
                  {
                  println("Failed to set the motor velocities");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playBuzzerToneAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer frequency = readInteger("Frequency (hz) [" + FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY + ", " + FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY + "]: ");
               if (frequency == null || frequency < FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY || frequency > FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY)
                  {
                  println("Invalid frequency");
                  return;
                  }
               final Integer duration = readInteger("Duration  (ms) [" + FinchConstants.BUZZER_DEVICE_MIN_DURATION + ", " + FinchConstants.BUZZER_DEVICE_MAX_DURATION + "]: ");
               if (duration == null || duration < FinchConstants.BUZZER_DEVICE_MIN_DURATION || duration > FinchConstants.BUZZER_DEVICE_MAX_DURATION)
                  {
                  println("Invalid duration");
                  return;
                  }

               playBuzzerTone(frequency, duration);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playToneAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer freq = readInteger("Frequency   (hz): ");
               if (freq == null || freq < FinchConstants.AUDIO_DEVICE_MIN_FREQUENCY)
                  {
                  println("Invalid frequency");
                  return;
                  }
               final Integer amp = readInteger("Amplitude [" + FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE + ", " + FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE + "]: ");
               if (amp == null || amp < FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE || amp > FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE)
                  {
                  println("Invalid amplitude");
                  return;
                  }
               final Integer dur = readInteger("Duration    (ms): ");
               if (dur == null || dur < FinchConstants.AUDIO_DEVICE_MIN_DURATION)
                  {
                  println("Invalid duration");
                  return;
                  }

               playTone(freq, amp, dur);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playClipAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final String filePath = readString("Absolute path to sound file: ");
               if (filePath == null || filePath.length() == 0)
                  {
                  println("Invalid path");
                  return;
                  }

               final File file = new File(filePath);
               if (file.exists() && file.isFile())
                  {
                  final byte[] data;
                  try
                     {
                     data = FileUtils.getFileAsBytes(file);
                     playClip(data);
                     }
                  catch (IOException e)
                     {
                     final String msg = "Error reading sound file (" + e.getMessage() + ")";
                     println(msg);
                     }
                  }
               else
                  {
                  println("Invalid path");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable emergencyStopAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               emergencyStop();
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable quitAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            println("Bye!");
            }
         };

   public BaseCommandLineFinch(final BufferedReader in)
      {
      super(in);

      registerAction("c", connectToFinchAction);
      registerAction("d", disconnectFromFinchAction);
      registerAction("f", fullColorLEDAction);
      registerAction("a", getAccelerometerStateAction);
      registerAction("A", pollingGetAccelerometerStateAction);
      registerAction("o", getObstacleDetectorStateAction);
      registerAction("O", pollingGetObstacleDetectorStateAction);
      registerAction("l", getPhotoresistorStateAction);
      registerAction("L", pollingGetPhotoresistorStateAction);
      registerAction("h", getThermistorStateAction);
      registerAction("H", pollingGetThermistorStateAction);
      registerAction("v", setMotorVelocitiesAction);
      registerAction("b", playBuzzerToneAction);
      registerAction("t", playToneAction);
      registerAction("s", playClipAction);
      registerAction("x", emergencyStopAction);
      registerAction(QUIT_COMMAND, quitAction);
      }

   protected final void menu()
      {
      println("COMMANDS -----------------------------------");
      println("");
      println("c         Connect to the finch");
      println("d         Disconnect from the finch");
      println("");
      println("f         Control the full-color LED");
      println("a         Get the accelerometer state");
      println("A         Continuously poll the accelerometer for 30 seconds");
      println("o         Get the state of the obstacle detectors");
      println("O         Continuously poll the obstacle detectors for 30 seconds");
      println("l         Get the state of the photoresistors");
      println("L         Continuously poll the photoresistors for 30 seconds");
      println("h         Get the state of the thermistor");
      println("H         Continuously poll the thermistor for 30 seconds");
      println("v         Set the velocity of both of the motors (in native units)");
      println("");
      println("b         Play a tone using the finch's buzzer");
      println("t         Play a tone using the computer's speaker");
      println("s         Play a sound clip using the computer's speaker");
      println("");
      println("x         Turn motors and LED off");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   private String convertAccelerometerStateToString()
      {
      final AccelerometerState accelerometerState = getAccelerometer();
      return "Accelerometer:" + accelerometerState + " = " + convertToAccelerometerGs(accelerometerState);
      }

   private String convertObstacleDetectorStateToString()
      {
      return "Obstacle Detectors: " + ArrayUtils.arrayToString(getObstacleDetectors());
      }

   private String convertPhotoresistorStateToString()
      {
      return "Photoresistors: " + ArrayUtils.arrayToString(getPhotoresistors());
      }

   private String convertThermistorStateToString()
      {
      final int rawValue = getThermistor();
      return "Thermistor: " + rawValue + " = " + convertToCelsiusTemperature(rawValue) + " degrees C";
      }

   private void poll(final Runnable strategy)
      {
      final long startTime = System.currentTimeMillis();
      while (System.currentTimeMillis() - startTime < THIRTY_SECONDS_IN_MILLIS)
         {
         strategy.run();
         try
            {
            Thread.sleep(30);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }
      }

   protected abstract boolean connect();

   protected abstract boolean disconnect();

   protected abstract void setFullColorLED(final int r, final int g, final int b);

   protected abstract AccelerometerState getAccelerometer();

   protected abstract AccelerometerGs convertToAccelerometerGs(final AccelerometerState accelerometerState);

   protected abstract boolean[] getObstacleDetectors();

   protected abstract int[] getPhotoresistors();

   protected abstract int getThermistor();

   protected abstract double convertToCelsiusTemperature(final Integer rawValue);

   protected abstract boolean setMotorVelocities(final int leftVelocity, final int rightVelocity);

   protected abstract void playBuzzerTone(final int frequency, final int duration);

   protected abstract void playTone(final int frequency, final int amplitude, final int duration);

   protected abstract void playClip(final byte[] data);

   protected abstract void emergencyStop();

   protected abstract boolean isInitialized();
   }