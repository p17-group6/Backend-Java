package com.beerproducts.backend_products_ms;

import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.beerproducts.backend_products_ms.models.Product;
import com.beerproducts.backend_products_ms.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.types.ObjectId;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = BackendProductsMsApplication.class)
@AutoConfigureMockMvc
class BackendProductsMsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Test
	void contextLoads() {
	}

	@Test
	public void whenPostRequestToProductsAndInvalidProduct_thenBadResponse() throws Exception {
		String product = "{\"name\": \"\", \"description\" : \"\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").content(product)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void whenPostRequestToProductsAndInvalidField_thenErrorResponse() throws Exception {
		String product = "{\"name\": \"test name\"}";
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/products").content(product).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.errors..description",
						IsIterableContaining.hasItem("Description is required")))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void whenPostRequestToProductsAndValidProduct_thenCorrectResponse() throws Exception {
		// MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN,
		// Charset.forName("UTF-8"));
		String product = "{\"name\": \"test product\",\"username\": \"jhon117\", \"description\" : \"test desc\", \"image\" : \"test image\", \"style\" : \"test style\", \"price\" : \"20\", \"category\" : [\"test category\"], \"avg_grade\": \"1.1\", \"ibu_grade\": \"1.2\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").content(product)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void whenPutRequestToProductAndValidProduct_thenCorrectResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Product product = new Product("Name test", "kuro", "Description test", List.of("category test"),
				"Image.png test",
				BigDecimal.TEN, "style test", Float.parseFloat("2.3"), Float.parseFloat("2.1"));

		product.setId(ObjectId.get().toHexString());
		product.setAt_created(new Date());
		doReturn(product).when(productService).saveOrUpdateProduct(product);
		doReturn(List.of(product)).when(productService).findProductsByUsername(product.getUsername());
		doReturn(Optional.of(product)).when(productService).findProductById(product.getId());

		Product productToPut = new Product("Name updated", "kuro", "Description updated", List.of("category updated"),
				"Image.png updated", BigDecimal.TEN, "style updated", Float.parseFloat("2.3"), Float.parseFloat("2.1"));

		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/products/{username}/update/{id}", product.getUsername(), product.getId())
				.content(mapper.writeValueAsString(productToPut)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void whenPutRequestToProductAndInvalidProductId_thenErrorResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Product product = new Product("Name test", "kuro", "Description test", List.of("category test"),
				"Image.png test",
				BigDecimal.TEN, "style test", Float.parseFloat("2.3"), Float.parseFloat("2.1"));

		product.setId(ObjectId.get().toHexString());
		product.setAt_created(new Date());
		doReturn(product).when(productService).saveOrUpdateProduct(product);
		doReturn(List.of(product)).when(productService).findProductsByUsername(product.getUsername());
		doReturn(Optional.of(product)).when(productService).findProductById(product.getId());

		Product productToPut = new Product("Name updated", "kuro", "Description updated", List.of("category updated"),
				"Image.png updated", BigDecimal.TEN, "style updated", Float.parseFloat("2.3"), Float.parseFloat("2.1"));

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/products/{username}/update/{id}", product.getUsername(), "999")
						.content(mapper.writeValueAsString(productToPut)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.errors",
						IsIterableContaining.hasItem("Not found product with the id 999")))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void whenDeleteRequestToProductAndValidProduct_thenCorrectResponse() throws Exception {
		Product product = new Product("Name test", "kuro", "Description test", List.of("category test"),
				"Image.png test",
				BigDecimal.TEN, "style test", Float.parseFloat("2.3"), Float.parseFloat("2.1"));

		product.setId(ObjectId.get().toHexString());
		product.setAt_created(new Date());
		doReturn(product).when(productService).saveOrUpdateProduct(product);
		doReturn(Optional.of(product)).when(productService).findProductById(product.getId());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", product.getId().toString()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
