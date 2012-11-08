application {
	
	name="VDIApplication"

	service {
		name = "ulteoManagerService"
	}

	service {
		name = "ulteoApplicationService"
			dependsOn = ["ulteoManagerService"]
	}
}
