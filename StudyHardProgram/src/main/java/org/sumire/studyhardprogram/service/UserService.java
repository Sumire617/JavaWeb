// G:\Sumire\JavaWeb\StudyHardProgram\src\main\java\org\sumire\studyhardprogram\service\UserService.java
package org.sumire.studyhardprogram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumire.studyhardprogram.model.User;
import org.sumire.studyhardprogram.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    //获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // 登录
    public boolean login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.isPresent() && userOptional.get().getPassword().equals(password);
    }
    // 根据用户名查找用户
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    // 根据id查找用户
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }
    // 注册用户
    public User createUser(User user) {
        // 不要手动设置 userId，让 Hibernate 自动生成
        return userRepository.save(user);
    }
    // 修改用户信息
    public User updateUser(String id, User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // 修改用户名
            user.setUsername(userDetails.getUsername());
            // 修改密码
            user.setPassword(userDetails.getPassword());
            // 修改邮箱
            user.setEmail(userDetails.getEmail());
            // 修改手机号
            user.setPhoneNumber(userDetails.getPhoneNumber());
            // 修改用户状态
            user.setUserStatus(userDetails.getUserStatus());
            // 修改更新时间
            user.setUpdatedAt(userDetails.getUpdatedAt());
            return userRepository.save(user);
        }
        return null;
    }
    // 删除用户
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}