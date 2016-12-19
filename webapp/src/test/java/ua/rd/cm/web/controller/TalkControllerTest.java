package ua.rd.cm.web.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.rd.cm.config.WebMvcConfig;
import ua.rd.cm.config.WebTestConfig;
import ua.rd.cm.domain.*;
import ua.rd.cm.services.TalkService;
import ua.rd.cm.services.UserInfoService;
import ua.rd.cm.services.UserService;
import ua.rd.cm.web.controller.dto.TalkDto;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebTestConfig.class, WebMvcConfig.class, })
@WebAppConfiguration
public class TalkControllerTest {
    public static final String API_TALK = "/api/talk";
    private MockMvc mockMvc;
    private TalkDto correctTalkDto;

    @Autowired
    private TalkService talkService;

    @Autowired
    private TalkController talkController;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

    private UserInfo userInfo;
    private Role role;
    private User user;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(talkController).build();
        correctTalkDto = setupCorrectTalkDto();
        userInfo = createUserInfo();
        role = createSpeakerRole();
        user = createUser(role, userInfo);
        mockServices(user, userInfo);
    }

    private void mockServices(User user, UserInfo userInfo) {
        when(userService.getByEmail(user.getEmail())).thenReturn(user);
        when(userInfoService.find(anyLong())).thenReturn(userInfo);
    }

    @Test
    public void correctSubmitNewTalkTest() throws Exception{
        Principal correctPrincipal = () -> user.getEmail();
        mockMvc.perform(post(API_TALK)
                .principal(correctPrincipal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(correctTalkDto))
        ).andExpect(status().isOk());
    }

    @Test
    public void emptyCompanyMyInfoSubmitNewTalkTest() {
        userInfo.setCompany("");
        Principal correctPrincipal = () -> user.getEmail();
        checkForForbidden(API_TALK, correctPrincipal);
    }

    @Test
    public void emptyJobMyInfoSubmitNewTalkTest() {
        userInfo.setJobTitle("");
        Principal correctPrincipal = () -> user.getEmail();
        checkForForbidden(API_TALK, correctPrincipal);
    }

    @Test
    public void emptyBioMyInfoSubmitNewTalkTest() {
        userInfo.setShortBio("");
        Principal correctPrincipal = () -> user.getEmail();
        checkForForbidden(API_TALK, correctPrincipal);
    }

    @Test
    public void nullPrincipleSubmitNewTalkTest() throws Exception {
        UserInfo userInfo = createUserInfo();
        Role role = createSpeakerRole();
        User user = createUser(role, userInfo);

        mockServices(user, userInfo);

        mockMvc.perform(post(API_TALK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(correctTalkDto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void nullTitleSubmitNewTalkTest() {
        correctTalkDto.setTitle(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyTitleSubmitNewTalkTest() {
        correctTalkDto.setTitle("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongTitleSubmitNewTalkTest() {
        correctTalkDto.setTitle(createStringWithLength(251));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void nullDescriptionSubmitNewTalkTest() {
        correctTalkDto.setDescription(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyDescriptionSubmitNewTalkTest() {
        correctTalkDto.setDescription("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongDescriptionSubmitNewTalkTest() {
        correctTalkDto.setDescription(createStringWithLength(3001));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void nullTopicSubmitNewTalkTest() {
        correctTalkDto.setTopicName(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyTopicSubmitNewTalkTest() {
        correctTalkDto.setTopicName("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongTopicSubmitNewTalkTest() {
        correctTalkDto.setTopicName(createStringWithLength(256));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void nullTypeSubmitNewTalkTest() {
        correctTalkDto.setTypeName(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyTypeSubmitNewTalkTest() {
        correctTalkDto.setTypeName("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongTypeSubmitNewTalkTest() {
        correctTalkDto.setTypeName(createStringWithLength(256));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void nullLanguageSubmitNewTalkTest() {
        correctTalkDto.setLanguageName(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyLanguageSubmitNewTalkTest() {
        correctTalkDto.setLanguageName("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongLanguageSubmitNewTalkTest() {
        correctTalkDto.setLanguageName(createStringWithLength(256));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void nullLevelSubmitNewTalkTest() {
        correctTalkDto.setLevelName(null);
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void emptyLevelSubmitNewTalkTest() {
        correctTalkDto.setLevelName("");
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongLevelSubmitNewTalkTest() {
        correctTalkDto.setLevelName(createStringWithLength(256));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void tooLongAddInfoSubmitNewTalkTest() {
        correctTalkDto.setAdditionalInfo(createStringWithLength(1501));
        checkForBadRequest(API_TALK, RequestMethod.POST);
    }

    @Test
    public void correctGetMyTalksTest() throws Exception {
        Principal correctPrincipal = () -> user.getEmail();
        Talk talk = createTalk();
        List talks = new ArrayList() {{
            add(talk);
        }};
        when(talkService.findByUserId(anyLong())).thenReturn(talks);
        mockMvc.perform(get(API_TALK)
                .principal(correctPrincipal)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(talk.getTitle())))
                .andExpect(jsonPath("$[0].description", is(talk.getDescription())))
                .andExpect(jsonPath("$[0].topic", is(talk.getTopic().getName())))
                .andExpect(jsonPath("$[0].type", is(talk.getType().getName())))
                .andExpect(jsonPath("$[0].lang", is(talk.getLanguage().getName())))
                .andExpect(jsonPath("$[0].level", is(talk.getLevel().getName())))
                .andExpect(jsonPath("$[0].addon", is(talk.getAdditionalInfo())))
                .andExpect(jsonPath("$[0].status", is(talk.getStatus().getName())));
    }
    @Test
    public void incorrectGetMyTalksTest() throws Exception {
        Talk talk = createTalk();
        List talks = new ArrayList() {{
            add(talk);
        }};
        when(talkService.findByUserId(anyLong())).thenReturn(talks);
        mockMvc.perform(get(API_TALK)
        ).andExpect(status().isUnauthorized());
    }

    private Talk createTalk() {
        Status status = new Status(1L, "New");
        Topic topic = new Topic(1L, "Topic");
        Type type = new Type(1L, "Type");
        Language language = new Language(1L, "Language");
        Level level = new Level(1L, "Level");
        return new Talk(1L, user, status, topic, type, language, level, LocalDateTime.now(), "Title", "Descr", "Add Info");
    }

    private TalkDto setupCorrectTalkDto() {
        TalkDto correctTalkDto = new TalkDto();
        correctTalkDto.setDescription("Description");
        correctTalkDto.setTitle("Title");
        correctTalkDto.setLanguageName("English");
        correctTalkDto.setLevelName("Beginner");
        correctTalkDto.setStatusName("New");
        correctTalkDto.setTypeName("Regular Talk");
        correctTalkDto.setTopicName("JVM Languages and new programming paradigms");
        correctTalkDto.setDate(LocalDateTime.now().toString());
        correctTalkDto.setAdditionalInfo("Info");
        return correctTalkDto;
    }

    private void checkForBadRequest(String uri, RequestMethod method) {
        try {
            if (method == RequestMethod.GET) {
                mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(correctTalkDto))
                ).andExpect(status().isBadRequest());
            } else if (method == RequestMethod.POST) {
                mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(correctTalkDto))
                ).andExpect(status().isBadRequest());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForForbidden(String uri, Principal principal) {
        try {
            mockMvc.perform(post(uri)
                    .principal(principal)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(convertObjectToJsonBytes(correctTalkDto))
            ).andExpect(status().isForbidden());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] convertObjectToJsonBytes(Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    private String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append("a");
        }
        return builder.toString();
    }

    private User createUser(Role role , UserInfo info){
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return new User(1L,"Olya","Ivanova","ivanova@gmail.com", "123456", null, User.UserStatus.CONFIRMED, info, roles);
    }

    private Role createSpeakerRole(){
        return new Role(1L, "SPEAKER");
    }

    private UserInfo createUserInfo(){
       return new UserInfo(1L, "bio", "job", "pastConference", "EPAM", null, "addInfo");
    }
}