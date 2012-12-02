
service {
	
	name "RunDeckService"
	type "WEB_SERVER"

	numInstances 1

	compute {
		// Use template with root access - to make things easier
		template "RunDeckServiceTemplate"
	}

	lifecycle {
	
	
		details {
			def currPublicIP
			
			if (  context.isLocalCloud()  ) {
				currPublicIP = InetAddress.localHost.hostAddress
			}
			else {
				currPublicIP =System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]
			}
			def ApacheWebServerURL	= "http://${currPublicIP}:${currentPort}"
		
			return [
					"WebServer URL":"<a href=\"${ApacheWebServerURL}\" target=\"_blank\">${ApacheWebServerURL}</a>",
			]
		}	
	
		install "RunDeck_install.sh"
		preStart "RunDeck_preStart.groovy"
		start ([
			//		"Win.*":"run.bat",
					"Linux.*":"RunDeck_start.sh"
				])

			
		startDetectionTimeoutSecs 800
		startDetection {			
			ServiceUtils.isPortOccupied(currentPort)
		}	
		
//		preStop ([	
//			"Win.*":"killAllHttpd.bat",		
//			"Linux.*":"RunDeck_stop.groovy"
//			])
		
			
		locator {			
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=rundeck")
			println ":RunDeck-service.groovy: current PIDs: ${myPids}"
			return myPids
        	}			

	}

	/*
	customCommands ([
		"addNode" : "RunDeck_addNode.groovy",
		"removeNode" : "RunDeck_removeNode.groovy",
	])
	*/
	
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
	
	network {
		port currentPort
		protocolDescription "HTTP"
	}
}
