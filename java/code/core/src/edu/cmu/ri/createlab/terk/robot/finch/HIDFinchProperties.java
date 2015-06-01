package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDFinchProperties extends BaseFinchProperties
   {
   private static final FinchProperties INSTANCE = new HIDFinchProperties();

   private static final String DEVICE_COMMON_NAME = "HID Finch";

   private static final FinchHardwareType HARDWARE_TYPE = FinchHardwareType.HID;

   private static final int BACKPACK_DEVICE_COUNT = 0;
   private static final int ANALOG_INPUT_DEVICE_COUNT = 0;

   static final class UsbHidConfiguration
      {
      public static final short USB_VENDOR_ID = 0x2354;
      public static final short USB_PRODUCT_ID = 0x1111;

      private static final int INPUT_REPORT_LENGTH_IN_BYTES = 9;  // count includes the report ID
      private static final int OUTPUT_REPORT_LENGTH_IN_BYTES = 9; // count includes the report ID

      public static final HIDDeviceDescriptor FINCH_HID_DEVICE_DESCRIPTOR = new HIDDeviceDescriptor(USB_VENDOR_ID,
                                                                                                    USB_PRODUCT_ID,
                                                                                                    INPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                    OUTPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                    DEVICE_COMMON_NAME);

      private UsbHidConfiguration()
         {
         // private to prevent instantiation
         }
      }

   static FinchProperties getInstance()
      {
      return INSTANCE;
      }

   private HIDFinchProperties()
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
