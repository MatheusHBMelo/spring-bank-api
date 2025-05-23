package br.com.springbank.controller.admin;

import br.com.springbank.controller.admin.dto.UserRequestDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.service.admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/user")
    public ResponseEntity<UsersResponseDto> findUserByUsername(@RequestBody @Valid UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.findUserByUsername(userRequestDto));
    }

    @PatchMapping(path = "/user")
    public ResponseEntity<UsersResponseDto> disableUserByUsername(@RequestBody @Valid UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.disableUser(userRequestDto));
    }
}
