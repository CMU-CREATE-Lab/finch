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
import edu.cmu.ri.createlab.speech.Mouth;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseFinchController implements FinchController, CommandExecutionFailureHandler
   {
   private static final Logger LOG = Logger.getLogger(BaseFinchController.class);

   private static final int DELAY_IN_SECONDS_BETWEEN_PEER_PINGS = 2;

   private boolean isDisconnected = false;
   private final Pinger pinger = new Pinger();
   private final ScheduledExecutorService pingExecutorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BaseFinchController.pingExecutorService"));
   private final ScheduledFuture<?> pingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   protected BaseFinchController()
      {
      // schedule periodic pings
      pingScheduledFuture = pingExecutorService.scheduleAtFixedRate(pinger,
                                                                    DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay before first ping
                                                                    DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay between pings
                                                                    TimeUnit.SECONDS);
      }

   @Override
   public final void handleExecutionFailure()
      {
      pinger.forceFailure();
      }

   @Override
   public final void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   @Override
   public final void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
      }

   @Override
   public final void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   @Override
   public final void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   @Override
   public final byte[] getSpeech(final String whatToSay)
      {
      if (whatToSay != null && whatToSay.length() > 0)
         {
         final Mouth mouth = Mouth.getInstance();

         if (mouth != null)
            {
            return mouth.getSpeech(whatToSay);
            }
         }
      return null;
      }

   @Override
   public final void speak(final String whatToSay)
      {
      final byte[] speechAudio = getSpeech(whatToSay);
      if (speechAudio != null)
         {
         // play it this way since Mouth.speak() is deprecated
         AudioHelper.playClip(speechAudio);
         }
      }

   protected abstract AccelerometerUnitConversionStrategy getAccelerometerUnitConversionStrategy();

   /**
    * Returns the state of the accelerometer in g's; returns <code>null</code> if an error occurred while trying to read
    * the state.
    */
   public final AccelerometerGs getAccelerometerGs()
      {
      final AccelerometerUnitConversionStrategy conversionStrategy = getAccelerometerUnitConversionStrategy();
      if (conversionStrategy != null)
         {
         return conversionStrategy.convert(getAccelerometerState());
         }

      return null;
      }

   public final Boolean isObstacleDetected(final int id)
      {
      if (id >= 0 && id < getFinchProperties().getSimpleObstacleSensorDeviceCount())
         {
         final boolean[] isDetected = areObstaclesDetected();
         if (isDetected != null && id < isDetected.length)
            {
            return isDetected[id];
            }
         }
      return null;
      }

   public final Integer getThermistor()
      {
      return getThermistor(0);
      }

   protected abstract ThermistorUnitConversionStrategy getThermistorUnitConversionStrategy();

   public final Double getThermistorCelsiusTemperature()
      {
      final ThermistorUnitConversionStrategy conversionStrategy = getThermistorUnitConversionStrategy();
      if (conversionStrategy != null)
         {
         return conversionStrategy.convertToCelsius(getThermistor(0));
         }

      return null;
      }

   /**
    * Sets the full-color LED to the given {@link Color color}.  Returns the current {@link Color} if the command
    * succeeded, <code>null</code> otherwise.
    */
   public final boolean setFullColorLED(final Color color)
      {
      return setFullColorLED(color.getRed(),
                             color.getGreen(),
                             color.getBlue());
      }

   @Override
   public final void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("BaseFinchController.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the pinger
      try
         {
         LOG.debug("BaseFinchController.disconnect(): Shutting down finch pinger...");
         pingScheduledFuture.cancel(false);
         pingExecutorService.shutdownNow();
         LOG.debug("BaseFinchController.disconnect(): Successfully shut down the Finch pinger.");
         }
      catch (Exception e)
         {
         LOG.error("BaseFinchController.disconnect(): Exception caught while trying to shut down pinger", e);
         }

      // optionally send goodbye command to the Finch
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("BaseFinchController.disconnect(): Now attempting to send the disconnect command to the Finch");
         try
            {
            if (disconnectAndReturnStatus())
               {
               LOG.debug("BaseFinchController.disconnect(): Successfully disconnected from the Finch.");
               }
            else
               {
               LOG.error("BaseFinchController.disconnect(): Failed to disconnect from the Finch.");
               }
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute the disconnect", e);
            }
         }

      // shut down the command queue, which closes the connection to the device
      try
         {
         LOG.debug("BaseFinchController.disconnect(): shutting down the CommandExecutionQueue...");
         shutdownCommandQueue();
         isDisconnected = true;
         LOG.debug("BaseFinchController.disconnect(): done shutting down the CommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("BaseFinchController.disconnect(): Exception while trying to shut down the CommandExecutionQueue", e);
         }
      }

   public final boolean isDisconnected()
      {
      return isDisconnected;
      }

   protected abstract boolean disconnectAndReturnStatus() throws Exception;

   protected abstract void shutdownCommandQueue();

   private class Pinger implements Runnable
      {
      public void run()
         {
         try
            {
            // ping the device
            final CommandResponse response = executePingCommand();
            final boolean pingSuccessful = (response != null) && response.wasSuccessful();

            // if the ping failed, then we know we have a problem, so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               handlePingFailure();
               }
            }
         catch (Exception e)
            {
            LOG.error("BaseFinchController$Pinger.run(): Exception caught while executing the pinger", e);
            }
         }

      private void handlePingFailure()
         {
         try
            {
            LOG.debug("BaseFinchController$Pinger.handlePingFailure(): Peer ping failed.  Attempting to disconnect...");
            disconnect(false);
            LOG.debug("BaseFinchController$Pinger.handlePingFailure(): Done disconnecting from the Finch");
            }
         catch (Exception e)
            {
            LOG.error("BaseFinchController$Pinger.handlePingFailure(): Exeption caught while trying to disconnect from the Finch", e);
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("BaseFinchController$Pinger.handlePingFailure(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
            }
         for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
            {
            try
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   BaseFinchController$Pinger.handlePingFailure(): Notifying " + listener);
                  }
               listener.handlePingFailureEvent();
               }
            catch (Exception e)
               {
               LOG.error("BaseFinchController$Pinger.handlePingFailure(): Exeption caught while notifying CreateLabDevicePingFailureEventListener", e);
               }
            }
         }

      private void forceFailure()
         {
         handlePingFailure();
         }
      }

   protected abstract CommandResponse executePingCommand() throws Exception;
   }
