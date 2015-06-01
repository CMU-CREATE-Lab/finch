package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetPhotoresistorCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to request the value of the finch's photoresistors. */
   private static final byte[] COMMAND = {'L'};

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 2;

   public GetPhotoresistorCommandStrategyHelper()
      {
      }

   public int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   public byte[] getCommand()
      {
      return COMMAND.clone();
      }

   public int[] convertResponse(final CommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new int[]{ByteUtils.unsignedByteToInt(responseData[0]),
                          ByteUtils.unsignedByteToInt(responseData[1])};
         }

      return null;
      }
   }
