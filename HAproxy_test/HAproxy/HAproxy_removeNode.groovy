
/**********************************************************************************************************************
 * Parameters:
 * 		arg[0] = IP address of remotenode VM. (The remotenode service will pass the VM's IP address when invoking the method.)
 */

import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit

def backend_ip = args[0]
println "HAproxy_removeNode.groovy: Removing host with IP: ${backend_ip}"
context = ServiceContextFactory.getServiceContext()

config = new ConfigSlurper().parse(new File("HAproxy-service.properties").toURL())
haproxy_cfg_dir="${config.haproxy_cfg_dir}"
haproxy_cfg_file="${config.haproxy_cfg_file}"

// Add the new server into resources.xml
Builder = new AntBuilder()
Builder.sequential {
	// find the line with the vm IP in it and delete it
	replaceregexp(file:"${haproxy_cfg_dir}/${haproxy_cfg_file}", match:".*${backend_ip}.*", replace:"");
}

