Changelog:

- Acquired API Key for Football Scores data - updated instructions to direct developers to the web site to create their own key.
- Added a "refresh" icon to the toolbar to allow the user to reload the scores.
- Added the date above the score - the layout is a bit confusing and easy to lose track of the match date (in the event the scores have not been
updated due to lack of network connectivity).
- Added functionality to retrieve the scores list from the saved instance state on a device rotation, rather than querying the database again.
- Added an overflow icon to the score card to allow users to share the score with friends.
- Added a section header for the score cards to group by league name.
- Added formatting to highlight the winning team on the score card.
- Added a ListView collection widget for today's football scores.
- Added layout mirroring to the views.
- Added "translatable" tag to strings that are excluded from translation - namely the API key and settings keys.
- Added a check to determine if the user is online before calling the ScoreService.
- Added a loading spinner during data retrieval.
- Added team crest images based on files uploaded by Chris Olsen at https://github.com/chrisolsen/football-scores-svgs.
- Cleaned up the xml resource files and removed unnecessary values.
- Updated About info to use an alert dialog rather than a separate activity.
- Updated the accessibility of the app by ensuring all images, buttons and text have relevant content descriptions tags (tags were added programmatically
to ensure accurate info and not a generic tag). In addition the app was tested when increasing the font size within the standard Android Settings app.
- Updated package structure to organize the Java classes.
- Updated resource folders to include drawable-xxxhdpi and framework-specific values.
- Updated the string resources to use strings.xml instead of hard-coding values in the app.
- Updated data provider to utilize objects - this allows the app to more easily use the RecyclerView as CursorAdapters are not supported, as well as
taking advantage of saved instance state for device rotation.
- Updated the table layout to include the image url for the team crest - this data is available via the API, however connection throttles make it
difficult to retrieve.
- Updated the layout of the score card to simplify the view (part of usability improvements).
- Updated the ScoresFragment to remove the call to the ScoreService - it was being called five times (once for each new instance of the fragment).
This functionality was moved to the MainActivity, where it is only called once each time the app loads.
- Updated the league ids and moved the values to a constants class for easier maintainability.
- Updated the share action implementation to more closely follow best practices.
- Updated the app to use Material Design elements, including CoordinatorLayout, AppBarLayout, Toolbar, RecyclerView, etc.
- Update Log calls to utilize a LogUtils helper class - only log during DEBUG mode.
- Refactored classes to follow Java naming conventions.
- Reviewed and updated error cases - identified in the code and in the changelog above.

Future improvements
- Add a setting to allow the user to select which leagues to include in the sync.
- Add a setting to allow the user to select their favorite team - receive notifications based on this selection.
- Add a configuration view to setup the app widget - select the day to display and the leagues.
- Add a detail view for the match, including player stats.
- Rework the database to follow a relational schema - teams, scores, players, etc.
