package edu.cmu.ri.createlab.terk.services.finch;

import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseFinchBackpackServiceImpl extends BaseDeviceControllingService implements FinchBackpackService
   {
   protected BaseFinchBackpackServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }
   }