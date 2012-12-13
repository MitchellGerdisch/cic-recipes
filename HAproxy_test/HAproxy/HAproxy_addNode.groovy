
/**********************************************************************************************************************
 * Parameters:
 * 		arg[0] = IP address of backend service VM. (The service VM will pass it's IP address when invoking the method.)
 */

import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.context.ServiceContextFactory

def backendVmIp = args[0]

context = ServiceContextFactory.getServiceContext()

config = new ConfigSlurper().parse(new File("HAproxy-service.properties").toURL())
haproxy_cfg_file="${config.haproxy_cfg_file}"
haproxy_cfg_dir="${config.haproxy_cfg_dir}"
backend_port="${config.backend_port}"

restartScript="HAproxy_restart.sh"


println "HAproxy_addNode: About to add ${backendVmIp} to ${haproxy_cfg_dir}/${haproxy_cfg_file} ..."

// Add the new server into resources.xml
Builder = new AntBuilder()
Builder.sequential {
	// First replace the last line of the resources.xml file with the new resource entry.
	replaceregexp(file:"${haproxy_cfg_dir}/${haproxy_cfg_file}", 
		match:"#INSERT BACKEND SERVER ENTRY HERE#", 
		replace:"server srv-${backendVmIp} ${backendVmIp}:${backend_port} weight 1 maxconn 100 check inter 4000 cookie srv-${backendVmIp}");	
	// Now add back in the line used to do the above substitution.
	echo(message:"#INSERT BACKEND SERVER ENTRY HERE#\n", file:"${haproxy_cfg_dir}/${haproxy_cfg_file}", append:"true");
	
	echo(message:"HAproxy_addNode.groovy: restarting haproxy ...")
	exec(executable: "${context.serviceDirectory}/${restartScript}",failonerror: "true")
}