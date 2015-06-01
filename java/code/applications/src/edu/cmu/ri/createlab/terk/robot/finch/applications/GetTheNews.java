package edu.cmu.ri.createlab.terk.robot.finch.applications;

/**
 * Created by:  Tom Lauwers
 * Date:  2/16/2009
 * Uses the news reader to return stories which include a word being searched for.
 * This class only uses the RSS feeds - it doesn't use the Finch in any way
 */

import java.util.Scanner;
import edu.cmu.ri.createlab.rss.readers.NewsReader;

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class GetTheNews
   {
   public static void main(final String[] args)
      {
      final NewsReader reader = new NewsReader(); // Make a news reader
      final Scanner s = new Scanner(System.in); // Make a scanner class

      System.out.print("Please enter a word or phrase to search the news for:  ");
      final String word = s.nextLine();

      // The search method in the news class basically does this program for you - returns all entries that have that word in them
      System.out.println(reader.search(word));

      System.exit(0);
      }

   private GetTheNews()
      {
      // private to prevent instantiation
      }
   }

