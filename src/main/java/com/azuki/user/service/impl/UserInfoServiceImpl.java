package com.azuki.user.service.impl;

import com.azuki.user.mapper.UserInfoMapper;
import com.azuki.user.pojo.UserInfo;
import com.azuki.user.service.UserInfoService;
import com.azuki.user.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 登录
     * @param userInfoVo
     * @return
     */
    public UserInfo selectUserInfoByUserNameAndPassword(UserInfoVo userInfoVo) {
        //获取userInfo集合中的userInfo对象（只有一个）
        return userInfoMapper.selectUserInfoByUserNameAndPassword(userInfoVo);
    }

    /**
     * 注册
     * @param userInfoVo
     * @return
     */
    public Integer insertIntoUserInfo(UserInfoVo userInfoVo) {
        return userInfoMapper.insertIntoUserInfo(userInfoVo);
    }

    /**
     * 修改个人信息
     * @param userInfoVo
     * @return
     */
    public Integer updateUserInfo(UserInfoVo userInfoVo) {
        return userInfoMapper.updateUserInfo(userInfoVo);
    }

    /**
     *记住密码：通过userId查找用户名和密码
     * @param userId
     * @return
     */
    public UserInfo selectUsernameAndPasswordByUserId(Integer userId) {
        return userInfoMapper.selectUsernameAndPasswordByUserId(userId);
    }

    public Integer selectUserInfoByUserName(UserInfoVo userInfoVo) {
        return userInfoMapper.selectUserInfoByUserName(userInfoVo);
    }
}
