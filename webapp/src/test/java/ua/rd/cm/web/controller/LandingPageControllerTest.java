package ua.rd.cm.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.rd.cm.config.TestSecurityConfig;
import ua.rd.cm.config.WebMvcConfig;
import ua.rd.cm.config.WebTestConfig;
import ua.rd.cm.domain.Conference;
import ua.rd.cm.domain.Talk;
import ua.rd.cm.domain.TalkStatus;
import ua.rd.cm.dto.CreateTopicDto;
import ua.rd.cm.dto.CreateTypeDto;
import ua.rd.cm.dto.TopicDto;
import ua.rd.cm.dto.TypeDto;
import ua.rd.cm.services.ConferenceService;
import ua.rd.cm.services.TopicService;
import ua.rd.cm.services.TypeService;

import javax.servlet.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebTestConfig.class, WebMvcConfig.class, TestSecurityConfig.class})
@WebAppConfiguration
public class LandingPageControllerTest extends TestUtil {
    public static final String API_CONFERENCE = "/api/conference";
    public static final String API_NEW_CONFERENCE = "/api/conference/new";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private TypeService typeService;
    @Autowired
    private TopicService topicService;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private LandingPageController landingPageController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getUpcomingConferencesUnauthorized() throws Exception {
        mockMvc.perform(prepareGetRequest(API_CONFERENCE + "/upcoming")).
                andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ORGANISER_EMAIL, roles = ADMIN_ROLE)
    public void getUpcomingConferencesWithNoTalks() throws Exception {
        List<Conference> conferences = new ArrayList<>();
        conferences.add(new Conference());
        when(conferenceService.findUpcoming()).thenReturn(conferences);
        mockMvc.perform(prepareGetRequest(API_CONFERENCE + "/upcoming")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$[0].new", is(0))).
                andExpect(jsonPath("$[0].approved", is(0))).
                andExpect(jsonPath("$[0].in-progress", is(0))).
                andExpect(jsonPath("$[0].rejected", is(0)));
    }

    @Test
    @WithMockUser(username = ORGANISER_EMAIL, roles = ORGANISER_ROLE)
    public void getUpcomingConferencesWithTalks() throws Exception {
        List<Conference> conferences = new ArrayList<>();
        Conference conference = createConference();
        conference.setCallForPaperEndDate(LocalDate.MAX);
        conferences.add(conference);
        when(conferenceService.findUpcoming()).thenReturn(conferences);
        mockMvc.perform(prepareGetRequest(API_CONFERENCE + "/upcoming")).
                andExpect(status().isOk());
    }

    @Test
    public void getPastConferencesTestUnauthorized() throws Exception {
        mockMvc.perform(prepareGetRequest(API_CONFERENCE + "/past")).
                andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ORGANISER_EMAIL, roles = ORGANISER_ROLE)
    public void getPastConferences() throws Exception {
        List<Conference> conferences = new ArrayList<>();
        conferences.add(createConference());
        when(conferenceService.findPast()).thenReturn(conferences);
        mockMvc.perform(prepareGetRequest(API_CONFERENCE + "/past")).
                andExpect(status().isOk());
    }

    @Test
    public void getTypesShouldNotWorkForUnauthorized() throws Exception {
        List<TypeDto> types = new ArrayList<>();
        when(typeService.findAll()).thenReturn(types);
        mockMvc.perform(prepareGetRequest("/api/type")).
                andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ORGANISER_ROLE)
    public void getTypesShouldNotWorkForOrganiser() throws Exception {
        List<TypeDto> types = new ArrayList<>();
        when(typeService.findAll()).thenReturn(types);
        mockMvc.perform(prepareGetRequest("/api/type")).
                andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = SPEAKER_ROLE)
    public void getTypesShouldNotWorkForSpeaker() throws Exception {
        List<TypeDto> types = new ArrayList<>();
        when(typeService.findAll()).thenReturn(types);
        mockMvc.perform(prepareGetRequest("/api/type")).
                andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getTypesShouldWorkForAdmin() throws Exception {
        List<TypeDto> types = new ArrayList<>();
        when(typeService.findAll()).thenReturn(types);
        mockMvc.perform(prepareGetRequest("/api/type")).
                andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getTypesShouldHaveRightValues() throws Exception {
        TypeDto typeDto = new TypeDto();
        typeDto.setId(1L);
        typeDto.setName("SomeName");
        List<TypeDto> types = new ArrayList<TypeDto>() {{
            add(typeDto);
        }};

        when(typeService.findAll()).thenReturn(types);
        mockMvc.perform(prepareGetRequest("/api/type")).
                andExpect(status().isOk()).
                andExpect(jsonPath("[0].id", is(typeDto.getId().intValue()))).
                andExpect(jsonPath("[0].name", is(typeDto.getName())));
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void createNewTypeShouldWorkForAdmin() throws Exception {
        CreateTypeDto dto = new CreateTypeDto("schweine");
        when(typeService.save(dto)).thenReturn(1L);
        mockMvc.perform(post("/api/type/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)));
    }

    @Test
    public void createNewTypeShouldNotWorkForUnauthorized() throws Exception {
        CreateTypeDto dto = new CreateTypeDto("schweine");
        mockMvc.perform(post("/api/type/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ORGANISER_ROLE)
    public void createNewTypeShouldNotWorkForOrganiser() throws Exception {
        CreateTypeDto dto = new CreateTypeDto("schweine");
        mockMvc.perform(post("/api/type/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = SPEAKER_ROLE)
    public void createNewTypeShouldNotWorkForSpeaker() throws Exception {
        CreateTypeDto dto = new CreateTypeDto("schweine");
        mockMvc.perform(post("/api/type/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void getTopicsShouldNotWorkForUnauthorized() throws Exception {
        mockMvc.perform(get("/api/topic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = SPEAKER_ROLE)
    public void getTopicsShouldNotWorkForSpeaker() throws Exception {
        mockMvc.perform(get("/api/topic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ORGANISER_ROLE)
    public void getTopicsShouldNotWorkForOrganiser() throws Exception {
        mockMvc.perform(get("/api/topic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getTopicsShouldWorkFroAdmin() throws Exception {
        TopicDto topicDto = new TopicDto();
        topicDto.setId(1L);
        topicDto.setName("SomeName");
        List<TopicDto> topics = new ArrayList<TopicDto>() {{
            add(topicDto);
        }};
        when(topicService.findAll()).thenReturn(topics);
        mockMvc.perform(get("/api/topic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(topicDto.getId().intValue())))
                .andExpect(jsonPath("[0].name", is(topicDto.getName())));
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void createNewTopicShouldWorkForAdmin() throws Exception {
        CreateTopicDto dto = new CreateTopicDto("schweine");
        when(topicService.save(dto)).thenReturn(1L);
        mockMvc.perform(post("/api/topic/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)));
    }

    @Test
    public void createNewTopicShouldNotWorkForUnauthorized() throws Exception {
        CreateTopicDto dto = new CreateTopicDto("schweine");
        mockMvc.perform(post("/api/topic/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ORGANISER_ROLE)
    public void createNewTopicShouldNotWorkForOrganiser() throws Exception {
        CreateTopicDto dto = new CreateTopicDto("schweine");
        mockMvc.perform(post("/api/topic/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = SPEAKER_ROLE)
    public void createNewTopicShouldNotWorkForSpeaker() throws Exception {
        CreateTopicDto dto = new CreateTopicDto("schweine");
        mockMvc.perform(post("/api/topic/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(dto))
        ).andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequestBuilder prepareGetRequest(String uri) throws Exception {
        return MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    private Conference createConference() {
        Conference conference = new Conference();
        conference.setTitle("JUG UA");
        conference.setLocation("Location");
        conference.setDescription("Description");
        conference.setCallForPaperEndDate(LocalDate.MIN);
        List<Talk> talks = new ArrayList<>();
        Talk talk1 = new Talk();
        talk1.setStatus(TalkStatus.APPROVED);
        talks.add(talk1);
        talk1 = new Talk();
        talk1.setStatus(TalkStatus.APPROVED);
        talks.add(talk1);
        talk1 = new Talk();
        talk1.setStatus(TalkStatus.IN_PROGRESS);
        talks.add(talk1);
        conference.setTalks(talks);
        return conference;
    }
}
