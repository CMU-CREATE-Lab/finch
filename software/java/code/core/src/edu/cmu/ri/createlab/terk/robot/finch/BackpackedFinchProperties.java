package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.StopBits;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BackpackedFinchProperties extends BaseFinchProperties
   {
   private static final FinchProperties INSTANCE = new BackpackedFinchProperties();

   private static final String DEVICE_COMMON_NAME = "Finch with Backpack";

   private static final FinchHardwareType HARDWARE_TYPE = FinchHardwareType.BACKPACK;

   private static final int BACKPACK_DEVICE_COUNT = 1;
   private static final int ANALOG_INPUT_DEVICE_COUNT = 7;

   static final class SerialConfiguration
      {
      public static final BaudRate BAUD_RATE = BaudRate.BAUD_125000;
      public static final CharacterSize CHARACTER_SIZE = CharacterSize.EIGHT;
      public static final Parity PARITY = Parity.NONE;
      public static final StopBits STOP_BITS = StopBits.ONE;
      public static final FlowControl FLOW_CONTROL = FlowControl.NONE;

      private SerialConfiguration()
         {
         // private to prevent instantiation
         }
      }

   static FinchProperties getInstance()
      {
      return INSTANCE;
      }

   private BackpackedFinchProperties()
      {
      // private to prevent instantiation
      }

   @Override
   public String getDeviceCommonName()
      {
      return DEVICE_COMMON_NAME;
      }

   @Override
   public FinchHardwareType getHardwareType()
      {
      return HARDWARE_TYPE;
      }

   @Override
   public int getFinchBackpackDeviceCount()
      {
      return BACKPACK_DEVICE_COUNT;
      }

   @Override
   public int getAnalogInputDeviceCount()
      {
      return ANALOG_INPUT_DEVICE_COUNT;
      }
   }
