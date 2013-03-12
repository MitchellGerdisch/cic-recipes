/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
import org.cloudifysource.dsl.context.ServiceContextFactory
import org.cloudifysource.usm.USMUtils
import java.util.concurrent.TimeUnit

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("webService-service.properties").toURL())



// Find the IP address to pass to load balancer.
NetworkInterface ni = NetworkInterface.getByName("eth0");
Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
def hostIp
while(inetAddresses.hasMoreElements()) {
	InetAddress ia = inetAddresses.nextElement();
	if(!ia.isLinkLocalAddress()) {
		ipAddr=ia.getHostAddress();
		println("Found host IP: " + ipAddr);
		hostIp = ipAddr;
	}
}


println "webService_start.groovy: HostIP is: $hostIp"

def haService = context.waitForService("HAproxy", 180, TimeUnit.SECONDS)
println "Invoking addNode, http://${hostIp}:80/"
haService.invoke("addNode", "${hostIp}" as String)
println "AddNode complete"

greetingText=config.WebPageGreeting

webServerDirectory=config.webServerDirectory
webServerCgibin=config.webServerCgibin
webServerHtml=config.webServerHtml

builder = new AntBuilder()

builder.sequential {

	echo(message:"webService_start.groovy: creating index.html file.")
	
	echo(message:"${greetingText} from: ${hostIp}", file:"${webServerDirectory}/${webServerHtml}/index.html")
	
	exec(executable: 'service', osfamily:"unix") {
							 arg value:"httpd"
							 arg value:"restart"
	}

}