package com.example.jobportal.controller;

import com.example.jobportal.entity.Users;
import com.example.jobportal.entity.UsersType;
import com.example.jobportal.services.UsersService;
import com.example.jobportal.services.UsersTypesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    private final UsersTypesService usersTypesService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypesService usersTypesService, UsersService usersService) {
        this.usersTypesService = usersTypesService;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String register(Model model){
        List<UsersType> usersTypes = usersTypesService.getAll();
        model.addAttribute("getAllTypes",usersTypes);
        model.addAttribute("user",new Users());
        return "register";
    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users , Model model){
        Optional<Users> optionalUser = usersService.getUserByEmail(users.getEmail());
        if(optionalUser.isPresent()){
            model.addAttribute("error","Email already Exist , try to login");
            List<UsersType> usersTypes = usersTypesService.getAll();
            model.addAttribute("getAllTypes",usersTypes);
            model.addAttribute("user",new Users());
            return "register";
        }
        usersService.addNew(users);
        return "redirect:/dashboard/";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request , HttpServletResponse response){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication != null ){
           new SecurityContextLogoutHandler().logout(request,response,authentication);
       }
        return "redirect:/";
    }

}

