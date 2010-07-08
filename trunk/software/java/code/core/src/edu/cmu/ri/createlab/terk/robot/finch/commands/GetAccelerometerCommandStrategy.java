package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResult;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAccelerometerCommandStrategy extends ReturnValueCommandStrategy<AccelerometerState>
   {
   /** The command character used to request the value of the finch's accelerometer. */
   private static final byte[] COMMAND = {'A'};

   /**
    * The size of the expected response, in bytes.  This is 4 bytes because the first byte is bogus data, the next three
    * are the X, Y, and Z values.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 4;

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   public AccelerometerState convertResult(final HIDCommandResult result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         // NOTE: we're ignoring responseData[0] here on purpose since it's bogus data (see SIZE_IN_BYTES_OF_EXPECTED_RESPONSE above)
         return new AccelerometerState(ByteUtils.unsignedByteToInt(responseData[1]),
                                       ByteUtils.unsignedByteToInt(responseData[2]),
                                       ByteUtils.unsignedByteToInt(responseData[3]));
         }

      return null;
      }
   }