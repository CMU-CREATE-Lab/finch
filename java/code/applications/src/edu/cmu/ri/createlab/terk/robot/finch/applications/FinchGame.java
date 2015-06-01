package edu.cmu.ri.createlab.terk.robot.finch.applications;

/**
 * @author Lisa Storey
 * @author Joey Gannon
 * @date 2/14/09
 *
 * A fast paced 'Simon Says' game for the Finch.
 *
 * Instructions: An arrow will display on the screen representing a command.
 * If the arrow is pointing left or right, turn the Finch on its left or right
 * side. If the arrow points up, point the Finch straight up. If the arrow
 * points down, hold the Finch flat. If the arrow is black, cover the Finch's light
 * sensors with your hand. If it is gray, uncover them. Respond quickly!
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;

public class FinchGame
   {
   // Commanded Finch states
   public static final int FLAT_COVERED = 0;
   public static final int LEFT_COVERED = 1;
   public static final int RIGHT_COVERED = 2;
   public static final int UP_COVERED = 3;
   public static final int FLAT_UNCOVERED = 4;
   public static final int LEFT_UNCOVERED = 5;
   public static final int RIGHT_UNCOVERED = 6;
   public static final int UP_UNCOVERED = 7;

   private static Finch myFinch;
   private double[] bias; // accelerometer calibration values

   private int[] lightBias;

   private static final int RESPONSE_TIME = 2000; // amount of time to follow the command

   public FinchGame()
      {
      myFinch = new Finch();

      System.out.println("Calibrating... please set your Finch on a level surface.");

      myFinch.sleep(5000);

      bias = myFinch.getAccelerations(); //get steady-state readings
      lightBias = myFinch.getLightSensors(); // get light level readings

      // Make the covered light values about 40 less than uncovered
      lightBias[0] -= 40;
      lightBias[1] -= 40;

      // Ensure they don't get too low
      if (lightBias[0] < 30)
         {
         lightBias[0] = 30;
         }
      if (lightBias[1] < 30)
         {
         lightBias[1] = 30;
         }

      bias[2]--; //remove gravity
      System.out.println("Done calibrating!");
      System.out.println("Pick up the Finch now");
      myFinch.saySomething("Pick up the Finch now");
      myFinch.sleep(5000);
      }

   public static void main(final String[] args) throws InterruptedException
      {
      FinchGame finchGame = new FinchGame();
      Random random = new Random();
      long startTime; // ms system time when this iteration was started
      boolean didIt; // flag for whether or not the action was completed in time

      FinchGameWindow window = new FinchGameWindow();
      window.repaint();

      int winCount = 0;

      for (int i = 0; i < 20; i++)
         {
         /* For each iteration, choose a random command,
             * turn the beak red until the command is completed,
             * and turn the beak green once it has been completed.
             * If the command was not done in time, beep.
             */

         finchGame.getFinch().setLED(Color.RED); // turn the beak red until the command is completed
         startTime = System.currentTimeMillis(); // set the trial start time
         didIt = false;
         int currentArrow = random.nextInt(8); // choose a random command type
         window.setArrow(currentArrow); // inform the display window of the chosen command and display it
         switch (currentArrow)
            {
            case FLAT_COVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isFlat() && finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case LEFT_COVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isLeft() && finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case RIGHT_COVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isRight() && finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case UP_COVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isUp() && finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case FLAT_UNCOVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isFlat() && !finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case LEFT_UNCOVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isLeft() && !finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case RIGHT_UNCOVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isRight() && !finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            case UP_UNCOVERED:
               while (System.currentTimeMillis() - startTime < RESPONSE_TIME)
                  {
                  if (finchGame.isUp() && !finchGame.isCovered())
                     {
                     finchGame.getFinch().setLED(Color.GREEN);
                     didIt = true;
                     }
                  }
               break;
            }

         if (!didIt)
            {
            finchGame.getFinch().buzz(1000, 200);
            } // beep if incorrect

         // If you succeeded, increase win count by 1
         if (didIt)
            {
            winCount++;
            }
         }
      // Say the result - can definitely improve this part to have the Finch congratulate or insult you
      // depending on score.
      myFinch.saySomething("You got " + winCount + " out of 20 tries.");
      System.out.println("You got " + winCount + " out of 20 tries.");
      myFinch.sleep(5000);
      System.exit(0);
      }

   private Finch getFinch()
      {
      return myFinch;
      }

   /* These methods check if a given game state is satisfied. */

   public boolean isRight()
      {
      return ((yAccel() < -0.5) &&
              (Math.abs(xAccel()) < 0.5) &&
              (Math.abs(zAccel()) < 0.5));
      }

   public boolean isLeft()
      {
      return ((yAccel() > 0.5) &&
              (Math.abs(xAccel()) < 0.5) &&
              (Math.abs(zAccel()) < 0.5));
      }

   public boolean isFlat()
      {
      return ((zAccel() > 0.5) &&
              (Math.abs(xAccel()) < 0.5) &&
              (Math.abs(yAccel()) < 0.5));
      }

   public boolean isUp()
      {
      return ((xAccel() > 0.5) &&
              (Math.abs(yAccel()) < 0.5) &&
              (Math.abs(zAccel()) < 0.5));
      }

   public boolean isCovered()
      {
      return (myFinch.getLeftLightSensor() < (lightBias[0]) && myFinch.getRightLightSensor() < (lightBias[1]));
      }

   /* These methods calculate a corrected acceleration, using the
     * current accelerometer readings and the biases calculated during
     * calibration.
     */

   private double xAccel()
      {
      return myFinch.getXAcceleration() - bias[0];
      }

   private double yAccel()
      {
      return myFinch.getYAcceleration() - bias[1];
      }

   private double zAccel()
      {
      return myFinch.getZAcceleration() - bias[2];
      }

   private static final class FinchGameWindow extends JFrame
      {
      private static final long serialVersionUID = -5183298795855931056L;

      private int currentArrow; // The current arrow type being displayed

      private FinchGameWindow()
         {
         super();

         // set up the game display
         this.setTitle("Game!");
         this.setSize(400, 420);
         this.setResizable(false);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setVisible(true);
         this.getContentPane().paint(getGraphics());
         currentArrow = 0;
         }

      public void setArrow(int arr)
         {
         // set the new arrow and repaint the screen
         currentArrow = arr;
         this.repaint();
         }

      public void paint(Graphics g)
         {
         // fill in the background in white
         g.setColor(Color.white);
         g.fillRect(0, 0, 400, 420);

         // display the arrow, gray for uncovered and black for covered
         g.setColor(Color.gray);
         switch (currentArrow)
            {
            case FLAT_COVERED:
               g.setColor(Color.black);
            case FLAT_UNCOVERED:
            {
            g.fillRect(180, 40, 40, 240);
            int[] xs = {120, 200, 280};
            int[] ys = {280, 360, 280};
            g.fillPolygon(xs, ys, 3);
            break;
            }
            case LEFT_COVERED:
               g.setColor(Color.black);
            case LEFT_UNCOVERED:
            {
            g.fillRect(120, 180, 240, 40);
            int[] xs = {120, 40, 120};
            int[] ys = {120, 200, 280};
            g.fillPolygon(xs, ys, 3);
            break;
            }
            case RIGHT_COVERED:
               g.setColor(Color.black);
            case RIGHT_UNCOVERED:
            {
            g.fillRect(40, 180, 240, 40);
            int[] xs = {280, 360, 280};
            int[] ys = {120, 200, 280};
            g.fillPolygon(xs, ys, 3);
            break;
            }
            case UP_COVERED:
               g.setColor(Color.black);
            case UP_UNCOVERED:
            {
            g.fillRect(180, 120, 40, 240);
            int[] xs = {120, 200, 280};
            int[] ys = {120, 40, 120};
            g.fillPolygon(xs, ys, 3);
            break;
            }
            }
         }
      }
   }

