package edu.cmu.ri.createlab.terk.services.finch;

import edu.cmu.ri.createlab.terk.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchBackpackService extends Service
   {
   String TYPE_ID = "::TeRK::finch::FinchBackpackService";

   // TODO: document what units are being used

   /** Returns the backpack's voltage or <code>null</code> if the voltage could not be obtained. */
   Integer getVoltage();
   }