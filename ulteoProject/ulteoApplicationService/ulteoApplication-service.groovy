import java.sql.*
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory

config = new ConfigSlurper().parse(new File("ulteoApplication.properties").toURL())

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
				
				scaleIndicator = "/root/${config.scaleCheck} ${managerIP} root root".execute().text

				println "Number of VDI sessions --->  : " + scaleIndicator
			 	return ["Number of VDI Sessions":scaleIndicator as Integer ]
	      }	
	}
	
	userInterface {

		metricGroups = ([
			metricGroup {

				name "process"

				metrics([
					"Number of VDI Sessions",
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
			}
		])
	}

	scaleCooldownInSeconds 10
	samplingPeriodInSeconds 1

	scalingRules ([
		scalingRule {
        
			serviceStatistics {
				metric "Current Active Sessions"
				//statistics Statistics.maximumOfAverages
				movingTimeRangeInSeconds 10
			}

			highThreshold {
				value 1
				instancesIncrease 1
			}

			lowThreshold {
				value 0
				instancesDecrease 1
			}
		}
	])
}
