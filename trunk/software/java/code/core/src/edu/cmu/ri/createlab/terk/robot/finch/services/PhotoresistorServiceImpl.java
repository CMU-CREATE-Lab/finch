package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.photoresistor.BasePhotoresistorServiceImpl;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class PhotoresistorServiceImpl extends BasePhotoresistorServiceImpl
   {
   static PhotoresistorServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, finchController.getFinchProperties().getPhotoresistorDeviceCount());
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_MIN_VALUE, finchController.getFinchProperties().getPhotoresistorMinValue());
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_MAX_VALUE, finchController.getFinchProperties().getPhotoresistorMaxValue());

      return new PhotoresistorServiceImpl(finchController,
                                          basicPropertyManager,
                                          finchController.getFinchProperties().getPhotoresistorDeviceCount());
      }

   private final FinchController finchController;

   private PhotoresistorServiceImpl(final FinchController finchController,
                                    final PropertyManager propertyManager,
                                    final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   public Integer getPhotoresistorValue(final int id)
      {
      final int[] values = getPhotoresistorValues();
      if ((values != null) && (id >= 0) && (id < values.length))
         {
         return values[id];
         }

      return null;
      }

   public int[] getPhotoresistorValues()
      {
      return finchController.getPhotoresistors();
      }
   }