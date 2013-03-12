/*******************************************************************************
* Set up a web interface that let's the user suspend/unsuspend the VM
*******************************************************************************/
import org.cloudifysource.dsl.context.ServiceContextFactory
import org.cloudifysource.usm.USMUtils
import java.util.concurrent.TimeUnit

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("webService-service.properties").toURL())

def hostIp

if (  context.isLocalCloud()  ) {
	hostIp = InetAddress.getLocalHost().getHostAddress()
}
else {
	hostIp = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
}

println "HostIP is: $hostIp"

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