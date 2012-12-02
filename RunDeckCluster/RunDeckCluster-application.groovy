application {
	
	name="RunDeckCluster"
	
	service {
		name = "RunDeckRemoteNodes"
	}
	
	service {
		name = "RunDeckService"
			dependsOn = ["RunDeckRemoteNodes"]
	}
}
