 import groovy.util.ConfigSlurper
 import org.hyperic.sigar.OperatingSystem
 import org.cloudifysource.usm.USMUtils
 import org.cloudifysource.dsl.context.ServiceContextFactory
 
println "###### > ulteoManager-poststart.groovy"

// Try to prime the pump so to speak
{
	// Hopefully this will fix the problem where the first demo user access gets an error.
	"wget --no-check-certificate 'https://127.0.0.1/ovd/client/start.php'".execute()
}