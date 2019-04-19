package com.azuki.user.mapper;

import com.azuki.user.pojo.UserInfo;
import com.azuki.user.vo.UserInfoVo;




public interface UserInfoMapper {
    //登录
    UserInfo selectUserInfoByUserNameAndPassword(UserInfoVo userInfoVo);
    //注册
    Integer insertIntoUserInfo(UserInfoVo userInfoVo);
    //修改个人信息
    Integer updateUserInfo(UserInfoVo userInfoVo);
    //记住密码：通过userId查找用户名和密码
    UserInfo selectUsernameAndPasswordByUserId(Integer userId);
    //通过用户名判断用户是否已被注册
    Integer selectUserInfoByUserName(UserInfoVo userInfoVo);


}
