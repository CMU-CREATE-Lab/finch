package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import edu.cmu.ri.createlab.userinterface.component.DatasetPlotter;

/**
 * @author Erik Pasternak (epastern@andrew.cmu.edu)
 *
 * This code is desiged to test the accelerometers and the wheels together by
 * changing the direction in which the wheels drive based on readings from the
 * accelerometers. Currently, it should drive to head uphill whenever not on a
 * flat surface.
 */
public class AccelerometerControlledWheels
   {
   private static final Dimension SPACER_DIMENSIONS = new Dimension(5, 5);

   //This class contains a finch object and some dataset plotters to help
   //with displaying the accelerometer outputs
   Finch finch;
   DatasetPlotter<Double> accelerometerPlotter;
   DatasetPlotter<Double> xPlot;
   DatasetPlotter<Double> yPlot;
   DatasetPlotter<Double> zPlot;

   //This is where all the work is done. In this case, the creation of the
   //object runs all the code for this application.

   public AccelerometerControlledWheels() throws IOException
      {
      //create a new finch
      finch = new Finch();
      //create an input for std input
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      //These are state declarations so we can keep track of our previous state
      //in an easy to read manner.
      final int FLAT = 0;
      final int FORW = 1;
      final int BACK = 2;
      final int LEFT = 3;
      final int RIGHT = 4;
      //And define a coordinate system to reference indices from.
      final int XX = 0;
      final int YY = 1;
      final int ZZ = 2;
      final int prevXX = 3;
      final int prevYY = 4;
      //These are vectors that describe the accelerometer readings at different
      //positions of the finch.
      double[] flat = {0, 0, 0};
      double[] diffs = new double[5];
      //These let us add velocities to the wheel relative to our forward/back
      //tilt and our left/right tilt
      int forwardBackV;
      int leftRightV;
      //the total velocity for each wheel
      int leftWheelV;
      int rightWheelV;
      //start the state in flat
      int state = FLAT;
      //Used as a flag to cause activity when the state is changed
      boolean changed = false;

      //Get the accelerometer readings from the finch when it is flat
      System.out.println("Hold the finch flat and press return.");
      in.readLine();
      flat = finch.getAccelerations();

      //Set the initial previous accelerations to the flat readings
      diffs[prevXX] = flat[XX];
      diffs[prevYY] = flat[YY];

      System.out.println("flat = " + flat[0] + " : " + flat[1] + ": " + flat[2]);

      //Create a HUD to display the accelerometer readings.
      HUD();
      System.out.println("Press return to exit");

      //While the user hasn't pressed return
      while (!in.ready())
         {
         //get the current accelerometer values
         double[] currAccel = finch.getAccelerations();
         //int minIndex = 0;

         //Update the hud display with the new value
         update_HUD(currAccel);

         //As the finch tilts the reading in g's (multiples of standard gravity)
         //will change to reflect that. After about 1/4 of a g from flata in any
         //direction the finch starts slipping, so we'll scale up to 1/4 of a g
         //from flat
         diffs[XX] = flat[XX] - currAccel[XX];
         diffs[YY] = flat[YY] - currAccel[YY];
         diffs[ZZ] = flat[ZZ] - currAccel[ZZ];

         //We don't want to do anything if the changes are too small, otherwise
         //the finch will just wiggle around like crazy from any noise in the
         //reading.
         //System.out.println("diffs " + diffs[XX] + " " + diffs[YY]);
         if (diffs[XX] < 0.08 && diffs[YY] < 0.08
             && diffs[XX] > -0.08 && diffs[YY] > -0.08)
            {
            finch.setWheelVelocities(0, 0);
            continue;
            }
         //If we just had a big acceleration jump it most likely means our motors
         //kicked in and we want to ignore it.
         if (Math.abs(diffs[XX] - diffs[prevXX]) > 0.05)
            {
            diffs[prevXX] = diffs[XX];
            continue;
            }
         //In either case, we want to set the prev diff to the current diff
         diffs[prevXX] = diffs[XX];

         //the XX direction is our forward backward tilt. We want to add up to
         //20 speed to the wheels for up to 1/2 of a g from flat and convert to
         //an integer.
         forwardBackV = (int)(-diffs[XX] * 25 / .25);
         //Trim it at +/- 20
         if (forwardBackV > 25)
            {
            forwardBackV = 25;
            }
         if (forwardBackV < -25)
            {
            forwardBackV = -25;
            }

         //Do the same thing for left/right in the YY direction, but we want to
         //weight left and right less.
         leftRightV = (int)(-diffs[YY] * 10 / .25);
         //Trim it at +/- 20
         if (leftRightV > 10)
            {
            leftRightV = 10;
            }
         if (leftRightV < -10)
            {
            leftRightV = -10;
            }

         //The forward back tilt affects the wheels the same, but the left right
         //affects them oppositely to cause it to turn.
         leftWheelV = forwardBackV + leftRightV;
         rightWheelV = forwardBackV - leftRightV;
         //and trim them to 35 since that's the max speed the finch accepts
         if (leftWheelV > 35)
            {
            leftWheelV = 35;
            }
         if (leftWheelV < -35)
            {
            leftWheelV = -35;
            }
         if (rightWheelV > 35)
            {
            rightWheelV = 35;
            }
         if (rightWheelV < -35)
            {
            rightWheelV = -35;
            }

         //set the finch's wheels
         finch.setWheelVelocities(leftWheelV, rightWheelV);
         //and output the settings for feedback
         System.out.println("left = " + leftWheelV + ". right = " + rightWheelV);
         }
      finch.quit();
      System.exit(0);
      }

   //This code is borrowed from AccelerometerHUD by Chris Bartley. This part
   //updates the current display with given accelerometer data

   public void update_HUD(double[] accelerometerState)
      {
      accelerometerPlotter.setCurrentValues(accelerometerState[0],
                                            accelerometerState[1],
                                            accelerometerState[2]);

      xPlot.setCurrentValues(accelerometerState[0]);
      yPlot.setCurrentValues(accelerometerState[1]);
      zPlot.setCurrentValues(accelerometerState[2]);
      }

   //This part creates a new display window and a plotter and plots the accelerations.

   public void HUD()
      {
      accelerometerPlotter = new DatasetPlotter<Double>(-1.5, 1.5, 610, 610, 10, TimeUnit.MILLISECONDS);
      accelerometerPlotter.addDataset(Color.RED);
      accelerometerPlotter.addDataset(Color.GREEN);
      accelerometerPlotter.addDataset(Color.BLUE);

      xPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
      xPlot.addDataset(Color.RED);

      yPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
      yPlot.addDataset(Color.GREEN);

      zPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
      zPlot.addDataset(Color.BLUE);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component horizontalSpacer = Box.createRigidArea(SPACER_DIMENSIONS);
               final Component topVerticalSpacer = Box.createRigidArea(SPACER_DIMENSIONS);
               final Component bottomVerticalSpacer = Box.createRigidArea(SPACER_DIMENSIONS);

               final Component plotComponent = accelerometerPlotter.getComponent();
               final Component xPlotComponent = xPlot.getComponent();
               final Component yPlotComponent = yPlot.getComponent();
               final Component zPlotComponent = zPlot.getComponent();

               final JPanel panel = new JPanel();
               final GroupLayout groupLayout = new GroupLayout(panel);
               panel.setLayout(groupLayout);
               panel.setBackground(Color.WHITE);
               panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                                                                  BorderFactory.createEmptyBorder(1, 1, 1, 1)));

               groupLayout.setHorizontalGroup(
                     groupLayout.createSequentialGroup()
                           .addComponent(plotComponent)
                           .addComponent(horizontalSpacer)
                           .addGroup(
                                 groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                       .addComponent(xPlotComponent)
                                       .addComponent(topVerticalSpacer)
                                       .addComponent(yPlotComponent)
                                       .addComponent(bottomVerticalSpacer)
                                       .addComponent(zPlotComponent))
               );

               groupLayout.setVerticalGroup(
                     groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                           .addComponent(plotComponent)
                           .addComponent(horizontalSpacer)
                           .addGroup(
                                 groupLayout.createSequentialGroup()
                                       .addComponent(xPlotComponent)
                                       .addComponent(topVerticalSpacer)
                                       .addComponent(yPlotComponent)
                                       .addComponent(bottomVerticalSpacer)
                                       .addComponent(zPlotComponent))
               );

               // create the main frame
               final JFrame jFrame = new JFrame("Accelerometer HUD");

               // add the root panel to the JFrame
               jFrame.add(panel);

               // set various properties for the JFrame
               jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
               jFrame.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        finch.quit();
                        System.exit(0);
                        }
                     });
               jFrame.setBackground(Color.WHITE);
               jFrame.setResizable(false);
               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   //In this case I'm doing everything in the object, so main just creates a new
   //object and lets it run.

   public static void main(final String[] args) throws IOException
      {
      new AccelerometerControlledWheels();
      }
   }

