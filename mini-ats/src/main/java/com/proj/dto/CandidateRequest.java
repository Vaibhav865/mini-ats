package com.proj.dto;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CandidateRequest( @NotBlank(message = "Name is required")
@Size(max = 120, message = "Name must be at most 120 characters")
String name,

@NotBlank(message = "Email is required")
@Email(message = "Email must be a valid address")
@Size(max = 160, message = "Email must be at most 160 characters")
String email,

@Pattern(regexp = "^$|^[+]?[0-9\\s-]{7,15}$", message = "Phone must be 7-15 digits (optional + prefix)")
String phone,

@NotBlank(message = "Role applied is required")
@Size(max = 120, message = "Role must be at most 120 characters")
String roleApplied,

@Size(max = 20, message = "At most 20 skills allowed")
List<@NotBlank(message = "Skill cannot be blank") @Size(max = 40, message = "Each skill must be at most 40 characters") String> skills,

@DecimalMin(value = "0.0", message = "Experience cannot be negative")
@DecimalMax(value = "50.0", message = "Experience must be at most 50 years")
Double experienceYears,

@Size(max = 60, message = "Source must be at most 60 characters")
String source,

@Pattern(regexp = "^$|^https?://\\S+$", message = "Resume URL must start with http:// or https://")
@Size(max = 500, message = "Resume URL must be at most 500 characters")
String resumeUrl,

@Size(max = 2000, message = "Notes must be at most 2000 characters")
String notes
) {

}
