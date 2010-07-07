package edu.cmu.ri.createlab.terk.robot.finch.applications;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import edu.cmu.ri.createlab.userinterface.component.DatasetPlotter;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AccelerometerHUD
   {
   private static final Dimension SPACER_DIMENSIONS = new Dimension(5, 5);

   public static void main(final String[] args) throws InterruptedException
      {
      final Finch finch = new Finch();

      final DatasetPlotter<Double> accelerometerPlotter = new DatasetPlotter<Double>(-1.5, 1.5, 610, 610, 10, TimeUnit.MILLISECONDS);
      accelerometerPlotter.addDataset(Color.RED);
      accelerometerPlotter.addDataset(Color.GREEN);
      accelerometerPlotter.addDataset(Color.BLUE);

      final DatasetPlotter<Double> xPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
      xPlot.addDataset(Color.RED);

      final DatasetPlotter<Double> yPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
      yPlot.addDataset(Color.GREEN);

      final DatasetPlotter<Double> zPlot = new DatasetPlotter<Double>(-1.5, 1.5, 200, 200, 10, TimeUnit.MILLISECONDS);
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
                           .add(plotComponent)
                           .add(horizontalSpacer)
                           .add(
                           groupLayout.createParallelGroup(GroupLayout.CENTER)
                                 .add(xPlotComponent)
                                 .add(topVerticalSpacer)
                                 .add(yPlotComponent)
                                 .add(bottomVerticalSpacer)
                                 .add(zPlotComponent))
               );

               groupLayout.setVerticalGroup(
                     groupLayout.createParallelGroup(GroupLayout.CENTER)
                           .add(plotComponent)
                           .add(horizontalSpacer)
                           .add(
                           groupLayout.createSequentialGroup()
                                 .add(xPlotComponent)
                                 .add(topVerticalSpacer)
                                 .add(yPlotComponent)
                                 .add(bottomVerticalSpacer)
                                 .add(zPlotComponent))
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

      //noinspection InfiniteLoopStatement
      while (true)
         {
         Thread.sleep(5);
         final double[] accelerometerState = finch.getAccelerations();

         accelerometerPlotter.setCurrentValues(accelerometerState[0],
                                               accelerometerState[1],
                                               accelerometerState[2]);

         xPlot.setCurrentValues(accelerometerState[0]);
         yPlot.setCurrentValues(accelerometerState[1]);
         zPlot.setCurrentValues(accelerometerState[2]);
         }
      }

   private AccelerometerHUD()
      {
      // private to prevent instantiation
      }
   }
