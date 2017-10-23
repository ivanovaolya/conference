package ua.rd.cm.services.businesslogic.impl;

import static java.util.Optional.ofNullable;
import static ua.rd.cm.services.exception.ResourceNotFoundException.USER_INFO_NOT_FOUND;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import ua.rd.cm.domain.Contact;
import ua.rd.cm.domain.User;
import ua.rd.cm.domain.UserInfo;
import ua.rd.cm.dto.UserDto;
import ua.rd.cm.repository.ContactTypeRepository;
import ua.rd.cm.repository.UserInfoRepository;
import ua.rd.cm.repository.UserRepository;
import ua.rd.cm.services.businesslogic.UserInfoService;
import ua.rd.cm.services.exception.ResourceNotFoundException;

@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;
    private UserRepository userRepository;
    private ModelMapper mapper;
    private ContactTypeRepository contactTypeRepository;

    @Override
    public UserInfo find(Long id) {
        UserInfo usersInfo = userInfoRepository.findById(id);
        if (usersInfo == null) {
            throw new ResourceNotFoundException(USER_INFO_NOT_FOUND);
        }
        return usersInfo;
    }

    @Override
    @Transactional
    public void save(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    @Override
    @Transactional
    public void update(String email, UserDto userDto) {
        User user = userRepository.findByEmail(email);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        UserInfo entity = userInfoDtoToEntity(userDto);
        entity.setId(user.getUserInfo().getId());

        userRepository.save(user);
        userInfoRepository.save(entity);
    }

    private UserInfo userInfoDtoToEntity(UserDto dto) {
        UserInfo userInfo = mapper.map(dto, UserInfo.class);

        ofNullable(createContact(dto.getLinkedIn(), "LinkedIn")).ifPresent(userInfo::addContact);
        ofNullable(createContact(dto.getTwitter(), "Twitter")).ifPresent(userInfo::addContact);
        ofNullable(createContact(dto.getFacebook(), "FaceBook")).ifPresent(userInfo::addContact);
        ofNullable(createContact(dto.getBlog(), "Blog")).ifPresent(userInfo::addContact);

        return userInfo;
    }
    private Contact createContact(String url, String contactType) {
        return (url != null)
                ? new Contact(url, contactTypeRepository.findFirstByName(contactType))
                : null;
    }
}
