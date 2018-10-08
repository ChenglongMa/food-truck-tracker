package mad.geo.service;

// simulated tracking service by Caspar for MAD s2, 2018
// Usage: add this class to project in appropriate package
// add tracking_data.txt to res/raw folder
// see: TestTrackingService.test() method for example

// NOTE: you may need to explicitly add the import for the generated some.package.R class
// which is based on your package declaration in the manifest

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import mad.geo.R;

public class TrackingService
{
   // PRIVATE PORTION
   private static final String LOG_TAG = TrackingService.class.getName();
   private List<TrackingInfo> trackingList = new ArrayList<>();
   private static Context context;

   // Singleton
   private TrackingService()
   {
   }

   // This is only a data access object (DAO)
   // You must extract data and place in your model
   public static class TrackingInfo
   {
      public Date date;
      public int trackableId;
      public int stopTime;
      public double latitude;
      public double longitude;

      @Override
      public String toString()
      {
         return String.format(Locale.getDefault(), "Date/Time=%s, trackableId=%d, stopTime=%d, lat=%.5f, long=%.5f", DateFormat.getDateTimeInstance(
                 DateFormat.SHORT, DateFormat.MEDIUM).format(date), trackableId, stopTime, latitude, longitude);
      }
   }

   // check if the source date is with the range of target date +/- minutes and seconds
   private boolean dateInRange(Date source, Date target, int periodMinutes, int periodSeconds)
   {
      Calendar sourceCal = Calendar.getInstance();
      Calendar targetCalStart = Calendar.getInstance();
      Calendar targetCalEnd = Calendar.getInstance();
      // set the calendars for comparison
      sourceCal.setTime(source);
      targetCalStart.setTime(target);
      targetCalEnd.setTime(target);

      // set up start and end range match for mins/secs
      // +/- period minutes/seconds to check
      targetCalStart.set(Calendar.MINUTE, targetCalStart.get(Calendar.MINUTE) - periodMinutes);
      targetCalStart.set(Calendar.SECOND, targetCalStart.get(Calendar.SECOND) - periodSeconds);
      targetCalEnd.set(Calendar.MINUTE, targetCalEnd.get(Calendar.MINUTE) + periodMinutes);
      targetCalEnd.set(Calendar.SECOND, targetCalEnd.get(Calendar.SECOND) + periodMinutes);

      // return if source date in the target range (inclusive of start/end range)
      return sourceCal.equals(targetCalStart) || sourceCal.equals(targetCalEnd)
              || (sourceCal.after(targetCalStart) && sourceCal.before(targetCalEnd));
   }

   // called internally before usage
   private void parseFile(Context context)
   {
      trackingList.clear();
      // resource reference to tracking_data.txt in res/raw/ folder of your project
      // supports trailing comments with //
      try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.tracking_data)))
      {
         // match comma and 0 or more whitespace OR trailing space and newline
         scanner.useDelimiter(",\\s*|\\s*\\n+");
         while (scanner.hasNext())
         {
            TrackingInfo trackingInfo = new TrackingInfo();
            trackingInfo.date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(scanner.next());
            trackingInfo.trackableId = Integer.parseInt(scanner.next());
            trackingInfo.stopTime = Integer.parseInt(scanner.next());
            trackingInfo.latitude = Double.parseDouble(scanner.next());
            String next=scanner.next();
            int commentPos;
            // strip trailing comment
            if((commentPos=next.indexOf("//")) >=0)
               next=next.substring(0, commentPos);
            trackingInfo.longitude = Double.parseDouble(next);
            trackingList.add(trackingInfo);
         }
      }
      catch (Resources.NotFoundException e)
      {
         Log.i(LOG_TAG, "File Not Found Exception Caught");
      }
      catch (ParseException e)
      {
         Log.i(LOG_TAG, "ParseException Caught (Incorrect File Format)");
      }
   }

   // singleton support
   private static class LazyHolder
   {
      static final TrackingService INSTANCE = new TrackingService();
   }

   // PUBLIC METHODS

   // singleton
   // thread safe lazy initialisation: see https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   public static TrackingService getSingletonInstance(Context context)
   {
      TrackingService.context = context;
      return LazyHolder.INSTANCE;
   }

   // log contents of file (for testing/logging only)
   public void logAll()
   {
      log(trackingList);
   }

   // log contents of provided list (for testing/logging and example purposes only)
   public void log(List<TrackingInfo> trackingList)
   {
      // we reparse file contents for latest data on every call
      parseFile(context);
      for (TrackingInfo trackingInfo : trackingList)
      {
         // to prevent this logging issue https://issuetracker.google.com/issues/77305804
         try
         {
            Thread.sleep(1);
         }
         catch (InterruptedException e)
         {
         }
         Log.i(LOG_TAG, trackingInfo.toString());
      }
   }

   // the main method you can call periodically to get data that matches a given date period
   // date +/- period minutes/seconds to check
   public List<TrackingInfo> getTrackingInfoForTimeRange(Date date, int periodMinutes, int periodSeconds)
   {
      // we reparse file contents for latest data on every call
      parseFile(context);
      List<TrackingInfo> returnList = new ArrayList<>();
      for (TrackingInfo trackingInfo : trackingList)
         if (dateInRange(trackingInfo.date, date, periodMinutes, periodSeconds))
            returnList.add(trackingInfo);
      return returnList;
   }
}
