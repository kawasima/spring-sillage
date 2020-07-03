package net.unit8.sillage.example;

import net.unit8.sillage.ResourceEngine;
import net.unit8.sillage.data.ClassResource;
import net.unit8.sillage.data.ClassResourceFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {
	private final ResourceEngine resourceEngine;
	private final ClassResourceFactory resourceFactory;

	public UserController(ResourceEngine resourceEngine,
						  ClassResourceFactory resourceFactory) {
		this.resourceEngine = resourceEngine;
		this.resourceFactory = resourceFactory;
	}

	@RequestMapping("/users")
	public Object greeting(HttpServletRequest request,
						   HttpServletResponse response) {
		ClassResource resource = resourceFactory.create(UsersResource.class);
		return resourceEngine.run(resource, request, response);
	}
}
