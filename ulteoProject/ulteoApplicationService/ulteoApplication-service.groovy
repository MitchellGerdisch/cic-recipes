import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.sql.*

service {

	name "ulteoApplicationService"
	type "WEB_SERVER"
	icon "ulteo.jpg"

    elastic true
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 4
	
	compute {
		template "QUAD_ROOT_ACCESS"
	}	
	
	lifecycle{

		install "ulteoApplication-install.groovy"	
		start "ulteoApplication-start.groovy"	
		preStop "ulteoApplication-stop.groovy"

		locator {
			def ovdPId= ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.eq=ulteo-ovd-subsystem")
			println "IPs are : ${ovdPId}"
			return ovdPId
		 }
		
		startDetectionTimeoutSecs 120
		startDetection {
			ServiceUtils.arePortsOccupied([1112,1113,3389])
		}
		
	 monitors{
				def ulteoManagerService = context.waitForService("ulteoManagerService", 180, TimeUnit.SECONDS)
				sessionManagerInstances = ulteoManagerService.waitForInstances(ulteoManagerService.numberOfPlannedInstances, 60, TimeUnit.SECONDS)
				managerIP=sessionManagerInstances[0].hostAddress
				
				println "Getting session count from ${managerIP}"
				
				sessionCount = "/root/getUlteoSessionsCount.sh ${managerIP} root root".execute().text

				println "Total Number of Sessions --->  : " + sessionCount as Integer
			 	return ["Current Active Sessions":sessionCount as Integer ]
	      }	
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

	scaleCooldownInSeconds 10
	samplingPeriodInSeconds 1

	scalingRules ([
		scalingRule {
        
			serviceStatistics {
				metric "Current Active Sessions"
				//statistics Statistics.maximumThroughput
				statistics Statistics.averageThroughput
				movingTimeRangeInSeconds 1
			}

			highThreshold {
				value 3
				instancesIncrease 1
			}

			lowThreshold {
				value 2
				instancesDecrease 1
			}
		}
	])
}
