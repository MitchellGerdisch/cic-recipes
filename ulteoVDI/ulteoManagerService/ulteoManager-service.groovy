service {

	name "ulteoManagerService"
	type "WEB_SERVER"
	icon "ulteo.jpg"

	numInstances 1
	
	compute {
		// The ulteoManager_Template is a snapshot of a manager VM that has been preconfigured.
		// This allows the system to be built and scaled automatically without requiring someone to go in as the ulteo admin to set things up.
		template "ulteoManager_Template"
	}	
	
	lifecycle{

		install "ulteoManager-install.groovy"	
		start "ulteoManager-start.groovy"	
		//postStart "ulteoManager-poststart.groovy"
		preStop "ulteoManager-stop.groovy"

		locator {
			def ovdPIds= ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.eq=httpd")
			return ovdPIds
		 }
		
		startDetectionTimeoutSecs 120
		startDetection {
			ServiceUtils.arePortsOccupied([80,1111])
		}

	
//	 monitors{
//
//		    	managerIP = InetAddress.localHost.hostAddress
//
//				def int sessionCount = GetMySqlSessions.sessionCount(managerIP,"root","root")
//
//				println "Number of Session is --->  : " + sessionCount.toInteger()
//			 	return ["Current Active Sessions":  sessionCount.toInteger() ]
//	      }	
	}
}

