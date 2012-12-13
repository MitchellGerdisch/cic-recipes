
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


println "HAproxy_addNode: About to add ${backendVmIp} to ${haproxy_cfg_dir}/${haproxy_cfg_file} ..."

// Add the new server into resources.xml
Builder = new AntBuilder()
Builder.sequential {
	// First replace the last line of the resources.xml file with the new resource entry.
	replaceregexp(file:"${haproxy_cfg_dir}/${haproxy_cfg_file}", 
		match:"#INSERT BACKEND SERVER ENTRY HERE#", 
		replace:"server srv-${backendVmIp} ${backendVmIp}:${backend_port} weight 1 maxconn 100 check inter 4000");	
	// Now add back in the line used to do the above substitution.
	echo(message:"#INSERT BACKEND SERVER ENTRY HERE#\n", file:"${haproxy_cfg_dir}/${haproxy_cfg_file}", append:"true");
}




def dnsServerListFile = new File("${serverListFile}")
def dnsServerListText = dnsServerListFile.text


// Update the server list file on the dnsLoadGenerator instance if necessary.
if ( dnsServerListText.contains(dnsVmIp)) {
	println "addNode: Not adding ${dnsVmIp} to ${serverListFile} because it's already there..."	
}
else { 
	def modifiedText = dnsServerListText.replace("${serverListPreamble}", "${serverListPreamble}" + System.getProperty("line.separator") + "${dnsVmIp}")				
	dnsServerListFile.text = modifiedText
	println "addNode: Added ${dnsVmIp} to ${serverListFile} text is now : ${modifiedText}..."

/* NOT using this logic since I don't think I need to save the data as part of the application attributes.
 * But if I do decide I do want to, I'm keeping this logic handy

	def balancerMembers=context.attributes.thisService["balancerMembers"]
	if ( balancerMembers == null ) {
		balancerMembers = ""
	}
	balancerMembers +=",${balancerMemberText},"										
	context.attributes.thisService["balancerMembers"]=balancerMembers
	println "addNode: Added ${node} to context balancerMembers which is now ${balancerMembers}"
*/

}
