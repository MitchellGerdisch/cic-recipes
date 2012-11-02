import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("dnsMaster.properties").toURL())

def node = args[0]
def instanceID= args[1]

println "removeSlave: About to remove ${node} instance (${instanceID}) to resolve.conf, named.conf..."

def namedConfigFile = new File("/etc/${config.namedFile}")
def configText = namedConfigFile.text
// TODO  what to modify ??.....

new AntBuilder().copy(file:'/etc/resolv.conf' , tofile:'/etc/resolv.conf.bak')
resolvFile = new File("/etc/resolv.conf")
resolvFile.write( updateFile(resolvFile,node) )

def updateFile( resolvFile,node ) { 
   def text = ""
   resolvFile.eachLine { line -> 
      if ( !line.contains("${node}")  ) 
         text += line + "\n" 
   } 
   text 
} 

//println "Reloading named.conf of Master DNS"
//builder = new AntBuilder().exec(executable:"${context.serviceDirectory}/${config.reloadScript}", osfamily:"unix")

