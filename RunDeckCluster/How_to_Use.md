This application recipe deploys a RunDeck server and a given number of remote nodes as specified in RunDeckRemoteNodes-service.groovy.
It is meant to be used by performance testers to deploy a set of VMs and then execute a command across all the VMs.

How to Use
**********

Point browser at http://IP-OF-RUNDECKSERVER:4440
Login as admin/admin
Create a job:
	Click on Jobs tab
	Click on "New job" on right side of screen
	Click "Save this job?" yes radio button
	Give the job a name
	Go to the Workflow section and enter a command.
		Click Save under the command entry window.
		Add steps if you want.
	Select Dispatch to Nodes checkbox
		Click on "Name" next to "Include"
			enter remotendoe-.* to match the existing set of nodes
			Click on "Matched nodes" to confirm the list of nodes the job will run on
		In the Thread Count field, enter a number that matches the number of remotenodes deployed
	Click "Create" button
	
Run a job:
	Click on Jobs tab
	Click "play" button next to the job you want to run.
	Click "Run Job Now" button
	Then meander about clicking on stuff until you find the output.
		Note the "Annotated" output shows the output from each node individually