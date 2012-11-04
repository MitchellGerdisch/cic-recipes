
/**********************************************************************************************************************
 * Parameters:
 * 		arg[0] = IP address of dns VM. (The dns VM will pass it's IP address when invoking the method.)
 */

import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.context.ServiceContextFactory

def dnsVmIp = args[0]

context = ServiceContextFactory.getServiceContext()

config = new ConfigSlurper().parse(new File("dnsLoadGenerator-service.properties").toURL())
webServerDirectory=config.webServerDirectory
webServerHtdocs=config.webServerHtdocs
webServerCgibin=config.webServerCgibin
serverList=config.serverList
serverListPreamble=config.serverListPreamble
serverListFile="${webServerDirectory}/${webServerCgibin}/${serverList}"


println "addNode: About to add ${dnsVmIp} to ${serverListFile} ..."
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
