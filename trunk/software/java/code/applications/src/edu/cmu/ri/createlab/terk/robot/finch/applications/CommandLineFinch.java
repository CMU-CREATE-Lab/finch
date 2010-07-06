package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.terk.robot.finch.DefaultFinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.robot.finch.application.BaseCommandLineFinch;
import edu.cmu.ri.createlab.terk.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.buzzer.BuzzerService;
import edu.cmu.ri.createlab.terk.services.finch.FinchService;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;
import edu.cmu.ri.createlab.terk.services.motor.OpenLoopVelocityControllableMotorService;
import edu.cmu.ri.createlab.terk.services.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.terk.services.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineFinch extends BaseCommandLineFinch
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineFinch(in).run();
      }

   private ServiceManager serviceManager;
   private FinchController finchController;

   public CommandLineFinch(final BufferedReader in)
      {
      super(in);
      }

   protected boolean connect()
      {
      finchController = DefaultFinchController.create();

      if (finchController == null)
         {
         println("Connection failed.");
         serviceManager = null;
         return false;
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
         serviceManager = new FinchServiceManager(finchController);
         return true;
         }
      }

   protected void setFullColorLED(final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(0, new Color(r, g, b));
      }

   protected AccelerometerState getAccelerometer()
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).getAccelerometerState(0);
      }

   protected AccelerometerGs convertToAccelerometerGs(final AccelerometerState accelerometerState)
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).convertToGs(accelerometerState);
      }

   protected boolean[] getObstacleDetectors()
      {
      return ((SimpleObstacleDetectorService)serviceManager.getServiceByTypeId(SimpleObstacleDetectorService.TYPE_ID)).areObstaclesDetected();
      }

   protected int[] getPhotoresistors()
      {
      return ((PhotoresistorService)serviceManager.getServiceByTypeId(PhotoresistorService.TYPE_ID)).getPhotoresistorValues();
      }

   protected int getThermistor()
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).getThermistorValue(0);
      }

   protected double convertToCelsiusTemperature(final Integer rawValue)
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).convertToCelsius(rawValue);
      }

   protected boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return ((OpenLoopVelocityControllableMotorService)serviceManager.getServiceByTypeId(OpenLoopVelocityControllableMotorService.TYPE_ID)).setVelocities(new int[]{leftVelocity, rightVelocity});
      }

   protected void playBuzzerTone(final int frequency, final int duration)
      {
      ((BuzzerService)serviceManager.getServiceByTypeId(BuzzerService.TYPE_ID)).playTone(0, frequency, duration);
      }

   protected void playTone(final int frequency, final int amplitude, final int duration)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playTone(frequency, amplitude, duration);
      }

   protected void playClip(final byte[] data)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playSound(data);
      }

   protected void emergencyStop()
      {
      ((FinchService)serviceManager.getServiceByTypeId(FinchService.TYPE_ID)).emergencyStop();
      }

   protected boolean isInitialized()
      {
      return serviceManager != null;
      }

   protected boolean disconnect()
      {
      if (finchController != null)
         {
         finchController.disconnect();
         finchController = null;
         }
      serviceManager = null;

      return true;
      }
   }