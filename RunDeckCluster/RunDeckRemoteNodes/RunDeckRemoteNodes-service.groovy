
service {
	
	name "RunDeckRemoteNodes"
	type "APP_SERVER"

	elastic true
	numInstances 4
	minAllowedInstances 1
	maxAllowedInstances 20

	compute {
		// Use template with root access - to make things easier
		template "RunDeckRemoteNodeTemplate"
	}

	lifecycle {	
	
		preStart "RemoteNode_preStart.groovy"
			
		startDetectionTimeoutSecs 800
		startDetection {		
			ServiceUtils.isPortOccupied(currentPort)
		}	
					
		locator {			
			return []
        }			

	}

	
	userInterface {
		metricGroups = ([
			metricGroup {
				name "process"
				metrics([
					"Total Process Cpu Time"
				])
			}
		])
	
		widgetGroups = ([
			widgetGroup {
				name "Total Process Cpu Time"
				widgets([
					balanceGauge{metric = "Total Process Cpu Time"},
					barLineChart {
						metric "Total Process Cpu Time"
						axisYUnit Unit.REGULAR
					}
				])
			}
		])
	}
}
