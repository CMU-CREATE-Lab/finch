package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetThermistorCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetThermistorCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   private final GetThermistorCommandStrategyHelper helper = new GetThermistorCommandStrategyHelper();

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
   public Integer convertResponse(final SerialDeviceCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }
