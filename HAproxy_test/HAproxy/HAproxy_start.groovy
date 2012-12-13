
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("HAproxy-service.properties").toURL())

startScript="HAproxy_start.sh"

// Install HAproxy using the install script

builder = new AntBuilder()
builder.sequential {
	echo(message:"HAproxy_start.groovy: Chmodding +x ${context.serviceDirectory} ...")
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")

	echo(message:"HAproxy_start.groovy: Running ${context.serviceDirectory}/${startScript} ...")
	exec(executable: "${context.serviceDirectory}/${startScript}",failonerror: "true")
	
}
