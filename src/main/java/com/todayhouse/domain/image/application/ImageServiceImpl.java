package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.dao.StoryImageRepository;
import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.image.exception.ImageNotFoundException;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final FileService fileService;
    private final StoryImageRepository storyImageRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public List<StoryImage> save(List<String> fileNames, Story story) {
        return storyImageRepository.saveAll(
                Optional.ofNullable(fileNames).orElseGet(Collections::emptyList)
                        .stream().filter(Objects::nonNull)
                        .map(file -> new StoryImage(file, story))
                        .collect(Collectors.toList()));
    }

    @Override
    public void saveOne(String fileName, Story story) {
        storyImageRepository.save(new StoryImage(fileName, story));
    }

    @Override
    public void save(List<String> fileNames, Product product) {
        productImageRepository.saveAll(
                Optional.ofNullable(fileNames).orElseGet(Collections::emptyList)
                        .stream().filter(Objects::nonNull)
                        .map(file -> new ProductImage(file, product))
                        .collect(Collectors.toList()));
    }

    @Override
    public void saveOne(String fileName, Product product) {
        productImageRepository.save(new ProductImage(fileName, product));
    }

    @Override
    public void deleteStoryImages(List<String> fileNames) {
        fileNames.forEach(storyImageRepository::deleteByFileName);
    }

    @Override
    public void deleteProductImages(List<String> fileNames) {
        for (String fileName : fileNames) {
            ProductImage productImage = productImageRepository.findByFileName(fileName).orElseThrow(ImageNotFoundException::new);
            Product product = productImage.getProduct();
            productImageRepository.deleteByFileName(fileName);

            // ????????? ?????? ???????????? ????????? ?????? ????????????
            productImageRepository.findFirstByProductOrderByCreatedAtAsc(product)
                    .ifPresent(i -> product.updateImage(i.getFileName()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Story story) {
        StoryImage image = storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(story).orElse(null);
        if (image == null) return null;
        return fileService.changeFileNameToUrl(image.getFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Product product) {
        ProductImage image = productImageRepository.findFirstByProductOrderByCreatedAtAsc(product).orElse(null);
        if (image == null) return null;
        return fileService.changeFileNameToUrl(image.getFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findStoryImageFileNamesAll() {
        return storyImageRepository.findAll().stream()
                .map(Image::getFileName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findProductImageFileNamesByProductId(Long id) {
        return productImageRepository.findByProductId(id).stream()
                .map(Image::getFileName)
                .collect(Collectors.toList());
    }
}
