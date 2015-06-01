package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.SortedMap;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.commandline.SerialDeviceCommandLineApplication;
import edu.cmu.ri.createlab.terk.robot.finch.BackpackedFinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchHardwareType;
import edu.cmu.ri.createlab.terk.robot.finch.HIDFinchController;
import edu.cmu.ri.createlab.terk.robot.finch.services.DefaultFinchServiceFactoryHelper;
import edu.cmu.ri.createlab.terk.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.buzzer.BuzzerService;
import edu.cmu.ri.createlab.terk.services.finch.FinchBackpackService;
import edu.cmu.ri.createlab.terk.services.finch.FinchService;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;
import edu.cmu.ri.createlab.terk.services.motor.OpenLoopVelocityControllableMotorService;
import edu.cmu.ri.createlab.terk.services.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.terk.services.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorService;
import edu.cmu.ri.createlab.util.ArrayUtils;
import edu.cmu.ri.createlab.util.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineFinch extends SerialDeviceCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(CommandLineFinch.class);
   private static final int THIRTY_SECONDS_IN_MILLIS = 30000;
   private static final String DEFAULT_SERIAL_PORT_NAME = "/dev/tty.finch-backpack";

   private void initializeFinchControllerAfterCreation()
      {
      if (finchController == null)
         {
         println("Connection failed.");
         serviceManager = null;
         }
      else
         {
         println("Connection successful.");
         finchController.addCreateLabDevicePingFailureEventListener(
               new CreateLabDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("Finch ping failure detected.  You will need to reconnect.");
                  serviceManager = null;
                  finchController = null;
                  }
               });
         serviceManager = new FinchServiceManager(finchController, DefaultFinchServiceFactoryHelper.getInstance());
         }
      }

   private final Runnable connectToHIDFinchAction =
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
               finchController = HIDFinchController.create();
               initializeFinchControllerAfterCreation();
               }
            }
         };

   private final Runnable connectToBackpackedFinchAction =
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
               String choice;
               do
                  {
                  choice = readString("Scan serial ports? [y/N]: ");
                  if ("".equals(choice))
                     {
                     choice = "n";
                     }
                  choice = (choice == null) ? "" : choice.trim().toLowerCase();
                  }
               while (!"y".equals(choice) && !"n".equals(choice));

               String serialPortName = null;
               if ("y".equals(choice))
                  {
                  final SortedMap<Integer, String> portMap = enumeratePorts();

                  if (!portMap.isEmpty())
                     {
                     final Integer index = readInteger("Connect to port number: ");

                     if (index == null)
                        {
                        println("Invalid port");
                        }
                     else
                        {
                        serialPortName = portMap.get(index);
                        }
                     }
                  }
               else
                  {
                  serialPortName = readString("Serial port name? [" + DEFAULT_SERIAL_PORT_NAME + "]: ");
                  if ("".equals(serialPortName))
                     {
                     serialPortName = DEFAULT_SERIAL_PORT_NAME;
                     }
                  if (serialPortName != null)
                     {
                     serialPortName = serialPortName.trim();
                     }
                  }

               if (serialPortName != null)
                  {
                  println("Attempting to connect to the Finch backpack on port [" + serialPortName + "]...");
                  finchController = BackpackedFinchController.create(serialPortName);
                  initializeFinchControllerAfterCreation();
                  }
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
               final int minIntensity = finchController.getFinchProperties().getFullColorLedDeviceMinIntensity();
               final int maxIntensity = finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity();
               final Integer r = readInteger("Red Intensity   [" + minIntensity + ", " + maxIntensity + "]: ");
               if (r == null || r < minIntensity || r > maxIntensity)
                  {
                  println("Invalid red intensity");
                  return;
                  }
               final Integer g = readInteger("Green Intensity [" + minIntensity + ", " + maxIntensity + "]: ");
               if (g == null || g < minIntensity || g > maxIntensity)
                  {
                  println("Invalid green intensity");
                  return;
                  }
               final Integer b = readInteger("Blue Intensity  [" + minIntensity + ", " + maxIntensity + "]: ");
               if (b == null || b < minIntensity || b > maxIntensity)
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

   private final Runnable getVoltageAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertVoltageStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getAnalogInputAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               if (FinchHardwareType.BACKPACK.equals(finchController.getFinchProperties().getHardwareType()))
                  {
                  final Integer analogInputId = readInteger("Analog input index [0 - " + (finchController.getFinchProperties().getAnalogInputDeviceCount() - 1) + "]: ");

                  if (analogInputId == null || analogInputId < 0 || analogInputId >= finchController.getFinchProperties().getAnalogInputDeviceCount())
                     {
                     println("Invalid analog input index");
                     }
                  else
                     {
                     println(getAnalogInput(analogInputId));
                     }
                  }
               else
                  {
                  println("The finch you are connected to doesn't have analog inputs.");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getAnalogInputsAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               if (FinchHardwareType.BACKPACK.equals(finchController.getFinchProperties().getHardwareType()))
                  {
                  poll(
                        new Runnable()
                        {
                        public void run()
                           {
                           final int[] analogInputValues = getAnalogInputs();
                           println("Analog inputs: " + arrayToFormattedString(analogInputValues));
                           }
                        });
                  }
               else
                  {
                  println("The finch you are connected to doesn't have analog inputs.");
                  }
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
               final int minVelocity = finchController.getFinchProperties().getMotorDeviceMinVelocity();
               final int maxVelocity = finchController.getFinchProperties().getMotorDeviceMaxVelocity();
               final Integer leftVelocity = readInteger("Left Velocity  [" + minVelocity + ", " + maxVelocity + "]: ");
               if (leftVelocity == null || leftVelocity < minVelocity || leftVelocity > maxVelocity)
                  {
                  println("Invalid velocity");
                  return;
                  }
               final Integer rightVelocity = readInteger("Right Velocity  [" + minVelocity + ", " + maxVelocity + "]: ");
               if (rightVelocity == null || rightVelocity < minVelocity || rightVelocity > maxVelocity)
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
               final int minFrequency = finchController.getFinchProperties().getBuzzerDeviceMinFrequency();
               final int maxFrequency = finchController.getFinchProperties().getBuzzerDeviceMaxFrequency();
               final Integer frequency = readInteger("Frequency (hz) [" + minFrequency + ", " + maxFrequency + "]: ");
               if (frequency == null || frequency < minFrequency || frequency > maxFrequency)
                  {
                  println("Invalid frequency");
                  return;
                  }

               final int minDuration = finchController.getFinchProperties().getBuzzerDeviceMinDuration();
               final int maxDuration = finchController.getFinchProperties().getBuzzerDeviceMaxDuration();
               final Integer duration = readInteger("Duration  (ms) [" + minDuration + ", " + maxDuration + "]: ");
               if (duration == null || duration < minDuration || duration > maxDuration)
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
               final int minFrequency = finchController.getFinchProperties().getAudioDeviceMinFrequency();
               final int maxFrequency = finchController.getFinchProperties().getAudioDeviceMaxFrequency();
               if (freq == null || freq < minFrequency || freq > maxFrequency)
                  {
                  println("Invalid frequency");
                  return;
                  }
               final int minAmplitude = finchController.getFinchProperties().getAudioDeviceMinAmplitude();
               final int maxAmplitude = finchController.getFinchProperties().getAudioDeviceMaxAmplitude();
               final Integer amp = readInteger("Amplitude [" + minAmplitude + ", " + maxAmplitude + "]: ");
               if (amp == null || amp < minAmplitude || amp > maxAmplitude)
                  {
                  println("Invalid amplitude");
                  return;
                  }
               final Integer dur = readInteger("Duration    (ms): ");
               final int maxDuration = finchController.getFinchProperties().getAudioDeviceMaxDuration();
               final int minDuration = finchController.getFinchProperties().getAudioDeviceMinDuration();
               if (dur == null || dur < minDuration || dur > maxDuration)
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
                  println("Invalid path (" + filePath + ")");
                  return;
                  }

               final File file = new File(filePath);
               if (file.exists() && file.isFile())
                  {
                  try
                     {
                     final byte[] data = FileUtils.getFileAsBytes(file);
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

   private final Runnable speakTextAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final String whatToSay = readString("Text to speak: ");
               if (whatToSay == null || whatToSay.length() == 0)
                  {
                  println("Text to speak cannot be empty");
                  return;
                  }

               speak(whatToSay);
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
   private ServiceManager serviceManager;
   private FinchController finchController;

   public CommandLineFinch(final BufferedReader in)
      {
      super(in);

      registerAction("C", connectToHIDFinchAction);
      registerAction("c", connectToBackpackedFinchAction);
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
      registerAction("V", getVoltageAction);
      registerAction("n", getAnalogInputAction);
      registerAction("N", getAnalogInputsAction);
      registerAction("b", playBuzzerToneAction);
      registerAction("t", playToneAction);
      registerAction("s", playClipAction);
      registerAction("S", speakTextAction);
      registerAction("x", emergencyStopAction);
      registerAction("i", new MovementAction(200, 200));
      registerAction("j", new MovementAction(0, 150));
      registerAction("k", new MovementAction(150, 0));
      registerAction("m", new MovementAction(-200, -200));
      registerAction(QUIT_COMMAND, quitAction);
      }

   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineFinch(in).run();
      }

   protected final void menu()
      {
      println("COMMANDS -----------------------------------");
      println("");
      println("C         Connect to the finch via USB");
      println("c         Connect to the finch with backpack via bluetooth");
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
      println("V         Get the finch's current voltage");
      println("n         Get the value of one of the analog inputs");
      println("N         Continuously poll the analog inputs for 30 seconds");
      println("");
      println("b         Play a tone using the finch's buzzer");
      println("t         Play a tone using the computer's speaker");
      println("s         Play a sound clip using the computer's speaker");
      println("S         Convert text to speech and then speak it");
      println("");
      println("i         Drive forward");
      println("j         Turn left");
      println("k         Turn right");
      println("m         Drive backward");
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
      final Integer rawValue = getThermistor();
      if (rawValue != null)
         {
         return "Thermistor: " + rawValue + " = " + convertToCelsiusTemperature(rawValue) + " degrees C";
         }

      return "Thermistor: failed to read value";
      }

   private String convertVoltageStateToString()
      {
      final Integer rawValue = getVoltage();
      if (rawValue != null)
         {
         return "Voltage: " + rawValue;   // TODO: display units
         }

      return "Voltage: failed to read value";
      }

   private void poll(final Runnable strategy)
      {
      final long startTime = System.currentTimeMillis();
      while (isConnected() && System.currentTimeMillis() - startTime < THIRTY_SECONDS_IN_MILLIS)
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

   private static String arrayToFormattedString(final int[] a)
      {
      if (a != null && a.length > 0)
         {
         final StringBuilder s = new StringBuilder();
         for (final int i : a)
            {
            s.append(String.format("%5d", i));
            }
         return s.toString();
         }
      return "";
      }

   private boolean isConnected()
      {
      return finchController != null && !finchController.isDisconnected();
      }

   protected final void disconnect()
      {
      if (finchController != null)
         {
         finchController.disconnect();
         finchController = null;
         }
      serviceManager = null;
      }

   private void setFullColorLED(final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(0, new Color(r, g, b));
      }

   private AccelerometerState getAccelerometer()
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).getAccelerometerState(0);
      }

   private AccelerometerGs convertToAccelerometerGs(final AccelerometerState accelerometerState)
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).convertToGs(accelerometerState);
      }

   private boolean[] getObstacleDetectors()
      {
      return ((SimpleObstacleDetectorService)serviceManager.getServiceByTypeId(SimpleObstacleDetectorService.TYPE_ID)).areObstaclesDetected();
      }

   private int[] getPhotoresistors()
      {
      return ((PhotoresistorService)serviceManager.getServiceByTypeId(PhotoresistorService.TYPE_ID)).getPhotoresistorValues();
      }

   private Integer getThermistor()
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).getThermistorValue(0);
      }

   private Integer getAnalogInput(final int id)
      {
      final AnalogInputsService service = (AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID);
      if (service != null)
         {
         return service.getAnalogInputValue(id);
         }
      return null;
      }

   private int[] getAnalogInputs()
      {
      final AnalogInputsService service = (AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID);
      if (service != null)
         {
         return service.getAnalogInputValues();
         }
      return null;
      }

   private Integer getVoltage()
      {
      final FinchBackpackService service = (FinchBackpackService)serviceManager.getServiceByTypeId(FinchBackpackService.TYPE_ID);
      if (service != null)
         {
         return service.getVoltage();
         }
      return null;
      }

   private Double convertToCelsiusTemperature(final Integer rawValue)
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).convertToCelsius(rawValue);
      }

   private boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return ((OpenLoopVelocityControllableMotorService)serviceManager.getServiceByTypeId(OpenLoopVelocityControllableMotorService.TYPE_ID)).setVelocities(new int[]{leftVelocity, rightVelocity});
      }

   private void playBuzzerTone(final int frequency, final int duration)
      {
      ((BuzzerService)serviceManager.getServiceByTypeId(BuzzerService.TYPE_ID)).playTone(0, frequency, duration);
      }

   private void playTone(final int frequency, final int amplitude, final int duration)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playTone(frequency, amplitude, duration);
      }

   private void playClip(final byte[] data)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playSound(data);
      }

   private void speak(final String whatToSay)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).speak(whatToSay);
      }

   private void emergencyStop()
      {
      ((FinchService)serviceManager.getServiceByTypeId(FinchService.TYPE_ID)).emergencyStop();
      }

   private boolean isInitialized()
      {
      return serviceManager != null;
      }

   private class MovementAction implements Runnable
      {
      private final int leftVelocity;
      private final int rightVelocity;

      private MovementAction(final int leftVelocity, final int rightVelocity)
         {
         this.leftVelocity = leftVelocity;
         this.rightVelocity = rightVelocity;
         }

      public void run()
         {
         if (isInitialized())
            {
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
      }
   }