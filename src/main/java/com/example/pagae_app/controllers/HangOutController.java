package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.hangout.HangOutRequestDTO;
import com.example.pagae_app.domain.hangout.HangOutResponseDTO;
import com.example.pagae_app.domain.hangout_member.AddMemberRequestDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.HangOutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hangouts")
public class HangOutController {

    @Autowired
    private HangOutService hangOutService;


    @PostMapping
    public ResponseEntity<HangOutResponseDTO> create(@RequestBody @Valid HangOutRequestDTO data, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        HangOutResponseDTO newHangOut = hangOutService.create(data, authenticatedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newHangOut);
    }

    @GetMapping
    public ResponseEntity<List<HangOutResponseDTO>> findMyHangOuts(Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        List<HangOutResponseDTO> hangOuts = hangOutService.findHangOutsByUserId(authenticatedUser.getId());
        return ResponseEntity.ok(hangOuts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid HangOutRequestDTO data, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();

        hangOutService.update(data, id, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();

        hangOutService.delete(id, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{hangOutId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long hangOutId, @RequestBody @Valid AddMemberRequestDTO memberData, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();

        hangOutService.addMemberToHangOut(hangOutId, memberData.userId(), authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }
}
