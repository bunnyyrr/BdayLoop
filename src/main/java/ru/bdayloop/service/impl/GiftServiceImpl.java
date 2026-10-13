package ru.bdayloop.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.GiftRepository;
import ru.bdayloop.repository.i.UserRepository;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.service.validation.Validation;

import java.util.List;

@Service
@Transactional
public class GiftServiceImpl implements GiftService {
    private final GiftRepository giftRepository;
    private final UserRepository userRepository;

    public GiftServiceImpl(GiftRepository giftRepository, UserRepository userRepository){
        this.giftRepository= giftRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Gift create(int ownerId, String title){
        Validation.requireText(title, "Название подарка не может быть пустым");
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        return giftRepository.save(new Gift(owner, title));
    }

    @Override
    public void delete(int id, int requesterId) {
        Gift gift = giftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подарок с id " + id + " не найден"));
        if (!gift.getOwner().getId().equals(requesterId)) {
            throw new ForbiddenException("Можно удалить только свой подарок");
        }
        giftRepository.delete(gift);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Gift> findByUserId(int userId) {
        return giftRepository.findByOwnerId(userId);
    }
}
