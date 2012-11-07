/*******************************************************************************
* Set up web page and scripts for load generator and a base dns server list file
*******************************************************************************/
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("dnsLoadGenerator-service.properties").toURL())

loadGenJar=config.loadGenJar
loadGenScript=config.loadGenScript
homePage=config.homePage
cssPage=config.cssPage
cssBackgroundImg=config.cssBackgroundImg
startLoadCgi=config.startLoadCgi
stopLoadCgi=config.stopLoadCgi
webServerDirectory=config.webServerDirectory
webServerHtdocs=config.webServerHtdocs
webServerCgibin=config.webServerCgibin
serverList=config.serverList
serverListPreamble=config.serverListPreamble
showServersCgi=config.showServersCgi
paintMainPageCgi=config.paintMainPageCgi
paintStopPageCgi=config.paintStopPageCgi
killLoadCgi=config.killLoadCgi
resolverList=config.resolverList


// stuff for talking to the openstack APIs
os_username=config.os_username
os_password=config.os_password
os_tenant_name=config.os_tenant_name
os_auth_url=config.os_auth_url
nova_list_serversCgi=config.nova_list_serversCgi


builder = new AntBuilder()

def os = OperatingSystem.getInstance()
def currVendor=os.getVendor()
def isLinux
switch (currVendor) {
	case ["Ubuntu", "Debian", "Mint"]:			
		isLinux=true
		break		
	case ["Red Hat", "CentOS", "Fedora", "Amazon",""]:			
		isLinux=true
		break					
	default: throw new Exception("Support for ${currVendor} is not implemented")
}

if ( isLinux ) {
	builder.sequential {		
		echo(message:"dnsLoadGenerator_prestart.groovy: installing web pages and cgi scripts....")

		copy(file:"${context.serviceDirectory}/${homePage}" , 		tofile:"${webServerDirectory}/${webServerHtdocs}/index.html")
		copy(file:"${context.serviceDirectory}/${cssPage}" , 		tofile:"${webServerDirectory}/${webServerHtdocs}/${cssPage}")
		copy(file:"${context.serviceDirectory}/${cssBackgroundImg}" , 		tofile:"${webServerDirectory}/${webServerHtdocs}/${cssBackgroundImg}")

		copy(file:"${context.serviceDirectory}/${loadGenJar}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${loadGenJar}")
		copy(file:"${context.serviceDirectory}/${loadGenScript}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${loadGenScript}")	
		copy(file:"${context.serviceDirectory}/${startLoadCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${startLoadCgi}")	
		copy(file:"${context.serviceDirectory}/${stopLoadCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${stopLoadCgi}")
		copy(file:"${context.serviceDirectory}/${stopLoadCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${stopLoadCgi}")
		copy(file:"${context.serviceDirectory}/${showServersCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${showServersCgi}")
		copy(file:"${context.serviceDirectory}/${paintMainPageCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${paintMainPageCgi}")
		copy(file:"${context.serviceDirectory}/${paintStopPageCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${paintStopPageCgi}")
		copy(file:"${context.serviceDirectory}/${killLoadCgi}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${killLoadCgi}")
		copy(file:"${context.serviceDirectory}/${resolverList}" , 		tofile:"${webServerDirectory}/${webServerCgibin}/${resolverList}")
		
		
		echo(message:"dnsLoadGenerator_prestart.groovy: setting all files in ${webServerDirectory}/${webServerCgibin} to 775 executable....")
		chmod(file:"${webServerDirectory}/${webServerCgibin}/*", perm:'755')
		
		// echo some text to a file that will act as the server list file used by the web page to show the DNS servers
		echo(message:"${serverListPreamble}", file:"${webServerDirectory}/${webServerCgibin}/${serverList}")
		
		// install python and novaclient used to access the CloudBand northbound OpenStack APIs.
		exec(executable:"${context.serviceDirectory}/dnsLoadGenerator_setup.sh", osfamily:"unix") {
			arg(line:"${os_username}")
			arg(line:"${os_password}")
			arg(line:"${os_tenant_name}")
			arg(line:"${os_auth_url}")
			arg(line: "${webServerDirectory}/${webServerCgibin}/${nova_list_serversCgi}")
		}
	}
}
