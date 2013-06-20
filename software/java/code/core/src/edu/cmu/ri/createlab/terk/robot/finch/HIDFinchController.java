package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.BuzzerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.GetAccelerometerCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.GetObstacleSensorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.GetPhotoresistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.GetThermistorCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.hid.MotorVelocityCommandStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.usb.hid.HIDCommandExecutionQueue;
import edu.cmu.ri.createlab.usb.hid.HIDConnectionException;
import edu.cmu.ri.createlab.usb.hid.HIDDevice;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceFactory;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotFoundException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDFinchController extends BaseFinchController
   {
   private static final Logger LOG = Logger.getLogger(HIDFinchController.class);

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public static FinchController create()
      {
      try
         {
         // create the HID device
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HIDFinchController.create(): creating HID device for vendor ID [" + Integer.toHexString(HIDFinchProperties.UsbHidConfiguration.USB_VENDOR_ID) + "] and product ID [" + Integer.toHexString(HIDFinchProperties.UsbHidConfiguration.USB_PRODUCT_ID) + "]");
            }
         final HIDDevice hidDevice = HIDDeviceFactory.create(HIDFinchProperties.UsbHidConfiguration.FINCH_HID_DEVICE_DESCRIPTOR);

         LOG.debug("HIDFinchController.create(): attempting connection...");
         hidDevice.connectExclusively();

         // create the HID device command execution queue (which will attempt to connect to the device)
         final HIDCommandExecutionQueue commandQueue = new HIDCommandExecutionQueue(hidDevice);
         if (commandQueue != null)
            {
            // create the FinchController
            final HIDFinchController finchController = new HIDFinchController(commandQueue, hidDevice);

            // call the emergency stop command immediately, to make sure the LED and motors are turned off.
            finchController.emergencyStop();

            return finchController;
            }
         }
      catch (UnsupportedOperationException e)
         {
         LOG.error("UnsupportedOperationException caught while trying to create the HIDCommandExecutionQueue", e);
         System.err.println(e);
         System.exit(1);
         }
      catch (HIDConnectionException e)
         {
         LOG.error("HIDConnectionException while trying to connect to the Finch, returning null", e);
         }
      catch (HIDDeviceNotFoundException e)
         {
         LOG.error("HIDDeviceNotFoundException while trying to connect to the Finch, returning null", e);
         }
      return null;
      }

   private final FinchProperties finchProperties = HIDFinchProperties.getInstance();

   private final HIDCommandExecutionQueue commandQueue;
   private final HIDDevice hidDevice;

   private final DisconnectCommandStrategy disconnectHIDCommandStrategy = new DisconnectCommandStrategy();
   private final GetAccelerometerCommandStrategy getAccelerometerCommandStrategy = new GetAccelerometerCommandStrategy();
   private final GetObstacleSensorCommandStrategy getObstacleSensorCommandStrategy = new GetObstacleSensorCommandStrategy();
   private final GetPhotoresistorCommandStrategy getPhotoresistorCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final GetThermistorCommandStrategy getThermistorCommandStrategy = new GetThermistorCommandStrategy();
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();

   private final HIDDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<AccelerometerState> accelerometerStateReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<boolean[]> booleanArrayStateReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<int[]> intArrayStateReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;

   private final AccelerometerUnitConversionStrategy accelerometerUnitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(finchProperties.getAccelerometerDeviceId());
   private final ThermistorUnitConversionStrategy thermistorUnitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(finchProperties.getThermistorDeviceId());

   private HIDFinchController(final HIDCommandExecutionQueue commandQueue, final HIDDevice hidDevice)
      {
      super(true);
      this.commandQueue = commandQueue;
      this.hidDevice = hidDevice;

      noReturnValueCommandExecutor = new HIDDeviceNoReturnValueCommandExecutor(commandQueue, this);
      accelerometerStateReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<AccelerometerState>(commandQueue, this);
      booleanArrayStateReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<boolean[]>(commandQueue, this);
      intArrayStateReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<int[]>(commandQueue, this);
      integerReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<Integer>(commandQueue, this);
      }

   @Override
   public FinchProperties getFinchProperties()
      {
      return finchProperties;
      }

   @Override
   public String getPortName()
      {
      return hidDevice.getDeviceFilename();
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

   public Integer getThermistor(final int id)
      {
      if (id >= 0 && id < finchProperties.getThermistorDeviceCount())
         {
         return integerReturnValueCommandExecutor.execute(getThermistorCommandStrategy);
         }

      return null;
      }

   /**
    * Finches connected via USB HID don't have analog inputs, so this method will always return <code>null</code>.
    */
   @Override
   public Integer getAnalogInput(final int id)
      {
      return null;
      }

   /**
    * Returns the voltage.  Finches connected via USB HID don't have a useful voltage value to return, so this method
    * will always return <code>null</code>.
    */
   @Override
   public Integer getVoltage()
      {
      return null;
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

   @Override
   protected CommandResponse executePingCommand() throws Exception
      {
      return commandQueue.execute(getThermistorCommandStrategy);
      }
   }
