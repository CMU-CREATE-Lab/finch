package edu.cmu.ri.createlab.terk.robot.finch.commands.hid;

import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.robot.finch.commands.MotorVelocityCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorVelocityCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final MotorVelocityCommandStrategyHelper helper;

   public MotorVelocityCommandStrategy(final int leftVelocity, final int rightVelocity, final FinchProperties finchProperties)
      {
      helper = new MotorVelocityCommandStrategyHelper(leftVelocity, rightVelocity, finchProperties);
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
   }