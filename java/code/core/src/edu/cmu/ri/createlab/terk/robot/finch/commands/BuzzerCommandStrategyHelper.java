package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BuzzerCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to set the buzzer frequency. */
   private static final byte COMMAND_PREFIX = 'B';

   private final byte[] command;

   public BuzzerCommandStrategyHelper(final int frequency, final int durationInMilliseconds, final FinchProperties finchProperties)
      {
      final int minFrequency = finchProperties.getBuzzerDeviceMinFrequency();
      final int maxFrequency = finchProperties.getBuzzerDeviceMaxFrequency();
      final int minDuration = finchProperties.getBuzzerDeviceMinDuration();
      final int maxDuration = finchProperties.getBuzzerDeviceMaxDuration();
      final int cleanedFrequency = MathUtils.ensureRange(frequency, minFrequency, maxFrequency);
      final int cleanedDurationInMilliseconds = MathUtils.ensureRange(durationInMilliseconds, minDuration, maxDuration);
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(cleanedDurationInMilliseconds),
                                getLowByteFromInt(cleanedDurationInMilliseconds),
                                getHighByteFromInt(cleanedFrequency),
                                getLowByteFromInt(cleanedFrequency)};
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }
