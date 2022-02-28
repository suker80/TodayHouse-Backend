package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.dao.CustomProductRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CustomProductRepository customProductRepository;

    @Mock
    CategoryRepository categoryRepository;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("product 저장")
    void saveProduct() {
        String email = "test@test.com";
        SecurityContextSetting(email);
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        User user = User.builder().email(email).seller(seller).build();
        ProductSaveRequest request = ProductSaveRequest.builder().categoryId(1L).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.saveProductRequest(request);
        assertThat(result).isEqualTo(product);
    }

//    @Test
//    void findAll() {
//        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());
//        Page<ProductResponse> response = Mockito.spy(Page.class);
//        Page<Product> products = Mockito.spy(Page.class);
//
//        when(customProductRepository.findAll(pageRequest)).thenReturn(products);
//
//        Page<ProductResponse> result = productService.findAll(pageRequest);
//        assertThat(result).isEqualTo(response);
//    }

    @Test
    @DisplayName("product id로 찾기")
    void findOne() {
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));

        Product result = productService.findOne(1L);
        assertThat(result).isEqualTo(product);
    }

    @Test
    @DisplayName("product 수정")
    void updateProduct() {
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(1L).categoryId(1L).build();
        Product product = getValidProduct(request.getId());
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(productRepository.save(product)).thenReturn(product);

        assertThat(productService.updateProduct(request)).isEqualTo(product);
    }

    @Test
    @DisplayName("product id로 삭제")
    void removeProduct() {
        getValidProduct(1L);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    private void SecurityContextSetting(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private Product getValidProduct(Long id) {
        String email = "email@test.com";
        SecurityContextSetting(email);
        Seller seller = Seller.builder().build();
        User user = User.builder().email(email).seller(seller).build();
        Product product = Product.builder().seller(seller).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.findById(id)).thenReturn(Optional.ofNullable(product));

        return product;
    }

}