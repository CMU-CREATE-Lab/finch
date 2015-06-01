package edu.cmu.ri.createlab.terk.robot.finch.commands;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DisconnectCommandStrategyHelper
   {
   /** The command character used to disconnect from the hummingbird and put it back into startup mode. */
   private static final byte[] COMMAND = {'R'};

   /** Returns a copy of command as a <code>byte</code> array. */
   public byte[] getCommand()
      {
      return COMMAND.clone();
      }
   }

