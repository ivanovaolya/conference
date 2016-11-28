package ua.rd.cm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.rd.cm.domain.ContactType;
import ua.rd.cm.repository.ContactTypeRepository;
import ua.rd.cm.repository.specification.contacttype.ContactTypeById;
import ua.rd.cm.repository.specification.contacttype.ContactTypeByName;

import java.util.List;

/**
 * @author Olha_Melnyk
 */
@Service
public class SimpleContactTypeService implements ContactTypeService {

    private ContactTypeRepository contactTypeRepository;

    @Autowired
    public SimpleContactTypeService(ContactTypeRepository contactTypeRepository) {
        this.contactTypeRepository = contactTypeRepository;
    }

    @Override
    public ContactType find(Long id) {
        return contactTypeRepository.findBySpecification(new ContactTypeById(id)).get(0);
    }


    @Override
    public List<ContactType> findByName(String name) {
        return contactTypeRepository.findBySpecification(new ContactTypeByName(name));
    }

    @Override
    @Transactional
    public void save(ContactType contactType) {
        contactTypeRepository.saveContactType(contactType);
    }

    @Override
    @Transactional
    public void update(ContactType contactType) {
        contactTypeRepository.updateContactType(contactType);
    }

    @Override
    public List<ContactType> findAll() {
        return contactTypeRepository.findAll();
    }

}
