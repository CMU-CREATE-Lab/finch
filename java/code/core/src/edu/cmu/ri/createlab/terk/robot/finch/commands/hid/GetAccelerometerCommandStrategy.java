package edu.cmu.ri.createlab.terk.robot.finch.commands.hid;

import edu.cmu.ri.createlab.terk.robot.finch.commands.GetAccelerometerCommandStrategyHelper;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAccelerometerCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<AccelerometerState>
   {
   /**
    * The size of the expected response, in bytes.  This is 5 bytes because the first byte is bogus data, the next three
    * are the X, Y, and Z values, and the last byte contains the bits for determining whether it was tapped or shaken.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 5;

   private final GetAccelerometerCommandStrategyHelper helper = new GetAccelerometerCommandStrategyHelper();

   public GetAccelerometerCommandStrategy()
      {
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }

   @Override
   public AccelerometerState convertResponse(final HIDCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         // NOTE: we're ignoring responseData[0] here on purpose since it's bogus data (see SIZE_IN_BYTES_OF_EXPECTED_RESPONSE above)
         return helper.convertResponse(responseData[1],
                                       responseData[2],
                                       responseData[3],
                                       responseData[4]);
         }

      return null;
      }
   }