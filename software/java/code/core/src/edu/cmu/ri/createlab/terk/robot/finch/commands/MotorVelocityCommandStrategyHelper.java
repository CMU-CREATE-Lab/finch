package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorVelocityCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to set the motor velocities. */
   private static final byte COMMAND_PREFIX = 'M';

   private final byte[] command;

   public MotorVelocityCommandStrategyHelper(final int leftVelocity, final int rightVelocity, final FinchProperties finchProperties)
      {
      final int minVelocity = finchProperties.getMotorDeviceMinVelocity();
      final int maxVelocity = finchProperties.getMotorDeviceMaxVelocity();
      final int cleanedLeftVelocity = MathUtils.ensureRange(leftVelocity, minVelocity, maxVelocity);
      final int cleanedRightVelocity = MathUtils.ensureRange(rightVelocity, minVelocity, maxVelocity);
      final byte leftDirection = (byte)((cleanedLeftVelocity < 0) ? 1 : 0);
      final byte leftSpeed = (byte)Math.abs(cleanedLeftVelocity);
      final byte rightDirection = (byte)((cleanedRightVelocity < 0) ? 1 : 0);
      final byte rightSpeed = (byte)Math.abs(cleanedRightVelocity);
      this.command = new byte[]{COMMAND_PREFIX,
                                leftDirection,
                                leftSpeed,
                                rightDirection,
                                rightSpeed};
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }
