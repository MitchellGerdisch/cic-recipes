import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.hyperic.sigar.OperatingSystem


service {

	name "dnsSlaveService"
	type "WEB_SERVER"
	icon "dns_bind.gif"

    elastic true
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 2
	
	compute {
		template "BIG_MEDIUM"
	}
	
	lifecycle{
		
		monitors {
			value="/var/named/named_monitor.sh".execute().text
			return ["DNS Request Rate":value as Integer]
		}

		install "dnsSlave-install.groovy"
 	    postInstall "dnsSlave-postinstall.groovy"
		start "dnsSlave-start.groovy"
		preStop "dnsSlave-stop.groovy"

		startDetectionTimeoutSecs 120
		startDetection {
			ServiceUtils.isPortOccupied(53)
		}
		
		postStart {
			def dnsLoadGeneratorService = context.waitForService("dnsLoadGeneratorService", 180, TimeUnit.SECONDS)
			def hostAddress=System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
			dnsLoadGeneratorService.invoke("addNode", "Slave:${hostAddress}" as String)
		}
		
		postStop {
			def dnsLoadGeneratorService = context.waitForService("dnsLoadGeneratorService", 180, TimeUnit.SECONDS)
			// Don't remove a node until there are 2 instances running
			// Doesn't seem to be working as expected ...
			//def serviceInstance=context.waitForInstances(2, 300, TimeUnit.SECONDS)
			def hostAddress=System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
			dnsLoadGeneratorService.invoke("removeNode", "Slave:${hostAddress}" as String)
		}
		
		locator {
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=named")
			println "dnsSlave-service.groovy: current PIDs: ${myPids}"
			return myPids
		}
		
	}
	
	userInterface {

		metricGroups = ([
			metricGroup {

				name "DNS"

				metrics([
					"DNS Request Rate"
				])
			}
		])

		widgetGroups = ([
			widgetGroup {
				name "DNS Request Rate"
				widgets ([
					balanceGauge{metric = "DNS Request Rate"},
					barLineChart{
						metric "DNS Request Rate"
						axisYUnit Unit.REGULAR
					}
				])
			},
	])
}

	// Once additional VMs have been added or removed (scaling has occured), the scaling rules will
	// be disabled this number of seconds.
	scaleCooldownInSeconds 20
	samplingPeriodInSeconds 1


	scalingRules ([
		scalingRule {

			serviceStatistics {
				metric "DNS Request Rate"
				movingTimeRangeInSeconds 10
			}

			highThreshold {
				value 200
				instancesIncrease 1
			}

			lowThreshold {
				value 10
				instancesDecrease 1
			}
		}
	])

}
