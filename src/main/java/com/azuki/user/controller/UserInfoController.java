package com.azuki.user.controller;

import com.azuki.user.pojo.UserInfo;
import com.azuki.user.service.impl.UserInfoServiceImpl;
import com.azuki.user.utils.Md5Utils;
import com.azuki.user.vo.UserInfoVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Constants;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import com.google.code.kaptcha.Producer;
import java.util.List;

import static com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlFormatName.JSON;

@RestController
public class UserInfoController {
    @Autowired
    UserInfoServiceImpl userInfoService;

    @Autowired
    private Producer captchaProducer = null;




    /**
     * 登录
     * @param userInfoVo
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkLogin")
    public Object checkLogin(@RequestBody(required = false) UserInfoVo userInfoVo, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        //定义flag，有0，1，2三种状态，0：验证码错误，2：验证码正确，用户名或密码错误，1：验证码，用户名和密码均正确
        int flag;
        //从session中获取验证码
        String kaptchaCode = (String) httpSession.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        //System.out.println("kaptchaCode-----------" + kaptchaCode);
        //获取输入的验证码
        String code = userInfoVo.getCode();
        //获取cookie集合
        Cookie[] cookiesCheck = request.getCookies();
        //遍历cookie，判断cookie中有没有name=“ckUserId”的cookie，
        // 如果有，根据cookie的userId从数据库查询用户名和密码，将用户名与输入的用户名进行比较，
        // 如果一致，则证明是该用户是记录在cookie中的， 密码不需要再次加密，直接与数据库中的密码对比即可，
        // 如果不一致，证明是换了一个用户登录，要将密码进行加密之后再与数据库中进行比较
        for (int i = 0; i < cookiesCheck.length; i++) {
            if ("ckUserId".equals(cookiesCheck[i].getName())) {
                UserInfo userInfo = (UserInfo) this.selectUsernameAndPasswordByUserId(Integer.parseInt(cookiesCheck[i].getValue()));
                if(userInfo.getUserName().equals(userInfoVo.getUserName())){
                    break;
                }
            }
            //如果没有name=“ckUserId”的cookie，则将密码进行加密
            if (i == cookiesCheck.length-1) {
                userInfoVo.setPassword(Md5Utils.encodePassword(userInfoVo.getPassword()));
                System.out.println(11111);
            }
        }
        System.out.println("userInfoVo-------" + userInfoVo);
        //通过用户名和密码查询数据库中是否有该用户的信息
        UserInfo userInfo = userInfoService.selectUserInfoByUserNameAndPassword(userInfoVo);
        System.out.println("userInfo----------" + userInfo);
        //将输入的验证码与session中正确的验证码进行比较，如果不匹配，直接返回0
        if (!kaptchaCode.equals(code)) {
            flag = 0;
        } else {
            //如果验证码匹配，则将判断用户名和密码是否正确，如果正确，返回1，不正确，则返回2
            if (userInfo != null) {
                flag = 1;
                httpSession.setAttribute("userInfo", userInfo);
                System.out.println("session--------------" + httpSession.getAttribute("userInfo"));
                //如果选择记住密码,则创建cookie,并将账号密码注入cookie
                if (userInfoVo.getRememberStatus() == 1) {
                    //创建cookie对象
                    Cookie ckUserId = new Cookie("ckUserId", userInfo.getUserId() + "");
                    //设置Cookie有效时间,单位为妙
                    ckUserId.setMaxAge(60 * 60 * 24);
                    //设置Cookie的有效范围,/为全部路径
                    ckUserId.setPath("/");
                    response.addCookie(ckUserId);
                } else {
                    //如果没有选中记住密码,则将已记住密码的cookie失效.即有效时间设为0
                    Cookie[] cookies = request.getCookies();
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("ckUserId")) {
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                        }
                    }
                }
            } else {
                flag = 2;
            }
        }
        return flag;
    }

    /**
     * 获取session中的userInfo
     * @param httpSession
     * @return
     * @throws JsonProcessingException
     */
    @ResponseBody
    @RequestMapping(value = "getSession",method = RequestMethod.POST,produces="text/html;charset=UTF-8")
    public Object getSession(HttpSession httpSession) throws JsonProcessingException {
        //定义一个json字符串
        String jsonUserInfo ;
        //从session中拿到userInfo
        UserInfo userInfo = (UserInfo) httpSession.getAttribute("userInfo");
        //将对象转为json字符串形式
        ObjectMapper objectMapper = new ObjectMapper();
        jsonUserInfo = objectMapper.writeValueAsString(userInfo);
        //System.out.println(jsonUserInfo);
        //System.out.println(userInfo);
        return jsonUserInfo;
    }

