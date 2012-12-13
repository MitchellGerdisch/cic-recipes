application {
	
	name="DNSApplication"
	
	service {
		name = "dnsLoadGeneratorService"
	}
	
	service {
		name = "dnsMasterService"
			dependsOn = ["dnsLoadGeneratorService"]
	}

	service {
		name = "dnsSlaveService"
			dependsOn = ["dnsMasterService"]
	}
}
