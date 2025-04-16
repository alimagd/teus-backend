package pt.teus.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.teus.backend.entity.user.UserInfo;
import pt.teus.backend.entity.user.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class UserInfoDetails implements UserDetails {

    private final String username;
    private final String password;
    private final UserRole role;

    public UserInfoDetails(UserInfo userInfo) {
        this.username = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.role = userInfo.getUserRole();
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
