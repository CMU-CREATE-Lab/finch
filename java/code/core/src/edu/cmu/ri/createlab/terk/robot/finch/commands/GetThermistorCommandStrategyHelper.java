package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetThermistorCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to request the value of the finch's thermistor. */
   private static final byte[] COMMAND = {'T'};

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 1;

   public GetThermistorCommandStrategyHelper()
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

   public Integer convertResponse(final CommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return ByteUtils.unsignedByteToInt(response.getData()[0]);
         }
      return null;
      }
   }
