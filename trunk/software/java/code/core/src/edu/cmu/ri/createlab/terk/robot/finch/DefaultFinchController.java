package edu.cmu.ri.createlab.terk.robot.finch;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.audio.AudioHelper;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
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
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.usb.hid.HIDCommandExecutionQueue;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResult;
import edu.cmu.ri.createlab.usb.hid.HIDCommandStrategy;
import edu.cmu.ri.createlab.util.MathUtils;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultFinchController implements FinchController
   {
   private static final Log LOG = LogFactory.getLog(DefaultFinchController.class);
   private static final int DELAY_BETWEEN_PEER_PINGS = 2;

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
   private final ScheduledExecutorService peerPingScheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("FinchController.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final HIDCommandStrategy disconnectHIDCommandStrategy = new DisconnectCommandStrategy();
   private final HIDCommandStrategy emergencyStopHIDCommandStrategy = new EmergencyStopCommandStrategy();
   private final GetAccelerometerCommandStrategy getAccelerometerHIDCommandStrategy = new GetAccelerometerCommandStrategy();
   private final GetObstacleSensorCommandStrategy getObstacleSensorHIDCommandStrategy = new GetObstacleSensorCommandStrategy();
   private final GetPhotoresistorCommandStrategy getPhotoresistorHIDCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final GetThermistorCommandStrategy getThermistorHIDCommandStrategy = new GetThermistorCommandStrategy();
   private final AccelerometerUnitConversionStrategy accelerometerUnitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.ACCELEROMETER_DEVICE_ID);
   private final ThermistorUnitConversionStrategy thermistorUnitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.THERMISTOR_DEVICE_ID);
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   private DefaultFinchController(final HIDCommandExecutionQueue commandExecutionQueue)
      {
      this.commandExecutionQueue = commandExecutionQueue;

      // schedule periodic peer pings
      peerPingScheduledFuture = peerPingScheduler.scheduleAtFixedRate(new FinchPinger(),
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay before first ping
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay between pings
                                                                      TimeUnit.SECONDS);
      }

   public String getPortName()
      {
      // TODO: return the actual USB HID port name
      return "USB HID";
      }

   public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
      }

   public AccelerometerState getAccelerometerState()
      {
      final HIDCommandResult response = commandExecutionQueue.execute(getAccelerometerHIDCommandStrategy);

      return getAccelerometerHIDCommandStrategy.convertResult(response);
      }

   public boolean[] areObstaclesDetected()
      {
      final HIDCommandResult result = commandExecutionQueue.execute(getObstacleSensorHIDCommandStrategy);

      return getObstacleSensorHIDCommandStrategy.convertResult(result);
      }

   public int[] getPhotoresistors()
      {
      final HIDCommandResult result = commandExecutionQueue.execute(getPhotoresistorHIDCommandStrategy);

      return getPhotoresistorHIDCommandStrategy.convertResult(result);
      }

   public Integer getThermistor(final int id)
      {
      if (id >= 0 && id < FinchConstants.THERMISTOR_DEVICE_COUNT)
         {
         final HIDCommandResult result = commandExecutionQueue.execute(getThermistorHIDCommandStrategy);

         return getThermistorHIDCommandStrategy.convertResult(result);
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
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      // turn off the peer pinger
      try
         {
         LOG.debug("DefaultFinchController.disconnect(): Shutting down finch pinger...");
         peerPingScheduledFuture.cancel(false);
         peerPingScheduler.shutdownNow();
         LOG.debug("DefaultFinchController.disconnect(): Successfully shut down finch pinger.");
         }
      catch (Exception e)
         {
         LOG.error("DefaultFinchController.disconnect(): Exception caught while trying to shut down peer pinger", e);
         }

      if (willAddDisconnectCommandToQueue)
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
         }
      else
         {
         LOG.debug("DefaultFinchController.disconnect(): Won't try to disconnect from the Finch since willAddDisconnectCommandToQueue was false");
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

   private class FinchPinger implements Runnable
      {
      public void run()
         {
         try
            {
            LOG.trace("FinchProxy$FinchPinger.run()");

            // for pings, we simply get the state of the thermistor
            final boolean pingSuccessful = (getThermistor() != null);

            // if the ping failed, then we know we have a problem so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               try
                  {
                  LOG.error("FinchProxy$FinchPinger.run(): Peer ping failed (received a null state).  Attempting to disconnect...");
                  disconnect(false);
                  LOG.error("FinchProxy$FinchPinger.run(): Done disconnecting from the finch");
                  }
               catch (Exception e)
                  {
                  LOG.error("FinchProxy$FinchPinger.run(): Exeption caught while trying to disconnect from the finch", e);
                  }

               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("FinchProxy$FinchPinger.run(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
                  }
               for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
                  {
                  try
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("   FinchProxy$FinchPinger.run(): Notifying " + listener);
                        }
                     listener.handlePingFailureEvent();
                     }
                  catch (Exception e)
                     {
                     LOG.error("FinchProxy$FinchPinger.run(): Exeption caught while notifying CreateLabDevicePingFailureEventListener", e);
                     }
                  }
               }
            }
         catch (Exception e)
            {
            LOG.error("FinchProxy$FinchPinger.run(): Exception caught while executing the peer pinger", e);
            }
         }
      }
   }
