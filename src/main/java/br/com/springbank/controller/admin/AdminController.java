package br.com.springbank.controller.admin;

import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.service.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<UsersResponseDto>> findAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.findAllUsers());
    }
}
