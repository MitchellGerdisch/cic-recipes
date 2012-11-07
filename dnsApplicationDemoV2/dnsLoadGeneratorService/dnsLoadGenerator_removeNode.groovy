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

println "removeNode: About to remove ${dnsVmIp} from ${serverListFile} ..."
def dnsServerListFile = new File("${serverListFile}")
def dnsServerListText = dnsServerListFile.text
def modifiedText = dnsServerListText.replace("${dnsVmIp}", "")
dnsServerListFile.text = modifiedText

println "removeNode: Removed ${dnsVmIp} from ${serverListFile} text is now : ${modifiedText}..."

/* NOT using this logic since I don't think I need to save the data as part of the application attributes.
 * But if I do decide I do want to, I'm keeping this logic handy
def balancerMembers=context.attributes.thisService["balancerMembers"]
balancerMembers=balancerMembers.replace(",${balancerMemberText},","")
if ( balancerMembers == "" ) {
	balancerMembers = null
}
context.attributes.thisService["balancerMembers"]=balancerMembers
println "removeNode: Cleaned ${node} from context balancerMembers"
*/

	