    /**
     * 注册
     * @param userInfoVo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "UserRegister",method = RequestMethod.POST)
    public Object UserRegister(@RequestBody UserInfoVo userInfoVo){
        //System.out.println(userInfoVo);
        Boolean flag=false;
        userInfoVo.setPassword(Md5Utils.encodePassword(userInfoVo.getPassword()));
        //System.out.println(userInfoVo);
        Integer rSet = userInfoService.insertIntoUserInfo(userInfoVo);
        if(rSet>0){
            flag=true;
        }
        return flag;
    }

    /**
     * 修改个人信息
     * @param userInfoVo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
    public Object updateUserInfo(@RequestBody UserInfoVo userInfoVo){
        //System.out.println(userInfoVo);
        Boolean flag=false;
        userInfoVo.setPassword(Md5Utils.encodePassword(userInfoVo.getPassword()));
        //System.out.println(userInfoVo);
        Integer rSet = userInfoService.updateUserInfo(userInfoVo);
        if(rSet>0){
            flag=true;
        }
        return flag;
    }


    /**
     * 验证码插件
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/captchaController/image")
    public String getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String code = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        //System.out.println("******************验证码是: " + code);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

    /**
     * 记住密码：通过userId查找用户名和密码
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectUsernameAndPasswordByUserId",method = RequestMethod.POST)
    public Object selectUsernameAndPasswordByUserId(Integer userId){
        //System.out.println(userInfoVo);
        UserInfo userInfo = userInfoService.selectUsernameAndPasswordByUserId(userId);
        if (userInfo!=null){
            return userInfo;
        }
        return null;
    }

    /**
     * 注册时，根据用户名判断该用户是否已经被注册
     * @param userName
     * @return
     */

    @RequestMapping(value = "selectUserInfoByUserName",method = RequestMethod.POST)
    public Object selectUserInfoByUserName(@Param("userName") String userName){
        //System.out.println("11111111111111");
        //System.out.println("userName-------------"+userName);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserName(userName);
        Integer rSet = userInfoService.selectUserInfoByUserName(userInfoVo);
        //System.out.println(rSet);
        if (rSet==0){
            return true;
        }
        return false;
    }

    /**
     * 修改用户时，用户名可以不修改，判断用户名和session中保存的userinfo是否一致，如果一致，则返回true，没有错误信息提示
     * @param userName
     * @param session
     * @return
     */
    @RequestMapping(value = "selectUserInfoByUserNameUpdate",method = RequestMethod.POST)
    public Object selectUserInfoByUserNameUpdate(@Param("userName") String userName,HttpSession session){
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
       // System.out.println("userInfo------------"+userInfo);
        //如果前端传过来的userName和session中存的一样
        if(userInfo!=null){
            if(userName.equals(userInfo.getUserName())){
                return true;
            }
        }
        System.out.println("userName-------------"+userName);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserName(userName);
        Integer rSet = userInfoService.selectUserInfoByUserName(userInfoVo);
       // System.out.println(rSet);
        if (rSet==0){
            return true;
        }
        return false;
    }

    /**
     * 修改用户密码：判断用户
     * @param oldPassword
     * @param session
     * @return
     */
    @RequestMapping(value = "checkPassword",method = RequestMethod.POST)
    public Object checkPassword(@Param("oldPassword") String oldPassword,HttpSession session){
        //System.out.println("11111111111111");
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String encodeOldPassword = Md5Utils.encodePassword(oldPassword);
        //System.out.println("userInfo------------"+userInfo);
        //如果前端传过来的userName和session中存的一样
        if(userInfo!=null){
            if(encodeOldPassword.equals(userInfo.getPassword())){
                return true;
            }
        }
        return false;
    }

}
