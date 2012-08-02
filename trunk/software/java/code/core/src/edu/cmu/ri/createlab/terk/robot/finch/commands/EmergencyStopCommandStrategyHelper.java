package edu.cmu.ri.createlab.terk.robot.finch.commands;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EmergencyStopCommandStrategyHelper
   {
   /** The command character used to trigger the emergency stop. */
   private static final byte COMMAND_PREFIX = 'X';

   private final byte[] command;

   public EmergencyStopCommandStrategyHelper()
      {
      this.command = new byte[]{COMMAND_PREFIX};
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }
