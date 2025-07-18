package com.example.jobportal.services;

import com.example.jobportal.entity.UsersType;
import com.example.jobportal.repository.UsersTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypesService {
    private final UsersTypeRepository usersTypeRepository ;

    @Autowired
    public UsersTypesService(UsersTypeRepository usersTypeRepository) {
        this.usersTypeRepository = usersTypeRepository;
    }

    public List<UsersType> getAll(){
        return  usersTypeRepository.findAll();
    }
}
