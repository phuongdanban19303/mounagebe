package com.example.model;

import com.example.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserUpdate implements UserDetails {
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserUpdate(User userEntity) {
        this.user = userEntity;

        // Tạo authority từ Role name
        String roleName = userEntity.getRole().getRoleName(); // Ví dụ: "ADMIN"
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash(); // lưu ý: password field là password_hash
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getIsActive());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // tùy theo bạn có cần check expired hay không
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // có thể kiểm tra thêm trạng thái khóa nếu có
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public User getUser() {
        return user;
    }
    // Implement other methods from UserDetails as needed

}
