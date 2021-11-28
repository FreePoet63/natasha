package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserSubscription;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    @Autowired
    public ProfileService(
            UserRepository userRepository, UserSubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;

    }

    public User changeSubscription(User channel, User subscriber) {
        List<UserSubscription> subcriptions = channel.getSubscribers()
                .stream()
                .filter(subscription ->
                        subscription.getSubscriber().equals(subscriber)
                )
                .collect(Collectors.toList());

        if (subcriptions.isEmpty()) {
            UserSubscription subscription = new UserSubscription(channel, subscriber);
            channel.getSubscribers().add(subscription);
        } else {
            channel.getSubscribers().removeAll(subcriptions);
        }

        return userRepository.save(channel);
    }

    public List<UserSubscription> getSubscribers(User channel) {
        return subscriptionRepository.findByChannel(channel);
    }

    public UserSubscription changeSubscriptionStatus(User channel, User subscriber) {
        UserSubscription subscription = subscriptionRepository.findByChannelAndSubscriber(channel, subscriber);
        subscription.setActive(!subscription.isActive());

        return subscriptionRepository.save(subscription);
    }
}
