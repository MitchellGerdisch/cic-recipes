import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.context.ServiceContextFactory


context = ServiceContextFactory.getServiceContext()

def node = args[0]

println "addNode: About to add ${node} instance (${instanceID}) to named.conf..."
config = new ConfigSlurper().parse(new File("dnsMaster.properties").toURL())

def namedFile = new File("/etc/${config.namedFile}")
namedFile.write( updateFile(namedFile,node) )

def updateFile( namedFile,node ) {
   def text = ""
   namedFile.eachLine { line ->

	   if (line.contains("allow-transfer"))  {
				 def int findLastEntryIdx = line.indexOf('}')
				 text += line.substring(0, findLastEntryIdx) +${node} + "};\n"
	   }
	   else  {
		   text += line + "\n"
	   		}
   	}
	text
}

new AntBuilder().copy(file:'/etc/resolv.conf' , tofile:'/etc/resolv.conf.bak')
resolvFile = new File("/etc/resolv.conf")
textline = "nameserver  " + node + "\n"
resolvFile.append(textline)

println "Reloading named.conf of Master DNS"
builder = new AntBuilder().exec(executable:"${context.serviceDirectory}/${config.reloadScript}", osfamily:"unix")