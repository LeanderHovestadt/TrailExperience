# Trail Experience - search for mountainbike and hiking tours

## What its about

This app is used to search for mountainbike and hiking tours. Tours are getting fetched from a server and stored in a local database.
You can open them in a detail view or you can check their location on a map.

## Requirement specifications

### criteria - Build a navigable interface consisting of multiple screens of functionality and data.

The App consists of 4 screens. At the start, there is the login screen owned by the AuthenticationActivity.
Once the user is logged in, the App does start the ToursActivity using an intent.
Within the ToursActivity, the user can navigate to the detail screen or the map screen using the navigation safe-args component.
THe arguments are passed as "id"'s and the specific screen does request the tour data from the local database.

### criteria - Construct interfaces that adhere to Android standards and display appropriately on screens of different size and resolution.

All screens does implement the constraint layout as required. All screens have a clear understandable layout.
All resources are placed in the res directory in the correct path. Every element within a constraint layout does have an id.
A recycler view is used to display all available tours.

### criteria - Animate UI components to better utilize screen real estate and create engaging content.

The login screen does have a simple motion layout. This makes the app a little bit more interesting to the user.
The motion layout is configured as required.

### criteria - Connect to and consume data from a remote data source such as a RESTful API.

Data is getting fetched from a firebase noSQL database. This way, it is possible to add tours after the user has installed the app.
The tour list layout has a refresh layout. Refreshing the layout does trigger a fetch of the data from the firebase database.
Once new data has been fetched, the data will be stored in the persistent local database and the UI is updated.
All async fetching operations are done within a coroutine, so that the UI is not blocked.

### criteria - load network resources, such as Bitmap Images, dynamically and on-demand.

glide is used to display an image of the tour in the detail screen. The fetched data of the server does only include a string url to the image.

### criteria - Store data locally on the device for use between application sessions and/or offline use.

All fetched data of the server is getting stored in the local room database. The data stored is accessible across user sessions.
Data storage operations are performed on the appropriate threads as to not stall the UI thread.
Data is structured with appropriate data types within the tours/data/objects directory.

### criteria - Architect application functionality using MVVM.

Responsibilities are separated using the MVVM pattern. All Fragments does have a viewModel which takes care about the data. 
View operations are done by the Fragment.

### criteria - Implement logic to handle and respond to hardware and system events that impact the Android Lifecycle.

The App handles orientation changes smoothly. All navigation's within the fragments are done using bundles. 
Therefore, the android lifecycle can always restore its state. The ToursActivity and the AuthenticationActivity does communicate via intents.
Android permissions are correctly handled within the manifest file.

### criteria - Utilize system hardware to provide the user with advanced functionality and features.

The app does make use of the Location hardware component. The location is used within the maps fragment to show the users location.
If the user does decline the request, the users location is not shown on the map.
All tours are shown on the map. The map does zoom in a way, that all tours are on the screen.
Once a tour has been clicked, some information is shown.
Also it is possible to navigate to the location using google maps.