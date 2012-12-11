 import groovy.util.ConfigSlurper
 import org.hyperic.sigar.OperatingSystem
 import org.cloudifysource.usm.USMUtils
 import org.cloudifysource.dsl.context.ServiceContextFactory
 
println "###### > Starting Ulteo Session Application"

context = ServiceContextFactory.getServiceContext()
def os = OperatingSystem.getInstance()
def currVendor=os.getVendor()
def startScript

switch (currVendor) {
	case ["Ubuntu", "Debian", "Mint"]:
		startScript="${context.serviceDirectory}/runOnUbuntu.sh"
		break
	case ["Red Hat", "CentOS", "Fedora", "Amazon",""]:
		startScript="${context.serviceDirectory}/run.sh"
		break
	default: throw new Exception("Support for ${currVendor} is not implemented")
}

println "###### > Wait for Manager to be available"

println "###### > Invoke Start"

builder = new AntBuilder()
builder.sequential {
	exec(executable:"${startScript}", osfamily:"unix",failonerror: "true")
}


