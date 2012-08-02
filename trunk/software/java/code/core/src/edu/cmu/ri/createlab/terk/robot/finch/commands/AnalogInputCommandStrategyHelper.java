package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AnalogInputCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to request the value of one of the finch's analog inputs. */
   private static final byte COMMAND_PREFIX = 'a';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 1;

   private final byte[] command;

   public AnalogInputCommandStrategyHelper(final int analogInputPortId, final FinchProperties finchProperties)
      {
      if (analogInputPortId < 0 || analogInputPortId >= finchProperties.getAnalogInputDeviceCount())
         {
         throw new IllegalArgumentException("Invalid analog input port index");
         }

      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex(analogInputPortId)};
      }

   public byte[] getCommand()
      {
      return command.clone();
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