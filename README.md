# Mobiquity_Challenge

The main feature of this app include:
1. Allow the user to authenticate with drop box credentials.
2. Allows the user to take a photo.
3. Uploads the taken pictures to a directory
4. List all the pictures that have been taken and view selected photo.

Some of the additional functionalities that has been included are as follows:-
1. Record Latitude and Longitude (GPS Coordinates) of the photos.
2. Using these GPS co-ordinates, obtain the location where the photo is taken.
3. Share the photo taken to friends on Whatsapp and Facebook.
4. The title of the photo contains the city name along with the date when the photo is taken
5. Allow user to apply special effects to the photos taken.

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
Once the user authenticates with Dropbox cerdentials, the user has two options. The first option includes a button to upload a new photo by using the camera of the phone. The Second option takes the user to another activity the has a list of all the photos that have been taken by the user. This activity also have a menu that takes the user to the map activity. The activity displays the map along with the photos taken. Clicking any one of the list view item, opens up the image in an imageview. Now this photo can be shared using facebook and whatsapp. This option is present in the action bar of the image activity as a menu option. This menu also has other options where user can apply a few special effects to the photos that have been taken.These photos with special effects can also be shared. 

