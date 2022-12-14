package com.todayhouse.domain.story.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.*;
import com.todayhouse.domain.story.domain.Story.Category;
import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoryControllerTest extends IntegrationBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    EntityManager em;

    @Autowired
    StoryReplyRepository replyRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider provider;

    String jwt = null;
    String storyUrl = "http://localhost:8080/stories/";
    User user;
    Story s1;
    StoryReply r1;
    StoryReply r2;
    StoryReply r3;

    @BeforeEach
    void setup() {
        Seller seller = Seller.builder().brand("test_brand").companyName("test").build();

        user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .nickname("admin1")
                .seller(seller)
                .build());

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.ADMIN));
        s1 = Story.builder()
                .title("??????")
                .content("??????")
                .category(Category.STORY)
                .user(user)
                .familyType(FamilyType.NUCLEAR)
                .floorSpace(10)
                .resiType(ResiType.APARTMENT)
                .styleType(StyleType.CLASSIC)
                .build();
        s1 = storyRepository.save(s1);
        r1 = StoryReply.builder().story(s1).user(user).content("r1").build();
        r2 = StoryReply.builder().story(s1).user(user).content("r2").build();
        r3 = StoryReply.builder().story(s1).user(user).content("r3").build();
        replyRepository.save(r1);
        replyRepository.save(r2);
        replyRepository.save(r3);
    }

    @Test
    @DisplayName("????????? ??????")
    void createStory() throws Exception {
        String url = "http://localhost:8080/stories/";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "foo.jpg", "image/jpeg", "?????????".getBytes(StandardCharsets.UTF_8));


        StoryCreateRequest storyCreateRequest = StoryCreateRequest.builder().title("??????").content("??????").category(Story.Category.STORY).build();
        MockMultipartFile request = new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsBytes(storyCreateRequest));
        mockMvc.perform(
                multipart(url)
                        .file(multipartFile)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ??????")
    void createReply() throws Exception {
        Story story = storyRepository.findAll().get(0);
        String url = storyUrl + "reply";

        ReplyCreateRequest request = new ReplyCreateRequest("???????????????", story.getId());
        mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ??????")
    void deleteReply() throws Exception {
        StoryReply storyReply = replyRepository.findAll().get(2);
        String url = storyUrl + "reply/" + storyReply.getId();
        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    void findReplies() throws Exception {
        String url = storyUrl + "reply/1";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        System.out.println("contentAsString = " + contentAsString);
    }

    @Test
    @DisplayName("????????? id ??????")
    void findById() throws Exception {
        String url = storyUrl + "1";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType("applicaation/json")
                .header("Authorization", "Bearer " + jwt)).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        System.out.println("contentAsString = " + contentAsString);
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    void searchStory() throws Exception {
        String url = storyUrl;

        FamilyType[] familyTypes = FamilyType.values();
        StyleType[] styleTypes = StyleType.values();
        ResiType[] resiTypes = ResiType.values();
        Optional<User> byId = userRepository.findById(user.getId());
        Story.Category[] categories = Story.Category.values();
        for (Story.Category category : categories) {
            for (ResiType resiType : resiTypes) {
                for (StyleType styleType : styleTypes) {
                    for (FamilyType familyType : familyTypes) {
                        for (int floorSpace = 0; floorSpace < 5; floorSpace++) {
                            Story build = Story.builder()
                                    .styleType(styleType)
                                    .category(category)
                                    .floorSpace(floorSpace)
                                    .resiType(resiType)
                                    .familyType(familyType)
                                    .content("??????")
                                    .title("??????")
                                    .user(byId.orElseThrow())
                                    .build();
                            storyRepository.save(build);
                        }
                    }
                }
            }

        }

        mockMvc.perform(get(url)
                .param("floorSpace", "3")
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
        ).andDo(print());

    }

    @Test
    @DisplayName("????????? ?????? ??? ????????? ?????? ??????")
    void findAllDescWithScrap() throws Exception {
        Story s2 = Story.builder().title("??????2").content("??????").category(Story.Category.STORY).user(user).build();
        s2 = storyRepository.save(s2);
        scrapRepository.save(Scrap.builder().user(user).story(s2).build());

        mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk()); // ????????? ??????

        mockMvc.perform(get("http://localhost:8080/stories?sort=id,DESC")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].isScraped", equalTo(true)))
                .andExpect(jsonPath("$.result.content[0].title", equalTo("??????2")))
                .andExpect(jsonPath("$.result.content[0].views", equalTo(0)))
                .andExpect(jsonPath("$.result.content[1].isScraped", equalTo(false)))
                .andExpect(jsonPath("$.result.content[1].title", equalTo("??????")))
                .andExpect(jsonPath("$.result.content[1].views", equalTo(1)))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ??? view ??????")
    void increaseView() throws Exception {
        mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        StoryGetDetailResponse story = objectMapper.registerModule(new JavaTimeModule()).convertValue(response.getResult(), StoryGetDetailResponse.class);
        assertThat(story.getViews()).isEqualTo(2);
    }

    @Test
    @DisplayName("????????? ????????????")
    void getImage() throws Exception {
        final String fileName = "f5e1526d-8678-4ae5-93ee-2d1bfc972934.png";
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/stories/images/" + fileName)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertNotNull(mvcResult.getResponse());


    }

    @Test
    @DisplayName("????????? ???????????? ??????")
    void findByUser() throws Exception {
        String url = storyUrl + "user";
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", user.getNickname()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.numberOfElements", equalTo(1)));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ??????")
    void getImageInStory() throws Exception {
        createStory();
        List<Story> all = storyRepository.findAll();
        Story story = all.get(all.size() - 1);

        String url = storyUrl + story.getId() + "/images";
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", notNullValue()));
    }

    @Test
    @DisplayName("????????? ????????????")
    void updateStory() throws Exception {
        StoryUpdateRequest request = StoryUpdateRequest.builder()
                .floorSpace(s1.getFloorSpace())
                .familyType(FamilyType.EXTENDED)
                .title(s1.getTitle())
                .category(s1.getCategory())
                .resiType(s1.getResiType())
                .styleType(s1.getStyleType())
                .content(s1.getContent())
                .build();
        assertNotEquals(s1.getFamilyType(), FamilyType.EXTENDED);
        String url = storyUrl + s1.getId();

        mockMvc.perform(patch(url)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print());

        em.flush();
        em.clear();
        Story updateStory = storyRepository.findById(s1.getId()).orElseThrow();
        assertEquals(updateStory.getFamilyType(), FamilyType.EXTENDED);
        assertNotNull(updateStory.getTitle());
    }
}