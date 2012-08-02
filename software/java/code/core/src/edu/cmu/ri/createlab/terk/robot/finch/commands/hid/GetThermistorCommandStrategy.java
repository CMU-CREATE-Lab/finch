package edu.cmu.ri.createlab.terk.robot.finch.commands.hid;

import edu.cmu.ri.createlab.terk.robot.finch.commands.GetThermistorCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetThermistorCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<Integer>
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
   public Integer convertResponse(final HIDCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }