# final-project-s17-blastoise
final-project-s17-blastoise created by GitHub Classroom

# Milestone Edit
We have a web-service that we are consuming that was built from scratch, the code for that can be found here: https://github.com/zskemp/SafeNights_Web
All of our main Database work is being done through that web-service, as we send/get requests for all our data.

When you load the app, you can either sign up, or use an existing user we have (username: zrs, password: 1234). This uses key-value pairs and shared-preferences

As shown in our wireframe, we have four ways to use the app once you log in - Get Started is the first, and is completed. You can "start"
a night and enter in a contact to message in case of an emergency, and a location you expect to arrive at. You can "stop" the night when
you feel your night is over. The emergency trigger sends a message under 3 conditions: phone battery reaches 10%, you're in the same location for
40 minutes after 2am that isn't your specified location, and if the app gets destroyed somehow before you click stop. This sends your contact
a message with your last known location, and where you wanted to end up.

Add drinks is also completed, and you can specify the next morning when you went out, and how much you drank, and how much you spent to keep track of your 
progress.

In the history tab, you can check your past spending history and drinking history in a graph view. This still needs a bit of UI work, but the logic is there

The Last Night tab has the data pushing/pulling completed, but we haven't worked on that UI yet.

We used SharedPreferences/Key Value for log in, consumed a web-service for all of our data, use GPS for tracking location, and send SMS messages.
