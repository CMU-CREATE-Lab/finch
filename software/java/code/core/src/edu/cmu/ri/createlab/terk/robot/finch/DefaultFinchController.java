package edu.cmu.ri.createlab.terk.robot.finch;

import java.awt.Color;
import edu.cmu.ri.createlab.terk.robot.finch.commands.BuzzerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetAccelerometerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetObstacleSensorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetPhotoresistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetThermistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.MotorVelocityCommandStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.terk.services.audio.AudioHelper;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.usb.hid.HIDCommandExecutionQueue;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResult;
import edu.cmu.ri.createlab.usb.hid.HIDCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.MathUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultFinchController implements FinchController
   {
   private static final Log LOG = LogFactory.getLog(DefaultFinchController.class);
   private final AccelerometerUnitConversionStrategy accelerometerUnitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.ACCELEROMETER_DEVICE_ID);
   private final ThermistorUnitConversionStrategy thermistorUnitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.THERMISTOR_DEVICE_ID);

   public static FinchController create()
      {
      try
         {
         // create the HID device command execution queue (which will attempt to connect to the device)
         final HIDCommandExecutionQueue commandExecutionQueue = HIDCommandExecutionQueue.create(FinchConstants.USB_VENDOR_ID, FinchConstants.USB_PRODUCT_ID);
         if (commandExecutionQueue != null)
            {
            return new DefaultFinchController(commandExecutionQueue);
            }
         }
      catch (NotImplementedException e)
         {
         LOG.error("NotImplementedException caught while trying to create the HIDCommandExecutionQueue", e);
         }
      return null;
      }

   private final HIDCommandExecutionQueue commandExecutionQueue;
   private final HIDCommandStrategy disconnectHIDCommandStrategy = new DisconnectCommandStrategy();
   private final HIDCommandStrategy emergencyStopHIDCommandStrategy = new EmergencyStopCommandStrategy();
   private final HIDCommandStrategy getAccelerometerHIDCommandStrategy = new GetAccelerometerCommandStrategy();
   private final HIDCommandStrategy getObstacleSensorHIDCommandStrategy = new GetObstacleSensorCommandStrategy();
   private final HIDCommandStrategy getPhotoresistorHIDCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final HIDCommandStrategy getThermistorHIDCommandStrategy = new GetThermistorCommandStrategy();

   private DefaultFinchController(final HIDCommandExecutionQueue commandExecutionQueue)
      {
      this.commandExecutionQueue = commandExecutionQueue;
      }

   public AccelerometerState getAccelerometerState()
      {
      final HIDCommandResult response = commandExecutionQueue.execute(getAccelerometerHIDCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new AccelerometerState(ByteUtils.unsignedByteToInt(responseData[0]),
                                       ByteUtils.unsignedByteToInt(responseData[1]),
                                       ByteUtils.unsignedByteToInt(responseData[2]));
         }

      return null;
      }

   public boolean[] areObstaclesDetected()
      {
      final HIDCommandResult response = commandExecutionQueue.execute(getObstacleSensorHIDCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new boolean[]{ByteUtils.unsignedByteToInt(responseData[0]) == 1,
                              ByteUtils.unsignedByteToInt(responseData[1]) == 1};
         }

      return null;
      }

   public int[] getPhotoresistors()
      {
      final HIDCommandResult response = commandExecutionQueue.execute(getPhotoresistorHIDCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new int[]{ByteUtils.unsignedByteToInt(responseData[0]),
                          ByteUtils.unsignedByteToInt(responseData[1])};
         }

      return null;
      }

   public Integer getThermistor(final int id)
      {
      if (id >= 0 && id < FinchConstants.THERMISTOR_DEVICE_COUNT)
         {
         final HIDCommandResult response = commandExecutionQueue.execute(getThermistorHIDCommandStrategy);

         if (response.wasSuccessful())
            {
            return ByteUtils.unsignedByteToInt(response.getData()[0]);
            }
         }

      return null;
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return commandExecutionQueue.executeAndReturnStatus(new FullColorLEDCommandStrategy(red, green, blue));
      }

   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return commandExecutionQueue.executeAndReturnStatus(new MotorVelocityCommandStrategy(MathUtils.ensureRange(leftVelocity, FinchConstants.MOTOR_DEVICE_MIN_VELOCITY, FinchConstants.MOTOR_DEVICE_MAX_VELOCITY),
                                                                                           MathUtils.ensureRange(rightVelocity, FinchConstants.MOTOR_DEVICE_MIN_VELOCITY, FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)));
      }

   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return commandExecutionQueue.executeAndReturnStatus(new BuzzerCommandStrategy(frequency, durationInMilliseconds));
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   public void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   public boolean emergencyStop()
      {
      return commandExecutionQueue.executeAndReturnStatus(emergencyStopHIDCommandStrategy);
      }

   public void disconnect()
      {
      LOG.debug("DefaultFinchController.disconnect(): Now attempting to send the disconnect command to the finch");
      if (commandExecutionQueue.executeAndReturnStatus(disconnectHIDCommandStrategy))
         {
         LOG.debug("DefaultFinchController.disconnect(): Successfully disconnected from the finch.");
         }
      else
         {
         LOG.error("DefaultFinchController.disconnect(): Failed to disconnect from the finch.");
         }

      LOG.debug("DefaultFinchController.disconnect(): Now shutting down the HIDCommandExecutionQueue...");
      commandExecutionQueue.shutdown();
      }

   /**
    * Returns the state of the accelerometer in g's; returns <code>null</code> if an error occurred while trying to read
    * the state.
    */
   public AccelerometerGs getAccelerometerGs()
      {
      if (accelerometerUnitConversionStrategy != null)
         {
         return accelerometerUnitConversionStrategy.convert(getAccelerometerState());
         }

      return null;
      }

   public Boolean isObstacleDetected(final int id)
      {
      if (id >= 0 && id < FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT)
         {
         final boolean[] isDetected = areObstaclesDetected();
         if (isDetected != null && id < isDetected.length)
            {
            return isDetected[id];
            }
         }
      return null;
      }

   public Integer getThermistor()
      {
      return getThermistor(0);
      }

   public Double getThermistorCelsiusTemperature()
      {
      if (thermistorUnitConversionStrategy != null)
         {
         return thermistorUnitConversionStrategy.convertToCelsius(getThermistor(0));
         }

      return null;
      }

   /**
    * Sets the full-color LED to the given {@link Color color}.  Returns the current {@link Color} if the command
    * succeeded, <code>null</code> otherwise.
    */
   public boolean setFullColorLED(final Color color)
      {
      return setFullColorLED(color.getRed(),
                             color.getGreen(),
                             color.getBlue());
      }
   }
