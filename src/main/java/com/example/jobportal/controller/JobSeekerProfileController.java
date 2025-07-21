package com.example.jobportal.controller;

import com.example.jobportal.entity.JobSeekerProfile;
import com.example.jobportal.entity.Skills;
import com.example.jobportal.entity.Users;
import com.example.jobportal.repository.UsersRepository;
import com.example.jobportal.services.JobPostActivityService;
import com.example.jobportal.services.JobSeekerProfileService;
import com.example.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/jobs-seeker-profile")
public class JobSeekerProfileController {

    private JobSeekerProfileService jobSeekerProfileService;
    private UsersRepository usersRepository;

    @Autowired
    public JobSeekerProfileController(
            JobSeekerProfileService jobSeekerProfileService,
            UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public String jobSeekerProfile(Model model){
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = usersRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(users.getUserId());
            if(seekerProfile.isPresent()){
                jobSeekerProfile=seekerProfile.get();
                if(jobSeekerProfile.getSkills().isEmpty()){
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills",skills);
            model.addAttribute("profile",jobSeekerProfile);
        }
        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(
             JobSeekerProfile jobSeekerProfile,@RequestParam("image")MultipartFile image
           ,@RequestParam("pdf")MultipartFile pdf , Model model
                         ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = usersRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            jobSeekerProfile.setUserAccountId(users.getUserId());
        }
        List<Skills> skillsList = new ArrayList<>();
        model.addAttribute("skills",skillsList);
        model.addAttribute("profile",jobSeekerProfile);
        for(Skills skills : jobSeekerProfile.getSkills()){
            skills.setJobSeekerProfile(jobSeekerProfile);
        }
        String imageName ="";
        String resumeName ="";

        if(Objects.equals(image.getOriginalFilename(),"")){
            imageName= StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }
        if(Objects.equals(pdf.getOriginalFilename(),"")){
            resumeName= StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }
        JobSeekerProfile jobSeekerProfile1 =  jobSeekerProfileService.addNew(jobSeekerProfile);

        try{
            String uploadDir = "photos/candidate"+jobSeekerProfile1.getUserAccountId() ;
            if(!Objects.equals(image.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir ,imageName,image);
            }
            if(!Objects.equals(pdf.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir ,resumeName,pdf);
            }
        } catch (IOException e) {
            throw new  RuntimeException(e);
        }
        return "redirect:/dashboard/";
    }

}
