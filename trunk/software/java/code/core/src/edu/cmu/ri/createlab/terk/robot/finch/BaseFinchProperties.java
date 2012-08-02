package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.accelerometer.unitconversionstrategies.AccelerometerUnitConversionStrategyFreescaleMMA7660FC;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.unitconversionstrategies.ThermistorUnitConversionStrategyMF52A103F3380;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseFinchProperties implements FinchProperties
   {
   /** The number of finches */
   public static final int FINCH_DEVICE_COUNT = 1;

   /** The number of accelerometers */
   public static final int ACCELEROMETER_DEVICE_COUNT = 1;

   /**
    * The unique device id for the accelerometer used by all finches.  This value is used to lookup the appropriate
    * {@link AccelerometerUnitConversionStrategy} for converting acclerometer values into g's.
    */
   public static final String ACCELEROMETER_DEVICE_ID = AccelerometerUnitConversionStrategyFreescaleMMA7660FC.DEVICE_ID;

   /** The minimum value returned by analog inputs */
   private static final int ANALOG_INPUT_MIN_VALUE = 0;

   /** The maximum value returned by analog inputs */
   private static final int ANALOG_INPUT_MAX_VALUE = 255;

   /** The number of audio outputs */
   private static final int AUDIO_DEVICE_COUNT = 1;

   /** The minimum supported tone frequency */
   private static final int AUDIO_DEVICE_MIN_AMPLITUDE = 0;

   /** The maximum supported tone frequency */
   private static final int AUDIO_DEVICE_MAX_AMPLITUDE = 10;

   /** The minimum supported tone duration */
   private static final int AUDIO_DEVICE_MIN_DURATION = 0;

   /** The maximum supported tone duration */
   private static final int AUDIO_DEVICE_MAX_DURATION = Integer.MAX_VALUE;

   /** The minimum supported tone frequency */
   private static final int AUDIO_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported tone frequency */
   private static final int AUDIO_DEVICE_MAX_FREQUENCY = Integer.MAX_VALUE;

   /** The number of buzzers */
   public static final int BUZZER_DEVICE_COUNT = 1;

   /** The minimum supported buzzer duration */
   public static final int BUZZER_DEVICE_MIN_DURATION = 0;

   /** The maximum supported buzzer duration */
   public static final int BUZZER_DEVICE_MAX_DURATION = 65535;

   /** The minimum supported buzzer frequency */
   public static final int BUZZER_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported buzzer frequency */
   public static final int BUZZER_DEVICE_MAX_FREQUENCY = 65535;

   /** The number of full-color LEDS */
   public static final int FULL_COLOR_LED_DEVICE_COUNT = 1;

   /** The minimum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of motors */
   public static final int MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported velocity (for velocity control) */
   public static final int MOTOR_DEVICE_MIN_VELOCITY = -255;

   /** The maximum supported velocity (for velocity control) */
   public static final int MOTOR_DEVICE_MAX_VELOCITY = 255;

   /** The number of photoresistors */
   public static final int PHOTORESISTOR_DEVICE_COUNT = 2;

   /** The minimum supported photoresistor value */
   public static final int PHOTORESISTOR_MIN_VALUE = 0;

   /** The maximum supported photoresistor value */
   public static final int PHOTORESISTOR_MAX_VALUE = 255;

   /** The number of simple obstacle sensors */
   public static final int SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT = 2;

   /** The number of thermistors */
   public static final int THERMISTOR_DEVICE_COUNT = 1;

   /** The minimum supported thermistor value */
   public static final int THERMISTOR_MIN_VALUE = 0;

   /** The maximum supported thermistor value */
   public static final int THERMISTOR_MAX_VALUE = 255;

   /**
    * The unique device id for the thermistor used by all finches.  This value is used to lookup the appropriate
    * {@link ThermistorUnitConversionStrategy} for converting thermistor values into temperatures.
    */
   public static final String THERMISTOR_DEVICE_ID = ThermistorUnitConversionStrategyMF52A103F3380.DEVICE_ID;

   @Override
   public final int getFinchDeviceCount()
      {
      return FINCH_DEVICE_COUNT;
      }

   public final int getAccelerometerDeviceCount()
      {
      return ACCELEROMETER_DEVICE_COUNT;
      }

   public final String getAccelerometerDeviceId()
      {
      return ACCELEROMETER_DEVICE_ID;
      }

   @Override
   public final int getAnalogInputMinValue()
      {
      return ANALOG_INPUT_MIN_VALUE;
      }

   @Override
   public final int getAnalogInputMaxValue()
      {
      return ANALOG_INPUT_MAX_VALUE;
      }

   @Override
   public final int getAudioDeviceCount()
      {
      return AUDIO_DEVICE_COUNT;
      }

   @Override
   public final int getAudioDeviceMinAmplitude()
      {
      return AUDIO_DEVICE_MIN_AMPLITUDE;
      }

   @Override
   public final int getAudioDeviceMaxAmplitude()
      {
      return AUDIO_DEVICE_MAX_AMPLITUDE;
      }

   @Override
   public final int getAudioDeviceMinDuration()
      {
      return AUDIO_DEVICE_MIN_DURATION;
      }

   @Override
   public final int getAudioDeviceMaxDuration()
      {
      return AUDIO_DEVICE_MAX_DURATION;
      }

   @Override
   public final int getAudioDeviceMinFrequency()
      {
      return AUDIO_DEVICE_MIN_FREQUENCY;
      }

   @Override
   public final int getAudioDeviceMaxFrequency()
      {
      return AUDIO_DEVICE_MAX_FREQUENCY;
      }

   @Override
   public int getBuzzerDeviceCount()
      {
      return BUZZER_DEVICE_COUNT;
      }

   @Override
   public int getBuzzerDeviceMinDuration()
      {
      return BUZZER_DEVICE_MIN_DURATION;
      }

   @Override
   public int getBuzzerDeviceMaxDuration()
      {
      return BUZZER_DEVICE_MAX_DURATION;
      }

   @Override
   public int getBuzzerDeviceMinFrequency()
      {
      return BUZZER_DEVICE_MIN_FREQUENCY;
      }

   @Override
   public int getBuzzerDeviceMaxFrequency()
      {
      return BUZZER_DEVICE_MAX_FREQUENCY;
      }

   @Override
   public final int getFullColorLedDeviceCount()
      {
      return FULL_COLOR_LED_DEVICE_COUNT;
      }

   @Override
   public final int getFullColorLedDeviceMinIntensity()
      {
      return FULL_COLOR_LED_DEVICE_MIN_INTENSITY;
      }

   @Override
   public final int getFullColorLedDeviceMaxIntensity()
      {
      return FULL_COLOR_LED_DEVICE_MAX_INTENSITY;
      }

   @Override
   public final int getMotorDeviceCount()
      {
      return MOTOR_DEVICE_COUNT;
      }

   @Override
   public final int getMotorDeviceMinVelocity()
      {
      return MOTOR_DEVICE_MIN_VELOCITY;
      }

   @Override
   public final int getMotorDeviceMaxVelocity()
      {
      return MOTOR_DEVICE_MAX_VELOCITY;
      }

   @Override
   public final int getPhotoresistorDeviceCount()
      {
      return PHOTORESISTOR_DEVICE_COUNT;
      }

   @Override
   public final int getPhotoresistorMinValue()
      {
      return PHOTORESISTOR_MIN_VALUE;
      }

   @Override
   public final int getPhotoresistorMaxValue()
      {
      return PHOTORESISTOR_MAX_VALUE;
      }

   @Override
   public final int getSimpleObstacleSensorDeviceCount()
      {
      return SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT;
      }

   @Override
   public final int getThermistorDeviceCount()
      {
      return THERMISTOR_DEVICE_COUNT;
      }

   @Override
   public final int getThermistorMinValue()
      {
      return THERMISTOR_MIN_VALUE;
      }

   @Override
   public final int getThermistorMaxValue()
      {
      return THERMISTOR_MAX_VALUE;
      }

   @Override
   public final String getThermistorDeviceId()
      {
      return THERMISTOR_DEVICE_ID;
      }
   }
