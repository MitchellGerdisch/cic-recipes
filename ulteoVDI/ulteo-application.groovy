application {
	
	name="ulteoVDI"

	service {
		name = "ulteoManagerService"
	}

	service {
		name = "ulteoApplicationService"
			dependsOn = ["ulteoManagerService"]
	}
}
