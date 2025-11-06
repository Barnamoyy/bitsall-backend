package com.bitsall.controller;

import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.CarpoolType;
import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.UserRole;
import com.bitsall.model.enums.VisibilityType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/clubs")
    public ResponseEntity<List<ClubType>> getClubs() {
        return ResponseEntity.ok(Arrays.asList(ClubType.values()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentType>> getDepartments() {
        return ResponseEntity.ok(Arrays.asList(DepartmentType.values()));
    }

    @GetMapping("/user-roles")
    public ResponseEntity<List<UserRole>> getUserRoles() {
        return ResponseEntity.ok(Arrays.asList(UserRole.values()));
    }

    @GetMapping("/carpool-types")
    public ResponseEntity<List<CarpoolType>> getCarpoolTypes() {
        return ResponseEntity.ok(Arrays.asList(CarpoolType.values()));
    }

    @GetMapping("/visibility-types")
    public ResponseEntity<List<VisibilityType>> getVisibilityTypes() {
        return ResponseEntity.ok(Arrays.asList(VisibilityType.values()));
    }
}

