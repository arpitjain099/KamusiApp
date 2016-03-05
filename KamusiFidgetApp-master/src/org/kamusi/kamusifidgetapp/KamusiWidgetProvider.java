package org.kamusi.kamusifidgetapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class KamusiWidgetProvider extends AppWidgetProvider {

	public static String NEXT_ACTION = "Next";
	public static String PROFILE_ACTION = "Profile";
	public static String INFO_ACTION = "Info";

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			// Create an Intent to launch
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.kamusiwidget);

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Globals g = Globals.getInstance();
		RemoteViews views = null;

		if (intent.getAction().equals(INFO_ACTION)) {
			views = new RemoteViews(context.getPackageName(),
					R.layout.tips);			
		}
		
		if (intent.getAction().equals(NEXT_ACTION)) {
			views = new RemoteViews(context.getPackageName(),
					R.layout.providetranslation);
			Intent profileintent = new Intent(context,
					KamusiWidgetProvider.class);
			profileintent.setAction(PROFILE_ACTION);
			PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
					context, 0, profileintent, 0);
			views.setOnClickPendingIntent(R.id.profile, updatePendingIntent);
		}

		if (intent.getAction().equals(PROFILE_ACTION)) {
			views = new RemoteViews(context.getPackageName(),
					R.layout.widgetprofile);
			views.setTextViewText(R.id.username, g.getUsername());
			// START BUTTON
			Intent nextintent = new Intent(context, KamusiWidgetProvider.class);
			nextintent.setAction(NEXT_ACTION);
			PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
					context, 0, nextintent, 0);
			views.setOnClickPendingIntent(R.id.next, updatePendingIntent);
			// INFO BUTTON
			Intent infointent = new Intent(context, KamusiWidgetProvider.class);
			nextintent.setAction(INFO_ACTION);
			PendingIntent infoPendingIntent = PendingIntent.getBroadcast(
					context, 0, infointent, 0);
			views.setOnClickPendingIntent(R.id.tips, updatePendingIntent);
		}

		if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

			if (g.getLogin()) {
				views = new RemoteViews(context.getPackageName(),
						R.layout.providetranslation);
				Intent profileintent = new Intent(context,
						KamusiWidgetProvider.class);
				profileintent.setAction(PROFILE_ACTION);
				PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
						context, 0, profileintent, 0);
				views.setOnClickPendingIntent(R.id.profile, updatePendingIntent);
			} else {
				views = new RemoteViews(context.getPackageName(),
						R.layout.kamusiwidget);
				// login screen clicks
				Intent launchIntent = new Intent(context, MainActivity.class);
				PendingIntent launchPendingIntent = PendingIntent.getActivity(
						context, 0, launchIntent, 0);
				views.setOnClickPendingIntent(R.id.textView1,
						launchPendingIntent);
				views.setOnClickPendingIntent(R.id.imageView1,
						launchPendingIntent);
			}
		} else {
			super.onReceive(context, intent);
		}
		if (views != null) {
			// kamusi bottom link to website
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://kamusi.org"));
			PendingIntent browserPendingIntent = PendingIntent.getActivity(
					context, 0, browserIntent, 0);
			views.setOnClickPendingIntent(R.id.kamusiURL, browserPendingIntent);

			// REFRESH WIDGET
			refresh(context, views);
		}

		// BUTTON TO REQUEST WIDGET UPDATE
		// Intent uiIntent = new
		// Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
		// uiIntent, 0);
		// views.setOnClickPendingIntent(R.id.button1, pendingIntent);

		// LIKE CLICKS
		// ImageView = (ImageView) V.findViewById(R.id.qStatusImage);
		// qImageView.setImageResource(R.drawable.thumbs_up);

	}

	public void refresh(Context context, RemoteViews view) {
		ComponentName cn = new ComponentName(context,
				KamusiWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cn, view);
	}

}
