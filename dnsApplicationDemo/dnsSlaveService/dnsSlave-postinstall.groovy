import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > DNS Slave file Configuration "
context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("dnsSlave.properties").toURL())

def dnsMasterService = context.waitForService("dnsMasterService", 180, TimeUnit.SECONDS)
masterHostInstances = dnsMasterService.waitForInstances(dnsMasterService.numberOfPlannedInstances, 60, TimeUnit.SECONDS)

hostIp = InetAddress.localHost.hostAddress

builder = new AntBuilder()
builder.sequential {
		copy(file:'/etc/resolv.conf' , tofile:'/etc/resolv.conf.bak')
		delete(file:"/etc/sysconfig/named")
		delete(file:"/etc/resolv.conf")
		copy(file:"${context.serviceDirectory}/${config.namedFile}" , 		tofile:"/etc/${config.namedFile}")
		copy(file:"${context.serviceDirectory}/${config.rootHintsFile}" , 	tofile:"/var/named/${config.rootHintsFile}")
		copy(file:"${context.serviceDirectory}/${config.localZoneFile}" , 	tofile:"/var/named/${config.localZoneFile}")	

		copy(file:"${context.serviceDirectory}/${config.revFile}" , 				tofile:"/var/named/${config.revFile}" )
		copy(file:"${context.serviceDirectory}/${config.namedZone}" , 		tofile:"/var/named/${config.namedZone}")
		copy(file:"${context.serviceDirectory}/${config.revFile}" , 				tofile:"/var/named/slaves/${config.revFile}" )
		copy(file:"${context.serviceDirectory}/${config.namedZone}" , 		tofile:"/var/named/slaves/${config.namedZone}")
		
		copy(file:"${context.serviceDirectory}/named_monitor.sh" , 		tofile:"/var/named/named_monitor.sh")
		chmod(file:"/var/named/named_monitor.sh", perm: '+x')
		
		chmod(file:"/etc/${config.namedFile}", perm:'+x')
		chmod(dir:"/var/named", perm:'777')
		chmod(dir:"/var/run/named", perm:'777')
		}

//println "addSlave ..."
//dnsMasterService.invoke("addSlave", InetAddress.getLocalHost().getHostAddress())

masterHostIp=masterHostInstances[0].hostAddress
println "Master host is  = ${masterHostIp}"

//namedUpdFile = new File("/etc/${config.namedFile}")
//namedUpdText = namedUpdFile.text
//namedUpdFile.text = namedUpdText.replaceAll("MASTER-IP", "\\${masterHostIp}")
//namedUpdText = namedUpdFile.text
//namedUpdFile.text = namedUpdText.replaceAll("HOST-IP", "\\${hostIp}")


resolvFile = new File("/etc/resolv.conf")
textline = "; Slave nameserver\n"
resolvFile.append(textline)
textline = "domain alu-cb.net\n"
resolvFile.append(textline)
textline = "search alu-cb.net\n"
resolvFile.append(textline)
textline = "nameserver  " + hostIp + "\n"
resolvFile.append(textline)
textline = "nameserver  " + masterHostIp + "\n"
resolvFile.append(textline)

println "###### >  End Configuration"


