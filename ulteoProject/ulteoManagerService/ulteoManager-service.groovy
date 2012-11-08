service {

	name "ulteoManagerService"
	type "WEB_SERVER"
	icon "ulteo.jpg"

	numInstances 1
	
	compute {
		template "QUAD_ROOT_ACCESS"
	}	
	
	lifecycle{

		install "ulteoManager-install.groovy"	
		start "ulteoManager-start.groovy"	
		postStart "ulteoManager-poststart.groovy"
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

	userInterface {

		metricGroups = ([
			metricGroup {

				name "process"

				metrics([
					"Process Cpu Usage",
					"Current Active Sessions",
					"Total Process Virtual Memory",
					"Num Of Active Threads"
				])
			}
		])

		widgetGroups = ([
			widgetGroup {
				name "Process Cpu Usage"
				widgets ([
					balanceGauge{metric = "Process Cpu Usage"},
					barLineChart{
						metric "Process Cpu Usage"
						axisYUnit Unit.PERCENTAGE
					}
				])
			},

			widgetGroup {
				name "Current Active Sessions"
				widgets ([
					balanceGauge{metric = "Current Active Sessions"},
					barLineChart{
						metric "Current Active Sessions"
						axisYUnit Unit.REGULAR
					}
				])
			},

		   widgetGroup {
				name "Total Process Virtual Memory"
				widgets([
					balanceGauge{metric = "Total Process Virtual Memory"},
					barLineChart {
						metric "Total Process Virtual Memory"
						axisYUnit Unit.MEMORY
					}
				])
			},
			widgetGroup {
				name "Num Of Active Threads"
				widgets ([
					balanceGauge{metric = "Num Of Active Threads"},
					barLineChart{
						metric "Num Of Active Threads"
						axisYUnit Unit.REGULAR
					}
				])
			}
		])
	}
}
