How to Use the Ulteo Demo Recipe

Prerequisites:
ulteoManager_Template Image: Currently, the recipe assumes you have an image of the Manager VM that is preconfigured. 
The Cloudify Work account on the DCB-1 has an image called "ulteoManager_Preconfigured_Template" that meets this requirement.
This image has 3 preconfigured users as well called demouser1, demouser2 and demouser3.

How to Use:
Spin up the recipe.
This will launch a single manager VM and a single application VM
Connect to http://ULTEO_MANAGER_IP/ovd as demouser1 (password is the same as the username)
On the cloudify UI you should see the number of active connections metric rise to 1.
Connect via another browser session as demouser2
Cloudify UI should show two active connections.
CloudBand should show new application VM being launched
Cloudify UI should show scaled up VM
Connect via another browser session as demouser3
	This connection should go to the newly scaled out application VM.
Disconnect at least 2 of the sessions
Scale-in should occur
	