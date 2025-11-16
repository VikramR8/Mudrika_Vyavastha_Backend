package in.vikramaditya.MudrikaVyavastha.service;

import in.vikramaditya.MudrikaVyavastha.dto.AuthDTO;
import in.vikramaditya.MudrikaVyavastha.dto.ProfileDTO;
import in.vikramaditya.MudrikaVyavastha.entity.ProfileEntity;
import in.vikramaditya.MudrikaVyavastha.repository.ProfileRepository;
import in.vikramaditya.MudrikaVyavastha.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Build activation link from application.properties
        String activationLink = activationURL + "/api/v1.0/activate?token=" + newProfile.getActivationToken();

        // Email subject
        String subject = "Activate Your Mudrika Vyavastha Account";

        // Premium HTML email template (same design as verification)
        String htmlContent =
                "<div style='font-family:Arial, sans-serif; max-width:600px; margin:20px auto;"
                        + "padding:20px; border:1px solid #eaeaea; border-radius:10px;'>"
                        + "<h2 style='color:#0d6efd; text-align:center;'>Activate your account</h2>"
                        + "<p style='font-size:14px;'>Hi " + newProfile.getFullName()
                        + ", please activate your account to start using Mudrika Vyavastha.</p>"
                        + "<p style='text-align:center; margin:20px 0;'>"
                        + "<a href='" + activationLink + "' "
                        + "style='display:inline-block; padding:10px 16px; background:#0d6efd; color:white;"
                        + "text-decoration:none; border-radius:6px;'>Activate Account</a>"
                        + "</p>"
                        + "<p style='font-size:14px;'>Or copy this link:</p>"
                        + "<p style='word-break:break-all; font-size:13px;'>" + activationLink + "</p>"
                        + "<p style='font-size:12px; color:#777; text-align:center;'>This link expires in 24 hours.</p>"
                        + "</div>";

        emailService.sendEmail(newProfile.getEmail(), subject, htmlContent);


        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found: " + auth.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity user;
        if (email == null) {
            user = getCurrentProfile();
        } else {
            user = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found: " + email));
        }

        return toDTO(user);
    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of("token", token, "user", getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
