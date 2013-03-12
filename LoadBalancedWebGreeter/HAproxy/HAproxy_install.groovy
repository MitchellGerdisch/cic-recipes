
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("HAproxy-service.properties").toURL())

installScript="HAproxy_install.sh"

// Install HAproxy using the install script

builder = new AntBuilder()
builder.sequential {
	echo(message:"HAproxy_install.groovy: Chmodding +x ${context.serviceDirectory} ...")
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")

	echo(message:"HAproxy_install.groovy: Running ${context.serviceDirectory}/${installScript} ...")
	exec(executable: "${context.serviceDirectory}/${installScript}",failonerror: "true")
	

}
