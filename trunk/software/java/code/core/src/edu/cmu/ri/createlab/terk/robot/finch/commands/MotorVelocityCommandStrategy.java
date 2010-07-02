package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorVelocityCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The command character used to set the motor velocities. */
   private static final byte COMMAND_PREFIX = 'M';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final byte[] command;

   public MotorVelocityCommandStrategy(final int leftVelocity, final int rightVelocity)
      {
      final byte leftDirection = (byte)((leftVelocity < 0) ? 1 : 0);
      final byte leftSpeed = (byte)Math.abs(leftVelocity);
      final byte rightDirection = (byte)((rightVelocity < 0) ? 1 : 0);
      final byte rightSpeed = (byte)Math.abs(rightVelocity);
      this.command = new byte[]{COMMAND_PREFIX,
                                leftDirection,
                                leftSpeed,
                                rightDirection,
                                rightSpeed};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }