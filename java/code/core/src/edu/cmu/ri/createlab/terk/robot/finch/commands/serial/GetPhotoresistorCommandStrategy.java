package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetPhotoresistorCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetPhotoresistorCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<int[]>
   {
   private final GetPhotoresistorCommandStrategyHelper helper = new GetPhotoresistorCommandStrategyHelper();

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return helper.getSizeOfExpectedResponse();
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }

   @Override
   public int[] convertResponse(final SerialDeviceCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }