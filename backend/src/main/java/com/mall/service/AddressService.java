package com.mall.service;

import com.mall.entity.Address;
import com.mall.entity.User;
import com.mall.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    public Address getAddressById(Long id, User user) {
        return addressRepository.findById(id)
            .filter(address -> address.getUser().getId().equals(user.getId()))
            .orElse(null);
    }

    @Transactional
    public Address createAddress(User user, Address address) {
        if (address.getIsDefault()) {
            unsetDefaultForUser(user);
        }
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, User user, Address updatedAddress) {
        Address existingAddress = getAddressById(id, user);
        if (existingAddress == null) {
            return null;
        }

        if (updatedAddress.getIsDefault() && !existingAddress.getIsDefault()) {
            unsetDefaultForUser(user);
        }

        existingAddress.setReceiverName(updatedAddress.getReceiverName());
        existingAddress.setReceiverPhone(updatedAddress.getReceiverPhone());
        existingAddress.setReceiverAddress(updatedAddress.getReceiverAddress());
        existingAddress.setDetailAddress(updatedAddress.getDetailAddress());
        existingAddress.setProvince(updatedAddress.getProvince());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setDistrict(updatedAddress.getDistrict());
        existingAddress.setIsDefault(updatedAddress.getIsDefault());

        return addressRepository.save(existingAddress);
    }

    @Transactional
    public void deleteAddress(Long id, User user) {
        Address address = getAddressById(id, user);
        if (address != null) {
            addressRepository.delete(address);
        }
    }

    @Transactional
    public Address setDefaultAddress(Long id, User user) {
        Address address = getAddressById(id, user);
        if (address != null) {
            unsetDefaultForUser(user);
            address.setIsDefault(true);
            return addressRepository.save(address);
        }
        return null;
    }

    private void unsetDefaultForUser(User user) {
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        for (Address address : addresses) {
            if (address.getIsDefault()) {
                address.setIsDefault(false);
            }
        }
    }

    public Address getDefaultAddress(User user) {
        return addressRepository.findDefaultByUser(user);
    }
}
