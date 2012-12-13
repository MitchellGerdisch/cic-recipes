/*
 * This service recipe deploys the HAproxy load balancer.
 * In its current form it is focused on setting up a single HAproxy frontend and backend.
 * Multiple frontends and backends is a future release.
 */
service {
	
	name "HAproxy"
	icon "feather-small.gif"
	type "WEB_SERVER"

	numInstances 1

	compute {
		// Use template with root access - to make things easier
		template "QUAD_ROOT_ACCESS"
	}

	lifecycle {
		install "HAproxy_install.groovy"
		postInstall "HAproxy_postInstall.groovy"
		start "HAproxy_start.groovy"
			
		startDetectionTimeoutSecs 800
		startDetection {			
			ServiceUtils.isPortOccupied(currentPort)
		}	
	
		// we need to be able to restart when we add/remove nodes so I'm returning a blank locator
		locator {			
			/* 
			 * def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=haproxy")
			 * println "HAproxy-service.groovy: current PIDs: ${myPids}"
			 * return myPids
			 */
			return []
        }			
	}
	
	customCommands ([
		"addNode" : "HAproxy_addNode.groovy",
		"removeNode" : "HAproxy_removeNode.groovy",
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
