package com.example.jobportal.controller;

import com.example.jobportal.entity.RecruiterProfile;
import com.example.jobportal.entity.Users;
import com.example.jobportal.repository.UsersRepository;
import com.example.jobportal.services.RecruiterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public RecruiterProfileController(UsersRepository usersRepository,RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if(authentication instanceof AnonymousAuthenticationToken){
           String currentUserName = authentication.getName();
           Users users = usersRepository.findByEmail(currentUserName).orElseThrow(
                   ()-> new UsernameNotFoundException("User Not Found")
                   );
           Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());
           if (!recruiterProfile.isEmpty()){
               model.addAttribute("profile",recruiterProfile.get());
           }
       }
       return "recruiter_profile";
    }
}

