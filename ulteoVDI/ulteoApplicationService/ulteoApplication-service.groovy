import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.hyperic.sigar.OperatingSystem

service {

	name "ulteoApplicationService"
	type "WEB_SERVER"
	icon "ulteo.jpg"

    elastic true
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 2
	
	compute {
		template "ulteoApplication_Template"
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
				sessionManagerInstances = ulteoManagerService.waitForInstances(ulteoManagerService.numberOfPlannedInstances, 180, TimeUnit.SECONDS)
				managerIP=sessionManagerInstances[0].hostAddress
				
				println "Checking ulteoManager DB at ${managerIP} to see if App Servers should be scaled"
				
				numSessions = "/root/numVdiSessions.sh ${managerIP} root root".execute().text
				numActiveServers = "/root/numActiveServers.sh ${managerIP} root root".execute().text
				
				// test code for now
				scaleIndicator = 0

				println "Number of VDI sessions --->  : " + numSessions
				println "Number of Active Servers ---> :" + numActiveServers
				println "Scale Indicator ---> :" + scaleIndicator
			 	return ["Number of VDI Sessions":numSessions as Integer, "Number of Active Servers":numActiveServers as Integer, "Scale Indicator": scaleIndicator as Integer]
	      }	
	}
	
	userInterface {

		metricGroups = ([
			metricGroup {

				name "process"

				metrics([
					"Number of VDI Sessions",
					"Number of Active Servers",
					"Scale Indicator",
					"Process Cpu Usage",
					"Total Process Virtual Memory",
					"Num Of Active Threads"
				])
			}
		])


		widgetGroups = ([
			widgetGroup {
				name "Number of VDI Sessions"
				widgets ([
					balanceGauge{metric = "Number of VDI Sessions"},
					barLineChart{
						metric "Number of VDI Sessions"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup {
				name "Number of Active Servers"
				widgets ([
					balanceGauge{metric = "Number of Active Servers"},
					barLineChart{
						metric "Number of Active Servers"
						axisYUnit Unit.REGULAR
					}
				])
			},
		
		])
	}

	scaleCooldownInSeconds 10
	samplingPeriodInSeconds 1

	scalingRules ([
		scalingRule {
        
			serviceStatistics {
				metric "Scale Indicator"
				//statistics Statistics.maximumOfAverages
				movingTimeRangeInSeconds 10
			}

			highThreshold {
				value 0.9
				instancesIncrease 1
			}

			lowThreshold {
				value 0.1
				instancesDecrease 1
			}
		}
	])
}
