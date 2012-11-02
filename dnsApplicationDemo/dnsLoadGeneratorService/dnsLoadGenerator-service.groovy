
service {
	
	name "dnsLoadGeneratorService"
	icon "feather-small.gif"
	type "WEB_SERVER"

	numInstances 1

	compute {
		// Use template with root access - to make things easier
		template "BIG_MEDIUM"
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
	
		install "ApacheWeb_install.groovy"
		postInstall "ApacheWeb_postInstall.groovy"
		preStart "dnsLoadGenerator_preStart.groovy"
		start ([
//			"Win.*":"run.bat",
			"Linux.*":"ApacheWeb_start.groovy"
			])
			
		startDetectionTimeoutSecs 800
		startDetection {			
			ServiceUtils.isPortOccupied(currentPort)
		}	
		
		preStop ([	
//			"Win.*":"killAllHttpd.bat",		
			"Linux.*":"ApacheWeb_stop.groovy"
			])
		
			
		locator {			
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=httpd|apache")
			println ":ApacheWeb-service.groovy: current PIDs: ${myPids}"
			return myPids
        	}			

	}

	customCommands ([
		"addNode" : "dnsLoadGenerator_addNode.groovy",
		"removeNode" : "dnsLoadGenerator_removeNode.groovy",
	])
	
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
