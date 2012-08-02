package edu.cmu.ri.createlab.terk.robot.finch;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FinchHardwareType</code> defines the various types of Finch hardware.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum FinchHardwareType
   {
      BACKPACK("Backpack"),
      HID("HID");

   private static final Logger LOG = Logger.getLogger(FinchHardwareType.class);

   /**
    * Returns the <code>FinchHardwareType</code> associated with the given <code>name</code> (case insensitive),
    * or <code>null</code> if no such type exists.
    */
   public static FinchHardwareType findByName(final String name)
      {
      if (name != null)
         {
         try
            {
            return FinchHardwareType.valueOf(name.toUpperCase());
            }
         catch (IllegalArgumentException e)
            {
            LOG.debug("IllegalArgumentException while trying to find a FinchHardwareType with name [" + name + "]", e);
            }
         }
      return null;
      }

   private final String name;

   FinchHardwareType(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   @Override
   public String toString()
      {
      return getName();
      }
   }
