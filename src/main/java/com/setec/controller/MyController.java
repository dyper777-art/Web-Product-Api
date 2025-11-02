package com.setec.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setec.dao.PostProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;

@RestController
@RequestMapping("/api/product")
public class MyController {
	//http://localhost:8080/swagger-ui/index.html

	@Autowired
	private ProductRepo productRepo;
	
	@GetMapping
	public Object getAllProduct() {
		var products = productRepo.findAll();
		
		if(products.size() > 0) {
			return products;
		}
		
		return ResponseEntity.status(404).body(Map.of("message", "product is empty."));
	}
	
	@GetMapping("{id}")
	public Object getById(@PathVariable("id") Integer id) {
		var product = productRepo.findById(id);
		if(product.isPresent())
			return product.get();
		
		return ResponseEntity.status(404).body(Map.of("message", "Product id="+id+" not found"));
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Object addProduct(@ModelAttribute PostProductDAO product) throws Exception {
		
		var file = product.getFile();
		
		String uploadDir = new File("myApp/static").getAbsolutePath();
		File dir = new File(uploadDir);
		
		if(!dir.exists()) {
			dir.mkdirs(); 
		}
		
		String fileName = file.getOriginalFilename();
		String uniqueName = UUID.randomUUID()+"_"+fileName;
		String filePath = Paths.get(uploadDir, uniqueName).toString();
		
		file.transferTo(new File(filePath));
		
		var pro = new Product();
		pro.setName(product.getName());
		pro.setPrice(product.getPrice());
		pro.setQty(product.getQty());
		pro.setImageUrl("/static/"+uniqueName);
		productRepo.save(pro);
		
		return ResponseEntity.status(201).body(pro);
	}
}
