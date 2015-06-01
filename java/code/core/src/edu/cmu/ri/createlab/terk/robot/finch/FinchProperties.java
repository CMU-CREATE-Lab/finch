package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorUnitConversionStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchProperties
   {
   String getDeviceCommonName();

   FinchHardwareType getHardwareType();

   /** Returns the number of finches. */
   int getFinchDeviceCount();

   /** Returns the number of finch backpacks. */
   int getFinchBackpackDeviceCount();

   /** Returns the number of accelerometers. */
   int getAccelerometerDeviceCount();

   /**
    * Returns the accelerometer device ID, used to look up the appropriate {@link AccelerometerUnitConversionStrategy}
    * for converting acclerometer values into g's.
    */
   String getAccelerometerDeviceId();

   /** Returns the number of analog inputs. */
   int getAnalogInputDeviceCount();

   /** Returns the minimum value returned by analog inputs. */
   int getAnalogInputMinValue();

   /** Returns the minimum value returned by analog inputs. */
   int getAnalogInputMaxValue();

   /** Returns the number of audio outputs */
   int getAudioDeviceCount();

   /** Returns the minimum supported audio device amplitude */
   int getAudioDeviceMinAmplitude();

   /** Returns the maximum supported audio device amplitude */
   int getAudioDeviceMaxAmplitude();

   /** Returns the minimum supported tone duration */
   int getAudioDeviceMinDuration();

   /** Returns the maximum supported tone duration */
   int getAudioDeviceMaxDuration();

   /** Returns the minimum supported tone frequency */
   int getAudioDeviceMinFrequency();

   /** Returns the maximum supported tone frequency */
   int getAudioDeviceMaxFrequency();

   /** Returns the number of buzzers. */
   int getBuzzerDeviceCount();

   /** Returns the minimum supported tone duration */
   int getBuzzerDeviceMinDuration();

   /** Returns the maximum supported tone duration */
   int getBuzzerDeviceMaxDuration();

   /** Returns the minimum supported tone frequency */
   int getBuzzerDeviceMinFrequency();

   /** Returns the maximum supported tone frequency */
   int getBuzzerDeviceMaxFrequency();

   /** Returns the number of full-color LEDS */
   int getFullColorLedDeviceCount();

   /** Returns the minimum supported full-color LED intensity */
   int getFullColorLedDeviceMinIntensity();

   /** Returns the maximum supported full-color LED intensity */
   int getFullColorLedDeviceMaxIntensity();

   /** Returns the number of motors */
   int getMotorDeviceCount();

   /** Returns the minimum supported velocity */
   int getMotorDeviceMinVelocity();

   /** Returns the maximum supported velocity */
   int getMotorDeviceMaxVelocity();

   /** Returns the number of photoresistors */
   int getPhotoresistorDeviceCount();

   /** Returns the minimum value returned by photoresistors. */
   int getPhotoresistorMinValue();

   /** Returns the maximum value returned by photoresistors. */
   int getPhotoresistorMaxValue();

   /** Returns the number of obstacle sensors */
   int getSimpleObstacleSensorDeviceCount();

   /** Returns the number of thermistors */
   int getThermistorDeviceCount();

   /** Returns the minimum value returned by thermistors. */
   int getThermistorMinValue();

   /** Returns the maximum value returned by thermistors. */
   int getThermistorMaxValue();

   /**
    * Returns the thermistor device ID, used to lookup the appropriate {@link ThermistorUnitConversionStrategy} for
    * converting thermistor values into temperatures.
    */
   String getThermistorDeviceId();
   }