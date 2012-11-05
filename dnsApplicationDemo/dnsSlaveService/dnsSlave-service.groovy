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
			key="DNS Request Delta"
			value="named_monitor.sh".execute()
			return [key:value]
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
		
		
	}
	
	userInterface {

		metricGroups = ([
			metricGroup {

				name "process"

				metrics([
				    "Total Process Cpu Time",
					"Process Cpu Usage",
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
			} ,
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

	// Once additional VMs have been added or removed (scaling has occured), the scaling rules will
	// be disabled this number of seconds.
	scaleCooldownInSeconds 20
	samplingPeriodInSeconds 1

	scalingRules ([
		scalingRule {

			serviceStatistics {
				metric "Total Process Cpu Time"
				timeStatistics Statistics.averageCpuPercentage
			    instancesStatistics Statistics.maximum
				movingTimeRangeInSeconds 10
			}

			highThreshold {
				value 5
				instancesIncrease 1
			}

			lowThreshold {
				value 2
				instancesDecrease 1
			}
		}
	])
}
