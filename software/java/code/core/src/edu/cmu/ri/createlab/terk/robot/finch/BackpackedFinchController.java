package edu.cmu.ri.createlab.terk.robot.finch;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.BuzzerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetAccelerometerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetAnalogInputCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetObstacleSensorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetPhotoresistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetThermistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.GetVoltageCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.HandshakeCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.serial.MotorVelocityCommandStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategyFinder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BackpackedFinchController extends BaseFinchController
   {
   private static final Logger LOG = Logger.getLogger(BackpackedFinchController.class);

   /**
    * Tries to create a <code>FinchController</code> by connecting to a Finch Backpack on the serial port specified by
    * the given <code>serialPortName</code>.  Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public static FinchController create(final String serialPortName)
      {
      // a little error checking...
      if (serialPortName == null)
         {
         throw new IllegalArgumentException("The serial port name may not be null");
         }

      // create the serial port configuration
      final SerialIOConfiguration config = new SerialIOConfiguration(serialPortName,
                                                                     BackpackedFinchProperties.SerialConfiguration.BAUD_RATE,
                                                                     BackpackedFinchProperties.SerialConfiguration.CHARACTER_SIZE,
                                                                     BackpackedFinchProperties.SerialConfiguration.PARITY,
                                                                     BackpackedFinchProperties.SerialConfiguration.STOP_BITS,
                                                                     BackpackedFinchProperties.SerialConfiguration.FLOW_CONTROL);

      try
         {
         // create the serial port command queue
         final SerialDeviceCommandExecutionQueue commandQueue = SerialDeviceCommandExecutionQueue.create(BackpackedFinchProperties.getInstance().getDeviceCommonName(), config);

         // see whether its creation was successful
         if (commandQueue == null)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("Failed to open serial port '" + serialPortName + "'");
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("Serial port '" + serialPortName + "' opened.");
               }

            // Check the battery voltage to make sure you're getting proper data back
             final SerialDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor =
             					new SerialDeviceReturnValueCommandExecutor<Integer>(commandQueue,
																								  new CommandExecutionFailureHandler()
																								  {
																								  @Override
																								  public void handleExecutionFailure()
																									 {
																										//commandQueue.shutdown();
																									 }
																								  });
			final Integer voltage =  integerReturnValueCommandExecutor.execute(new GetVoltageCommandStrategy());

			boolean wasHandshakeSuccessful = false;
			if(voltage != null)
			{
				if(voltage <= 255 && voltage > 100)
					wasHandshakeSuccessful = true;
			}

			// see if the handshake was a success

			 if (wasHandshakeSuccessful)
			   {
			   LOG.info("Finch handshake successful!");

			   // now create and return the proxy
			   return new BackpackedFinchController(commandQueue, serialPortName);
			   }
			else
			   {
			   LOG.error("Failed to handshake with finch");
			   }

			// the handshake failed, so shutdown the command queue to release the serial port
			commandQueue.shutdown();
			}
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to create the BackpackedFinchController", e);
         }

      return null;
      }

   private final FinchProperties finchProperties = BackpackedFinchProperties.getInstance();

   private final SerialDeviceCommandExecutionQueue commandQueue;
   private final String serialPortName;

   private final DisconnectCommandStrategy disconnectHIDCommandStrategy = new DisconnectCommandStrategy();
   private final GetAccelerometerCommandStrategy getAccelerometerCommandStrategy = new GetAccelerometerCommandStrategy();
   private final GetObstacleSensorCommandStrategy getObstacleSensorCommandStrategy = new GetObstacleSensorCommandStrategy();
   private final GetPhotoresistorCommandStrategy getPhotoresistorCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final GetThermistorCommandStrategy getThermistorCommandStrategy = new GetThermistorCommandStrategy();
   private final GetVoltageCommandStrategy getVoltageCommandStrategy = new GetVoltageCommandStrategy();
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();

   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();

   private final SerialDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<AccelerometerState> accelerometerStateReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<boolean[]> booleanArrayStateReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<int[]> intArrayStateReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;

   private final AccelerometerUnitConversionStrategy accelerometerUnitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(finchProperties.getAccelerometerDeviceId());
   private final ThermistorUnitConversionStrategy thermistorUnitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(finchProperties.getThermistorDeviceId());

   private BackpackedFinchController(final SerialDeviceCommandExecutionQueue commandQueue, final String serialPortName)
      {
      super(false);
      this.commandQueue = commandQueue;
      this.serialPortName = serialPortName;

      noReturnValueCommandExecutor = new SerialDeviceNoReturnValueCommandExecutor(commandQueue, this);
      accelerometerStateReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<AccelerometerState>(commandQueue, this);
      booleanArrayStateReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<boolean[]>(commandQueue, this);
      intArrayStateReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<int[]>(commandQueue, this);
      integerReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<Integer>(commandQueue, this);

      // initialize the analog input command strategy map
      for (int i = 0; i < finchProperties.getAnalogInputDeviceCount(); i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i, finchProperties));
         }
      }

   @Override
   public FinchProperties getFinchProperties()
      {
      return finchProperties;
      }

   @Override
   public String getPortName()
      {
      return serialPortName;
      }

   @Override
   protected AccelerometerUnitConversionStrategy getAccelerometerUnitConversionStrategy()
      {
      return accelerometerUnitConversionStrategy;
      }

   @Override
   public AccelerometerState getAccelerometerState()
      {
      return accelerometerStateReturnValueCommandExecutor.execute(getAccelerometerCommandStrategy);
      }

   @Override
   public boolean[] areObstaclesDetected()
      {
      return booleanArrayStateReturnValueCommandExecutor.execute(getObstacleSensorCommandStrategy);
      }

   @Override
   public int[] getPhotoresistors()
      {
      return intArrayStateReturnValueCommandExecutor.execute(getPhotoresistorCommandStrategy);
      }

   @Override
   protected ThermistorUnitConversionStrategy getThermistorUnitConversionStrategy()
      {
      return thermistorUnitConversionStrategy;
      }

   @Override
   public Integer getThermistor(final int id)
      {
      if (id >= 0 && id < finchProperties.getThermistorDeviceCount())
         {
         return integerReturnValueCommandExecutor.execute(getThermistorCommandStrategy);
         }

      return null;
      }

   @Override
   public Integer getAnalogInput(final int id)
      {
      final GetAnalogInputCommandStrategy strategy = analogInputCommandStategyMap.get(id);

      if (strategy != null)
         {
         return integerReturnValueCommandExecutor.execute(strategy);
         }

      return null;
      }

   @Override
   public Integer getVoltage()
      {
      return integerReturnValueCommandExecutor.execute(getVoltageCommandStrategy);
      }

   @Override
   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(red, green, blue, finchProperties));
      }

   @Override
   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return noReturnValueCommandExecutor.execute(new MotorVelocityCommandStrategy(leftVelocity,
                                                                                   rightVelocity,
                                                                                   finchProperties));
      }

   @Override
   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return noReturnValueCommandExecutor.execute(new BuzzerCommandStrategy(frequency, durationInMilliseconds, finchProperties));
      }

   @Override
   public boolean emergencyStop()
      {
      return noReturnValueCommandExecutor.execute(emergencyStopCommandStrategy);
      }

   @Override
   protected boolean disconnectAndReturnStatus() throws Exception
      {
      return commandQueue.executeAndReturnStatus(disconnectHIDCommandStrategy);
      }

   @Override
   protected void shutdownCommandQueue()
      {
      commandQueue.shutdown();
      }
   }
