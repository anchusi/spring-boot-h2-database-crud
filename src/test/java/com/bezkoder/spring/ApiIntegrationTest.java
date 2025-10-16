package com.bezkoder.spring;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bezkoder.spring.order.OrderRepository;
import com.bezkoder.spring.product.Product;
import com.bezkoder.spring.product.ProductRepository;
import com.bezkoder.spring.user.User;
import com.bezkoder.spring.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

	private static final String USERNAME = "user";
	private static final String ADMIN = "admin";
	private static final String PASSWORD = "password";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	private User savedUser;

	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();

		savedUser = userRepository.save(new User("jdoe", "John Doe"));

		List<Product> products = List.of(
			new Product("Monitor", BigDecimal.valueOf(199.99)),
			new Product("Keyboard", BigDecimal.valueOf(79.50)),
			new Product("Mouse", BigDecimal.valueOf(35.00))
		);

		productRepository.saveAll(products);
	}

	@Test
	void getUserDetails_returnsUser() throws Exception {
		mockMvc.perform(get("/api/users/" + savedUser.getId())
				.with(httpBasic(USERNAME, PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
			.andExpect(jsonPath("$.username", is("jdoe")))
			.andExpect(jsonPath("$.displayName", is("John Doe")));
	}

	@Test
	void getUserDetails_unknownId_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/users/{id}", 9999L)
				.with(httpBasic(USERNAME, PASSWORD)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.error", is("User not found")))
			.andExpect(jsonPath("$.timestamp", notNullValue()))
			.andExpect(jsonPath("$.path", is("/api/users/9999")))
			.andExpect(jsonPath("$.fieldErrors", hasSize(0)));
	}

	@Test
	void createOrder_withValidPayload_returnsCreated() throws Exception {
		String payload = """
			{
				"userId": %d,
				"reference": "ORD-12345",
				"total": 120.50,
				"itemCount": 2
			}
			""".formatted(savedUser.getId());

		mockMvc.perform(post("/api/orders")
				.with(httpBasic(USERNAME, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.reference", is("ORD-12345")))
			.andExpect(jsonPath("$.total", is(120.50)))
			.andExpect(jsonPath("$.itemCount", is(2)))
			.andExpect(jsonPath("$.userId", is(savedUser.getId().intValue())));
	}

	@Test
	void createOrder_withInvalidPayload_returnsValidationErrors() throws Exception {
		String payload = """
			{
				"userId": null,
				"reference": "",
				"total": 0,
				"itemCount": 0
			}
			""";

		mockMvc.perform(post("/api/orders")
				.with(httpBasic(USERNAME, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.fieldErrors", hasSize(4)));
	}

	@Test
	void getProducts_returnsPaginatedList() throws Exception {
		mockMvc.perform(get("/api/products")
				.with(httpBasic(USERNAME, PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(3)))
			.andExpect(jsonPath("$.page", is(0)))
			.andExpect(jsonPath("$.size", is(20)))
			.andExpect(jsonPath("$.totalElements", is(3)))
			.andExpect(jsonPath("$.totalPages", is(1)));
	}

	@Test
	void getProducts_outOfRangePage_returnsEmptyContent() throws Exception {
		mockMvc.perform(get("/api/products")
				.param("page", "5")
				.param("size", "2")
				.with(httpBasic(USERNAME, PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", empty()))
			.andExpect(jsonPath("$.page", is(5)))
			.andExpect(jsonPath("$.size", is(2)))
			.andExpect(jsonPath("$.totalElements", is(3)))
			.andExpect(jsonPath("$.totalPages", is(2)));
	}

	@Test
	void adminOverview_requiresAdminRole() throws Exception {
		mockMvc.perform(get("/api/admin/overview")
				.with(httpBasic(USERNAME, PASSWORD)))
			.andExpect(status().isForbidden());
	}

	@Test
	void adminOverview_withAdminCredentials_returnsCounts() throws Exception {
		mockMvc.perform(get("/api/admin/overview")
				.with(httpBasic(ADMIN, PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userCount", is(1)))
			.andExpect(jsonPath("$.productCount", is(3)))
			.andExpect(jsonPath("$.orderCount", is(0)));
	}
}

