# DropBox App

The main feature of this app is that it:
1. Allows the user to authenticate with drop box credentials.
2. Allows the user to take a photo.
3. Uploads the taken pictures to a directory
4. Lists all the photos taken and displays selected photo.

Some of the additional functionalities that have been included are as follows:-
1. Records Latitude and Longitude (GPS Coordinates) of the photo.
2. Using these GPS co-ordinates, obtain the location of the photo taken.
3. Share the photo taken to friends on Whatsapp and Facebook.
4. The title of the photo contains the city name along with the date when the photo is taken
5. Allows the user to apply special effects to the photos taken.

## Initial Setup
1. Setup android studios.
2. Go to dropbox api, register for a project to get the app_key and secret_app_key.
3. Go to Google Maps API, register the project to get an app_key for the map.

## Getting Started
1. Open Android studios. Import the project.
2. Some of the libraries may have to be imported, But android studios does most of the work.
3. Maximum SDK supported is 22.
4. A number of permissions are required. These include INTERNET,WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION,READ_PHONE_STATE,ACCESS_NETWORK_STATE,ACCESS_FINE_LOCATION.

##Brief Description
Once the user authenticates with Dropbox cerdentials, he/she has two options. The first option allows the user to upload a new photo by using the camera of the phone. The Second option takes the user to another activity the displays a list of all the photos that have been taken by the user. This activity also has a menu option that takes the user to the map activity. This activity displays the map along with the photos taken. Clicking on any one of the list view item, opens up the image in an imageview. Now this photo can be shared using facebook, whatsapp or any other social networking app. This menu also has other options allowing the user to apply special effects to the photos that have been taken.These photos with special effects can also be shared. 

