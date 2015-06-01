package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.finch.BaseFinchBackpackServiceImpl;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FinchBackpackServiceImpl extends BaseFinchBackpackServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(FinchBackpackServiceImpl.class);

   static FinchBackpackServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, finchController.getFinchProperties().getFinchBackpackDeviceCount());

      return new FinchBackpackServiceImpl(finchController,
                                          basicPropertyManager,
                                          finchController.getFinchProperties().getFinchBackpackDeviceCount());
      }

   private final FinchController finchController;

   private FinchBackpackServiceImpl(final FinchController finchController,
                                    final PropertyManager propertyManager,
                                    final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   @Override
   public Integer getVoltage()
      {
      return finchController.getVoltage();
      }
   }