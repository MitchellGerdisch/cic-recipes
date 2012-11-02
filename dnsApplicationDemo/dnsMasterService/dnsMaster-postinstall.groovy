import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > DNS Master file Configuration "

config = new ConfigSlurper().parse(new File("dnsMaster.properties").toURL())
context = ServiceContextFactory.getServiceContext()

hostIp=InetAddress.localHost.hostAddress

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
		
		chmod(file:"/etc/${config.namedFile}", perm:'+x')
		chmod(file:"${context.serviceDirectory}/${config.reloadScript}", perm:'777')

		chmod(dir:"/var/named", perm:'777')
		chmod(dir:"/var/run/named", perm:'777')
		}

resolvFile = new File("/etc/resolv.conf")
textline = "; Master nameserver\n"
resolvFile.append(textline)
textline = "domain alu-cb.net\n"
resolvFile.append(textline)
textline = "search alu-cb.net\n"
resolvFile.append(textline)
textline = "nameserver  " + hostIp + "\n"
resolvFile.append(textline)
