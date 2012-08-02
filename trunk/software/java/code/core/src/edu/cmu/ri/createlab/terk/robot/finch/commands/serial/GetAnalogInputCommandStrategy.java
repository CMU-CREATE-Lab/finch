package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.robot.finch.commands.AnalogInputCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAnalogInputCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   private final AnalogInputCommandStrategyHelper helper;

   public GetAnalogInputCommandStrategy(final int analogInputPortId, final FinchProperties finchProperties)
      {
      this.helper = new AnalogInputCommandStrategyHelper(analogInputPortId, finchProperties);
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return AnalogInputCommandStrategyHelper.SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
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