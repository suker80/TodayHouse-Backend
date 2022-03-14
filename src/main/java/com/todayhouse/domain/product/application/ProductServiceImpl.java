package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.dto.ImageResponse;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import com.todayhouse.domain.user.exception.SellerNotFoundException;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final FileService fileService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Long saveProductRequest(List<MultipartFile> multipartFiles, ProductSaveRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        // jwt로 seller 찾기
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getSeller() == null)
            throw new SellerNotFoundException();
        return saveEntity(multipartFiles, request, user, category);
    }

    // 이미지 저장, 대표 이미지가 없으면 맨 처음 파일이 대표 이미지로 설정
    @Override
    public Long saveProductImages(List<MultipartFile> multipartFiles, Long productId) {
        Product product = getValidProduct(productId);
        List<String> fileNames = fileService.upload(multipartFiles);
        imageService.save(fileNames, product);

        if (product.getImage() == null && !fileNames.isEmpty())
            product.updateImage(fileNames.get(0));

        return product.getId();
    }

    // seller와 join한 모든 product, 대표 이미지
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllWithSeller(ProductSearchRequest productSearch, Pageable pageable) {
        Page<ProductResponse> page = productRepository.findAllWithSeller(productSearch, pageable)
                .map(p -> {
                    ProductResponse response = new ProductResponse(p);
                    if (StringUtils.hasText(p.getImage()))
                        response.setImages(List.of(createImageResponse(p.getImage())));
                    return response;
                });
        return page;
    }

    // product 와 productImage, 모든 options, seller left join
    @Override
    @Transactional(readOnly = true)
    public Product findByIdWithOptionsAndSellerAndImages(Long id) {
        return productRepository.findByIdWithOptionsAndSellerAndImages(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        Product product = getValidProduct(request.getId());
        product.update(request, category);
        return productRepository.save(product);
    }

    // s3의 이미지도 삭제
    @Override
    public void deleteProduct(Long id) {
        getValidProduct(id);
        List<String> fileNames = productImageRepository.findByProductId(id)
                .stream().map(i -> i.getFileName()).collect(Collectors.toList());
        fileService.delete(fileNames);
        productRepository.deleteById(id);
    }

    // product의 seller와 user의 seller가 같은지 확인
    private Product getValidProduct(Long productId) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(jwtEmail).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findByIdWithSeller(productId).orElseThrow(ProductNotFoundException::new);
        if (!user.getSeller().equals(product.getSeller()))
            throw new InvalidRequestException();
        return product;
    }

    // product, image, filename 저장
    private Long saveEntity(List<MultipartFile> multipartFiles, ProductSaveRequest request,
                            User user, Category category) {
        List<String> fileNames = saveFiles(multipartFiles);
        String first = null;
        if (fileNames != null && !fileNames.isEmpty())
            first = fileNames.get(0);
        Product product = productRepository.save(request.toEntityWithParentAndSelection(user.getSeller(), category, first));
        imageService.save(fileNames, product);
        return product.getId();
    }

    private List<String> saveFiles(List<MultipartFile> multipartFiles) {
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            return fileService.upload(multipartFiles);
        }
        return null;
    }

    private ImageResponse createImageResponse(String fileName) {
        byte[] image = fileService.getImage(fileName);
        return new ImageResponse(fileName, image);
    }
}
