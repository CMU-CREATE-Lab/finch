package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.thermistor.BaseThermistorServiceImpl;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ThermistorServiceImpl extends BaseThermistorServiceImpl
   {
   static ThermistorServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, finchController.getFinchProperties().getThermistorDeviceCount());
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_THERMISTOR_DEVICE_ID, finchController.getFinchProperties().getThermistorDeviceId());
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_MIN_VALUE, finchController.getFinchProperties().getThermistorMinValue());
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_MAX_VALUE, finchController.getFinchProperties().getThermistorMaxValue());

      return new ThermistorServiceImpl(finchController,
                                       basicPropertyManager,
                                       finchController.getFinchProperties().getThermistorDeviceCount());
      }

   private final FinchController finchController;

   private ThermistorServiceImpl(final FinchController finchController,
                                 final PropertyManager propertyManager,
                                 final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   public Integer getThermistorValue(final int id)
      {
      return finchController.getThermistor(id);
      }
   }