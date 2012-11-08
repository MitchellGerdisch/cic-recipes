import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import java.util.concurrent.TimeUnit
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > Installing Ulteo Session Application"

config = new ConfigSlurper().parse(new File("ulteoApplication.properties").toURL())
context = ServiceContextFactory.getServiceContext()
osConfig = USMUtils.isWindows() ? config.win32 : config.linux

def ulteoManagerService = context.waitForService("ulteoManagerService", 180, TimeUnit.SECONDS)
sessionManagerInstances = ulteoManagerService.waitForInstances(ulteoManagerService.numberOfPlannedInstances, 60, TimeUnit.SECONDS)

builder = new AntBuilder()
os = OperatingSystem.getInstance()
currVendor = os.getVendor()

managerIP=sessionManagerInstances[0].hostAddress

switch (currVendor) {
	case ["Ubuntu", "Debian", "Mint"]:
		builder.sequential {
			chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
			echo(message:"Running ${context.serviceDirectory}/installOnUbuntu.sh os is ${currVendor}...")
			exec(executable: "${context.serviceDirectory}/installOnUbuntu.sh", osfamily:"unix", failonerror: "true" ) {
											arg value: "${managerIP}"
											}
		}
		break

	case ["Red Hat", "CentOS", "Fedora", "Amazon",""]:
		builder.sequential {
			chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
			echo(message:"Running ${context.serviceDirectory}/installOnLinux.sh os is ${currVendor}...")
			exec(executable: "${context.serviceDirectory}/installOnLinux.sh", osfamily:"unix", failonerror: "true" ) {
											arg value: "${managerIP}"
											}
		}

		break
	default: throw new Exception("Support for ${currVendor} is not implemented")
}


println("Registering MySQL Driver Class")
try {
	Class.forName("com.mysql.jdbc.Driver").newInstance()
} catch (ClassNotFoundException e) {
			println("Where is your MySQL JDBC Driver?" + + e.getMessage())
			e.printStackTrace()
			return
		}
println("MySQL JDBC Driver Registered!")
