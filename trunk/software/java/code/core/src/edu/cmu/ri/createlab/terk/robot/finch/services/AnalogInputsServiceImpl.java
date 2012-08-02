package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.analog.BaseAnalogInputsServiceImpl;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AnalogInputsServiceImpl extends BaseAnalogInputsServiceImpl
   {
   static AnalogInputsServiceImpl create(final FinchController finchController)
      {
      final FinchProperties finchProperties = finchController.getFinchProperties();
      final int deviceCount = finchProperties.getAnalogInputDeviceCount();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);
      basicPropertyManager.setReadOnlyProperty(AnalogInputsService.PROPERTY_NAME_MIN_VALUE, finchProperties.getAnalogInputMinValue());
      basicPropertyManager.setReadOnlyProperty(AnalogInputsService.PROPERTY_NAME_MAX_VALUE, finchProperties.getAnalogInputMaxValue());

      return new AnalogInputsServiceImpl(finchController,
                                         basicPropertyManager,
                                         deviceCount);
      }

   private final FinchController finchController;

   private AnalogInputsServiceImpl(final FinchController finchController,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   @Override
   public Integer getAnalogInputValue(final int analogInputPortId)
      {
      // check id
      if (analogInputPortId < 0 || analogInputPortId >= getDeviceCount())
         {
         throw new IllegalArgumentException("The analog input port id " + analogInputPortId + " is not valid.  Ids must be within the range [0," + getDeviceCount() + ")");
         }

      return finchController.getAnalogInput(analogInputPortId);
      }

   @Override
   public int[] getAnalogInputValues()
      {
      final int[] values = new int[getDeviceCount()];
      for (int i = 0; i < getDeviceCount(); i++)
         {
         values[i] = getAnalogInputValue(i);
         }
      return values;
      }
   }