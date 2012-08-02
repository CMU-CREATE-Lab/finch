package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetAccelerometerCommandStrategyHelper;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAccelerometerCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<AccelerometerState>
   {
   /**
    * The size of the expected response, in bytes.  This is 4 bytes because the first three are the X, Y, and Z values,
    * and the last byte contains the bits for determining whether it was tapped or shaken.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 4;

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
   public AccelerometerState convertResponse(final SerialDeviceCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         return helper.convertResponse(responseData[0],
                                       responseData[1],
                                       responseData[2],
                                       responseData[3]);
         }

      return null;
      }
   }