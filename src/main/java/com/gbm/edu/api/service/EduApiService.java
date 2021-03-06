package com.gbm.edu.api.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.net.URLCodec;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gbm.edu.api.model.BoardModel;
import com.gbm.edu.api.model.ResModel;
import com.gbm.edu.api.model.UserModel;
import com.gbm.edu.api.resttemplate.RestTemplateService;
import com.gbm.edu.api.util.CommonUtils;
import com.gbm.edu.security.model.SecurityUser;
import com.gbm.edu.security.service.UserService;
import com.gbm.edu.service.EduApiMapper;
import com.gbm.edu.util.AES256Util;
import com.gbm.edu.util.CamelCaseMap;
import com.gbm.edu.util.CommonUtil;
import com.gbm.edu.config.PagingVO;

import org.apache.log4j.Logger;

@Service
public class EduApiService {
	
	@Autowired
	private EduApiMapper eduApiMapper;
	
	@Autowired
	public SqlSession sqlSession;
	
	private Logger log = Logger.getLogger(UserService.class);
	
	SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	Calendar time = Calendar.getInstance();
	
	
	public List<CamelCaseMap> getSignup() throws Exception {
		//????????????
		return null;
	}
	
    public List<CamelCaseMap> position() throws Exception {
		//???????????? ??????
		List<CamelCaseMap> position = eduApiMapper.position();
    	return position;
    }
	
    public List<CamelCaseMap> department() throws Exception {
		//???????????? ??????
		List<CamelCaseMap> department = eduApiMapper.department();
    	return department;
    }

    public List<CamelCaseMap> getList1() throws Exception {
		//?????????(meeting) ?????????
		List<CamelCaseMap> result1 = eduApiMapper.getList1();
    	return result1;
    }

	public List<CamelCaseMap> getRes() throws Exception {
		//???????????????(reservation)
		List<CamelCaseMap> res = eduApiMapper.getRes();
		return res;
	}
	
	public int getcountUser() {
		return eduApiMapper.getcountUser();
	}
	
	public List<CamelCaseMap> getUser(PagingVO vo){
		return eduApiMapper.getUser(vo.getStart()+"",vo.getEnd()+"");
	}

	public List<CamelCaseMap> getManager() throws Exception {		
		//???????????????(manager_level) ?????????
		List<CamelCaseMap> manager = eduApiMapper.getManager();
		return manager;
	}
	
	public List<CamelCaseMap> modifyUser(HttpSession session) throws Exception {		
		//user ????????? ???????????? (???????????? ??????)
		String sessionId = (String)session.getAttribute("userId");
		List<CamelCaseMap> user = eduApiMapper.modifyUser(sessionId);
		return user;
	}
	
	public List<CamelCaseMap> approvalRes() throws Exception {
		//???????????????(reservation)
		List<CamelCaseMap> res = eduApiMapper.approvalRes();
		return res;
	}
	
	public void checkPw(SecurityUser securityUser) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		securityUser.getPassword();
	}

	//???????????? ?????? (?????? ???????????? ??????)
	public String pwChange(String existPass, HttpSession session) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 

		String id = (String)session.getAttribute("userId");
		String password = eduApiMapper.getPassword(id);
		boolean passwordMatch = passwordEncoder.matches(existPass, password);

		if(passwordMatch==true) {
			log.info("???????????? ?????? ??????");
			return "Y";
		}
		else {
			log.info("???????????? ?????? ??????");
			return "N";
		}
		
	}

	//???????????? ??????
	public String updatePw(String newPass, String sessionId) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
		String passwordEn = passwordEncoder.encode(newPass);
		eduApiMapper.updatePw(sessionId, passwordEn);
		
		return "Y";
		
	}

	//?????? ??????
	public String deleteMember(String sessionId) {
		
		eduApiMapper.deleteMember(sessionId);
		log.info("???????????? ??????");
		return "Y";
	}

	public List<CamelCaseMap> modifyUser(String sessionId) {
		return null;
	}

	public List<CamelCaseMap> updateMember() {
		return null;
	}

	public String updateMember(SecurityUser securityUser, HttpServletRequest request) {

		securityUser.setModId(securityUser.getUserNm());
		
        String ip = request.getHeader("X-FORWARDED-FOR"); 
	    //proxy ????????? ??????
	    if (ip == null || ip.length() == 0) ip = request.getHeader("Proxy-Client-IP");
	    //????????? ????????? ??????
	    if (ip == null || ip.length() == 0) ip = request.getHeader("WL-Proxy-Client-IP");
	    if (ip == null || ip.length() == 0) ip = request.getRemoteAddr() ;
	        
	    securityUser.setModIp(ip);
	    securityUser.setCudMode("Update");
		
		eduApiMapper.updateMember(securityUser);
		log.info("-------???????????? ?????? ??????-------");
		return "Y";
	}

	//???????????? (insert)
	public String insertRes(String stHour, String stMinute, String finHour, String finMinute, ResModel resModel, HttpServletRequest request) throws ParseException {
		
		//?????? set
		
		//???????????? ??????
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd");
		Date set = new Date();
		String date = format1.format(set);
		 
		String time1 = stHour + ":" + stMinute + ":00";
   	 	String stTime = date + " " + time1;
   	 	//Date transStTime = transFormat.parse(stTime);
   	 	resModel.setStartTm(stTime);
        log.info("????????? ?????? ?????? ?????? : " + stTime);
        
        String time2 = finHour + ":" + finMinute + ":00";
        String finTime = date + " " + time2;
        //Date transFinTime = transFormat.parse(stTime);
        resModel.setFinishTm(finTime);
        log.info("????????? ?????? ?????? ?????? : " + finTime);
		
        String ip = request.getHeader("X-FORWARDED-FOR"); 
	    //proxy ????????? ??????
	    if (ip == null || ip.length() == 0) ip = request.getHeader("Proxy-Client-IP");
	    //????????? ????????? ??????
	    if (ip == null || ip.length() == 0) ip = request.getHeader("WL-Proxy-Client-IP");
	    if (ip == null || ip.length() == 0) ip = request.getRemoteAddr() ;
        
        resModel.setRegId(resModel.getUserNm());
        resModel.setRegIp(ip);
        resModel.setModId(resModel.getUserNm());
        resModel.setModIp(ip);
        resModel.setCudMode("Create");
        
        eduApiMapper.insertRes(resModel);
        
		return "Y";
	}

	public String updateRes(ResModel resModel) {
		
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		Date time = new Date();
		resModel.setApprovalTm(format1.format(time));
		eduApiMapper.updateRes(resModel);
		return "Y";
		
	}

	public int countApproval() {
		
		int approval = eduApiMapper.countApproval();
		return approval;
		
	}

	public String updateAdmin(SecurityUser securityUser) {
		
		securityUser.setRole("ADMIN");
		securityUser.setManagerLe("A002");
		securityUser.setCudMode("Update");
		
		eduApiMapper.updateAdmin(securityUser);
		return "Y";
	}
	
}
